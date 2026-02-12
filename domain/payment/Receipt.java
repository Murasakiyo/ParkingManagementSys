package domain.payment;

import java.time.LocalDateTime;

public class Receipt {
    private final String plate;
    private final double totalDue;
    private final double amountPaid;
    private final double change;
    private final PaymentMethod method;
    private final LocalDateTime timestamp;
    private final double remainingUnpaidFines;

    public Receipt(String plate, double totalDue, double amountPaid, double change, PaymentMethod method, 
        LocalDateTime timestamp, double remainingUnpaidFines) {
        this.plate = plate;
        this.totalDue = totalDue;
        this.amountPaid = amountPaid;
        this.change = change;
        this.method = method;
        this.timestamp = timestamp;
        this.remainingUnpaidFines = remainingUnpaidFines;
    }

    public String getPlate() { return plate; }
    public double getTotalDue() { return totalDue; }
    public double getAmountPaid() { return amountPaid; }
    public double getChange() { return change; }
    public PaymentMethod getMethod() { return method; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getRemainingUnpaidFines() { return remainingUnpaidFines; }
}
