package parking.admin;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class AdminFeedbackPage extends JFrame {

    JPanel mainPanel;
    JTextField searchField;

    Color primaryPurple = new Color(100, 80, 220);
    Color bgTop = new Color(245, 247, 255);
    Color bgBottom = new Color(230, 235, 250);

    public AdminFeedbackPage() {

        setTitle("Feedback & Complaints - Admin");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //BACKGROUND
        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0, 0, bgTop,
                        0, getHeight(), bgBottom
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        bg.setLayout(new BorderLayout(20, 20));
        bg.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(bg);

        //TITLE 
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Feedback & Complaints");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(new Color(60, 60, 60));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Manage all user feedback & complaints");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setForeground(Color.GRAY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(sub);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(100, 80, 230));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        topBar.add(titlePanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(backBtn);

        topBar.add(rightPanel, BorderLayout.EAST);

        bg.add(topBar, BorderLayout.NORTH);

        //MAIN PANEL
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bgTop);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.getViewport().setBackground(bgTop);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        bg.add(scroll, BorderLayout.CENTER);

        loadFeedback();
        backBtn.addActionListener(e -> {
            new AdminDashboard();   
            dispose();              
        });

        setVisible(true);
    }

    //LOAD FEEDBACK
    private void loadFeedback() {
    	mainPanel.removeAll();

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM feedback ORDER BY id DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery(); mainPanel.removeAll();

            while (rs.next()) {

                JPanel card = createCard(
                        rs.getInt("id"),
                        rs.getString("user_email"),
                        rs.getString("feedback_type"),
                        rs.getString("rating"),
                        rs.getString("comment"),
                        rs.getString("timestamp"),
                        rs.getString("status")
                );

                mainPanel.add(card);
                mainPanel.add(Box.createVerticalStrut(15));
            }

            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //CARD UI 
    private JPanel createCard(int id, String email, String type, String rating,
                              String comment, String date, String statusValue) {

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(1100, 170));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        //TYPE LABEL
        JLabel typeLabel = new JLabel(type.toUpperCase());
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        typeLabel.setBounds(20, 15, 150, 25);
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabel.setOpaque(true);
        typeLabel.setBackground(new Color(235, 235, 255));
        typeLabel.setBorder(new LineBorder(primaryPurple));
        card.add(typeLabel);

        //STATUS LABEL
        JLabel status = new JLabel(statusValue == null ? "PENDING" : statusValue.toUpperCase());
        status.setFont(new Font("Segoe UI", Font.BOLD, 12));
        status.setBounds(1040, 45, 110, 25);
        status.setHorizontalAlignment(SwingConstants.CENTER);
        status.setOpaque(true);

        if ("APPROVED".equalsIgnoreCase(statusValue)) {
            status.setBackground(new Color(46, 204, 113));
            status.setForeground(Color.WHITE);
        } else if ("REJECTED".equalsIgnoreCase(statusValue)) {
            status.setBackground(new Color(231, 76, 60));
            status.setForeground(Color.WHITE);
        } else {
            status.setBackground(new Color(255, 140, 0));
            status.setForeground(Color.WHITE);
        }

        card.add(status);

        //EMAIL
        JLabel emailLabel = new JLabel("User: " + email);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setBounds(20, 50, 500, 20);
        card.add(emailLabel);

        //RATING
        JLabel ratingLabel = new JLabel("Rating: " + rating + " ⭐");
        ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ratingLabel.setBounds(20, 75, 200, 20);
        card.add(ratingLabel);

        //COMMENT
        JTextArea commentArea = new JTextArea(comment);
        commentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commentArea.setWrapStyleWord(true);
        commentArea.setLineWrap(true);
        commentArea.setEditable(false);

        JScrollPane sp = new JScrollPane(commentArea);
        sp.setBounds(20, 100, 850, 55);
        sp.setBorder(new LineBorder(new Color(230, 230, 230)));
        card.add(sp);

        //DATE
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setBounds(880, 140, 200, 20);
        card.add(dateLabel);

        //APPROVE BUTTON
        JButton approve = createButton("Approve", new Color(46, 204, 113));
        approve.setBounds(990, 85, 90, 28);
        card.add(approve);

        //REJECT BUTTON
        JButton reject = createButton("Reject", new Color(231, 76, 60));
        reject.setBounds(1090, 85, 90, 28);
        card.add(reject);

        //ACTIONS
        approve.addActionListener(e -> updateStatus(id, "APPROVED"));
        reject.addActionListener(e -> updateStatus(id, "REJECTED"));

        return card;
    }

    //UPDATE STATUS
    private void updateStatus(int id, String status) {

        try {
            Connection con = DBConnection.getConnection();

            String sql = "UPDATE feedback SET status=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, status);
            ps.setInt(2, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Updated to " + status);

            loadFeedback();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //BUTTON STYLE 
    private JButton createButton(String text, Color color) {

        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    public static void main(String[] args) {
        new AdminFeedbackPage();
    }
}