package parking.gui;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import parking.gui.LoginForm;

public class UserProfile extends JFrame {

    JTextField nameField, emailField, phoneField, ageField, addressField, vehicleNumberField;
    JComboBox<String> genderBox, vehicleTypeBox;

    String currentUser;

    public UserProfile(String email) {
        this.currentUser = email;

        setTitle("User Profile");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //BG 
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 247, 255));
        panel.setLayout(null);
        setContentPane(panel);

        //CARD PANEL
        int cardWidth = 600;
        int cardHeight = 570;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - cardWidth) / 2;
        int y = (screenSize.height - cardHeight) / 2 - 50;

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(x, y, cardWidth, cardHeight);
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220), 1, true),
                new EmptyBorder(20,20,20,20)
        ));
        panel.add(card);

        //ICON
        JLabel icon = new JLabel("👤", SwingConstants.CENTER);
        icon.setBounds((cardWidth - 120) / 2, 20, 120, 80);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 65));
        card.add(icon);
      
        //TITLE
        JLabel title = new JLabel("My Profile", SwingConstants.CENTER);
        title.setBounds((cardWidth - 200) / 2, 90, 200, 40);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        card.add(title);

        //FONTS
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 15);

        int yy = 140;
        int labelX = 50;
        int fieldX = 200;
        int fieldWidth = 350;
        int fieldHeight = 35;
        int gap = 50;

        //NAME
        card.add(createLabel("Name:", labelX, yy, labelFont));
        nameField = createField(fieldX, yy, fieldWidth, fieldHeight, fieldFont);
        card.add(nameField);

        yy += gap;

        //EMAIL
        card.add(createLabel("Email:", labelX, yy, labelFont));
        emailField = createField(fieldX, yy, fieldWidth, fieldHeight, fieldFont);
        card.add(emailField);

        yy += gap;

        //PHONE
        card.add(createLabel("Phone:", labelX, yy, labelFont));
        phoneField = createField(fieldX, yy, fieldWidth, fieldHeight, fieldFont);
        card.add(phoneField);

        yy += gap;

        //AGE 
        card.add(createLabel("Age:", labelX, yy, labelFont));
        ageField = createField(fieldX, yy, fieldWidth, fieldHeight, fieldFont);
        card.add(ageField);

        yy += gap;

        //GENDER
        card.add(createLabel("Gender:", labelX, yy, labelFont));
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setBounds(fieldX, yy, fieldWidth, fieldHeight);
        genderBox.setFont(fieldFont);
        card.add(genderBox);

        yy += gap;

        //ADDRESS
        card.add(createLabel("Address:", labelX, yy, labelFont));
        addressField = createField(fieldX, yy, fieldWidth, fieldHeight, fieldFont);
        card.add(addressField);

        yy += gap;

        //VEHICLE TYPE 
        card.add(createLabel("Vehicle Type:", labelX, yy, labelFont));
        vehicleTypeBox = new JComboBox<>(new String[]{"Car", "Bike", "Truck"});
        vehicleTypeBox.setBounds(fieldX, yy, fieldWidth, fieldHeight);
        vehicleTypeBox.setFont(fieldFont);
        card.add(vehicleTypeBox);

        yy += gap;

        //VEHICLE NUMBER
        card.add(createLabel("Vehicle No:", labelX, yy, labelFont));
        vehicleNumberField = createField(fieldX, yy, fieldWidth, fieldHeight, fieldFont);
        card.add(vehicleNumberField);

        //BUTTON COLORS
        Color normal = new Color(120, 80, 220);
        Color hover = new Color(100, 60, 200);

        //BACK BUTTON
        JButton backBtn = new JButton("Back");
        backBtn.setBounds(screenSize.width - 150, 20, 120, 40);
        backBtn.setFont(new Font("Arial", Font.BOLD, 18));
        styleButton(backBtn, normal, hover);
        panel.add(backBtn);
        backBtn.addActionListener(e -> {
            dispose();             
            new LoginForm();       
        });
        
     //LOGOUT BUTTON
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(screenSize.width - 150, 70, 120, 40); ;
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 18));
        styleButton(logoutBtn, normal, hover);
        panel.add(logoutBtn);

        logoutBtn.addActionListener(e -> {
            dispose();             
            new FirstPage();        
        });

        //ALL SLOTS BUTTON
        JButton allSlotsBtn = new JButton("All Parking Slots");
        allSlotsBtn.setBounds(x + cardWidth / 4 - 60, y + cardHeight + 10, 200, 40);
        allSlotsBtn.setFont(new Font("Arial", Font.BOLD, 18));
        styleButton(allSlotsBtn, normal, hover);
        panel.add(allSlotsBtn);
        allSlotsBtn.addActionListener(e -> new ParkingSlots(currentUser));

        //MY BOOKINGS BUTTON
        JButton myBookingsBtn = new JButton("My Bookings");
        myBookingsBtn.setBounds(x + 3 * cardWidth / 4 - 100, y + cardHeight + 10, 200, 40);
        myBookingsBtn.setFont(new Font("Arial", Font.BOLD, 18));
        styleButton(myBookingsBtn, normal, hover);
        panel.add(myBookingsBtn);
        myBookingsBtn.addActionListener(e -> new MyBookings(currentUser));

        loadUserData();
        setVisible(true);
    }

    private JLabel createLabel(String text, int x, int y, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 150, 30);
        lbl.setFont(font);
        return lbl;
    }

    private JTextField createField(int x, int y, int w, int h, Font font) {
        JTextField field = new JTextField();
        field.setBounds(x, y, w, h);
        field.setFont(font);
        field.setBorder(new LineBorder(new Color(200,200,200), 1, true));
        return field;
    }

    //BUTTON STYLE 
    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(new LineBorder(Color.WHITE, 2, true));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(hover);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(normal);
            }
        });
    }

    //LOAD DATA
    private void loadUserData() {
        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT * FROM users WHERE email=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, currentUser);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));
                ageField.setText(rs.getString("age"));
                addressField.setText(rs.getString("address"));
                vehicleNumberField.setText(rs.getString("vehicle_number"));

                genderBox.setSelectedItem(rs.getString("gender"));
                vehicleTypeBox.setSelectedItem(rs.getString("vehicle_type"));
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading profile!");
        }
    }

    public static void main(String[] args) {
        new UserProfile("user@example.com");
    }
}

