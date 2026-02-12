package domain.payment;

import java.time.LocalDateTime;

public class Payment {
    private final PaymentMethod method;
    private final double amountPaid;
    private final LocalDateTime timestamp;

    public Payment(PaymentMethod method, double amountPaid, LocalDateTime timestamp) {
        this.method = method;
        this.amountPaid = amountPaid;
        this.timestamp = timestamp;
    }

    public PaymentMethod getMethod() { return method; }
    public double getAmountPaid() { return amountPaid; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
