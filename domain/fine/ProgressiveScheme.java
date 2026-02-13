package domain.fine;

public class ProgressiveScheme implements FineScheme {

    @Override
    public String getName() {
        return "Progressive";
    }

    @Override
    public double compute(int units) {
        // No overstay => no fine
        if (units <= 0) return 0.0;
        // First 24 hours fine
        double fine = 50.0;
        // 24-46 hours
        if (units <= 24) {
            fine += 100.0;
        // 48 -72 hours
        } else if (units <= 48) {
            fine += 150.0;
        // More than 72
        } else {
            fine += 200.0;
        }

        return fine;
    }

    @Override
    public boolean allowsUnpaidExit() {
        return false;
    }
}
