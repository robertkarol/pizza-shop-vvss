package pizzashop;

import org.junit.jupiter.api.*;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.PaymentRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentRepositoryUnitTest {
    private static PaymentRepository paymentRepo;
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
        //repo
        paymentRepo = new PaymentRepositoryMock();
    }

    @AfterEach
    void tearDownEach() {
        paymentRepo = null;
    }

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
    void test_getAll() {
        //arrange
        //in setUp & setUpEach methods

        //act & assert
        assertEquals(4, paymentRepo.getAll().size());
    }

    @Test
    void test_add() {
        //arrange
        //in setUp & setUpEach methods

        //act
        Payment p = mock(Payment.class);
        when(p.getTableNumber()).thenReturn(8);
        when(p.getType()).thenReturn(PaymentType.CARD);
        when(p.getAmount()).thenReturn(12.0);
        String pAsString = p.getTableNumber() + "," + p.getType() + "," + p.getAmount();
        when(p.toString()).thenReturn(pAsString);

        paymentRepo.add(p);

        //assert
        assertEquals(5, paymentRepo.getAll().size());
        assertEquals(5, readPayments().size());
    }
}