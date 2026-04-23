package parking.admin;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageSlots extends JFrame {

    JTable table;
    DefaultTableModel model;
    JComboBox<String> locationFilter;

    public ManageSlots() {

        setTitle("Manage Slots");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(235, 240, 250));

        JPanel topPanel1 = new JPanel(new BorderLayout());
        topPanel1.setBackground(new Color(235, 240, 250));
        topPanel1.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(170, 35));
        leftPanel.setBackground(new Color(235, 240, 250));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(235, 240, 250));

        JLabel title = new JLabel("Slots");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage individual slot status");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(title);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(subtitle);

        //LOCATION FILTER
        locationFilter = new JComboBox<>();
        locationFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        locationFilter.setPreferredSize(new Dimension(170, 35));
        locationFilter.setBackground(new Color(100, 80, 230));
        locationFilter.setForeground(Color.WHITE);
        locationFilter.setFocusable(false);

        locationFilter.addItem("All Locations");

        loadLocations();

        locationFilter.addActionListener(e -> loadSlots());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(235, 240, 250));
        JPanel dropdownWrapper = new JPanel();
        dropdownWrapper.setOpaque(false);
        dropdownWrapper.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); 

        dropdownWrapper.add(locationFilter);

        rightPanel.add(dropdownWrapper);

        topPanel1.add(leftPanel, BorderLayout.WEST);
        topPanel1.add(centerPanel, BorderLayout.CENTER);
        topPanel1.add(rightPanel, BorderLayout.EAST);

        add(topPanel1, BorderLayout.NORTH);
        
        JButton backBtn = new JButton("Back");

     backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
     backBtn.setBackground(new Color(100, 80, 230));
     backBtn.setForeground(Color.WHITE);
     backBtn.setFocusPainted(false);
     backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

     //hover effect
     backBtn.addMouseListener(new MouseAdapter() {
         public void mouseEntered(MouseEvent e) {
             backBtn.setBackground(new Color(120, 100, 240));
         }

         public void mouseExited(MouseEvent e) {
             backBtn.setBackground(new Color(100, 80, 230));
         }
     });

     //action
     backBtn.addActionListener(e -> {
         dispose();             
         new AdminDashboard();  
     });

     JLayeredPane lp = getLayeredPane();
     lp.add(backBtn, JLayeredPane.PALETTE_LAYER);

     addComponentListener(new java.awt.event.ComponentAdapter() {
         public void componentResized(java.awt.event.ComponentEvent evt) {
             backBtn.setBounds(getWidth() - 155, 20, 100, 35);
         }
     });

        model = new DefaultTableModel(
                new String[]{"Slot", "Location", "Type", "Status", "Delete"}, 0
        );

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 200));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(100, 80, 230));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));

        //STATUS EDITOR
        String[] statusOptions = {"available", "occupied", "reserved", "maintenance"};
        TableColumn statusColumn = table.getColumnModel().getColumn(3);
        JComboBox<String> comboBox = new JComboBox<>(statusOptions);
        statusColumn.setCellEditor(new DefaultCellEditor(comboBox));

        statusColumn.setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String status = value.toString();

                if (status.equals("available")) {
                    c.setBackground(new Color(212, 237, 218));
                } else if (status.equals("occupied")) {
                    c.setBackground(new Color(248, 215, 218));
                } else if (status.equals("reserved")) {
                    c.setBackground(new Color(255, 243, 205));
                } else {
                    c.setBackground(new Color(255, 224, 178));
                }

                c.setOpaque(true);
                return c;
            }
        });

        //DELETE BUTTON
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                JLabel label = new JLabel("Delete");
                label.setOpaque(true);
                label.setBackground(new Color(220, 53, 69));
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(235, 240, 250));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        wrapper.add(scroll, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);

        loadSlots();

        model.addTableModelListener(e -> {

            if (e.getColumn() != 3) return;

            int row = e.getFirstRow();

            String slot = model.getValueAt(row, 0).toString();
            String location = model.getValueAt(row, 1).toString();
            String newStatus = model.getValueAt(row, 3).toString();

            try (Connection con = DBConnection.getConnection()) {

                PreparedStatement psOld = con.prepareStatement(
                        "SELECT status FROM slots WHERE slot_number=? AND location_name=?"
                );
                psOld.setString(1, slot);
                psOld.setString(2, location);

                ResultSet rsOld = psOld.executeQuery();

                String oldStatus = "";
                if (rsOld.next()) oldStatus = rsOld.getString("status");

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE slots SET status=? WHERE slot_number=? AND location_name=?"
                );
                ps.setString(1, newStatus);
                ps.setString(2, slot);
                ps.setString(3, location);
                ps.executeUpdate();

                if (!oldStatus.equals("occupied") && newStatus.equals("occupied")) {

                    PreparedStatement ps2 = con.prepareStatement(
                            "UPDATE parking_locations SET occupied_slots = occupied_slots + 1, available_slots = available_slots - 1 WHERE location_name=?"
                    );
                    ps2.setString(1, location);
                    ps2.executeUpdate();

                } else if (oldStatus.equals("occupied") && !newStatus.equals("occupied")) {

                    PreparedStatement ps2 = con.prepareStatement(
                            "UPDATE parking_locations SET occupied_slots = occupied_slots - 1, available_slots = available_slots + 1 WHERE location_name=?"
                    );
                    ps2.setString(1, location);
                    ps2.executeUpdate();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        //DELETE
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (col == 4) {

                    String slot = model.getValueAt(row, 0).toString();
                    String location = model.getValueAt(row, 1).toString();

                    int confirm = JOptionPane.showConfirmDialog(
                            null, "Delete this slot?", "Confirm",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        try (Connection con = DBConnection.getConnection()) {

                            PreparedStatement ps = con.prepareStatement(
                                    "DELETE FROM slots WHERE slot_number=? AND location_name=?"
                            );

                            ps.setString(1, slot);
                            ps.setString(2, location);
                            ps.executeUpdate();

                            loadSlots();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        setVisible(true);

        new javax.swing.Timer(3000, e -> loadSlots()).start();
    }

    //FIXED LOCATIONS
    private void loadLocations() {

        locationFilter.removeAllItems();
        locationFilter.addItem("All Locations");

        try (Connection con = DBConnection.getConnection()) {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT location_name FROM parking_locations"
            );

            while (rs.next()) {
                locationFilter.addItem(rs.getString("location_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //LOAD SLOTS
    private void loadSlots() {

        model.setRowCount(0);

        try (Connection con = DBConnection.getConnection()) {

            String query = "SELECT * FROM slots";

            boolean filter = locationFilter.getSelectedItem() != null
                    && !locationFilter.getSelectedItem().equals("All Locations");

            if (filter) query += " WHERE location_name=? ORDER BY CAST(SUBSTRING(slot_number, 2) AS UNSIGNED)";

            PreparedStatement ps = con.prepareStatement(query);

            if (filter) {
                ps.setString(1, locationFilter.getSelectedItem().toString());
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getString("slot_number"),
                        rs.getString("location_name"),
                        "Car",
                        rs.getString("status"),
                        "Delete"
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageSlots::new);
    }
}

