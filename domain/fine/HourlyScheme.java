package domain.fine;

public class HourlyScheme implements FineScheme {
    private final double ratePerUnit;

    public HourlyScheme(double ratePerUnit) {
        this.ratePerUnit = ratePerUnit;
    }

    @Override
    public String getName() {
        return "Hourly";
    }

    @Override
    public double compute(int units) {
        return ratePerUnit * units;
    }
}
