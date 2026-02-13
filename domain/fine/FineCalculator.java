package domain.fine;

import java.time.Duration;
import java.time.LocalDateTime;

public class FineCalculator {

    // Overstay threshold in minutes: 24h = 1440 minutes
    private static final long OVERSTAY_THRESHOLD_MINUTES = 1;

    public double computeOverstayFine(LocalDateTime entry, LocalDateTime exit, FineScheme scheme) {
        long minutes = Duration.between(entry, exit).toMinutes();
        if (minutes <= OVERSTAY_THRESHOLD_MINUTES) return 0.0;

        long overMinutes = minutes - OVERSTAY_THRESHOLD_MINUTES;

        // units = ceil(overMinutes / 60)
        int units = (int) Math.ceil(overMinutes / 60.0);
        units = Math.max(units, 1);

        return scheme.compute(units);
    }

    public double computeReservedMisuseFine(FineScheme scheme) {
        // treat misuse as 1 unit
        return scheme.compute(1);
    }
}