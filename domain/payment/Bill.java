package domain.payment;

import java.time.LocalDateTime;

public class Bill {
    private final String plate;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;

    private final int hoursCharged;
    private final double hourlyRate;
    private final double parkingFee;

    private final double unpaidFinesPrevious;
    private final double fineDueNow;
    private final double totalDue;

    public Bill(String plate, LocalDateTime entryTime, LocalDateTime exitTime, int hoursCharged, 
        double hourlyRate, double parkingFee, double unpaidFinesPrevious, double fineDueNow) {
        this.plate = plate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hoursCharged = hoursCharged;
        this.hourlyRate = hourlyRate;
        this.parkingFee = parkingFee;
        this.unpaidFinesPrevious = unpaidFinesPrevious;
        this.fineDueNow = fineDueNow;
        this.totalDue = parkingFee + unpaidFinesPrevious + fineDueNow;
    }

    public String getPlate() { return plate; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }

    public int getHoursCharged() { return hoursCharged; }
    public double getHourlyRate() { return hourlyRate; }
    public double getParkingFee() { return parkingFee; }

    public double getUnpaidFinesPrevious() { return unpaidFinesPrevious; }
    public double getFineDueNow() { return fineDueNow; }
    public double getTotalDue() { return totalDue; }
}