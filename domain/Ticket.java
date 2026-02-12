package domain;

import java.time.LocalDateTime;
import domain.fine.FineScheme;

public class Ticket {
    private final String ticketID;
    private final String plate;
    private final String spotID;
    private final LocalDateTime entryTime;

    private final boolean hasReservation;
    private final FineScheme fineSchemeAtEntry;

    public Ticket(String ticketID, String plate, String spotID, LocalDateTime entryTime, 
        boolean hasReservation, FineScheme fineSchemeAtEntry) {
        this.ticketID = ticketID;
        this.plate = plate;
        this.spotID = spotID;
        this.entryTime = entryTime;
        this.hasReservation = hasReservation;
        this.fineSchemeAtEntry = fineSchemeAtEntry;
    }

    public String getTicketID() { return ticketID; }
    public String getPlate() { return plate; }
    public String getSpotID() { return spotID; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public boolean hasReservation() { return hasReservation; }
    public FineScheme getFineSchemeAtEntry() { return fineSchemeAtEntry; }
}