package service;
import java.time.LocalDateTime;
import java.util.List;

import domain.ParkingLot;
import domain.Ticket;
import domain.Vehicle;
import domain.VehicleType;

import domain.Motorcycle;
import domain.Car;
import domain.SuvTruck;
import domain.Handicapped;

import domain.parking.*;
import domain.payment.*;
import domain.fine.*;

public class ParkingService {
    private final ParkingLot lot;

    // constructor
    public ParkingService(ParkingLot lot) {
        this.lot = lot;
    }

    public List<ParkingSpot> searchSpots(String plate, VehicleType type, boolean handicappedCardHolder) {
        Vehicle v = makeVehicle(plate, type, handicappedCardHolder);
        return lot.findSuitableSpots(v);
    }

    public Ticket confirmPark(String plate, VehicleType type, boolean handicappedCardHolder, 
        boolean hasReservation, String spotID) {
        Vehicle v = makeVehicle(plate, type, handicappedCardHolder);
        return lot.parkVehicle(v, spotID, hasReservation, LocalDateTime.now());
    }

    public int getAvailable(){
        int total = lot.getTotalSpotCount();
        int occupied = lot.getOccupiedCount();
        int available = total - occupied;
        return (available);
    }

    // Create vehicle based on licensed plate and vehicle type
    private Vehicle makeVehicle(String plate, VehicleType type, boolean handicappedCardHolder) {
        if (plate == null || plate.trim().isEmpty()) {
            throw new IllegalArgumentException("Plate cannot be empty");
        }
        String cleanPlate = plate.trim().toUpperCase();

        switch (type) {
            case MOTORCYCLE: return new Motorcycle(cleanPlate);
            case CAR: return new Car(cleanPlate);
            case SUV_TRUCK: return new SuvTruck(cleanPlate);
            case HANDICAPPED: return new Handicapped(cleanPlate, handicappedCardHolder);
            default: throw new IllegalArgumentException("Unknown vehicle type");
        }
    }

    // Calculate bill for users
    public Bill calculateBill(String plate) {
        return lot.buildBill(plate, LocalDateTime.now());
    }

    public Receipt payAndExit(String plate, PaymentMethod method, double amountPaid, boolean payFinesNow) {
        if (amountPaid < 0) {
            throw new IllegalArgumentException("Amount paid cannot be negative.");
        }
        Payment payment = new Payment(method, amountPaid, LocalDateTime.now());
        return lot.payAndExit(plate, payment, LocalDateTime.now(), payFinesNow);
    }

    // Set fine schemes ---------------------------------------------------------
    public void changeFineSchemeToFixed(double amount) {
        lot.setFineScheme(new FixedScheme(amount));
    }

    public void changeFineSchemeToProgressive(double base, double incPerUnit) {
        lot.setFineScheme(new ProgressiveScheme(base, incPerUnit));
    }

    public void changeFineSchemeToHourly(double ratePerUnit) {
        lot.setFineScheme(new HourlyScheme(ratePerUnit));
    }
    // ---------------------------------------------------------------------------

    // List of all vehicles currently in the lot 
    // - Revenue report 
    // - Occupancy report 
    // - Fine report (outstanding fines)
    public String ReportSummary() {
        int total = lot.getTotalSpotCount();
        int occupied = lot.getOccupiedCount();
        double revenue = lot.getTotalRevenue();
        int rate;
        if (total == 0) {
            rate = 0;
        } else {
            rate = occupied * 100 / total;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Reports\n");
        sb.append("Total spots: ").append(total).append("\n");
        sb.append("Occupied: ").append(occupied).append("\n");
        sb.append("Occupancy rate: ").append(rate).append("%\n");
        sb.append("Total revenue: RM ").append(revenue).append("\n\n");

        sb.append("Current vehicles:\n");
        for (String s : lot.getCurrentVehicles()) sb.append("- ").append(s).append("\n");

        sb.append("\nOutstanding fines:\n");
        java.util.List<String> fines = lot.getFinesReport();
        if (fines.isEmpty()) sb.append("(none)\n");
        else for (String f : fines) sb.append("- ").append(f).append("\n");

        return sb.toString();
    }

    // Write Summary for Admin
    public String AdminSummary(String scheme) {
        StringBuilder sb = new StringBuilder();

        int total = lot.getTotalSpotCount();
        int occupied = lot.getOccupiedCount();
        int available = total - occupied;
        int occupancyPercent = (total == 0) ? 0 : (occupied * 100 / total);

        sb.append("Admin Summary\n");
        
        if (scheme == "Fixed"){
            sb.append("\nFixed Fine Scheme: RM 50 fine for overstaying\n\n");
        }
        else if (scheme == "Progressive")
        {
            sb.append("\nProgressive Fine Scheme : \n");
            sb.append("- First 24 hours: RM 50 \n" + 
                        "- Hours 24-48: Additional RM 100 \n" + 
                        "- Hours 48-72: Additional RM 150 \n" + 
                        "- Above 72 hours: Additional RM 200 \n\n");
        }
        else if (scheme == "Hourly"){
            sb.append("\nHourly Fine Scheme : \n");
            sb.append("- RM 20 per hour for overstaying \n" + 
                        "- Fines are added to the customer's account \n" + 
                        "- Customers can pay fines when exiting \n" + 
                        "- the next exit will show the unpaid fine + the current parking fee\n\n");
        }
        
        sb.append("Total spots: ").append(total).append("\n");
        sb.append("Occupied: ").append(occupied).append("\n");
        sb.append("Available: ").append(available).append("\n");
        sb.append("Occupancy rate: ").append(occupancyPercent).append("%\n");
        sb.append("Total revenue: RM ").append(lot.getTotalRevenue()).append("\n\n");

        sb.append("All Spots (by floor/row)\n");
        for (Floor f : lot.getFloors()) {
            sb.append("Floor ").append(f.getfloorNum()).append("\n");
            for (Row r : f.getRows()) {
                sb.append("  Row ").append(r.getRowNum()).append("\n");
                for (ParkingSpot s : r.getSpots()) {
                    String status = s.isOccupied() ? "OCCUPIED" : "AVAILABLE";
                    String vehicle = s.isOccupied() ? s.getCurrentVehiclePlate() : "-";
                    sb.append("    ")
                    .append(s.getSpotID())
                    .append(" | ")
                    .append(s.getType())
                    .append(" | RM ")
                    .append(s.getHourlyRate())
                    .append("/hr | ")
                    .append(status)
                    .append(" | Vehicle: ")
                    .append(vehicle)
                    .append("\n");
                }
            }
            sb.append("\n");
        }

        sb.append("Current Vehicles\n");
        java.util.List<String> vehicles = lot.getCurrentVehicles();
        if (vehicles.isEmpty()) {
            sb.append("(none)\n");
        } else {
            for (String line : vehicles) {
                sb.append("- ").append(line).append("\n");
            }
        }
        sb.append("\n");

        sb.append("Unpaid Fines\n");
        java.util.List<String> fines = lot.getFinesReport();
        if (fines.isEmpty()) {
            sb.append("(none)\n");
        } else {
            for (String line : fines) {
                sb.append("- ").append(line).append("\n");
            }
        }

        return sb.toString();
    }
}