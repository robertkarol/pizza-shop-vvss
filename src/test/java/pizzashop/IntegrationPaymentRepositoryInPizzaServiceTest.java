package pizzashop;

import org.junit.jupiter.api.*;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;
import pizzashop.service.PizzaService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
    * Integration of PaymentRepository in PizzaService.
 */
public class IntegrationPaymentRepositoryInPizzaServiceTest {
    private static PizzaService service;
    private final static String pathToPayments = "data/payments.txt";
    private static List<String> initialPayments;
    private static File filePayments;



    @Disabled
    private static List<String> readPayments()
    {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePayments))) {
            lines.addAll(br.lines().collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }



    //Before & After
    @BeforeAll
    static void setUp(){
        //get file
        ClassLoader classLoader = PaymentRepository.class.getClassLoader();
        filePayments = new File(Objects.requireNonNull(classLoader.getResource(pathToPayments))
                .getFile());

        //payments before testing
        initialPayments = readPayments();
    }

    @BeforeEach
    void setUpEach() {
        //get service
        PaymentRepository paymentRepo = new PaymentRepositoryMock();
        MenuRepository menuRepo = mock(MenuRepository.class);

        service = new PizzaService(menuRepo, paymentRepo);
    }

    @AfterEach
    void tearDownEach() {
        service = null;
    }

    //After
    @AfterAll
    static void tearDown() {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePayments))) {
            for (String line: initialPayments) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //TESTS
    @Test
    void test_getTotalAmount()
    {
        //arrange
        //in setUp & setUpEach methods

        //act & assert
        assertEquals(4, service.getPayments().size());
        assertEquals(40.0, service.getTotalAmount(PaymentType.CARD));
        assertEquals(30.0, service.getTotalAmount(PaymentType.CASH));
    }

    @Test
    void test_addPayment() {
        //arrange
        //in setUp & setUpEach methods
        Payment p = mock(Payment.class);
        when(p.getTableNumber()).thenReturn(7);
        when(p.getType()).thenReturn(PaymentType.CASH);
        when(p.getAmount()).thenReturn(7.57);
        String pAsString = p.getTableNumber() + "," + p.getType() + "," + p.getAmount();
        when(p.toString()).thenReturn(pAsString);

        //act
        service.addPayment(p.getTableNumber(), p.getType(),p.getAmount());

        //assert
        assertEquals(5, service.getPayments().size());
        assertEquals(5, readPayments().size());
    }
}
