package domain.parking;
import domain.Vehicle;

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

    public void occupy(Vehicle v) {
        if (occupied) throw new IllegalStateException("Spot already occupied.");
        this.currentVehicle = v;
        this.parkedPlate = v.getPlate();
        this.occupied = true;
    }

    public void vacate() {
        this.currentVehicle = null;
        this.parkedPlate = null;
        this.occupied = false;
    }

    public String getCurrentVehiclePlate() {
        if (currentVehicle == null) {
            return "";
        } else {
            return currentVehicle.getPlate();
        }
    }

    @Override
    public String toString() {
        if (occupied) {
            return spotID + " (" + type + ") OCCUPIED";
        } else {
            return spotID + " (" + type + ") AVAILABLE";
        }
    }
}