package UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import domain.Ticket;
import domain.VehicleType;
import domain.parking.ParkingSpot;
import service.ParkingService;

import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    //-----------------------------------------------------------------------------------------------------------------------
    private final ParkingService service;

    // Entry fields
    private final JTextField plateField = new JTextField(10);
    private final JComboBox<VehicleType> vehicleTypeBox = new JComboBox<>(VehicleType.values());
    private final JCheckBox handicappedCardBox = new JCheckBox("Handicapped card holder");
    private final JCheckBox reservationBox = new JCheckBox("Has reservation");

    // Table
    private final DefaultTableModel spotsModel = new DefaultTableModel(
        new Object[]{"Spot ID (Floor, Row, Spot)", "Type", "Rate (RM)", "Status", "Vehicle Number"}, 0
    );
    private final JTable spotsTable = new JTable(spotsModel);

    // Area
    private final JTextArea ticketArea = new JTextArea(10, 60);
    private final JTextArea billArea = new JTextArea(10, 60);
    private final JTextArea reportArea = new JTextArea(20, 60);
    private final JTextArea adminArea = new JTextArea(20, 60);

    // Exit fields
    private final JTextField exitPlateField = new JTextField(10);
    private final JComboBox<domain.payment.PaymentMethod> payMethodBox = new JComboBox<>(domain.payment.PaymentMethod.values());
    private final JTextField amountPaidField = new JTextField(8);

    // Admin
    private final JComboBox<String> schemeBox = new JComboBox<>(new String[]{"Fixed", "Progressive", "Hourly"});

    // ------------------------------------------------------------------------------------------------------------------------
    public MainFrame(ParkingService service) {
        super("Parking Management System");
        this.service = service;

        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        ticketArea.setEditable(false);
        billArea.setEditable(false);
        reportArea.setEditable(false);
        adminArea.setEditable(false);

        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.addTab("Entry / Exit", EntryExitPanel());
        mainTabs.addTab("Admin", AdminPanel());
        mainTabs.addTab("Reports", ReportsPanel());

        add(mainTabs, BorderLayout.CENTER);
    }


    private void onSearch() {
        try {
            String plate = plateField.getText();
            VehicleType type = (VehicleType) vehicleTypeBox.getSelectedItem();
            boolean card = handicappedCardBox.isSelected();

            List<ParkingSpot> spots = service.searchSpots(plate, type, card);
            refillSpotsTable(spots);

            ticketArea.setText("Found " + spots.size() + " suitable available spots.\nSelect one and click Park.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPark() {
        try {
            int row = spotsTable.getSelectedRow();
            if (row < 0) throw new IllegalStateException("Please select a spot from the table.");
            String status = String.valueOf(spotsModel.getValueAt(row, 3));

            if ("OCCUPIED".equals(status)) {
                throw new IllegalStateException("This spot is already occupied. Select an available spot.");
            }

            String spotID = String.valueOf(spotsModel.getValueAt(row, 0));

            String plate = plateField.getText();
            VehicleType type = (VehicleType) vehicleTypeBox.getSelectedItem();
            boolean card = handicappedCardBox.isSelected();

            Ticket t = service.confirmPark(plate, type, card, reservationBox.isSelected(), spotID);

            ticketArea.setText(
                    "Ticket generated:\n" +
                    "Ticket ID: " + t.getTicketID() + "\n" +
                    "Plate: " + t.getPlate() + "\n" +
                    "Spot: " + t.getSpotID() + "\n" +
                    "Entry time: " + t.getEntryTime() + "\n"
            );

            // refresh table so chosen spot becomes occupied and disappears from suitable list
            onSearch();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refillSpotsTable(List<ParkingSpot> spots) {
        spotsModel.setRowCount(0);
        for (ParkingSpot s : spots) {
            String status = s.isOccupied() ? "OCCUPIED" : "AVAILABLE";
            String vehicleNum = s.isOccupied() ? s.getParkedPlate() : "-";

            spotsModel.addRow(new Object[]{
                s.getSpotID(),
                s.getType(),
                s.getHourlyRate(),
                status,
                vehicleNum
            });
        }
    }

    private void onCalculateBill() {
        try {
            String plate = exitPlateField.getText();
            domain.payment.Bill bill = service.calculateBill(plate);

            billArea.setText(
                "Bill:\n" +
                "Plate: " + bill.getPlate() + "\n" +
                "Entry: " + bill.getEntryTime() + "\n" +
                "Exit: " + bill.getExitTime() + "\n" +
                "Hours charged: " + bill.getHoursCharged() + "\n" +
                "Rate: RM " + bill.getHourlyRate() + " / hr\n" +
                "Parking fee: RM " + bill.getParkingFee() + "\n" +
                "Unpaid fines (previous): RM " + bill.getUnpaidFinesPrevious() + "\n" +
                "Fine due now: RM " + bill.getFineDueNow() + "\n" +
                "Total due: RM " + bill.getTotalDue() + "\n"
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPayAndExit() {
        try {
            String plate = exitPlateField.getText();
            domain.payment.PaymentMethod method =
                (domain.payment.PaymentMethod) payMethodBox.getSelectedItem();

            double amountPaid = Double.parseDouble(amountPaidField.getText().trim());

            domain.payment.Receipt receipt = service.payAndExit(plate, method, amountPaid);

            billArea.setText(
                "Receipt:\n" +
                "Plate: " + receipt.getPlate() + "\n" +
                "Paid by: " + receipt.getMethod() + "\n" +
                "Total due: RM " + receipt.getTotalDue() + "\n" +
                "Amount paid: RM " + receipt.getAmountPaid() + "\n" +
                "Change: RM " + receipt.getChange() + "\n" +
                "Time: " + receipt.getTimestamp() + "\n"
            );

            // Clear exit inputs
            amountPaidField.setText("");

            // refresh entry spot list
            onSearch();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount paid must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Entry Exit panel
    private JPanel EntryExitPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Top entry bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Plate:"));
        top.add(plateField);

        top.add(new JLabel("Vehicle:"));
        top.add(vehicleTypeBox);

        top.add(handicappedCardBox);
        top.add(reservationBox);

        JButton searchBtn = new JButton("Search Suitable Spots");
        JButton parkBtn = new JButton("Park in Selected Spot");
        top.add(searchBtn);
        top.add(parkBtn);

        // center: table + output tabs
        JScrollPane tableScroll = new JScrollPane(spotsTable);

        JTabbedPane outputTabs = new JTabbedPane();
        outputTabs.addTab("Ticket", new JScrollPane(ticketArea));
        outputTabs.addTab("Bill / Receipt", new JScrollPane(billArea));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, outputTabs);
        split.setResizeWeight(0.65);

        // bottom exit bar
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exitPanel.add(new JLabel("Exit Plate:"));
        exitPanel.add(exitPlateField);

        JButton calcBillBtn = new JButton("Calculate Bill");
        exitPanel.add(calcBillBtn);

        exitPanel.add(new JLabel("Pay method:"));
        exitPanel.add(payMethodBox);

        exitPanel.add(new JLabel("Amount paid (RM):"));
        exitPanel.add(amountPaidField);

        JButton payExitBtn = new JButton("Pay & Exit");
        exitPanel.add(payExitBtn);

        // listeners
        searchBtn.addActionListener(e -> onSearch());
        parkBtn.addActionListener(e -> onPark());
        calcBillBtn.addActionListener(e -> onCalculateBill());
        payExitBtn.addActionListener(e -> onPayAndExit());

        // Disable handicapped card box unless vehicle type is handicapped
        vehicleTypeBox.addActionListener(e -> {
            VehicleType t = (VehicleType) vehicleTypeBox.getSelectedItem();
            boolean enable = (t == VehicleType.HANDICAPPED);
            handicappedCardBox.setEnabled(enable);
            if (!enable) {
                handicappedCardBox.setSelected(false);
            }
        });
        // trigger once
        vehicleTypeBox.setSelectedItem(vehicleTypeBox.getSelectedItem());

        panel.add(top, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        panel.add(exitPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Admin Panel
    private JPanel AdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Fine scheme:"));
        top.add(schemeBox);

        JButton applySchemeBtn = new JButton("Apply Fine Scheme");
        JButton refreshBtn = new JButton("Refresh Admin View");
        top.add(applySchemeBtn);
        top.add(refreshBtn);

        applySchemeBtn.addActionListener(e -> {
            String select = (String) schemeBox.getSelectedItem();

            if ("Fixed".equals(select)) {
                service.changeFineSchemeToFixed(50.0);
            } else if ("Progressive".equals(select)) {
                service.changeFineSchemeToProgressive(30.0, 10.0);
            } else {
                service.changeFineSchemeToHourly(15.0);
            }

            JOptionPane.showMessageDialog(this, "Fine scheme applied for future entries.");
        });

        refreshBtn.addActionListener(e -> {
            adminArea.setText(service.AdminSummary());
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(adminArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel ReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton reportBtn = new JButton("Generate Report");
        top.add(reportBtn);

        reportBtn.addActionListener(e -> {
            reportArea.setText(service.buildReport());
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        return panel;
    }
}