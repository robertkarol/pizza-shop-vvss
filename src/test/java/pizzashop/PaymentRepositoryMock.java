package pizzashop;

import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.PaymentRepository;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class PaymentRepositoryMock extends PaymentRepository {

    @Override
    protected void readPayments() {
        String paymentAsString;

        //create mocks
        Payment p1 = mock(Payment.class);
        when(p1.getAmount()).thenReturn(20.5);
        when(p1.getType()).thenReturn(PaymentType.CASH);
        when(p1.getTableNumber()).thenReturn(1);
        paymentAsString = p1.getTableNumber() + ","+ p1.getType() +"," + p1.getAmount();
        when(p1.toString()).thenReturn(paymentAsString);

        Payment p2 = mock(Payment.class);
        when(p2.getAmount()).thenReturn(31.25);
        when(p2.getType()).thenReturn(PaymentType.CARD);
        when(p2.getTableNumber()).thenReturn(2);
        paymentAsString = p2.getTableNumber() + ","+ p2.getType() +"," + p2.getAmount();
        when(p2.toString()).thenReturn(paymentAsString);

        Payment p3 = mock(Payment.class);
        when(p3.getAmount()).thenReturn(9.5);
        when(p3.getType()).thenReturn(PaymentType.CASH);
        when(p3.getTableNumber()).thenReturn(3);
        paymentAsString = p3.getTableNumber() + ","+ p3.getType() +"," + p3.getAmount();
        when(p3.toString()).thenReturn(paymentAsString);

        Payment p4 = mock(Payment.class);
        when(p4.getAmount()).thenReturn(8.75);
        when(p4.getType()).thenReturn(PaymentType.CARD);
        when(p4.getTableNumber()).thenReturn(4);
        paymentAsString = p4.getTableNumber() + ","+ p4.getType() +"," + p4.getAmount();
        when(p4.toString()).thenReturn(paymentAsString);

        paymentList.addAll(Arrays.asList(p1,p2,p3,p4));
    }
}
