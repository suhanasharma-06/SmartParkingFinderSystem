package parking.gui;

import javax.swing.*;
import java.awt.*;
import parking.admin.AdminLogin;
import parking.gui.LoginForm;
import parking.gui.SignupForm;

public class FirstPage extends JFrame {

    public FirstPage() {

        setTitle("Smart Parking - First Page");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Gradient Background
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                Color c1 = new Color(90, 70, 200);
                Color c2 = new Color(140, 100, 255);

                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(60));
        
        JLabel mainHeading = new JLabel("Smart Parking Finder System");
        mainHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainHeading.setForeground(Color.BLACK);
        mainHeading.setFont(new Font("Segoe UI", Font.BOLD, 50)); 
        panel.add(mainHeading);
        panel.add(Box.createVerticalStrut(20));

        //TITLE
        JLabel title = new JLabel("Find your perfect parking space in seconds...");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 43));
        panel.add(title);

        panel.add(Box.createVerticalStrut(15));

        JLabel subText = new JLabel("Smart, secure, and hassle-free parking management for your daily commute.");
        subText.setAlignmentX(Component.CENTER_ALIGNMENT);
        subText.setForeground(new Color(230, 230, 230));
        subText.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        panel.add(subText);

        panel.add(Box.createVerticalStrut(50));

        //FEATURES
        panel.add(createFeature("✔ Secure payments"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFeature("✔ Real-time availability"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFeature("✔ Instant booking confirmation"));

        panel.add(Box.createVerticalStrut(50));

        //2 COLUMN MAIN
        JPanel mainBox = new JPanel(new GridLayout(1, 2, 80, 0));
        mainBox.setOpaque(false);
        mainBox.setBorder(BorderFactory.createEmptyBorder(10, 150, 10, 150));

        //SIGNUP
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel l1 = new JLabel("If you don't have an account,");
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);
        l1.setForeground(Color.WHITE);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel l2 = new JLabel("create one...");
        l2.setAlignmentX(Component.CENTER_ALIGNMENT);
        l2.setForeground(Color.WHITE);
        l2.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JButton signupBtn = new JButton("Sign Up");
        styleButton(signupBtn);
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        signupBtn.addActionListener(e -> {
            dispose();
            new SignupForm();
        });
        
        Dimension btnSize = new Dimension(160, 45);

        signupBtn.setPreferredSize(btnSize);
        signupBtn.setMinimumSize(btnSize);
        signupBtn.setMaximumSize(btnSize);

        left.add(l1);
        left.add(Box.createVerticalStrut(5));
        left.add(l2);
        left.add(Box.createVerticalStrut(15));
        left.add(signupBtn);

        //LOGIN
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel r1 = new JLabel("Already have an account?");
        r1.setAlignmentX(Component.CENTER_ALIGNMENT);
        r1.setForeground(Color.WHITE);
        r1.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel r2 = new JLabel("How do you want to login?");
        r2.setAlignmentX(Component.CENTER_ALIGNMENT);
        r2.setForeground(Color.WHITE);
        r2.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton adminBtn = new JButton("Login as Admin");
        JButton userBtn = new JButton("Login as User");

        styleButton(adminBtn);
        styleButton(userBtn);

        adminBtn.addActionListener(e -> {
            dispose();
            new AdminLogin();
        });

        userBtn.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        btnRow.add(adminBtn);
        btnRow.add(userBtn);

        right.add(r1);
        right.add(Box.createVerticalStrut(5));
        right.add(r2);
        right.add(Box.createVerticalStrut(15));
        right.add(btnRow);

        mainBox.add(left);
        mainBox.add(right);

        panel.add(mainBox);

        add(panel);
        setVisible(true);
    }

    //Feature Label
    private JLabel createFeature(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        return label;
    }

    //Button Style
    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(90, 70, 200));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(200, 45));
    }

    public static void main(String[] args) {
        new FirstPage();
    }
}