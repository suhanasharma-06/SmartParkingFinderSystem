package parking.admin;

import parking.database.DBConnection;
import parking.gui.FirstPage;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import parking.admin.ParkingLocationManagement;

public class AdminDashboard extends JFrame {

    JLabel usersLabel, bookingsLabel, availableSlotsLabel, occupiedSlotsLabel;

    public AdminDashboard() {

        setTitle("Admin Dashboard - Smart Parking");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //BACKGROUND
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                Color topColor = new Color(245, 247, 255);
                Color bottomColor = new Color(230, 235, 250);

                GradientPaint gp = new GradientPaint(
                        0, 0, topColor,
                        0, getHeight(), bottomColor
                );

                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        background.setLayout(new BorderLayout(0, 50));
        add(background);

        //TITLE
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(new Color(60, 60, 60));

        titlePanel.add(title);
        background.add(titlePanel, BorderLayout.NORTH);

        //CARDS 
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 30, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel card1 = createModernCard("Total Users", new Color(255, 183, 77), "\uD83D\uDC64");
        JPanel card2 = createModernCard("Total Active Bookings", new Color(102, 187, 106), "\uD83D\uDCCB");
        JPanel card3 = createModernCard("Available Slots", new Color(66, 165, 245), "\uD83D\uDE97");
        JPanel card4 = createModernCard("Occupied Slots", new Color(239, 83, 80), "\uD83D\uDED2");

        cardsPanel.add(card1);
        cardsPanel.add(card2);
        cardsPanel.add(card3);
        cardsPanel.add(card4);

        background.add(cardsPanel, BorderLayout.CENTER);

        //BUTTONS
        JPanel btnPanel = new JPanel(new GridLayout(1, 5, 30, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));

        JButton manageUsersBtn = createModernButton("Manage Users");
        JButton manageSlotsBtn = createModernButton("Manage Slots");
        JButton manageLocationsBtn = createModernButton("Manage Locations");
        JButton manageBookingsBtn = createModernButton("Manage Bookings");
        JButton reportsBtn = createModernButton("Manage Feedbacks");

        btnPanel.add(manageUsersBtn);
        btnPanel.add(manageSlotsBtn);
        btnPanel.add(manageLocationsBtn);
        btnPanel.add(manageBookingsBtn);
        btnPanel.add(reportsBtn);

        background.add(btnPanel, BorderLayout.SOUTH);

        manageUsersBtn.addActionListener(e -> new ManageUsers());
        manageSlotsBtn.addActionListener(e -> new ManageSlots());
        manageLocationsBtn.addActionListener(e -> new ParkingLocationManagement());
        manageBookingsBtn.addActionListener(e -> new ManageBookings());
        reportsBtn.addActionListener(e -> new AdminFeedbackPage());

        //LOGOUT
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(100, 80, 220));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);

        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.add(logoutBtn, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                logoutBtn.setBounds(getWidth() - 150, 20, 120, 35);
            }
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new FirstPage();
        });

        loadStats();

        Timer timer = new Timer(3000, e -> loadStats());
        timer.start();

        setVisible(true);
    }

    //CARD
    private JPanel createModernCard(String title, Color color, String icon) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel numberLabel = new JLabel("0");
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        numberLabel.setForeground(Color.WHITE);

        switch (title) {
            case "Total Users": usersLabel = numberLabel; break;
            case "Total Active Bookings": bookingsLabel = numberLabel; break;
            case "Available Slots": availableSlotsLabel = numberLabel; break;
            case "Occupied Slots": occupiedSlotsLabel = numberLabel; break;
        }

        card.add(iconLabel, BorderLayout.WEST);
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(numberLabel, BorderLayout.EAST);

        return card;
    }

    //BUTTON
    private JButton createModernButton(String text) {

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(new Color(100, 80, 220));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(120, 100, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 80, 220));
            }
        });

        return button;
    }

    //LOAD STATS
    private void loadStats() {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rsUsers = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rsUsers.next()) usersLabel.setText(rsUsers.getString(1));

            ResultSet rsBookings = stmt.executeQuery(
            	    "SELECT COUNT(*) FROM bookings WHERE LOWER(status)='booked'"
            	);
            if (rsBookings.next()) bookingsLabel.setText(rsBookings.getString(1));

            ResultSet rsAvailable = stmt.executeQuery(
                    "SELECT COUNT(*) FROM slots WHERE LOWER(status)='available'"
            );
            if (rsAvailable.next()) availableSlotsLabel.setText(rsAvailable.getString(1));

            ResultSet rsOccupied = stmt.executeQuery(
                    "SELECT COUNT(*) FROM slots WHERE LOWER(status)='occupied'"
            );
            if (rsOccupied.next()) occupiedSlotsLabel.setText(rsOccupied.getString(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}
