//package parking.gui;
//
//import parking.database.DBConnection;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.sql.*;
//
//public class FeedbackPage extends JFrame {
//
//    JComboBox<String> typeBox;
//    JTextArea commentArea;
//    JLabel disclaimerLabel;
//    ButtonGroup ratingGroup;
//    String selectedRating = "";
//
//    String userEmail;
//
//    public FeedbackPage(String email) {
//        this.userEmail = email;
//
//        setTitle("Feedback");
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//
//        JPanel bg = new JPanel();
//        bg.setBackground(new Color(245, 247, 255));
//        bg.setLayout(null);
//
//        //TITLE 
//        JLabel title = new JLabel("Feedback & Support");
//        title.setFont(new Font("Arial", Font.BOLD, 32));
//        title.setBounds(250, 40, 500, 40);
//        bg.add(title);
//
//        JLabel sub = new JLabel("Help us improve the ParkSpace experience.");
//        sub.setFont(new Font("Arial", Font.PLAIN, 16));
//        sub.setForeground(Color.GRAY);
//        sub.setBounds(250, 80, 500, 25);
//        bg.add(sub);
//
//        //CARD
//        JPanel card = new JPanel();
//        card.setBackground(Color.WHITE);
//        card.setLayout(null);
//        card.setBorder(new CompoundBorder(
//                new LineBorder(new Color(220,220,220), 1, true),
//                new EmptyBorder(10,20,20,20)
//        ));
//
//        int cardWidth = 775;
//        int cardHeight = 535;
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        int x = (screenSize.width - cardWidth) / 2;
//        int y = (screenSize.height - cardHeight) / 2 + 20;
//
//        card.setBounds(x, y, cardWidth, cardHeight);
//
//        Font labelFont = new Font("Arial", Font.BOLD, 16);
//
//        //Feedback Type
//        JLabel typeLabel = new JLabel("Feedback Type");
//        typeLabel.setFont(labelFont);
//        typeLabel.setBounds(30, 20, 200, 25);
//        card.add(typeLabel);
//
//        String[] types = {"General Rating", "Feature Suggestion", "File a Complaint"};
//        typeBox = new JComboBox<>(types);
//        typeBox.setBounds(30, 50, 700, 45);
//        styleBox(typeBox);
//        card.add(typeBox);
//        
//        typeBox.addActionListener(e -> {
//            String selected = (String) typeBox.getSelectedItem();
//
//            if (selected.equals("File a Complaint")) {
//                disclaimerLabel.setVisible(true);
//            } else {
//                disclaimerLabel.setVisible(false);
//            }
//        });
//        
//        disclaimerLabel = new JLabel("<html><font color='red'>Notice: False complaints against parking locations or staff may result in account suspension. Please provide accurate details.</font></html>");
//        disclaimerLabel.setFont(new Font("Arial", Font.PLAIN, 13));
//        disclaimerLabel.setBounds(30, 445, 715, 30);
//        disclaimerLabel.setVisible(false); 
//        card.add(disclaimerLabel);
//
//        //Rating
//        JLabel rateLabel = new JLabel("Rating");
//        rateLabel.setFont(labelFont);
//        rateLabel.setBounds(30, 120, 200, 25);
//        card.add(rateLabel);
//
//        String[] ratings = {"Excellent", "Very Good", "Good", "Average", "Poor"};
//        ratingGroup = new ButtonGroup();
//
//        int yPos = 145;
//        for (String r : ratings) {
//            JRadioButton rb = new JRadioButton(r);
//            rb.setBounds(30, yPos, 200, 25);
//            rb.setBackground(Color.WHITE);
//            rb.setFont(new Font("Arial", Font.PLAIN, 15));
//
//            rb.addActionListener(e -> selectedRating = r);
//
//            ratingGroup.add(rb);
//            card.add(rb);
//
//            yPos += 30;
//        }
//
//        //Comments
//        JLabel commentLabel = new JLabel("Comments");
//        commentLabel.setFont(labelFont);
//        commentLabel.setBounds(30, 300, 200, 25);
//        card.add(commentLabel);
//
//        commentArea = new JTextArea("Tell us more about your experience...");
//        commentArea.setForeground(Color.GRAY);
//        commentArea.setFont(new Font("Arial", Font.PLAIN, 15));
//        commentArea.setLineWrap(true);
//        commentArea.setWrapStyleWord(true);
//
//        commentArea.addFocusListener(new FocusAdapter() {
//            public void focusGained(FocusEvent e) {
//                if (commentArea.getText().equals("Tell us more about your experience...")) {
//                    commentArea.setText("");
//                    commentArea.setForeground(Color.BLACK);
//                }
//            }
//
//            public void focusLost(FocusEvent e) {
//                if (commentArea.getText().isEmpty()) {
//                    commentArea.setText("Tell us more about your experience...");
//                    commentArea.setForeground(Color.GRAY);
//                }
//            }
//        });
//
//        JScrollPane scroll = new JScrollPane(commentArea);
//        scroll.setBounds(30, 330, 715, 100);
//        scroll.setBorder(new LineBorder(new Color(200,200,200),1,true));
//        card.add(scroll);
//
//        //Submit Button
//        JButton submitBtn = new JButton("Submit Feedback");
//        submitBtn.setBounds(530, 470, 200, 45);
//        submitBtn.setBackground(new Color(120, 80, 220));
//        submitBtn.setForeground(Color.WHITE);
//        submitBtn.setFont(new Font("Arial", Font.BOLD, 16));
//        submitBtn.setFocusPainted(false);
//        card.add(submitBtn);
//
//        bg.add(card);
//        add(bg);
//
//        submitBtn.addActionListener(e -> saveFeedback());
//
//        setVisible(true);
//    }
//
//    private void styleBox(JComboBox box) {
//        box.setFont(new Font("Arial", Font.PLAIN, 15));
//        box.setBorder(new LineBorder(new Color(200,200,200), 1, true));
//    }
//
//    private void saveFeedback() {
//        String type = (String) typeBox.getSelectedItem();
//        String comment = commentArea.getText();
//
//        if (selectedRating.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please select rating!");
//            return;
//        }
//
//        if (comment.equals("Tell us more about your experience...") || comment.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please write your feedback!");
//            return;
//        }
//
//        try {
//            Connection con = DBConnection.getConnection();
//
//            String query = "INSERT INTO feedback (user_email, feedback_type, rating, comment, status) VALUES (?, ?, ?, ?, ?)";
//            PreparedStatement ps = con.prepareStatement(query);
//
//            ps.setString(1, userEmail);
//            ps.setString(2, type);
//            ps.setString(3, selectedRating);
//            ps.setString(4, comment);
//            ps.setString(5, "PENDING");   
//
//            ps.executeUpdate();
//
//            JOptionPane.showMessageDialog(this, "Feedback Submitted Successfully!");
//
//            commentArea.setText("Tell us more about your experience...");
//            commentArea.setForeground(Color.GRAY);
//            ratingGroup.clearSelection();
//            selectedRating = "";
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    public static void main(String[] args) {
//        new FeedbackPage("user@example.com");
//    }
//}


















package parking.gui;

import parking.database.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FeedbackPage extends JFrame {

    JComboBox<String> typeBox;
    JTextArea commentArea;
    JLabel disclaimerLabel;
    ButtonGroup ratingGroup;
    String selectedRating = "";

    String userEmail;
    String locationName;

    public FeedbackPage(String email, String locationName) {
        this.userEmail = email;
        this.locationName = locationName;

        setTitle("Feedback");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel bg = new JPanel();
        bg.setBackground(new Color(245, 247, 255));
        bg.setLayout(null);

        //TITLE 
        JLabel title = new JLabel("Feedback & Support");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setBounds(250, 40, 500, 40);
        bg.add(title);

        JLabel sub = new JLabel("Help us improve the ParkSpace experience.");
        sub.setFont(new Font("Arial", Font.PLAIN, 16));
        sub.setForeground(Color.GRAY);
        sub.setBounds(250, 80, 500, 25);
        bg.add(sub);
        
        JButton backBtn = new JButton("Back");
        
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(100, 80, 220));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setBounds(1150, 30, 100, 35);

        // hover effect
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backBtn.setBackground(new Color(120, 100, 240));
            }

            public void mouseExited(MouseEvent e) {
                backBtn.setBackground(new Color(100, 80, 220));
            }
        });
      
        backBtn.addActionListener(e -> {
            dispose();
            new ViewSlots(locationName, userEmail);
        });

        bg.add(backBtn);

        //CARD
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220), 1, true),
                new EmptyBorder(10,20,20,20)
        ));

        int cardWidth = 775;
        int cardHeight = 535;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - cardWidth) / 2;
        int y = (screenSize.height - cardHeight) / 2 + 20;

        card.setBounds(x, y, cardWidth, cardHeight);

        Font labelFont = new Font("Arial", Font.BOLD, 16);

        //Feedback Type
        JLabel typeLabel = new JLabel("Feedback Type");
        typeLabel.setFont(labelFont);
        typeLabel.setBounds(30, 20, 200, 25);
        card.add(typeLabel);

        String[] types = {"General Rating", "Feature Suggestion", "File a Complaint"};
        typeBox = new JComboBox<>(types);
        typeBox.setBounds(30, 50, 700, 45);
        styleBox(typeBox);
        card.add(typeBox);
        
        typeBox.addActionListener(e -> {
            String selected = (String) typeBox.getSelectedItem();

            if (selected.equals("File a Complaint")) {
                disclaimerLabel.setVisible(true);
            } else {
                disclaimerLabel.setVisible(false);
            }
        });
        
        disclaimerLabel = new JLabel("<html><font color='red'>Notice: False complaints against parking locations or staff may result in account suspension. Please provide accurate details.</font></html>");
        disclaimerLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        disclaimerLabel.setBounds(30, 445, 715, 30);
        disclaimerLabel.setVisible(false); 
        card.add(disclaimerLabel);

        //Rating
        JLabel rateLabel = new JLabel("Rating");
        rateLabel.setFont(labelFont);
        rateLabel.setBounds(30, 120, 200, 25);
        card.add(rateLabel);

        String[] ratings = {"Excellent", "Very Good", "Good", "Average", "Poor"};
        ratingGroup = new ButtonGroup();

        int yPos = 145;
        for (String r : ratings) {
            JRadioButton rb = new JRadioButton(r);
            rb.setBounds(30, yPos, 200, 25);
            rb.setBackground(Color.WHITE);
            rb.setFont(new Font("Arial", Font.PLAIN, 15));

            rb.addActionListener(e -> selectedRating = r);

            ratingGroup.add(rb);
            card.add(rb);

            yPos += 30;
        }

        //Comments
        JLabel commentLabel = new JLabel("Comments");
        commentLabel.setFont(labelFont);
        commentLabel.setBounds(30, 300, 200, 25);
        card.add(commentLabel);

        commentArea = new JTextArea("Tell us more about your experience...");
        commentArea.setForeground(Color.GRAY);
        commentArea.setFont(new Font("Arial", Font.PLAIN, 15));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);

        commentArea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (commentArea.getText().equals("Tell us more about your experience...")) {
                    commentArea.setText("");
                    commentArea.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (commentArea.getText().isEmpty()) {
                    commentArea.setText("Tell us more about your experience...");
                    commentArea.setForeground(Color.GRAY);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(commentArea);
        scroll.setBounds(30, 330, 715, 100);
        scroll.setBorder(new LineBorder(new Color(200,200,200),1,true));
        card.add(scroll);

        //Submit Button
        JButton submitBtn = new JButton("Submit Feedback");
        submitBtn.setBounds(530, 470, 200, 45);
        submitBtn.setBackground(new Color(120, 80, 220));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        submitBtn.setFocusPainted(false);
        card.add(submitBtn);

        bg.add(card);
        add(bg);

        submitBtn.addActionListener(e -> saveFeedback());

        setVisible(true);
    }

    private void styleBox(JComboBox box) {
        box.setFont(new Font("Arial", Font.PLAIN, 15));
        box.setBorder(new LineBorder(new Color(200,200,200), 1, true));
    }

    private void saveFeedback() {
        String type = (String) typeBox.getSelectedItem();
        String comment = commentArea.getText();

        if (selectedRating.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select rating!");
            return;
        }

        if (comment.equals("Tell us more about your experience...") || comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please write your feedback!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO feedback (user_email, feedback_type, rating, comment, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, userEmail);
            ps.setString(2, type);
            ps.setString(3, selectedRating);
            ps.setString(4, comment);
            ps.setString(5, "PENDING");   

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Feedback Submitted Successfully!");

            commentArea.setText("Tell us more about your experience...");
            commentArea.setForeground(Color.GRAY);
            ratingGroup.clearSelection();
            selectedRating = "";

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new FeedbackPage("user@example.com","Default Location");
    }
}