package parking.gui;

import parking.database.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class ParkingSlots extends JFrame {

    public static ParkingSlots currentInstance;
    JPanel mainPanel;
    String userEmail;

    //Release expired bookings
    private void releaseExpiredSlots() {
        try(Connection con = DBConnection.getConnection()) {

            String selectSql = "SELECT parking_location, slot_number FROM bookings WHERE end_time <= NOW() AND status='booked'";
            PreparedStatement ps = con.prepareStatement(selectSql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String location = rs.getString("parking_location");
                String slot = rs.getString("slot_number");

                //slot available
                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE slots SET status='available' WHERE location_name=? AND slot_number=?"
                );
                ps2.setString(1, location);
                ps2.setString(2, slot);
                ps2.executeUpdate();

                //count update
                PreparedStatement ps3 = con.prepareStatement(
                    "UPDATE parking_locations SET available_slots = available_slots + 1, occupied_slots = occupied_slots - 1 WHERE location_name=?"
                );
                ps3.setString(1, location);
                ps3.executeUpdate();
            }

            //booking complete
            PreparedStatement ps4 = con.prepareStatement(
                "UPDATE bookings SET status='completed' WHERE end_time <= NOW() AND status='booked'"
            );
            ps4.executeUpdate();

        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public ParkingSlots(String userEmail) {
        this.userEmail = userEmail;
        currentInstance = this;

        setTitle("Parking Slots");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Main Panel
        mainPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(new EmptyBorder(10, 30, 30, 30));

        loadLocations();

        new javax.swing.Timer(5000, e -> refreshData()).start();

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);

        //BACK BUTTON
        JButton backBtn = new JButton("Back");

        Color normal = new Color(120, 80, 220);
        Color hover = new Color(100, 60, 200);

        backBtn.setBackground(normal);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setPreferredSize(new Dimension(100, 35));

        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(normal);
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new UserProfile(userEmail);
        });

        //TOP PANEL 
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("Parking Locations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(new EmptyBorder(5, 20, 5, 10));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        rightPanel.setOpaque(false);
        rightPanel.add(backBtn);

        topPanel.setPreferredSize(new Dimension(0, 60));
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        //FRAME
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    //Load Locations from DB
    public void loadLocations() {
        try {
            mainPanel.removeAll();

            Connection con = DBConnection.getConnection();

            //FIXED QUERY
            String query = "SELECT location_name, total_slots, available_slots, occupied_slots FROM parking_locations";

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String name = rs.getString("location_name");
                int total = rs.getInt("total_slots");
                int available = rs.getInt("available_slots");
                int occupied = rs.getInt("occupied_slots");

                mainPanel.add(createCard(name, total, available, occupied));
            }

            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Refresh
    public void refreshData() {
        releaseExpiredSlots();
        loadLocations();
    }

    //Card UI
    public JPanel createCard(String name, int total, int available, int occupied) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        statsPanel.setOpaque(false);

        statsPanel.add(createBox("Total", total, new Color(230,230,230)));
        statsPanel.add(createBox("Available", available, new Color(220, 210, 255)));
        statsPanel.add(createBox("Occupied", occupied, new Color(255,200,200)));

        JButton btn = new JButton("View Slots");Color normal = new Color(120, 80, 220);
        Color hover = new Color(100, 60, 200);

        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(130, 30));

        btn.addActionListener(e -> new ViewSlots(name, userEmail));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        bottom.add(btn);

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    //Box UI
    public JPanel createBox(String title, int value, Color bgColor) {

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(bgColor);
        box.setBorder(new EmptyBorder(8,10,8,10));
        box.setPreferredSize(new Dimension(90,100));

        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(valueLabel);
        box.add(titleLabel);

        return box;
    }

    public static void main(String[] args) {
        new ParkingSlots("user@example.com");
    }
}


