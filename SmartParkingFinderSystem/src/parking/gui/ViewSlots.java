package parking.gui;

import parking.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewSlots extends JFrame {

    String locationName;
    String userEmail;
    JPanel slotPanel;

    //Release expired slots
    private void releaseExpiredSlots() {
        try(Connection con = DBConnection.getConnection()) {

            String selectSql = "SELECT parking_location, slot_number FROM bookings WHERE end_time <= NOW() AND status='booked'";
            PreparedStatement ps = con.prepareStatement(selectSql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String location = rs.getString("parking_location");
                String slot = rs.getString("slot_number");

                //update slot status
                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE slots SET status='available' WHERE location_name=? AND slot_number=?"
                );
                ps2.setString(1, location);
                ps2.setString(2, slot);
                ps2.executeUpdate();

                //update counts
                PreparedStatement ps3 = con.prepareStatement(
                    "UPDATE parking_locations SET available_slots = available_slots + 1, occupied_slots = occupied_slots - 1 WHERE location_name=?"
                );
                ps3.setString(1, location);
                ps3.executeUpdate();
            }

            //mark booking completed
            PreparedStatement ps4 = con.prepareStatement(
                "UPDATE bookings SET status='completed' WHERE end_time <= NOW() AND status='booked'"
            );
            ps4.executeUpdate();

        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    //CONSTRUCTOR
    public ViewSlots(String locationName, String userEmail) {
        this.locationName = locationName;
        this.userEmail = userEmail;

        setTitle("View Slots - " + locationName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel(locationName + " Parking Hub");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));

        JButton backBtn = new JButton("Back");
        backBtn.setFocusPainted(false);
        backBtn.setBackground(new Color(138, 43, 226));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backBtn.setPreferredSize(new Dimension(100, 40));

        backBtn.addActionListener(e -> {
            dispose();
            new ParkingSlots(userEmail);
        });

        //RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        rightPanel.add(Box.createVerticalStrut(20));

        backBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(backBtn);

        rightPanel.add(Box.createVerticalStrut(12));

        JButton feedbackBtn = new JButton("Give Feedback");
        feedbackBtn.setFocusPainted(false);
        feedbackBtn.setBackground(new Color(138, 43, 226));
        feedbackBtn.setForeground(Color.WHITE);
        feedbackBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        feedbackBtn.setPreferredSize(new Dimension(160, 45));
        feedbackBtn.setMaximumSize(new Dimension(180, 45));
        feedbackBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);

        feedbackBtn.addActionListener(e -> {
        	new FeedbackPage(userEmail, locationName);
        });

        rightPanel.add(feedbackBtn);

        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        //MAIN PANEL
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        //LEGEND
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendBox("Available", new Color(144, 238, 144)));
        legendPanel.add(createLegendBox("Booked", Color.RED));
        legendPanel.add(createLegendBox("Reserved", Color.YELLOW));
        legendPanel.add(createLegendBox("Maintenance", Color.GRAY));

        mainPanel.add(legendPanel, BorderLayout.NORTH);

        //SLOT PANEL
        slotPanel = new JPanel(new GridLayout(0, 10, 15, 15));
        slotPanel.setBackground(Color.WHITE);
        slotPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadSlots();

        //AUTO REFRESH
        new javax.swing.Timer(3000, e -> {
            releaseExpiredSlots();
            loadSlots();
            slotPanel.revalidate();
            slotPanel.repaint();
        }).start();

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        wrapper.setOpaque(false);
        wrapper.add(slotPanel, BorderLayout.CENTER);

        mainPanel.add(wrapper, BorderLayout.CENTER);

        //BOOK BUTTON
        JButton bookBtn = new JButton("Book a Slot Here");
        bookBtn.setBackground(new Color(138, 43, 226));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookBtn.setFocusPainted(false);
        bookBtn.setPreferredSize(new Dimension(220, 45));

        bookBtn.addActionListener(e -> {
            new SlotBooking(locationName, userEmail, this);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(bookBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    //LOAD SLOTS
    public void loadSlots() {
        slotPanel.removeAll();

        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM slots WHERE location_name=? " +
                    "ORDER BY LEFT(slot_number,1), CAST(SUBSTRING(slot_number,2) AS UNSIGNED)";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, locationName);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                String slotNo = rs.getString("slot_number");
                String status = rs.getString("status");

                JPanel slotBox = new JPanel();
                slotBox.setPreferredSize(new Dimension(75,75));
                slotBox.setLayout(new GridBagLayout());

                JLabel label = new JLabel(slotNo);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                //COLOR LOGIC
                if (status.equals("occupied")) {
                    slotBox.setBackground(Color.RED);
                } 
                else if (status.equals("reserved")) {
                    slotBox.setBackground(Color.YELLOW);
                } 
                else if (status.equals("maintenance")) {
                    slotBox.setBackground(Color.GRAY);
                } 
                else {
                    slotBox.setBackground(new Color(144, 238, 144)); 
                }

                slotBox.add(label);
                slotPanel.add(slotBox);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //LEGEND BOX
    public JPanel createLegendBox(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);

        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(15, 15));
        box.setBackground(color);

        JLabel label = new JLabel(text);

        panel.add(box);
        panel.add(label);
        return panel;
    }

    //REFRESH METHOD
    public void refresh() {
        releaseExpiredSlots();
        loadSlots();
        slotPanel.revalidate();
        slotPanel.repaint();
    }

    public static void main(String[] args) {
        new ViewSlots("CGC University", "user@example.com");
    }
}

