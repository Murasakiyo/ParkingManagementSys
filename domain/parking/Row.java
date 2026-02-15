package domain.parking;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private final int rowNum;
    // Rows contains a list of Parkingspot objects
    private final List<ParkingSpot> spots = new ArrayList<>();

    // constructor initializes a row with an assigned row number
    public Row(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getRowNum() {
        return rowNum;
    }

    // Adds a parking spot to this row
    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    // Returns list of parking spots in this row
    public List<ParkingSpot> getSpots() {
        return spots;
    }
    
}
