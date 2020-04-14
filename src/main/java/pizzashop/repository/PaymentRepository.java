package pizzashop.repository;

import pizzashop.model.Payment;
import pizzashop.model.PaymentType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public class PaymentRepository {
    private static String filename = "data/payments.txt";
    private List<Payment> paymentList;
    private StringBuilder paymentBuilder;
    private File filePayments;

    public PaymentRepository(){
        this.paymentList = new ArrayList<>();
        this.paymentBuilder = new StringBuilder();
        ClassLoader classLoader = PaymentRepository.class.getClassLoader();
        filePayments = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());
        readPayments();
    }

    private void readPayments(){
        try(BufferedReader br = new BufferedReader(new FileReader(filePayments))) {
            String line = null;
            while((line=br.readLine())!=null){
                Payment payment=getPayment(line);
                if (payment == null) continue;  // C06: added null check since getPayment function may return null and
                                                // such situation was not handled
                paymentList.add(payment);
            }
        } catch (Exception e)  {
            e.printStackTrace();
        }
    }

    private Payment getPayment(String line){
        Payment item=null;
        if (line==null|| line.equals("")) return null;
        StringTokenizer st=new StringTokenizer(line, ",");
        int tableNumber= Integer.parseInt(st.nextToken());
        String type= st.nextToken();
        double amount = Double.parseDouble(st.nextToken());
        item = new Payment(tableNumber, PaymentType.valueOf(type), amount);
        return item;
    }

    public void add(Payment payment){
        paymentList.add(payment);
        writeAll();
    }

    public List<Payment> getAll(){
        return paymentList;
    }

    public void writeAll(){
        paymentBuilder.setLength(0);
        for (Payment p:paymentList) {
            System.out.println(p.toString());
            paymentBuilder.append(p.toString());
            paymentBuilder.append(System.lineSeparator());
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePayments))) {
            bw.write(paymentBuilder.toString());
        } catch (IOException e) {
            paymentList.remove(paymentList.size()-1); //sync memory with file
            throw new IllegalStateException(e);
        }
    }

}
