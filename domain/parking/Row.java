package domain.parking;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private final int rowNum;
    private final List<ParkingSpot> spots = new ArrayList<>();

    public Row(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getRowNum() {
        return rowNum;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }
}
