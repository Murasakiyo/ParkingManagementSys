package domain.payment;

import java.time.LocalDateTime;

public class Bill {

    // Vehicle plate number
    private final String plate;

    // Entry and exit timestamps
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;

    // Parking duration and rate information
    private final int hoursCharged;
    private final double hourlyRate;
    private final double parkingFee;

    // unpaid fines from earlier visits
    private final double unpaidFinesPrevious;

    // new fine generated in this session
    private final double fineDueNow;

    // Total amount due (parking fee + all fines)
    private final double totalDue;

    // Constructor initializes all billing components
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

        // Calculate total amount payable
        this.totalDue = parkingFee + unpaidFinesPrevious + fineDueNow;
    }

    // Getter methods to access billing details (Mostly use in mainframe)
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