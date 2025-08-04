package login.manager;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import login.User; // Import kelas User

public class ProfilePanel extends JPanel {

    /**
     * Inner class untuk membuat border dengan sudut melengkung.
     * Ini trik untuk meniru desain modern di Swing.
     */
    private static class RoundedBorder implements Border {
        private int radius;
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }
        public boolean isBorderOpaque() {
            return true;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    // Constructor untuk menerima objek User
    public ProfilePanel(User user) {
        // --- Setup Panel Utama ---
        setBackground(Color.decode("#f0f0f0")); // Warna latar belakang abu-abu muda
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40)); // Padding keliling

        // "Identity" di Atas 
        JLabel titleLabel = new JLabel("Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        // --- Panel Kartu Putih di Tengah ---
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        // Memberi border lengkung dan bayangan (sebagai padding)
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20), // Padding luar
            new RoundedBorder(15) // Border lengkung
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20); // Jarak antar komponen
        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font dataFont = new Font("Arial", Font.PLAIN, 18);

        // --- Menampilkan Data ---
        // NAMA
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel namaLabel = new JLabel("Nama :");
        namaLabel.setFont(labelFont);
        cardPanel.add(namaLabel, gbc);

        gbc.gridx = 1;
        JLabel namaData = new JLabel(user.getUsername()); // Ambil data dari objek User
        namaData.setFont(dataFont);
        cardPanel.add(namaData, gbc);
        
        // ROLE
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel roleLabel = new JLabel("Role :");
        roleLabel.setFont(labelFont);
        cardPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        JLabel roleData = new JLabel(user.getRole().toString()); // Ambil data dari objek User
        roleData.setFont(dataFont);
        cardPanel.add(roleData, gbc);

        // ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel idLabel = new JLabel("ID :");
        idLabel.setFont(labelFont);
        cardPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        JLabel idData = new JLabel(user.getId()); // Ambil data dari objek User
        idData.setFont(dataFont);
        cardPanel.add(idData, gbc);

        add(cardPanel, BorderLayout.CENTER);
    }
}