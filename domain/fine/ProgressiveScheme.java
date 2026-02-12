package domain.fine;

public class ProgressiveScheme implements FineScheme {
    private final double baseAmount;
    private final double incrementPerUnit;

    public ProgressiveScheme(double baseAmount, double incrementPerUnit) {
        this.baseAmount = baseAmount;
        this.incrementPerUnit = incrementPerUnit;
    }

    @Override
    public String getName() {
        return "Progressive";
    }

    @Override
    public double compute(int units) {
        if (units <= 1) return baseAmount;
        return baseAmount + (units - 1) * incrementPerUnit;
    }
}
