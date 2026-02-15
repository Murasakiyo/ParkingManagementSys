package domain;
//import domain.VehicleType;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

import domain.parking.*;
import domain.payment.*;
import domain.fine.*;

public class ParkingLot {

    // parking lot: floors -> rows -> spots
    private final List<Floor> floors = new ArrayList<>();

     // Active parking sessions indexed by vehicle plate number
    private final Map<String, Ticket> activeTicketsByPlate = new HashMap<>();

    // Total revenue collected from successful payments
    private double totalRevenue = 0.0;

    // Outstanding and paid fine history indexed by vehicle plate number
    private final Map<String, java.util.List<FineRecord>> finesByPlate = new HashMap<>();

    // Reference to FineCalculator to compute fines throughout the system
    private final FineCalculator fineCalculator = new FineCalculator();

    private FineScheme currentScheme = new FixedScheme(50.0);

    // Used in app.java
    public ParkingLot() {
        buildParkingLot();
    }

    //Build layout for parking lot
    private void buildParkingLot() {
        // 5 floors, Each floor has 2 rows, Each row has 6 spots (2 COMPACT, 2 REGULAR, 1 HANDICAPPED, 1 RESERVED)
        for (int floorNum = 1; floorNum <= 3; floorNum++) {
            Floor floor = new Floor(floorNum);

            for (int rowNum = 1; rowNum <= 2; rowNum++) {
                Row row = new Row(rowNum);

                // 1-2 COMPACT (RM2), 3-4 REGULAR (RM5), 5 HANDICAPPED (RM2), 6 RESERVED (RM10)
                row.addSpot(makeSpot(floorNum, rowNum, 1, SpotType.COMPACT, 2));
                row.addSpot(makeSpot(floorNum, rowNum, 2, SpotType.COMPACT, 2));
                row.addSpot(makeSpot(floorNum, rowNum, 3, SpotType.REGULAR, 5));
                row.addSpot(makeSpot(floorNum, rowNum, 4, SpotType.REGULAR, 5));
                row.addSpot(makeSpot(floorNum, rowNum, 5, SpotType.HANDICAPPED, 2));
                row.addSpot(makeSpot(floorNum, rowNum, 6, SpotType.RESERVED, 10));

                floor.addRow(row);
            }

            floors.add(floor);
        }
    }

    // Returns a parkingspot object with its row ID
    private ParkingSpot makeSpot(int floorNum, int rowNum, int spotNum, SpotType type, double rate) {
        String id = "F" + floorNum + "-R" + rowNum + "-S" + spotNum;
        return new ParkingSpot(id, type, rate);
    }

    // Function returning a list of suitable spots based on ParkingSpot
    public List<ParkingSpot> findSuitableSpots(Vehicle v) {

        // List of all spots the Vehicle (v) can occupy (into table)
        List<ParkingSpot> result = new ArrayList<>();

        for (int i = 0; i < floors.size(); i++) {
            // Get the current floor
            Floor f = floors.get(i);
            // Get all rows belonging to the current floor
            List<Row> rows = f.getRows();

            // Check each row within the current floor
            for (int j = 0; j < rows.size(); j++) {
                Row r = rows.get(j);

                List<ParkingSpot> spots = r.getSpots();

                // Check each parking spot
                for (int k = 0; k < spots.size(); k++) {
                    ParkingSpot s = spots.get(k);

                    // Check if vehicle fits the parking spot
                    if (s.canFit(v)) {
                        result.add(s);
                    }
                }
            }
        }
        // Return the list of suitable parking spots
        return result;
    }

    // Create ticket object for the vehicle
    public Ticket parkVehicle(Vehicle v, String spotID, boolean hasReservation, LocalDateTime now) {
        if (activeTicketsByPlate.containsKey(v.getPlate())) {
            throw new IllegalStateException("This plate already has an active ticket: " + v.getPlate());
        }
        ParkingSpot spot = getSpotByID(spotID);
        spot.occupy(v);

        // Ticket is generated using the exact time vehicle is parked
        String ticketID = "T-" + v.getPlate() + "-" + System.currentTimeMillis();
        FineScheme schemeSnapshot = this.currentScheme;
        Ticket ticket = new Ticket(ticketID, v.getPlate(), spotID, now, hasReservation, schemeSnapshot);
        activeTicketsByPlate.put(v.getPlate(), ticket);
        return ticket;
    }

