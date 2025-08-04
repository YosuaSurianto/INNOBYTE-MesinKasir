package login.kitchen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import login.LoginPage;
import login.User;

public class KitchenDashboard extends JFrame implements ActionListener {

    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    
    // Tombol-tombol navigasi di sidebar
    private JButton pesananButton;
    private JButton logoutButton;
    
    // Panel untuk "Pesanan"
    private PesananPanel pesananPanel;

    // Nama konstanta untuk panel
    private final static String PESANAN_PANEL = "Pesanan";

    public KitchenDashboard(User user) {
        this.currentUser = user;
        
        setTitle("Aplikasi Dapur - " + currentUser.getUsername());
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        
        mainContentPanel = new JPanel();
        cardLayout = new CardLayout();
        mainContentPanel.setLayout(cardLayout);
        
        //instance dari panel pesanan dan menyimpannya
        this.pesananPanel = new PesananPanel();
        mainContentPanel.add(this.pesananPanel, PESANAN_PANEL);
        add(mainContentPanel, BorderLayout.CENTER);
        
        // panel pesanan sebagai halaman default
        cardLayout.show(mainContentPanel, PESANAN_PANEL);
    }

    // Metode untuk membuat sidebar
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 15));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(Color.decode("#212121"));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 20));
        
        // Panel untuk menu navigasi
        JPanel menuPanel = new JPanel(new GridLayout(10, 1, 0, 15));
        menuPanel.setOpaque(false);

        // Info Pengguna
        JLabel title = new JLabel("Innobyte", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        JLabel kitchenLabel = new JLabel("Dapur: " + currentUser.getUsername(), SwingConstants.CENTER);
        kitchenLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        kitchenLabel.setForeground(Color.WHITE);

        JLabel idLabel = new JLabel("ID: " + currentUser.getId(), SwingConstants.CENTER);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        idLabel.setForeground(Color.LIGHT_GRAY);

        menuPanel.add(title);
        menuPanel.add(kitchenLabel);
        menuPanel.add(idLabel);
        menuPanel.add(new JSeparator());

        //tombol navigasi "Pesanan"
        pesananButton = createSidebarButton(PESANAN_PANEL);
        menuPanel.add(pesananButton);

        // logout
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setOpaque(false);

        logoutButton = new JButton("Log Out");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(255, 87, 87));
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this);
        bottomPanel.add(logoutButton, BorderLayout.CENTER);

        JLabel copyrightLabel = new JLabel("© 2025 Yosua Surianto", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        copyrightLabel.setForeground(Color.GRAY);
        bottomPanel.add(copyrightLabel, BorderLayout.SOUTH);
        
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    // Metode helper untuk membuat tombol sidebar
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.decode("#212121"));
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(5, 20, 5, 0));
        button.addActionListener(this);
        return button;
    }

    // Metode untuk menangani semua klik tombol
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == pesananButton) {
            pesananPanel.refreshPesanan();
            cardLayout.show(mainContentPanel, PESANAN_PANEL);
        } else if (source == logoutButton) {
            int response = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin log out?", "Konfirmasi Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginPage().setVisible(true);
            }
        }
    }
}