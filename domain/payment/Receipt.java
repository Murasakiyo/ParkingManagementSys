package domain.payment;

import java.time.LocalDateTime;

// Receipt generated after successful payment and exit
public class Receipt {

    // Vehicle plate number associated with the transaction
    private final String plate;

    // Total amount charged in this transaction
    private final double totalDue;

    // Amount paid by the customer
    private final double amountPaid;
    private final double change;

    // Payment method used (CASH, CARD)
    private final PaymentMethod method;

    // Timestamp of payment
    private final LocalDateTime timestamp;

    // Remaining unpaid fines after this transaction (if any)
    private final double remainingUnpaidFines;

    // Constructor intializing
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

    // Methods to access receipt information
    public String getPlate() { return plate; }
    public double getTotalDue() { return totalDue; }
    public double getAmountPaid() { return amountPaid; }
    public double getChange() { return change; }
    public PaymentMethod getMethod() { return method; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getRemainingUnpaidFines() { return remainingUnpaidFines; }
}
