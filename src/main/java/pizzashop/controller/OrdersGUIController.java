package pizzashop.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pizzashop.model.MenuDataModel;
import pizzashop.model.PaymentType;
import pizzashop.service.PizzaService;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrdersGUIController {

    @FXML
    private ComboBox<Integer> orderQuantity;
    @FXML
    private TableView orderTable;
    @FXML
    private TableColumn tableQuantity;
    @FXML
    protected TableColumn tableMenuItem;
    @FXML
    private TableColumn tablePrice;
    @FXML
    private Label pizzaTypeLabel;
    @FXML
    private Button addToOrder;
    @FXML
    private Label orderStatus;
    @FXML
    private Button placeOrder;
    @FXML
    private Button orderServed;
    @FXML
    private Button payOrder;
    @FXML
    private Button exitTable; // C11: rename newOrder to exitTable. Name was misleading since the button does exit action.

    private   List<String> orderList = FXCollections.observableArrayList();
    private List<Double> orderPaymentList = FXCollections.observableArrayList();
    public static double getTotalAmount() {
        return totalAmount;
    }
    public static synchronized void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    private PizzaService service;
    private int tableNumber;

    public ObservableList<String> observableList;
    private TableView<MenuDataModel> table = new TableView<MenuDataModel>();
    private ObservableList<MenuDataModel> menuData;// = FXCollections.observableArrayList();
    private Calendar now = Calendar.getInstance();
    private static double totalAmount;

    public OrdersGUIController(){ }

    public void setService(PizzaService service, int tableNumber){
        this.service=service;
        this.tableNumber=tableNumber;
        initData();

    }

    public void showPaymentAlert() {
        Alert paymentAlert = new Alert(Alert.AlertType.CONFIRMATION);

        paymentAlert.setTitle("Payment for Table "+tableNumber);
        paymentAlert.setHeaderText("Total amount: " + totalAmount);
        paymentAlert.setContentText("Please choose payment option");

        ButtonType cardPayment = new ButtonType("Pay by Card");
        ButtonType cashPayment = new ButtonType("Pay Cash");
        ButtonType cancel = new ButtonType("Cancel");

        paymentAlert.getButtonTypes().setAll(cardPayment, cashPayment, cancel);
        Optional<ButtonType> result = paymentAlert.showAndWait();

        if (result.get() == cardPayment) {
            cardPayment();
            service.addPayment(tableNumber, PaymentType.CARD,totalAmount);
        } else if (result.get() == cashPayment) {
            cashPayment();
            service.addPayment(tableNumber, PaymentType.CASH,totalAmount);
        } else if (result.get() == cancel) {
            cancelPayment();
        } else {
            cancelPayment();
        }
    }

    private void cardPayment() {
        System.out.println("--------------------------");
        System.out.println("Paying by card...");
        System.out.println("Please insert your card!");
        System.out.println("--------------------------");
    }
    private void cashPayment() {
        System.out.println("--------------------------");
        System.out.println("Paying cash...");
        System.out.println("Please show the cash...!");
        System.out.println("--------------------------");
    }
    private void cancelPayment()
    {
        System.out.println("--------------------------");
        System.out.println("Payment choice needed...");
        System.out.println("--------------------------");
    }

    private void initData(){
        menuData = FXCollections.observableArrayList(service.getMenuData());
        menuData.setAll(service.getMenuData());
        orderTable.setItems(menuData);

        //Controller for Place Order Button
        placeOrder.setOnAction(event ->{
            orderList= menuData.stream()
                    .filter(x -> x.getQuantity()>0)
                    .map(menuDataModel -> menuDataModel.getQuantity() +" "+ menuDataModel.getMenuItem())
                    .collect(Collectors.toList());
            observableList = FXCollections.observableList(orderList);
            KitchenGUIController.order.add("Table" + tableNumber +" "+ orderList.toString());
            orderStatus.setText("Order placed at: " +  now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE));
        });

        //Controller for Order Served Button
        orderServed.setOnAction(event -> {orderStatus.setText("Served at: " + now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE));
        });

        //Controller for Pay Order Button
        payOrder.setOnAction(event -> {
            orderPaymentList= menuData.stream()
                    .filter(x -> x.getQuantity()>0)
                    .map(menuDataModel -> menuDataModel.getQuantity()*menuDataModel.getPrice())
                    .collect(Collectors.toList());
            setTotalAmount(orderPaymentList.stream().mapToDouble(e->e.doubleValue()).sum());
            orderStatus.setText("Total amount: " + getTotalAmount());
            System.out.println("--------------------------");
            System.out.println("Table: " + tableNumber);
            System.out.println("Total: " + getTotalAmount());
            System.out.println("--------------------------");
            showPaymentAlert();
        });
    }

    public void initialize(){

        //populate table view with menuData from OrderGUI
        table.setEditable(true);
        tableMenuItem.setCellValueFactory(
                new PropertyValueFactory<MenuDataModel, String>("menuItem"));
        tablePrice.setCellValueFactory(
                new PropertyValueFactory<MenuDataModel, Double>("price"));
        tableQuantity.setCellValueFactory(
                new PropertyValueFactory<MenuDataModel, Integer>("quantity"));

        //bind pizzaTypeLabel and quantity combo box with the selection on the table view
        orderTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MenuDataModel>() {
        @Override
        public void changed(ObservableValue<? extends MenuDataModel> observable, MenuDataModel oldValue, MenuDataModel newValue) {
           pizzaTypeLabel.textProperty().bind(newValue.menuItemProperty());
              }
        });

        //Populate Combo box for Quantity
        ObservableList<Integer> quantityValues =  FXCollections.observableArrayList(0, 1, 2,3,4,5);
        orderQuantity.getItems().addAll(quantityValues);
        orderQuantity.setPromptText("Quantity");

        //Controller for Add to order Button
        addToOrder.setOnAction(event -> {
            orderTable.getSelectionModel().selectedItemProperty().addListener(
                    (ChangeListener<MenuDataModel>) (observable, oldValue, newValue) -> {
                Integer quantity = orderQuantity.getValue();
                if(oldValue!=null && quantity!=null)
                    oldValue.setQuantity(quantity);
                });
        });

        //Controller for Exit table Button
        exitTable.setOnAction(event -> {
            Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION, "Exit table?",ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = exitAlert.showAndWait();
            if (result.get() == ButtonType.YES){
                Stage stage = (Stage) exitTable.getScene().getWindow();
                stage.close();
                }
        });
    }
}
