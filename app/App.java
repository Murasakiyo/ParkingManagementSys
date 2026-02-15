package app;
import javax.swing.SwingUtilities;

import UI.MainFrame;
import domain.ParkingLot;
import service.ParkingService;

public class App {
    public static void main(String[] args) {

        //Create the parking lot object
        ParkingLot lot = new ParkingLot();

        //Create the service layer
        ParkingService service = new ParkingService(lot);

        //Start Swing on the Event Dispatch Thread (Run on only one thread)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }
}