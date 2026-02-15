package domain.fine;

import java.time.LocalDateTime;

public class FineRecord {
    // get vehicle plate number
    private final String plate;

    // get fine reason
    private final FineReason reason;

    // get total fine amount from previous and current parking
    private final double amount;
    // get time first fine was created
    private final LocalDateTime createdAt;

    // boolean to check whether debts are cleared or not
    private boolean paid;

    // constructor
    public FineRecord(String plate, FineReason reason, double amount, LocalDateTime createdAt) {
        this.plate = plate;
        this.reason = reason;
        this.amount = amount;
        this.createdAt = createdAt;
        this.paid = false;
    }

    // methods to access FineRecord variables
    public String getPlate() { return plate; }
    public FineReason getReason() { return reason; }
    public double getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isPaid() { return paid; }

    // function to mark all debts as cleared
    public void markPaid() { this.paid = true; }
}
