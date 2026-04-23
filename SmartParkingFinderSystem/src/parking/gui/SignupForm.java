package parking.gui;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignupForm extends JFrame {

    JTextField nameField, emailField, phoneField, ageField, addressField, vehicleNumberField;
    JPasswordField passwordField;
    JComboBox<String> genderBox, vehicleTypeBox;
    JButton signupButton;

    public SignupForm() {

        setTitle("SmartPark Signup");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //BACKGROUND
        JPanel bgPanel = new JPanel();
        bgPanel.setBackground(new Color(245, 247, 255));
        bgPanel.setLayout(null);

        //TITLE
        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setBounds(480, 10, 400, 40);
        bgPanel.add(title);

        JLabel sub = new JLabel("Create your account to access smart parking system.");
        sub.setFont(new Font("Arial", Font.PLAIN, 16));
        sub.setForeground(Color.GRAY);
        sub.setBounds(410, 50, 600, 25);
        bgPanel.add(sub);
        
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.setBackground(new Color(100, 80, 230));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        backBtn.setBounds(screenSize.width - 140, 20, 100, 35);
        bgPanel.add(backBtn);

        //CARD
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220), 1, true),
                new EmptyBorder(20,20,20,20)
        ));

        int cardWidth = 700;
        int cardHeight = 580; 

        Dimension screenSize1 = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize1.width - cardWidth) / 2;
        int y = (screenSize1.height - cardHeight) / 2 + 20; 

        card.setBounds(x, y, cardWidth, cardHeight);
        bgPanel.add(card);

        //FIELDS
        nameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();
        phoneField = new JTextField();
        ageField = new JTextField();
        addressField = new JTextField();
        vehicleNumberField = new JTextField();

        genderBox = new JComboBox<>(new String[]{"Male","Female","Other"});
        vehicleTypeBox = new JComboBox<>(new String[]{"Car","Bike","Scooter"});

        styleField(nameField);
        styleField(emailField);
        styleField(passwordField);
        styleField(phoneField);
        styleField(ageField);
        styleField(addressField);
        styleField(vehicleNumberField);

        styleCombo(genderBox);
        styleCombo(vehicleTypeBox);

        int yPos = 20;  

        yPos = addRow(card, "Name", nameField, yPos);
        yPos = addRow(card, "Email", emailField, yPos);
        yPos = addRow(card, "Password", passwordField, yPos);
        yPos = addRow(card, "Phone", phoneField, yPos);
        yPos = addRow(card, "Age", ageField, yPos);
        yPos = addRow(card, "Gender", genderBox, yPos);
        yPos = addRow(card, "Address", addressField, yPos);
        yPos = addRow(card, "Vehicle", vehicleTypeBox, yPos);
        yPos = addRow(card, "Vehicle No", vehicleNumberField, yPos);

        //BUTTON
        signupButton = new JButton("Signup");
        signupButton.setBounds(250, yPos + 10, 200, 45);

        signupButton.setBackground(new Color(120, 80, 220));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.setFocusPainted(false);

        signupButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                signupButton.setBackground(new Color(100, 60, 200));
            }
            public void mouseExited(MouseEvent e) {
                signupButton.setBackground(new Color(120, 80, 220));
            }
        });

        card.add(signupButton);

        signupButton.addActionListener(e -> registerUser());

        //LOGIN LINK
        JLabel text = new JLabel("Already have an account?");
        text.setFont(new Font("Arial", Font.PLAIN, 14));
        text.setBounds(265, yPos + 70, 300, 20);
        card.add(text);

        JButton login = new JButton("Login");
        login.setBounds(300, yPos + 95, 100, 30);
        login.setBorderPainted(false);
        login.setContentAreaFilled(false);
        login.setForeground(new Color(120, 80, 220));
        login.setFont(new Font("Arial", Font.PLAIN, 14));
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));

        login.addActionListener(e -> {
            new LoginForm();
            dispose();
        });

        card.add(login);

        add(bgPanel);
        backBtn.addActionListener(e -> {
            new FirstPage();  
            dispose();        
        });
        setVisible(true);
    }

    //ROW
    private int addRow(JPanel card, String label, JComponent field, int y) {

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setBounds(50, y, 150, 25);

        field.setBounds(200, y, 400, 35);

        card.add(lbl);
        card.add(field);

        return y + 45;
    }

    //STYLE FIELD
    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBorder(new LineBorder(new Color(200,200,200), 1, true));
    }

    private void styleCombo(JComboBox box) {
        box.setFont(new Font("Arial", Font.PLAIN, 15));
        box.setBorder(new LineBorder(new Color(200,200,200), 1, true));
    }

    //DB
    private void registerUser() {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO users(name,email,password,phone,age,gender,address,vehicle_type,vehicle_number) VALUES(?,?,?,?,?,?,?,?,?)";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, nameField.getText());
            pst.setString(2, emailField.getText());
            pst.setString(3, new String(passwordField.getPassword()));
            pst.setString(4, phoneField.getText());
            pst.setString(5, ageField.getText());
            pst.setString(6, (String) genderBox.getSelectedItem());
            pst.setString(7, addressField.getText());
            pst.setString(8, (String) vehicleTypeBox.getSelectedItem());
            pst.setString(9, vehicleNumberField.getText());

            int i = pst.executeUpdate();

            if (i > 0) {
                JOptionPane.showMessageDialog(this, "Signup Successful!");
                new LoginForm();
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args) {
        new SignupForm();
    }
}