    public Ticket getActiveTicket(String plate) {
        return activeTicketsByPlate.get(plate);
    }

    public List<Floor> getFloors() {
        return floors;
    }

    // Get parking spot by vehicle ID (For exiting)
    private ParkingSpot getSpotByID(String spotID) {

        for (int i = 0; i < floors.size(); i++) {
            Floor floor = floors.get(i);
            List<Row> rows = floor.getRows();

            for (int j = 0; j < rows.size(); j++) {
                Row row = rows.get(j);
                List<ParkingSpot> spots = row.getSpots();

                for (int k = 0; k < spots.size(); k++) {
                    ParkingSpot spot = spots.get(k);
                    if (spot.getSpotID().equals(spotID)) {
                        return spot;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Spot not found: " + spotID);
    }

    // Get amount of minutes vehicle is parked
    private int computeChargedHours(LocalDateTime entry, LocalDateTime exit) {
        // Converts duration to billable hours using ceiling rounding, with a minimum of 1 hour
        long minutes = java.time.Duration.between(entry, exit).toMinutes();
        if (minutes < 0) {
            throw new IllegalArgumentException("Exit time cannot be before entry time.");
        }
        int hours = (int) Math.ceil(minutes / 60.0);
        return Math.max(hours, 1);
    }

    // ---------------------------- FINES ---------------------------------------------------
    // Updates fine scheme for future parking entries
    public void setFineScheme(FineScheme scheme) {
        if (scheme == null) {
            throw new IllegalArgumentException("Fine scheme cannot be null.");
        }
        this.currentScheme = scheme;
    }

    // Compile all fines recorded for the specific plate
    private double getUnpaidFineTotal(String plate) {
        java.util.List<FineRecord> list = finesByPlate.get(plate);
        if (list == null) return 0.0;

        double sum = 0.0;
        for (FineRecord f : list) {
            if (!f.isPaid()) sum += f.getAmount();
        }
        return sum;
    }

    // Add unpaid fine
    private void addUnpaidFine(String plate, double amount, LocalDateTime now) {
        if (amount <= 0) {
            return;
        }

        // create a new fine record list for new vehicle plates that is fined
        List<FineRecord> list = finesByPlate.get(plate);
        if (list == null) {
            list = new ArrayList<>();
            finesByPlate.put(plate, list);
        }
        // Add the new fine record
        FineRecord fine = new FineRecord(plate, FineReason.OVER_24HOURS, amount, now);
        list.add(fine);
    }

    // Get all the fine record from the specific plate and mark is as paid
    private void markAllFinesPaid(String plate) {
        java.util.List<FineRecord> list = finesByPlate.get(plate);
        if (list == null) return;
        for (FineRecord f : list) {
            if (!f.isPaid()) f.markPaid();
        }
    }
    // --------------------------------------------------------------------------------------

    public Bill buildBill(String plate, LocalDateTime now) {
        if (plate == null){
            throw new IllegalArgumentException("Plate cannot be empty.");
        }
        String cleanPlate = plate.trim().toUpperCase();

        Ticket ticket = activeTicketsByPlate.get(cleanPlate);
        if (ticket == null) {
            throw new IllegalStateException("No active ticket found for plate: " + cleanPlate);
        }

        ParkingSpot spot = getSpotByID(ticket.getSpotID());

        int hours = computeChargedHours(ticket.getEntryTime(), now);
        double rate = spot.getHourlyRate();

        // Gets a discounted price of RM 2/hour only for a handicapped card holder
        Vehicle parked = spot.getCurrentVehicle();
        if (parked != null && parked.getType() == VehicleType.HANDICAPPED && parked.isHandicapped()) {
            rate = 2.0;

            // Free if handicapped vehicle has card AND parks in handicapped spot
            if (spot.getType() == SpotType.HANDICAPPED) {
                rate = 0.0;
            }
        }

        double parkingFee = rate * hours;
        double unpaidPrevious = getUnpaidFineTotal(cleanPlate);
        double fineNow = 0.0;

        // Over 24 hours fine
        fineNow += fineCalculator.computeOverstayFine(ticket.getEntryTime(), now, ticket.getFineSchemeAtEntry());

        // Reserved misuse fine: parked in RESERVED without reservation
        if (spot.getType() == SpotType.RESERVED && !ticket.hasReservation()) {
            fineNow += fineCalculator.computeReservedMisuseFine(ticket.getFineSchemeAtEntry());
        }

        return new Bill(cleanPlate, ticket.getEntryTime(), now, hours, rate, parkingFee, unpaidPrevious, fineNow);
    }


    public Receipt payAndExit(String plate, Payment payment, LocalDateTime now, boolean payFinesNow) {
        Bill bill = buildBill(plate, now);

        Ticket ticket = activeTicketsByPlate.get(bill.getPlate());
        FineScheme scheme = ticket.getFineSchemeAtEntry();

        // For fixed and progressive fine scheme, must pay fully before being able to leave
        if (!scheme.allowsUnpaidExit() && (bill.getFineDueNow() > 0 || bill.getUnpaidFinesPrevious() > 0) && !payFinesNow ) {
            throw new IllegalStateException("This fine scheme requires paying fines before exit.");
        }

        // Parking fee must always be paid to exit
        double mustPay = bill.getParkingFee();

        // If fines paid now, must pay in full
        if (payFinesNow) {
            mustPay = bill.getTotalDue(); // parking + unpaid previous + fine due now
        }
        if (payment.getAmountPaid() < mustPay) {
            throw new IllegalStateException("Insufficient payment. Minimum required: RM " + mustPay);
        }
        
        // Add the new fine to account if fine unpaid during this time
        if (bill.getFineDueNow() > 0) {
            addUnpaidFine(bill.getPlate(), bill.getFineDueNow(), now);
        }

        // If customer chooses to pay fines now, mark them all paid
        if (payFinesNow) {
            markAllFinesPaid(bill.getPlate());
        }

        // revenue: always add the parking fee; if paying fines, add them too
        totalRevenue += bill.getParkingFee();
        if (payFinesNow) {
            totalRevenue += bill.getUnpaidFinesPrevious() + bill.getFineDueNow();
        }

        // Release spot + remove ticket
        Ticket t = activeTicketsByPlate.remove(bill.getPlate());
        domain.parking.ParkingSpot spot = getSpotByID(t.getSpotID());
        spot.vacate();

        // Get change if customer pay extra
        double change = payment.getAmountPaid() - mustPay;

        // Remaining unpaid fine total should be visible on receipt
        double remainingUnpaid = getUnpaidFineTotal(bill.getPlate());

        // Return receipt
        return new Receipt(
                bill.getPlate(),
                mustPay,      // amount charged in this transaction
                payment.getAmountPaid(),
                change,
                payment.getMethod(),
                payment.getTimestamp(),
                remainingUnpaid
        );
    }
    
    // ------------------------------------------------Methods for Reports --------------------------------------------------
    // Total revenue for admin/reporting
    public double getTotalRevenue() { return totalRevenue; }

    //
    public int getOccupiedCount() {
        int occupied = 0;
        for (Floor f : floors) {
            for (Row r : f.getRows()) {
                for (ParkingSpot s : r.getSpots()) {
                    if (s.isOccupied()) occupied++;
                }
            }
        }
        return occupied;
    }

    // Get total spot from the overall parking lot (row.size * floors)
    public int getTotalSpotCount() {
        int total = 0;
        for (Floor f : floors) {
            for (Row r : f.getRows()) {
                total += r.getSpots().size();
            }
        }
        return total;
    }

    // return a list of vehicles currently parked
    public java.util.List<String> getCurrentVehicles() {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (Ticket t : activeTicketsByPlate.values()) {
            list.add(t.getPlate() + " at " + t.getSpotID() + " since " + t.getEntryTime());
        }
        return list;
    }

    // Reports for fines
    public java.util.List<String> getFinesReport() {
        java.util.List<String> out = new java.util.ArrayList<>();
        for (var e : finesByPlate.entrySet()) {
            String plate = e.getKey();
            double sum = 0.0;
            for (FineRecord f : e.getValue()) {
                if (!f.isPaid()) sum += f.getAmount();
            }
            if (sum > 0) out.add(plate + " : RM " + sum);
        }
        return out;
    }

}

