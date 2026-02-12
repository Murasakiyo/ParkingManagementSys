package domain.fine;

import java.time.LocalDateTime;

public class FineRecord {
    private final String plate;
    private final FineReason reason;
    private final double amount;
    private final LocalDateTime createdAt;
    private boolean paid;

    public FineRecord(String plate, FineReason reason, double amount, LocalDateTime createdAt) {
        this.plate = plate;
        this.reason = reason;
        this.amount = amount;
        this.createdAt = createdAt;
        this.paid = false;
    }

    public String getPlate() { return plate; }
    public FineReason getReason() { return reason; }
    public double getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isPaid() { return paid; }

    public void markPaid() { this.paid = true; }
}
