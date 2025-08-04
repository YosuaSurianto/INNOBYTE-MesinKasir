package login.kasir;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import login.User;

public class KasirProfilePanel extends JPanel {

    // Inner class untuk border lengkung
    private static class RoundedBorder implements Border {
        private int radius;
        RoundedBorder(int radius) { this.radius = radius; }
        public Insets getBorderInsets(Component c) { return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius); }
        public boolean isBorderOpaque() { return true; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { g.drawRoundRect(x, y, width-1, height-1, radius, radius); }
    }

    public KasirProfilePanel(User user) {
        setBackground(Color.decode("#f0f0f0"));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JLabel titleLabel = new JLabel("Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            new RoundedBorder(15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);
        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font dataFont = new Font("Arial", Font.PLAIN, 18);

        // Menampilkan Data
        gbc.gridx = 0; gbc.gridy = 0;
        cardPanel.add(new JLabel("Nama :"), gbc);
        gbc.gridx = 1; cardPanel.add(new JLabel(user.getUsername()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        cardPanel.add(new JLabel("Role :"), gbc);
        gbc.gridx = 1; cardPanel.add(new JLabel(user.getRole().toString()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        cardPanel.add(new JLabel("ID :"), gbc);
        gbc.gridx = 1; cardPanel.add(new JLabel(user.getId()), gbc);

        add(cardPanel, BorderLayout.CENTER);
    }
}