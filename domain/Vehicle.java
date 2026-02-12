package domain;
public abstract class Vehicle {
    private final String plate;

    protected Vehicle(String plate) {
        this.plate = plate;
    }

    public String getPlate() {
        return plate;
    }

    public abstract VehicleType getType();

    public boolean isHandicapped() {
        return false;
    }
}