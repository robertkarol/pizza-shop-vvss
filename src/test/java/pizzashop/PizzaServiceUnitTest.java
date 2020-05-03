package pizzashop;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;
import pizzashop.service.PizzaService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PizzaServiceUnitTest {
    @Mock
    private PaymentRepository paymentRepo;

    @Mock
    private MenuRepository menuRepo;

    @InjectMocks
    private PizzaService service;

    private static List<Payment> paymentList;


    //Before & After
    @BeforeEach
    void setUpEach() {
        //create mocks
        Payment p1 = mock(Payment.class);
        when(p1.getAmount()).thenReturn(20.5);
        when(p1.getType()).thenReturn(PaymentType.CASH);
        when(p1.getTableNumber()).thenReturn(1);

        Payment p2 = mock(Payment.class);
        when(p2.getAmount()).thenReturn(31.25);
        when(p2.getType()).thenReturn(PaymentType.CARD);
        when(p2.getTableNumber()).thenReturn(2);

        Payment p3 = mock(Payment.class);
        when(p3.getAmount()).thenReturn(9.5);
        when(p3.getType()).thenReturn(PaymentType.CASH);
        when(p3.getTableNumber()).thenReturn(3);

        Payment p4 = mock(Payment.class);
        when(p4.getAmount()).thenReturn(8.75);
        when(p4.getType()).thenReturn(PaymentType.CARD);
        when(p4.getTableNumber()).thenReturn(4);

        paymentList = new ArrayList<>(Arrays.asList(p1,p2,p3,p4));
        MockitoAnnotations.initMocks(this);

        //behaviour of mocks
        when(paymentRepo.getAll()).thenReturn(paymentList);

        doAnswer(invocationOnMock -> {
            Payment p = invocationOnMock.getArgument(0);
            paymentList.add(p);
            return null;
        }).when(paymentRepo).add(any(Payment.class));
    }

    @AfterEach
    void tearDownEach() {
        paymentList = null;
    }



    //TESTS
    @Test
    void test_getTotalAmount()
    {
        //arrange
        //in method setUpEach()

        //act & assert
        assertEquals(4, service.getPayments().size());
        assertEquals(40.0, service.getTotalAmount(PaymentType.CARD));
        assertEquals(30.0, service.getTotalAmount(PaymentType.CASH));

        verify(paymentRepo, times(3)).getAll();
    }

    @Test
    void test_addPayment() {
        //arrange
        //in method setUpEach()

        //act & assert
        assertEquals(4, service.getPayments().size());
        service.addPayment(8, PaymentType.CARD, 10.0);
        assertEquals(5, service.getPayments().size());

        verify(paymentRepo).add(any(Payment.class));
        verify(paymentRepo, times(2)).getAll();
    }
}