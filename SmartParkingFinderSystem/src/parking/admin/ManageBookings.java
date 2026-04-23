package parking.admin;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ManageBookings extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public ManageBookings() {

        setTitle("Manage Bookings");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        //BACKGROUND
        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                Color top = new Color(245, 247, 255);
                Color bottom = new Color(230, 235, 250);

                GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        bg.setLayout(new BorderLayout(20, 20));
        bg.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(bg);
        
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(100, 80, 230));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        //TITLE
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel mainTitle = new JLabel("All Bookings");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 34));
        mainTitle.setForeground(new Color(60, 60, 60));
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("All system reservations");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subTitle.setForeground(Color.GRAY);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(mainTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subTitle);
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        topBar.add(titlePanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(backBtn);

        topBar.add(rightPanel, BorderLayout.EAST);

        bg.add(topBar, BorderLayout.NORTH);

        //TABLE MODEL
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Token", "User", "Location (Slot)", "Time", "Status"
        });

        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                JTextArea area = new JTextArea();
                area.setText(value != null ? value.toString() : "");
                area.setWrapStyleWord(true);
                area.setLineWrap(true);
                area.setOpaque(true);
                area.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                area.setBackground(Color.WHITE);
                area.setForeground(Color.BLACK);

                //STATUS COLOR LOGIC
                try {
                    String status = table.getValueAt(row, 4).toString(); 

                    if (status.equalsIgnoreCase("CANCELLED")) {
                        area.setForeground(Color.RED);
                    }
                    else if (status.equalsIgnoreCase("ACTIVE")) {
                        area.setForeground(new Color(0, 150, 0)); 
                    }
                    else if (status.equalsIgnoreCase("COMPLETED")) {
                        area.setForeground(new Color(120, 120, 120)); 
                    }

                } catch (Exception ex) {
                	
                }

                if (isSelected) {
                    area.setBackground(new Color(220, 220, 250));
                }

                return area;
            }
        });
        
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(220, 220, 250));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(100, 80, 220));
        header.setForeground(Color.WHITE);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

        //CARD
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 50), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        tableCard.add(sp);
        bg.add(tableCard, BorderLayout.CENTER);

        //LOAD DATA
        loadBookings();
        Timer timer = new Timer(3000, e -> loadBookings());
        timer.start();
        backBtn.addActionListener(e -> {
            new AdminDashboard();   
            dispose();              
        });

        setVisible(true);
    }

    //LOAD BOOKINGS
    private void loadBookings() {

        try (Connection con = DBConnection.getConnection()) {

            String sql =
                    "SELECT b.token, u.name AS user_name, " +
                    "b.parking_location, b.slot_number, " +
                    "b.start_time, b.end_time, b.status " +
                    "FROM bookings b " +
                    "JOIN users u ON b.user_email = u.email";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {

                String token = rs.getString("token");

                String userName = rs.getString("user_name");

                String location = rs.getString("parking_location")
                        + " (" + rs.getString("slot_number") + ")";

                String time =
                        "Start: " + rs.getString("start_time") +
                        "\nEnd: " + rs.getString("end_time");

                String status = rs.getString("status");

                model.addRow(new Object[]{
                        token,
                        userName,
                        location,
                        time,
                        status
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading bookings: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ManageBookings();
    }
}