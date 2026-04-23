package parking.gui;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyBookings extends JFrame {

    private String userEmail;
    private JPanel mainPanel;
    private JPanel topPanel; 

    public MyBookings(String userEmail) {
        this.userEmail = userEmail;
        initUI();
        loadBookings();

        new javax.swing.Timer(5000, e -> {
            loadBookings();
        }).start();
    }

    private void initUI() {

        setTitle("My Bookings");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245,247,255));

        //Title and Notifications button
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245,247,255));
        topPanel.setBorder(new EmptyBorder(20,20,20,20));

        JLabel pageTitle = new JLabel("My Bookings");
        pageTitle.setFont(new Font("Arial", Font.BOLD, 28));
        topPanel.add(pageTitle, BorderLayout.WEST);

        //Back + Notifications
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(new Color(245,247,255));

        //Back Button
        JButton backBtn = new JButton("Back");
        backBtn.setBackground(new Color(100, 100, 255));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));

        //Back action
        backBtn.addActionListener(e -> {
            this.dispose(); 
            new UserProfile(userEmail);
        });

        //Notifications Button
        JButton notifBtn = new JButton("Notifications");
        notifBtn.setBackground(new Color(100, 100, 255));
        notifBtn.setForeground(Color.WHITE);
        notifBtn.setFocusPainted(false);
        notifBtn.setFont(new Font("Arial", Font.BOLD, 16));

        //open notifications page
        notifBtn.addActionListener(e -> new NotificationsPage(userEmail));

        rightPanel.add(backBtn);
        rightPanel.add(notifBtn);

        topPanel.add(rightPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());

     add(topPanel, BorderLayout.NORTH);

     //BOOKINGS PANEL
     JPanel bookingsPanel = new JPanel();
     bookingsPanel.setLayout(new BoxLayout(bookingsPanel, BoxLayout.Y_AXIS));
     bookingsPanel.setBackground(new Color(245,247,255));

     JScrollPane scrollPane = new JScrollPane(bookingsPanel);
     scrollPane.setBorder(null);

     add(scrollPane, BorderLayout.CENTER);

     this.mainPanel = bookingsPanel;
        setVisible(true);
    }

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
    
    private void loadBookings() {

        Component[] components = mainPanel.getComponents();
        for(int i = components.length-1; i >= 0; i--) {
            if(components[i] != topPanel) {
                mainPanel.remove(components[i]);
            }
        }

        try (Connection con = DBConnection.getConnection()) {
        	String sql = "SELECT * FROM bookings WHERE user_email = ? ORDER BY start_time DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, userEmail);

            ResultSet rs = ps.executeQuery();

            SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfUI = new SimpleDateFormat("dd MMM yyyy, hh:mm a");

            Date now = new Date();

            while (rs.next()) {

                String location = rs.getString("parking_location");
                String slot = rs.getString("slot_number");
                String token = rs.getString("token");

                Date start = sdfDB.parse(rs.getString("start_time"));
                Date end = sdfDB.parse(rs.getString("end_time"));

                String dbStatus = rs.getString("status");

                String status;
                Color statusColor;

                if ("Cancelled".equalsIgnoreCase(dbStatus)) {
                    status = "Cancelled";
                    statusColor = Color.RED;
                }
                else if ("completed".equalsIgnoreCase(dbStatus)) {
                    status = "Completed";
                    statusColor = Color.GRAY;
                }
                else if (end.before(now)) {
                    status = "Completed";
                    statusColor = Color.GRAY;
                }
                else {
                    status = "Active";
                    statusColor = new Color(0, 150, 0);
                }

                //CARD
                JPanel card = new JPanel(new BorderLayout());
                card.setBackground(Color.WHITE);
                card.setBorder(new CompoundBorder(
                        new EmptyBorder(10,10,10,10),
                        new LineBorder(new Color(220,220,220),1,true)
                ));
                card.setMaximumSize(new Dimension(900, 220));

                //INFO
                JPanel infoPanel = new JPanel(new GridLayout(6,1));
                infoPanel.setBackground(Color.WHITE);

                infoPanel.add(new JLabel("📍 Location: " + location));
                infoPanel.add(new JLabel("🚗 Slot: " + slot));
                infoPanel.add(new JLabel("⏰ Start: " + sdfUI.format(start)));
                infoPanel.add(new JLabel("⏰ End: " + sdfUI.format(end)));
                infoPanel.add(new JLabel("🎟 Token: " + token));

                JLabel statusLabel = new JLabel("Status: " + status);
                statusLabel.setForeground(statusColor);
                statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
                infoPanel.add(statusLabel);

                //BUTTONS
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                btnPanel.setBackground(Color.WHITE);

                JButton cancelBtn = new JButton("Cancel");
                JButton extendBtn = new JButton("Extend");
                JButton exitBtn = new JButton("Exit");

                cancelBtn.setBackground(new Color(255, 80, 80));
                cancelBtn.setForeground(Color.WHITE);

                extendBtn.setBackground(new Color(0, 140, 255));
                extendBtn.setForeground(Color.WHITE);

                exitBtn.setBackground(new Color(0, 170, 0));
                exitBtn.setForeground(Color.WHITE);

                //CANCEL
                cancelBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, "Cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try (Connection con2 = DBConnection.getConnection()) {

                            PreparedStatement ps1 = con2.prepareStatement(
                                "UPDATE bookings SET status='Cancelled' WHERE token=?"
                            );
                            ps1.setString(1, token);
                            ps1.executeUpdate();

                            PreparedStatement ps2 = con2.prepareStatement(
                                "UPDATE slots SET status='available' WHERE location_name=? AND slot_number=?"
                            );
                            ps2.setString(1, location);
                            ps2.setString(2, slot);
                            ps2.executeUpdate();

                            PreparedStatement ps3 = con2.prepareStatement(
                                "UPDATE parking_locations SET available_slots=available_slots+1, occupied_slots=occupied_slots-1 WHERE location_name=?"
                            );
                            ps3.setString(1, location);
                            ps3.executeUpdate();

                            JOptionPane.showMessageDialog(this, "Booking Cancelled!");

                            if (ParkingSlots.currentInstance != null)
                                ParkingSlots.currentInstance.refreshData();

                            loadBookings();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                //EXTEND
                extendBtn.addActionListener(e -> {
                    String extra = JOptionPane.showInputDialog(this, "Enter extra hours:");

                    try {
                        int hours = Integer.parseInt(extra);

                        Date newEnd = new Date(end.getTime() + hours * 60 * 60 * 1000);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try (Connection con2 = DBConnection.getConnection()) {
                            PreparedStatement ps2 = con2.prepareStatement(
                                "UPDATE bookings SET end_time=? WHERE token=?"
                            );
                            ps2.setString(1, sdf.format(newEnd));
                            ps2.setString(2, token);
                            ps2.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(this, "Time Extended!");
                        loadBookings();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid input!");
                    }
                });

                //EXIT
                exitBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, "Exit and free slot?", "Confirm", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try (Connection con2 = DBConnection.getConnection()) {

                            PreparedStatement ps1 = con2.prepareStatement(
                                "UPDATE bookings SET status='Completed', end_time=NOW() WHERE token=?"
                            );
                            ps1.setString(1, token);
                            ps1.executeUpdate();

                            PreparedStatement ps2 = con2.prepareStatement(
                                "UPDATE slots SET status='available' WHERE location_name=? AND slot_number=?"
                            );
                            ps2.setString(1, location);
                            ps2.setString(2, slot);
                            ps2.executeUpdate();

                            PreparedStatement ps3 = con2.prepareStatement(
                                "UPDATE parking_locations SET available_slots=available_slots+1, occupied_slots=occupied_slots-1 WHERE location_name=?"
                            );
                            ps3.setString(1, location);
                            ps3.executeUpdate();

                            JOptionPane.showMessageDialog(this, "Exit Done!");

                            if (ParkingSlots.currentInstance != null)
                                ParkingSlots.currentInstance.refreshData();

                            loadBookings();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                if (status.equals("Active")) {
                    btnPanel.add(cancelBtn);
                    btnPanel.add(extendBtn);
                    btnPanel.add(exitBtn);
                }
                if (!status.equals("Active")) {
                    cancelBtn.setEnabled(false);
                    extendBtn.setEnabled(false);
                    exitBtn.setEnabled(false);
                }
                

                card.add(infoPanel, BorderLayout.CENTER);
                card.add(btnPanel, BorderLayout.SOUTH);

                mainPanel.add(Box.createVerticalStrut(10));
                mainPanel.add(card);
            }

            if (mainPanel.getComponentCount() == 1) { 
                JLabel empty = new JLabel("No bookings found!");
                empty.setFont(new Font("Arial", Font.BOLD, 18));
                empty.setForeground(Color.GRAY);
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                mainPanel.add(Box.createVerticalStrut(20));
                mainPanel.add(empty);
            }

            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new MyBookings("user@example.com");
    }
}

