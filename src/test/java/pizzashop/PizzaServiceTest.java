package pizzashop;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;
import pizzashop.service.PizzaService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(2)
class PizzaServiceTest {
    private static PizzaService service;
    private final static String pathToPayments = "data/payments.txt";
    private static List<String> initialPayments;
    private static List<String> previousPayments;
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

    @Disabled
    private static Stream<Arguments> validParamsProvider()
    {
        return Stream.of(
                Arguments.arguments(1,0),
                Arguments.arguments(2,0.01),
                Arguments.arguments(7,Double.MAX_VALUE-1),
                Arguments.arguments(8,Double.MAX_VALUE)
        );

    }

    @Disabled
    private static Stream<Arguments> invalidParamsProvider()
    {
        return Stream.of(
                Arguments.arguments(0, 10.5),
                Arguments.arguments(9, 12.5),
                Arguments.arguments(5, -0.01),
                Arguments.arguments(-1,-5.5)
        );
    }




    //Before
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
        //initial payments in file
        previousPayments = readPayments();

        //service
        service = new PizzaService(new MenuRepository(), new PaymentRepository());
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



    //Tests
    @DisplayName("BBT -> TCs with valid input parameters!")
    @ParameterizedTest
    @MethodSource("validParamsProvider")
    void testAddPaymentValid(int tableNumber, double amount) {
        //arrange
        //setUpEach method

        //act
        service.addPayment(tableNumber, PaymentType.CASH, amount);

        //assert
        assertEquals(readPayments().size(), previousPayments.size() + 1);
    }

    @DisplayName("BBT -> TCs with invalid parameters!")
    @ParameterizedTest
    @MethodSource("invalidParamsProvider")
    void testAddPaymentInvalid(int tableNumber, double amount) {
        //arrange
        //setUpEach method

        //act & assert
        assertThrows(IllegalArgumentException.class,
                ()-> service.addPayment(tableNumber, PaymentType.CARD, amount));
    }

    @DisplayName("WBT -> TCs with valid input parameters!")
    @Test
    void addPaymentValidWBT() {
        //arrange
        //setUpEach method

        //act
        service.addPayment(1, PaymentType.CASH, 10);

        //assert
        assertEquals(readPayments().size(), previousPayments.size() + 1);
    }

    @DisplayName("WBT -> TCs with invalid input parameters!")
    @Test
    void addPaymentInvalidWBT() {
        //arrange
        //setUpEach method

        //act & assert
        assertThrows(IllegalArgumentException.class,
                ()-> service.addPayment(0, PaymentType.CARD, 12.5));
        assertThrows(IllegalArgumentException.class,
                ()-> service.addPayment(8, PaymentType.CARD, -1.5));
    }

    @DisplayName("WBT -> TC with invalid state!")
    @Test
    void addPaymentInvalidStateWBT() {
        //arrange
        //setUpEach method

        //act & assert
        assertTrue(filePayments.setWritable(false));

        assertThrows(IllegalStateException.class,
                ()-> service.addPayment(2, PaymentType.CARD, 30.2));
        assertEquals(readPayments().size(), previousPayments.size());

        assertTrue(filePayments.setWritable(true));
    }
}
