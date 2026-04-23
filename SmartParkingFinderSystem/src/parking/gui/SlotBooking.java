package parking.gui;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class SlotBooking extends JFrame {

    JComboBox<String> locationBox, slotBox;
    JTextField startTime, endTime;
    JButton confirmBtn;

    private ViewSlots viewSlots;
    String userEmail;

    Map<String, Set<String>> bookedSlots = new HashMap<>();

    public SlotBooking() {
        initUI();
        releaseExpiredSlots();
        loadBookedSlotsFromDB();
    }

    public SlotBooking(String location, String userEmail, ViewSlots vs) {
        this.userEmail = userEmail;
        this.viewSlots = vs;

        initUI();

        locationBox.setSelectedItem(location);

        releaseExpiredSlots();
        loadBookedSlotsFromDB();
        updateSlots();
    }

    // Release expired slots
    private void releaseExpiredSlots() {
        try (Connection con = DBConnection.getConnection()) {

            String selectSql = "SELECT parking_location, slot_number FROM bookings WHERE end_time <= NOW() AND status='booked'";
            PreparedStatement ps = con.prepareStatement(selectSql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String location = rs.getString("parking_location");
                String slot = rs.getString("slot_number");

                PreparedStatement ps2 = con.prepareStatement(
                        "UPDATE slots SET status='available' WHERE location_name=? AND slot_number=?");
                ps2.setString(1, location);
                ps2.setString(2, slot);
                ps2.executeUpdate();

                PreparedStatement ps3 = con.prepareStatement(
                        "UPDATE parking_locations SET available_slots = available_slots + 1, occupied_slots = occupied_slots - 1 WHERE location_name=?");
                ps3.setString(1, location);
                ps3.executeUpdate();
            }

            PreparedStatement ps4 = con.prepareStatement(
                    "UPDATE bookings SET status='completed' WHERE end_time <= NOW() AND status='booked'");
            ps4.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // UI Setup
    private void initUI() {

        setTitle("Book Parking");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel bg = new JPanel(null);
        bg.setBackground(new Color(245, 247, 255));

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(1150, 20, 100, 35);
        backBtn.setBackground(new Color(120, 80, 220));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setFocusPainted(false);

        backBtn.addActionListener(e -> {
            dispose();
            new ViewSlots(locationBox.getSelectedItem().toString(), userEmail);
        });
        bg.add(backBtn);

        JLabel title = new JLabel("Book a Parking Space");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setBounds(250, 40, 500, 40);
        bg.add(title);

        JLabel sub = new JLabel("Reserve your spot securely in advance.");
        sub.setFont(new Font("Arial", Font.PLAIN, 16));
        sub.setForeground(Color.GRAY);
        sub.setBounds(250, 80, 500, 25);
        bg.add(sub);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        int cardWidth = 800, cardHeight = 450;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        card.setBounds((screen.width - cardWidth) / 2, (screen.height - cardHeight) / 2, cardWidth, cardHeight);

        Font labelFont = new Font("Arial", Font.BOLD, 16);

        // Location
        JLabel locLabel = new JLabel("Select Location");
        locLabel.setFont(labelFont);
        locLabel.setBounds(30, 50, 200, 25);
        card.add(locLabel);

        locationBox = new JComboBox<>();
        locationBox.setBounds(30, 80, 720, 45);
        styleBox(locationBox);
        card.add(locationBox);

        loadLocationsFromDB();

        // Slot
        JLabel slotLabel = new JLabel("Select Available Slot");
        slotLabel.setFont(labelFont);
        slotLabel.setBounds(30, 140, 250, 25);
        card.add(slotLabel);

        slotBox = new JComboBox<>();
        slotBox.setBounds(30, 170, 720, 45);
        styleBox(slotBox);
        card.add(slotBox);

        // Start Time
        JLabel startLabel = new JLabel("Start Time");
        startLabel.setFont(labelFont);
        startLabel.setBounds(30, 230, 150, 25);
        card.add(startLabel);

        startTime = new JTextField();
        startTime.setBounds(30, 260, 320, 45);
        styleField(startTime);
        addPlaceholderLogic(startTime, "dd-MM-yyyy hh:mm AM/PM");
        card.add(startTime);

        // End Time
        JLabel endLabel = new JLabel("End Time");
        endLabel.setFont(labelFont);
        endLabel.setBounds(430, 230, 150, 25);
        card.add(endLabel);

        endTime = new JTextField();
        endTime.setBounds(430, 260, 320, 45);
        styleField(endTime);
        addPlaceholderLogic(endTime, "dd-MM-yyyy hh:mm AM/PM");
        card.add(endTime);

        // Button
        confirmBtn = new JButton("Confirm Booking");
        confirmBtn.setBounds(520, 360, 230, 50);
        confirmBtn.setBackground(new Color(120, 80, 220));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 16));
        confirmBtn.setFocusPainted(false);
        card.add(confirmBtn);

        bg.add(card);
        add(bg);

        locationBox.addActionListener(e -> updateSlots());
        confirmBtn.addActionListener(e -> confirmBooking());

        updateSlots();
        setVisible(true);
    }

    private void styleBox(JComboBox<?> box) {
        box.setFont(new Font("Arial", Font.PLAIN, 15));
        box.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
    }

    private void addPlaceholderLogic(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    // Load locations
    private void loadLocationsFromDB() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT location_name FROM parking_locations";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                locationBox.addItem(rs.getString("location_name"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Update slots
    private void updateSlots() {

        slotBox.removeAllItems();

        String location = (String) locationBox.getSelectedItem();
        if (location == null) return;

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT slot_number, status FROM slots WHERE location_name=? " +
                    "ORDER BY LEFT(slot_number,1), CAST(SUBSTRING(slot_number,2) AS UNSIGNED)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, location);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String slot = rs.getString("slot_number");
                String status = rs.getString("status");

                if (status.equals("occupied")) {
                    slotBox.addItem(slot + " (Booked)");
                } else {
                    slotBox.addItem(slot);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Load booked slots
    private void loadBookedSlotsFromDB() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT parking_location, slot_number FROM bookings WHERE status='booked' AND end_time > NOW()";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String location = rs.getString("parking_location");
                String slot = rs.getString("slot_number");

                bookedSlots.putIfAbsent(location, new HashSet<>());
                bookedSlots.get(location).add(slot);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Confirm Booking
    private void confirmBooking() {

        String location = (String) locationBox.getSelectedItem();
        String slot = (String) slotBox.getSelectedItem();
        String start = startTime.getText();
        String end = endTime.getText();

        if (location == null || slot == null || start.isEmpty() || end.isEmpty()
                || start.equals("dd-MM-yyyy hh:mm AM/PM")
                || end.equals("dd-MM-yyyy hh:mm AM/PM")) {

            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        if (slot.contains("Booked")) {
            JOptionPane.showMessageDialog(this, "This slot is already booked!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String slotValue = slot.split(" ")[0];

            String token = UUID.randomUUID().toString().substring(0, 8);

            SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            SimpleDateFormat mysql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date startDate = input.parse(start);
            Date endDate = input.parse(end);

            String startSQL = mysql.format(startDate);
            String endSQL = mysql.format(endDate);

            // INSERT BOOKING
            String sql = "INSERT INTO bookings(parking_location, slot_number, user_email, start_time, end_time, token, status) VALUES(?,?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, location);
            ps.setString(2, slotValue);
            ps.setString(3, userEmail);
            ps.setString(4, startSQL);
            ps.setString(5, endSQL);
            ps.setString(6, token);
            ps.setString(7, "booked");

            int rows = ps.executeUpdate();

            if (rows > 0) {

                // UPDATE SLOT STATUS
                PreparedStatement ps2 = con.prepareStatement(
                        "UPDATE slots SET status='occupied' WHERE location_name=? AND slot_number=?");
                ps2.setString(1, location);
                ps2.setString(2, slotValue);
                ps2.executeUpdate();

                // UPDATE COUNTS
                PreparedStatement ps3 = con.prepareStatement(
                        "UPDATE parking_locations SET available_slots = available_slots - 1, occupied_slots = occupied_slots + 1 WHERE location_name=?");
                ps3.setString(1, location);
                ps3.executeUpdate();

                // ADD NOTIFICATION (FIXED)
                PreparedStatement psNotif = con.prepareStatement(
                        "INSERT INTO notifications(user_email, message, status) VALUES(?,?,?)");

                psNotif.setString(1, userEmail);
                psNotif.setString(2,
                        "Booking Confirmed! Location: " + location +
                        ", Slot: " + slotValue +
                        ", Token: " + token);

                psNotif.setString(3, "unread");
                psNotif.executeUpdate();

                // SUCCESS MESSAGE
                JOptionPane.showMessageDialog(this,
                        "Booking Confirmed!\nToken: " + token);

                if (viewSlots != null) {
                    viewSlots.refresh();
                }

                SwingUtilities.invokeLater(() -> {
                    new MyBookings(userEmail);
                });

                dispose();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new SlotBooking();
    }
}