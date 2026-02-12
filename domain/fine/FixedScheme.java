package domain.fine;

public class FixedScheme implements FineScheme {
    private final double fixedAmount;

    public FixedScheme(double fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    @Override
    public String getName() {
        return "Fixed";
    }

    @Override
    public double compute(int units) {
        return fixedAmount; // ignores units
    }
}
