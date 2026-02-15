package domain.fine;

import java.time.Duration;
import java.time.LocalDateTime;

public class FineCalculator {

    // Overstay for 24 hours = 1440 minutes
    private static final long OVERSTAY_MINUTES = 1440;

    // Calculates fine for vehicles overstaying more than 24 hours
    public double computeOverstayFine(LocalDateTime entry, LocalDateTime exit, FineScheme scheme) {
        long minutes = Duration.between(entry, exit).toMinutes();
        // If vehicle parked less than 24 hours/1440 minutes then fine = 0
        if (minutes <= OVERSTAY_MINUTES) return 0.0;

        // additional hours after 24 hours
        long overMinutes = minutes - OVERSTAY_MINUTES;

        // Convert extra minutes into hours
        // e.g: overMinutes = 61 / 60.0 = 1.0166 -> Math.ceil(1.0166) -> 2 hours
        int units = (int) Math.ceil(overMinutes / 60.0);
        // Minimum of 1 hour/unit
        units = Math.max(units, 1);

        return scheme.compute(units);
    }

    // Compute the amount of penalty based on the fine scheme
    public double computeReservedMisuseFine(FineScheme scheme) {
        // 1 unit = 1 hour
        return scheme.compute(1);
    }
}