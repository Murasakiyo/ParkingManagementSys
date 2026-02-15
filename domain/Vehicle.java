package domain;

// base class (cannot create an object from this class)
public abstract class Vehicle {
    private final String plate;

    // Constructor sets the vehicle's plate number
    protected Vehicle(String plate) {
        this.plate = plate;
    }

    public String getPlate() {
        return plate;
    }

    // Subclasses define their specific vehicle type
    public abstract VehicleType getType();

    // Indicates whether the vehicle has a handicapped card (default: false)
    public boolean isHandicapped() {
        return false;
    }
}