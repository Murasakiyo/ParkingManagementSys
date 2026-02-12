package domain.parking;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final int floorNum;
    private final List<Row> rows = new ArrayList<>();

    public Floor(int floorNum) {
        this.floorNum = floorNum;
    }

    public int getfloorNum() {
        return floorNum;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void addRow(Row row) {
        rows.add(row);
    }

    public List<ParkingSpot> getAllSpots() {
        List<ParkingSpot> all = new ArrayList<>();
        for (Row r : rows) {
            all.addAll(r.getSpots());
        }
        return all;
    }
}
