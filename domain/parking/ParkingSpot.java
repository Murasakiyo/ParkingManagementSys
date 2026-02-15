package domain.parking;
import domain.Vehicle;

// Create every spot ID for all spots in each row and level on the Parking Lot
public class ParkingSpot {
    private final String spotID;
    private final SpotType type;
    private final double hourlyRate;

    private boolean occupied;
    private String parkedPlate;
    private Vehicle currentVehicle;

    public ParkingSpot(String spotID, SpotType type, double hourlyRate) {
        this.spotID = spotID;
        this.type = type;
        this.hourlyRate = hourlyRate;
    }

    public String getSpotID() { return spotID; }
    public SpotType getType() { return type; }
    public double getHourlyRate() { return hourlyRate; }
    public boolean isOccupied() { return occupied; }
    public String getParkedPlate() { return parkedPlate; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }

     // Determines whether the specified vehicle type is allowed to park in this spot
    public boolean canFit(Vehicle v) {
        if (type == SpotType.RESERVED) {
            return true;
        }
        switch (v.getType()) {
            case MOTORCYCLE:
                return type == SpotType.COMPACT;
            case CAR:
                return type == SpotType.COMPACT || type == SpotType.REGULAR;
            case SUV_TRUCK:
                return type == SpotType.REGULAR;
            case HANDICAPPED:
                return true; // handicapped vehicle can park anywhere
            default:
                return false;
        }
    }

    // Marks the spot as occupied by a specific vehicle
    public void occupy(Vehicle v) {
        // Prevents double occupancy
        if (occupied) throw new IllegalStateException("Spot already occupied.");
        this.currentVehicle = v;
        this.parkedPlate = v.getPlate();
        this.occupied = true;
    }

    // Releases the parking spot and clears vehicle information
    public void vacate() {
        this.currentVehicle = null;
        this.parkedPlate = null;
        this.occupied = false;
    }

    // Returns the plate number of the currently parked vehicle
    public String getCurrentVehiclePlate() {
        if (currentVehicle == null) {
            return "";
        } else {
            return currentVehicle.getPlate();
        }
    }

    // Returns a formatted string describing the spot’s ID, type, and occupancy status
    @Override
    public String toString() {
        if (occupied) {
            return spotID + " (" + type + ") OCCUPIED";
        } else {
            return spotID + " (" + type + ") AVAILABLE";
        }
    }
}