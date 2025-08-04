package login;

import java.awt.*;
import java.io.InputStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import login.kasir.KasirDashboard;
import login.kitchen.KitchenDashboard;
import login.manager.ManagerDashboard;

public class LoginPage extends JFrame {

    private Font customFont;
    private JTextField userText;
    private JPasswordField passText;
    private JButton loginButton;

    public LoginPage() {
        setTitle("Application Login");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        loadCustomFont();

        // Panel utama sekarang menggunakan BorderLayout untuk menempatkan copyright
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#212121"));
        add(mainPanel);

        // Panel khusus untuk form login agar tetap di tengah
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Buat transparan
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 40, 10, 40);

        JLabel titleLabel = new JLabel("LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(customFont);
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 40, 40, 40);
        formPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 40, 5, 40);
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        formPanel.add(userLabel, gbc);

        userText = new JTextField(20);
        styleTextField(userText);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 20, 40);
        formPanel.add(userText, gbc);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.LIGHT_GRAY);
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 40, 5, 40);
        formPanel.add(passLabel, gbc);

        passText = new JPasswordField(20);
        styleTextField(passText);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 40, 40, 40);
        formPanel.add(passText, gbc);

        loginButton = new JButton("login");
        styleLoginButton(loginButton);
        gbc.gridy = 5;
        gbc.ipady = 15;
        gbc.insets = new Insets(10, 40, 20, 40);
        formPanel.add(loginButton, gbc);

        // Menambahkan form ke tengah panel utama
        mainPanel.add(formPanel, BorderLayout.CENTER);

        //  label copyright 
        JLabel copyrightLabel = new JLabel("© 2025 Yosua Surianto", SwingConstants.LEFT);
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        copyrightLabel.setForeground(Color.GRAY);
        copyrightLabel.setBorder(new EmptyBorder(0, 10, 5, 0));
        mainPanel.add(copyrightLabel, BorderLayout.SOUTH);

        addLoginAction();
    }

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/AutourOne-Regular.ttf");
            if (is == null) {
                System.err.println("Font 'AutourOne-Regular.ttf' tidak ditemukan.");
                customFont = new Font("Arial", Font.BOLD, 50);
                return;
            }
            customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 50f);
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 50);
        }
    }
    
    private void styleTextField(JTextField textField) {
        textField.setBackground(Color.decode("#212121"));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

    private void styleLoginButton(JButton button) {
        button.setBackground(Color.decode("#4A47A3"));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addLoginAction() {
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());
            
            User authenticatedUser = AppService.getAuthenticationService().authenticate(username, password);

            if (authenticatedUser != null) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Login Berhasil! Selamat datang " + authenticatedUser.getUsername() + " !", 
                    "Login Sukses", 
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                dispose(); 

                switch (authenticatedUser.getRole()) {
                    case MANAGER_FBM:
                        new ManagerDashboard(authenticatedUser).setVisible(true);
                        break;
                    case KASIR:
                        new KasirDashboard(authenticatedUser).setVisible(true);
                        break;
                    case KITCHEN:
                        new KitchenDashboard(authenticatedUser).setVisible(true);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah.", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}