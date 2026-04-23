package parking.admin;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ParkingLocationManagement extends JFrame {

    JTable table;
    DefaultTableModel model;

    public ParkingLocationManagement() {

        setTitle("Manage Locations");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //BACKGROUND
        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                Color top = new Color(245,247,255);
                Color bottom = new Color(230,235,250);

                GradientPaint gp = new GradientPaint(0,0,top,0,getHeight(),bottom);
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        bg.setLayout(new BorderLayout(20,20));
        bg.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));
        add(bg);

        //TITLE
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel mainTitle = new JLabel("Locations");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 34));
        mainTitle.setForeground(new Color(60,60,60));
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Manage parking facilities");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subTitle.setForeground(Color.GRAY);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(mainTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subTitle);

        JButton addBtn = createPurpleButton("Add Location");
        addBtn.setPreferredSize(new Dimension(140, 35));
        
        JButton backBtn = createPurpleButton("Back");
        backBtn.setBackground(new Color(100, 80, 230));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        buttonPanel.add(addBtn);
        buttonPanel.add(backBtn);

        topBar.add(titlePanel, BorderLayout.CENTER);
        topBar.add(buttonPanel, BorderLayout.EAST);

        bg.add(topBar, BorderLayout.NORTH);
        
       
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Name","Slots (Available/Total)","Delete"
        });

        JTable table = new JTable(model){
            public boolean isCellEditable(int r,int c){ return false; }
        };

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(220,220,250));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(100,80,220));
        header.setForeground(Color.WHITE);

        table.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220,220,220),1,true));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200,50),1,true),
                BorderFactory.createEmptyBorder(15,15,15,15)
        ));
        card.add(sp);

        bg.add(card, BorderLayout.CENTER);

        loadLocations();

        new Timer(3000, e -> loadLocations()).start();

        addBtn.addActionListener(e -> addLocation());
        
        backBtn.addActionListener(e -> {
            new AdminDashboard();   
            dispose();              
        });

        //DELETE CLICK
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();

                String name = model.getValueAt(row,0).toString();

                if(col == 2){
                    int confirm = JOptionPane.showConfirmDialog(null,"Delete this location?");
                    if(confirm == JOptionPane.YES_OPTION){
                        deleteLocation(name);
                    }
                }
            }
        });

        setVisible(true);
    }

    //LOAD FROM DB
    private void loadLocations() {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM parking_locations";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            model.setRowCount(0);

            while(rs.next()){
                String name = rs.getString("location_name");
                int total = rs.getInt("total_slots");
                int avail = rs.getInt("available_slots");

                model.addRow(new Object[]{
                        name,
                        avail + " / " + total,
                        "Delete"
                });
            }

        } catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    //ADD LOCATION
    private void addLocation(){

        JTextField name = new JTextField();
        JTextField slots = new JTextField();

        Object[] fields = {
                "Location Name:", name,
                "Total Slots:", slots
        };

        int op = JOptionPane.showConfirmDialog(this, fields,"Add Location",JOptionPane.OK_CANCEL_OPTION);

        if(op == JOptionPane.OK_OPTION){
            try{
                Connection conn = DBConnection.getConnection();

                int total = Integer.parseInt(slots.getText());

                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM parking_locations");
                rs.next();
                int count = rs.getInt(1);

                char prefix = (char) ('A' + count);

                PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO parking_locations(location_name,total_slots,available_slots,occupied_slots) VALUES(?,?,?,?)"
                );

                pst.setString(1,name.getText());
                pst.setInt(2,total);
                pst.setInt(3,total);
                pst.setInt(4,0);
                pst.executeUpdate();

                PreparedStatement slotPst = conn.prepareStatement(
                        "INSERT INTO slots(location_name, slot_number, status) VALUES(?,?,?)"
                );

                for(int i=1; i<=total; i++){
                    String slot = prefix + String.valueOf(i);

                    slotPst.setString(1, name.getText());
                    slotPst.setString(2, slot);
                    slotPst.setString(3, "available");

                    slotPst.executeUpdate();
                }

                JOptionPane.showMessageDialog(this,"Location + Slots Added Successfully!");
                loadLocations();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //DELETE
    private void deleteLocation(String name){
        try{
            Connection conn = DBConnection.getConnection();

            conn.setAutoCommit(false);

            PreparedStatement ps1 = conn.prepareStatement(
                    "DELETE FROM slots WHERE location_name=?"
            );
            ps1.setString(1, name);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                    "DELETE FROM parking_locations WHERE location_name=?"
            );
            ps2.setString(1, name);
            ps2.executeUpdate();

            conn.commit();

            JOptionPane.showMessageDialog(this,"Location + Slots Deleted Successfully!");

            loadLocations();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //BUTTON STYLE
    private JButton createPurpleButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(100, 80, 220));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5,15,5,15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){
                btn.setBackground(new Color(120,100,240));
            }
            public void mouseExited(MouseEvent e){
                btn.setBackground(new Color(100,80,220));
            }
        });

        return btn;
    }

    //DELETE BUTTON
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text){
            setText(text);
            setBackground(new Color(239,83,80));
            setForeground(Color.WHITE);
        }

        public Component getTableCellRendererComponent(JTable table,Object value,
                                                       boolean isSelected,boolean hasFocus,int row,int col){
            setText(value==null?"":value.toString());
            return this;
        }
    }

    public static void main(String[] args) {
        new ParkingLocationManagement();
    }
}