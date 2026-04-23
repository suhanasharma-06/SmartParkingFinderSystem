package parking.gui;

import parking.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class NotificationsPage extends JFrame {

    private String userEmail;
    private JPanel notificationsPanel;
    private JScrollPane scrollPane;

    public NotificationsPage(String userEmail) {
        this.userEmail = userEmail;

        setTitle("Notifications");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Back Button 
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setPreferredSize(new Dimension(80, 33));
        backBtn.setFocusPainted(false);
        backBtn.setBackground(new Color(100, 100, 255));
        backBtn.setForeground(Color.WHITE);

        backBtn.addActionListener(e -> {
            this.dispose();
            new MyBookings(userEmail);
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(backBtn);

        //Title 
        JLabel title = new JLabel("Notifications", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        JLabel leftSpacer = new JLabel("      ");

        topPanel.add(leftSpacer, BorderLayout.WEST);
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        //Notifications panel
        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        notificationsPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(notificationsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        loadNotifications();

        setVisible(true);
    }

    private void loadNotifications() {
        notificationsPanel.removeAll();

        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM notifications WHERE user_email=? ORDER BY timestamp DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, userEmail);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
            	int notifId = rs.getInt("id");
                String message = rs.getString("message");
                String location = rs.getString("location");
                String token = rs.getString("token");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                int readStatus = rs.getInt("read_status");
                String slot = rs.getString("slot");
                Timestamp start = rs.getTimestamp("start_time");
                Timestamp end = rs.getTimestamp("end_time");

                //Notification box
                JPanel box = new JPanel();
                box.setLayout(new BorderLayout());
                box.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                box.setBackground(readStatus == 0 ? new Color(230, 230, 250) : Color.WHITE);
                box.setMaximumSize(new Dimension(550, 100)); // width & height
                box.setAlignmentX(Component.CENTER_ALIGNMENT);

                //Rounded corners
                box.setOpaque(true);
                box.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));

                //Message
                JLabel msgLabel = new JLabel(
                	    "<html><b>" + message + "</b><br>" +
                	    "Location: " + location +
                	    " | Slot: " + slot +
                	    " | Token: " + token +
                	    "<br>From: " + start +
                	    " To: " + end +
                	    "</html>"
                	);
                msgLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JPanel bottomPanel = new JPanel(new BorderLayout());
                bottomPanel.setOpaque(false);
                JLabel timeLabel = new JLabel(getTimeAgo(timestamp));
                timeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                JButton markReadBtn = new JButton(readStatus == 0 ? "Mark as Read" : "Read");
                markReadBtn.setEnabled(readStatus == 0);
                markReadBtn.setFont(new Font("Arial", Font.PLAIN, 12));
                markReadBtn.setFocusPainted(false);
                markReadBtn.setBackground(new Color(180, 180, 255));
                markReadBtn.setForeground(Color.BLACK);
                markReadBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                markReadBtn.addActionListener(e -> {
                    markAsRead(notifId, box, markReadBtn);
                });

                bottomPanel.add(timeLabel, BorderLayout.WEST);
                bottomPanel.add(markReadBtn, BorderLayout.EAST);

                box.add(msgLabel, BorderLayout.CENTER);
                box.add(bottomPanel, BorderLayout.SOUTH);

                notificationsPanel.add(Box.createVerticalStrut(10));
                notificationsPanel.add(box);
            }

            notificationsPanel.revalidate();
            notificationsPanel.repaint();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void markAsRead(int id, JPanel box, JButton btn) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE notifications SET read_status=1 WHERE id=?"
            );
            ps.setInt(1, id);
            ps.executeUpdate();

            box.setBackground(Color.WHITE);

            //Button change
            btn.setText("Read");
            btn.setEnabled(false);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getTimeAgo(Timestamp timestamp) {
        long milliseconds = System.currentTimeMillis() - timestamp.getTime();
        long minutes = milliseconds / (1000 * 60);
        if(minutes < 60) return minutes + " minutes ago";
        long hours = minutes / 60;
        if(hours < 24) return hours + " hours ago";
        long days = hours / 24;
        return days + " days ago";
    }
    public static void main(String[] args) {
        new NotificationsPage("user@example.com");
    }
}


