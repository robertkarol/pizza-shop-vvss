package pizzashop;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;
import pizzashop.service.PizzaService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(2)
class PizzaServiceTest {
    private static PizzaService service;
    private final static double validAmount = 20.5;
    private final static String pathToPayments = "data/payments.txt";
    private static List<String> initialPayments;
    private static int filePaymentsLines;



    //Ignored
    @Disabled
    private static List<String> readPayments()
    {
        //initial payments in file
        ClassLoader classLoader = PaymentRepository.class.getClassLoader();
        File file = new File(classLoader.getResource(pathToPayments).getFile());
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            lines.addAll(br.lines().collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }



    //Before
    @BeforeAll
    static void setUp() {
        //initial payments in file
        initialPayments = readPayments();
        filePaymentsLines = initialPayments.size();

        //service
        service = new PizzaService(new MenuRepository(), new PaymentRepository());
    }



    //After
    @AfterEach
    void tearDownEach() {
        ClassLoader classLoader = PaymentRepository.class.getClassLoader();
        File file = new File(classLoader.getResource(pathToPayments).getFile());

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line: initialPayments) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //Tests
    @DisplayName("TCs with valid table numbers!")
    @ParameterizedTest(name="nrTable used: {argumentsWithNames}")
    @ValueSource(ints = {1,2,7,8})
    void tcsOK(int tableNumber) {
        service.addPayment(tableNumber, PaymentType.CASH, validAmount);

        int currentSize = readPayments().size();
        assertTrue(currentSize == (filePaymentsLines+1));
        filePaymentsLines+=1;
    }

    @DisplayName("TCs with invalid table numbers!")
    @ParameterizedTest(name="nrTable used: {argumentsWithNames}")
    @ValueSource(ints = {0,9})
    void tcsNotOK(int tableNumber) {
        assertThrows(IllegalArgumentException.class,
                ()-> service.addPayment(tableNumber, PaymentType.CARD, validAmount));
    }
}
