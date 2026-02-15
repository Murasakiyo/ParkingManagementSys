package domain;

import java.time.LocalDateTime;
import domain.fine.FineScheme;

public class Ticket {
    private final String ticketID;
    // Vehicle plate number associated with this ticket
    private final String plate;

    // Parking spot ID where the vehicle is parked
    private final String spotID;

    // Timestamp of vehicle entry
    private final LocalDateTime entryTime;

    // Indicates whether the vehicle had a valid reservation
    private final boolean hasReservation;

    // Fine scheme used at the time of entry
    private final FineScheme fineSchemeAtEntry;

    // Constructor initializes all ticket details at entry time
    public Ticket(String ticketID, String plate, String spotID, LocalDateTime entryTime, 
        boolean hasReservation, FineScheme fineSchemeAtEntry) {
        this.ticketID = ticketID;
        this.plate = plate;
        this.spotID = spotID;
        this.entryTime = entryTime;
        this.hasReservation = hasReservation;
        this.fineSchemeAtEntry = fineSchemeAtEntry;
    }

    // Methods for accessing ticket information
    public String getTicketID() { return ticketID; }
    public String getPlate() { return plate; }
    public String getSpotID() { return spotID; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public boolean hasReservation() { return hasReservation; }
    public FineScheme getFineSchemeAtEntry() { return fineSchemeAtEntry; }
}