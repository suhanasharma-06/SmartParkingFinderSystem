package parking.admin;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageUsers extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextField searchField;

    public ManageUsers() {

        setTitle("Manage Users");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Background Panel
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

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        //Main Title
        JLabel mainTitle = new JLabel("USERS");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 34));
        mainTitle.setForeground(new Color(60,60,60));
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Subtitle
        JLabel subTitle = new JLabel("Manage all registered users");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subTitle.setForeground(Color.GRAY);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(mainTitle);
        titlePanel.add(Box.createVerticalStrut(5)); 
        titlePanel.add(subTitle);

        bg.add(titlePanel, BorderLayout.NORTH);
        
        JButton backBtn = new JButton("Back");

     backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
     backBtn.setBackground(new Color(100, 80, 220));
     backBtn.setForeground(Color.WHITE);
     backBtn.setFocusPainted(false);
     backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

     //hover effect
     backBtn.addMouseListener(new MouseAdapter() {
         public void mouseEntered(MouseEvent e) {
             backBtn.setBackground(new Color(120, 100, 240));
         }
         public void mouseExited(MouseEvent e) {
             backBtn.setBackground(new Color(100, 80, 220));
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
             backBtn.setBounds(getWidth() - 140, 20, 100, 35);
         }
     });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
        topPanel.setOpaque(false);

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton searchBtn = createPurpleButton("Search");
        JButton refreshBtn = createPurpleButton("Refresh");

        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(refreshBtn);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        //TITLE PANEL
        JPanel titlePanel1 = new JPanel();
        titlePanel1.setLayout(new BoxLayout(titlePanel1, BoxLayout.Y_AXIS));
        titlePanel1.setOpaque(false);

        JLabel mainTitle1 = new JLabel("Users");
        mainTitle1.setFont(new Font("Segoe UI", Font.BOLD, 32));
        mainTitle1.setForeground(new Color(60,60,60));
        mainTitle1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle1 = new JLabel("Manage all registered users");
        subTitle1.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subTitle1.setForeground(Color.GRAY);
        subTitle1.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel1.add(mainTitle1);
        titlePanel1.add(Box.createVerticalStrut(5));
        titlePanel1.add(subTitle1);

        //SEARCH PANEL
        JPanel topPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
        topPanel1.setOpaque(false);

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton searchBtn1 = createPurpleButton("Search");
        JButton refreshBtn1 = createPurpleButton("Refresh");

        topPanel1.add(searchField);
        topPanel1.add(searchBtn1);
        topPanel1.add(refreshBtn1);

        topContainer.add(titlePanel1);
        topContainer.add(Box.createVerticalStrut(10)); 
        topContainer.add(topPanel1);

        bg.add(topContainer, BorderLayout.NORTH);

        //TABLE MODEL
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID","Name","Email","Phone","Age","Gender",
                "Address","Vehicle Type","Vehicle Number","Delete"
        });

        //TABLE
        table = new JTable(model){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(220,220,250));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(100,80,220));
        header.setForeground(Color.WHITE);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220,220,220),1,true));

        //CARD PANEL 
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200,50),1,true),
                BorderFactory.createEmptyBorder(15,15,15,15)
        ));
        tableCard.add(sp);

        bg.add(tableCard, BorderLayout.CENTER);

        //DELETE BUTTON COLUMN
        table.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));

        //LOAD DATA
        loadUsers("");

        //BUTTON ACTIONS
        searchBtn1.addActionListener(e -> loadUsers(searchField.getText()));
        refreshBtn1.addActionListener(e -> loadUsers(""));

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();

                int id = (int) model.getValueAt(row,0);

                if(col == 9){
                    int confirm = JOptionPane.showConfirmDialog(null,"Delete this user?");
                    if(confirm == JOptionPane.YES_OPTION){
                        deleteUser(id);
                    }
                }
            }
        });

        setVisible(true);
    }

    //LOAD USERS FROM DATABASE
    private void loadUsers(String keyword) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM users WHERE name LIKE ? OR email LIKE ? OR vehicle_number LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            String key = "%" + keyword + "%";
            pst.setString(1,key);
            pst.setString(2,key);
            pst.setString(3,key);

            ResultSet rs = pst.executeQuery();

            model.setRowCount(0);

            boolean found = false;
            int userId = -1;

            while(rs.next()){
                found = true;
                userId = rs.getInt("id");

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("vehicle_type"),
                        rs.getString("vehicle_number"),
                        "Delete"
                });
            }

            if(keyword != null && !keyword.isEmpty()){
                if(found){
                    JOptionPane.showMessageDialog(this, "✅ User found! ID: " + userId);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ User not found!");
                }
            }

        } catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    //DELETE USER
    private void deleteUser(int id){
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement("DELETE FROM users WHERE id=?");
            pst.setInt(1,id);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,"User Deleted!");
            loadUsers("");

        }catch(Exception e){
            System.out.println("Delete Error: "+e.getMessage());
        }
    }

    private JButton createPurpleButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(100, 80, 220));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8,20,8,20));
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

    //BUTTON RENDERER (DELETE)
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text){
            setText(text);
            setBackground(new Color(239,83,80));
            setForeground(Color.WHITE);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value==null)?"":value.toString());
            return this;
        }
    }

    public static void main(String[] args) {
        new ManageUsers();
    }
}



