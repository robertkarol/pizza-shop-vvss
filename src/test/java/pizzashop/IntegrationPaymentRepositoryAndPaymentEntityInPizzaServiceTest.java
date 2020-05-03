package pizzashop;

import org.junit.jupiter.api.*;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;
import pizzashop.service.PizzaService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/*
    * Integration of PaymentRepository & PaymentEntity in PizzaService.
 */
class IntegrationPaymentRepositoryAndPaymentEntityInPizzaServiceTest {
    private static PizzaService service;
    private final static String pathToPayments = "data/payments.txt";
    private static List<String> initialPayments;
    private static File filePayments;



    //Ignored
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
    static void setUp() {
        //get file
        ClassLoader classLoader = PaymentRepository.class.getClassLoader();
        filePayments = new File(Objects.requireNonNull(classLoader.getResource(pathToPayments))
                .getFile());

        //payments before testing
        initialPayments = readPayments();
    }

    @BeforeEach
    void setUpEach() {
        //service
        service = new PizzaService(new MenuRepository(), new PaymentRepository());
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
    void addPaymentValid() {
        //arrange
        //setUpEach method
        int currentSize = service.getPayments().size();

        //act
        service.addPayment(2, PaymentType.CARD, 8.25);

        //assert
        assertEquals(currentSize+1, service.getPayments().size());
        assertEquals(currentSize+1, readPayments().size());
    }

    @Test
    void addPaymentInvalid() {
        //arrange
        //setUpEach method
        int currentSize = service.getPayments().size();

        //act & assert
        assertThrows(IllegalArgumentException.class,
                ()-> service.addPayment(-7, PaymentType.CARD, 10.5));
        assertThrows(IllegalArgumentException.class,
                ()-> service.addPayment(20, PaymentType.CASH, -10.5));
        assertEquals(currentSize, service.getPayments().size());
        assertEquals(currentSize, readPayments().size());
    }
}