package parking.gui;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {

    JTextField emailField;
    JPasswordField passwordField;
    JButton loginButton;
    JCheckBox showPassword;

    public LoginForm() {
        initUI();
    }

    private void initUI() {

        setTitle("User Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //BG
        JPanel bg = new JPanel();
        bg.setBackground(new Color(245, 247, 255));
        bg.setLayout(null);

        //TITLE
        JLabel title = new JLabel("User Login");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setBounds(350, 100, 400, 40);
        bg.add(title);

        JLabel sub = new JLabel("Login to access your account.");
        sub.setFont(new Font("Arial", Font.PLAIN, 16));
        sub.setForeground(Color.GRAY);
        sub.setBounds(350, 140, 400, 25);
        bg.add(sub);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(1150, 30, 120, 40); 

        // STYLE
        logoutButton.setBackground(new Color(120, 80, 220));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setFocusPainted(false);

        // Hover effect
        logoutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                logoutButton.setBackground(new Color(100, 60, 200));
            }

            public void mouseExited(MouseEvent evt) {
                logoutButton.setBackground(new Color(120, 80, 220));
            }
        });

        // ACTION
        logoutButton.addActionListener(e -> {
            dispose();         
            new FirstPage();    
        });

        bg.add(logoutButton);

        //WHITE CARD
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220), 1, true),
                new EmptyBorder(20,20,20,20)
        ));

        int cardWidth = 600;
        int cardHeight = 350;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - cardWidth) / 2;
        int y = (screenSize.height - cardHeight) / 2;

        card.setBounds(x, y, cardWidth, cardHeight);

        Font labelFont = new Font("Arial", Font.BOLD, 16);

        //EMAIL
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(labelFont);
        emailLabel.setBounds(50, 50, 200, 25);
        card.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(50, 80, 500, 45);
        styleField(emailField);
        card.add(emailField);

        //PASSWORD
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(labelFont);
        passLabel.setBounds(50, 140, 200, 25);
        card.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 170, 500, 45);
        styleField(passwordField);
        card.add(passwordField);

        //SHOW PASSWORD
        showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(50, 220, 200, 25);
        showPassword.setBackground(Color.WHITE);
        showPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        showPassword.addActionListener(e -> {
            if(showPassword.isSelected()) passwordField.setEchoChar((char)0);
            else passwordField.setEchoChar('•');
        });
        card.add(showPassword);

        //LOGIN BUTTON
        loginButton = new JButton("Login");
        loginButton.setBounds(380, 270, 170, 50);
        loginButton.setBackground(new Color(120, 80, 220));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);

        //Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(new Color(100, 60, 200));
            }
            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(new Color(120, 80, 220));
            }
        });

        card.add(loginButton);

        bg.add(card);
        add(bg);

        //ACTION
        loginButton.addActionListener(e -> loginUser());

        setVisible(true);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBorder(new LineBorder(new Color(200,200,200), 1, true));
    }

   
    private void loginUser() {

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new UserProfile(email);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Email or Password!");
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    

    public static void main(String[] args) {
        new LoginForm();
    }
}