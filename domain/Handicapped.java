package domain;
public class Handicapped extends Vehicle {
    private final boolean cardHolder;

    public Handicapped(String plate, boolean cardHolder) {
        super(plate);
        this.cardHolder = cardHolder;
    }

    @Override public VehicleType getType() { return VehicleType.HANDICAPPED; }

    @Override
    public boolean isHandicapped() {
        return cardHolder;
    }
}
