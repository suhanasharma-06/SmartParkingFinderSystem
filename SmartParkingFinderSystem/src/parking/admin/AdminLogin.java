package parking.admin;

import parking.database.DBConnection;
import parking.gui.FirstPage;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLogin extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;
    JCheckBox showPassword;

    public AdminLogin() {
        initUI();
    }

    private void initUI() {

        setTitle("Admin Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel bg = new JPanel();
        bg.setBackground(new Color(245, 247, 255)); 
        bg.setLayout(null);

        //TITLE
        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setBounds(350, 100, 400, 40);
        bg.add(title);

        JLabel sub = new JLabel("Login to manage parking system.");
        sub.setFont(new Font("Arial", Font.PLAIN, 16));
        sub.setForeground(Color.GRAY);
        sub.setBounds(350, 140, 400, 25);
        bg.add(sub);

        //CARD 
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
        
        JButton backButton = new JButton("Logout");
        backButton.setBounds(1150, 30, 120, 40); 
        backButton.setBackground(new Color(120, 80, 220));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setFocusPainted(false);

        //Hover effect
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(100, 60, 200));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(120, 80, 220));
            }
        });

        //ACTION
        backButton.addActionListener(e -> {
            dispose();         
            new FirstPage();    
        });

        bg.add(backButton);

        //USERNAME
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(labelFont);
        userLabel.setBounds(50, 50, 200, 25);
        card.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(50, 80, 500, 45);
        styleField(usernameField);
        card.add(usernameField);

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
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(100, 60, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(120, 80, 220));
            }
        });

        card.add(loginButton);

        bg.add(card);
        add(bg);

        //ACTION
        loginButton.addActionListener(e -> login());

        setVisible(true);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBorder(new LineBorder(new Color(200,200,200), 1, true));
    }

    private void login() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM admin WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                this.dispose();
                new AdminDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password");
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new AdminLogin();
    }
}


