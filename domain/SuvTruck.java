package domain;
public class SuvTruck extends Vehicle {
    public SuvTruck(String plate) { super(plate); }
    @Override public VehicleType getType() { return VehicleType.SUV_TRUCK; }
}
