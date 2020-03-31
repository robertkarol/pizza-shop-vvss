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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(2)
class PizzaServiceTest {
    private static PizzaService service;
    private final static String pathToPayments = "data/payments.txt";
    private static List<String> initialPayments;



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
    @BeforeEach
    void setUpEach() {
        //initial payments in file
        initialPayments = readPayments();

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
    @DisplayName("TCs with valid input parameters!")
    @ParameterizedTest
    @MethodSource("validParamsProvider")
    void testAddPaymentValid(int tableNumber, double amount) {
        //arrange
        //setUpEach method

        //act
        service.addPayment(tableNumber, PaymentType.CASH, amount);

        //assert
        assertTrue(readPayments().size() == initialPayments.size()+1);
    }

    @DisplayName("TCs with invalid parameters!")
    @ParameterizedTest
    @MethodSource("invalidParamsProvider")
    void testAddPaymentInvalid(int tableNumber, double amount) {
        //arrange
        //setUpEach method


        //act & assert
        assertThrows(IllegalArgumentException.class,
                ()-> service.addPayment(tableNumber, PaymentType.CARD, amount));
    }
}
