package domain.parking;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final int floorNum;

    // List of rows in this floor
    private final List<Row> rows = new ArrayList<>();

    // constructor initializes floor with an assigned number
    public Floor(int floorNum) {
        this.floorNum = floorNum;
    }

    public int getfloorNum() {
        return floorNum;
    }

    // Returns the list of rows on this floor
    public List<Row> getRows() {
        return rows;
    }

    // Adds a row to this floor
    public void addRow(Row row) {
        rows.add(row);
    }

    // Returns a list of all parking spots across every row on this floor
    public List<ParkingSpot> getAllSpots() {
        List<ParkingSpot> all = new ArrayList<>();

        // Compile all spots from the rows that exist on the floor
        for (int i = 0; i < rows.size(); i++) {
            Row currentRow = rows.get(i);

            List<ParkingSpot> rowSpots= currentRow.getSpots();
            for (int j = 0; j < rowSpots.size(); j++) {
                ParkingSpot spot = rowSpots.get(j);
                all.add(spot);
            }
        }
        return all;
    }
}
