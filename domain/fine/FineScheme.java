package domain.fine;

public interface FineScheme {
    String getName();

    // units = how many "hours" the fine is based on
    double compute(int units);
}
