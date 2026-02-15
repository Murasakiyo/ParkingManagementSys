package domain.payment;

import java.time.LocalDateTime;

public class Payment {

    // Method used for payment
    private final PaymentMethod method;

    // Amount paid by customer
    private final double amountPaid;
    
    // Timestamp when payment was made
    private final LocalDateTime timestamp;

    // constructor initilizing payment
    public Payment(PaymentMethod method, double amountPaid, LocalDateTime timestamp) {
        this.method = method;
        this.amountPaid = amountPaid;
        this.timestamp = timestamp;
    }

    // methods to access payment information
    public PaymentMethod getMethod() { return method; }
    public double getAmountPaid() { return amountPaid; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
