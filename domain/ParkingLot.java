package domain;

import java.time.LocalDateTime;
import java.util.*;

import domain.parking.*;
import domain.payment.*;
import domain.fine.*;

public class ParkingLot {
    private final List<Floor> floors = new ArrayList<>();
    private final Map<String, Ticket> activeTicketsByPlate = new HashMap<>();
    private double totalRevenue = 0.0;

    private final Map<String, java.util.List<FineRecord>> finesByPlate = new HashMap<>();
    private final FineCalculator fineCalculator = new FineCalculator();

    private FineScheme currentScheme = new FixedScheme(50.0); // pick any default you want

    public ParkingLot() {
        buildParkingLot();
    }

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

    private ParkingSpot makeSpot(int floorNum, int rowNum, int spotNum, SpotType type, double rate) {
        String id = "F" + floorNum + "-R" + rowNum + "-S" + spotNum;
        return new ParkingSpot(id, type, rate);
    }

    public List<ParkingSpot> findSuitableSpots(Vehicle v) {
        List<ParkingSpot> result = new ArrayList<>();
        for (Floor f : floors) {
            for (Row r : f.getRows()) {
                for (ParkingSpot s : r.getSpots()) {
                    if (s.canFit(v)) result.add(s);
                }
            }
        }
        return result;
    }

    public Ticket parkVehicle(Vehicle v, String spotID, boolean hasReservation, LocalDateTime now) {
        if (activeTicketsByPlate.containsKey(v.getPlate())) {
            throw new IllegalStateException("This plate already has an active ticket: " + v.getPlate());
        }

        ParkingSpot spot = getSpotByID(spotID);
        spot.occupy(v);

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

    private ParkingSpot getSpotByID(String spotID) {
        for (Floor f : floors) {
            for (Row r : f.getRows()) {
                for (ParkingSpot s : r.getSpots()) {
                    if (s.getSpotID().equals(spotID)) return s;
                }
            }
        }
        throw new IllegalArgumentException("Spot not found: " + spotID);
    }

    private int computeChargedHours(LocalDateTime entry, LocalDateTime exit) {
        long minutes = java.time.Duration.between(entry, exit).toMinutes();
        if (minutes < 0) {
            throw new IllegalArgumentException("Exit time cannot be before entry time.");
        }
        int hours = (int) Math.ceil(minutes / 60.0);
        return Math.max(hours, 1);
    }

    // private ParkingSpot getSpotForTicket(Ticket t) {
    //     return getSpotByID(t.getSpotID());
    // }

    public void setFineScheme(FineScheme scheme) {
        if (scheme == null) {
            throw new IllegalArgumentException("Fine scheme cannot be null.");
        }
        this.currentScheme = scheme;
    }

    private double getUnpaidFineTotal(String plate) {
        java.util.List<FineRecord> list = finesByPlate.get(plate);
        if (list == null) return 0.0;

        double sum = 0.0;
        for (FineRecord f : list) {
            if (!f.isPaid()) sum += f.getAmount();
        }
        return sum;
    }

    private void markAllFinesPaid(String plate) {
        java.util.List<FineRecord> list = finesByPlate.get(plate);
        if (list == null) return;
        for (FineRecord f : list) {
            if (!f.isPaid()) f.markPaid();
        }
    }

    public Bill buildBill(String plate, LocalDateTime now) {
        if (plate == null) throw new IllegalArgumentException("Plate cannot be empty.");
        String cleanPlate = plate.trim().toUpperCase();

        Ticket ticket = activeTicketsByPlate.get(cleanPlate);
        if (ticket == null) {
            throw new IllegalStateException("No active ticket found for plate: " + cleanPlate);
        }

        ParkingSpot spot = getSpotByID(ticket.getSpotID());

        int hours = computeChargedHours(ticket.getEntryTime(), now);
        double rate = spot.getHourlyRate();
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


    public Receipt payAndExit(String plate, Payment payment, LocalDateTime now) {
        Bill bill = buildBill(plate, now);

        if (payment.getAmountPaid() < bill.getTotalDue()) {
            throw new IllegalStateException("Insufficient payment. Total due: RM " + bill.getTotalDue());
        }

        double change = payment.getAmountPaid() - bill.getTotalDue();
        totalRevenue += bill.getTotalDue();

        // if a new fine is due now, record it as unpaid first then pay it in this transaction
        if (bill.getFineDueNow() > 0) {
            FineRecord f = new FineRecord(
                bill.getPlate(),
                FineReason.OVER_24_HOURS, 
                bill.getFineDueNow(),
                now
            );
            finesByPlate.computeIfAbsent(bill.getPlate(), k -> new ArrayList<>()).add(f);
        }

        // Mark all fines for this plate as paid (previous + new)
        markAllFinesPaid(bill.getPlate());

        // Release spot + remove ticket
        Ticket t = activeTicketsByPlate.remove(bill.getPlate());
        ParkingSpot spot = getSpotByID(t.getSpotID());
        spot.vacate();

        return new Receipt(
            bill.getPlate(),
            bill.getTotalDue(),
            payment.getAmountPaid(),
            change,
            payment.getMethod(),
            payment.getTimestamp()
        );
    }
    public double getTotalRevenue() { return totalRevenue; }

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

    public int getTotalSpotCount() {
        int total = 0;
        for (Floor f : floors) {
            for (Row r : f.getRows()) {
                total += r.getSpots().size();
            }
        }
        return total;
    }

    public java.util.List<String> getCurrentVehicles() {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (Ticket t : activeTicketsByPlate.values()) {
            list.add(t.getPlate() + " at " + t.getSpotID() + " since " + t.getEntryTime());
        }
        return list;
    }

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

