package domain;
public class Motorcycle extends Vehicle {
    public Motorcycle(String plate) { super(plate); }
    @Override public VehicleType getType() { return VehicleType.MOTORCYCLE; }
}
