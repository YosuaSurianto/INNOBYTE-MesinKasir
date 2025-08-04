package login.kasir;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import login.LoginPage;
import login.User;

public class KasirDashboard extends JFrame implements ActionListener {

    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    
    // Tombol-tombol navigasi di sidebar
    private JButton transaksiButton;
    private JButton listPesananButton;
    private JButton logoutButton;
    
    // Panel untuk "List Pesanan"
    private ListPesananPanel listPesananPanel;

    // Nama konstanta untuk setiap panel
    private final static String TRANSAKSI_PANEL = "Transaksi";
    private final static String LIST_PESANAN_PANEL = "List Pesanan";

    public KasirDashboard(User user) {
        this.currentUser = user;
        
        setTitle("Aplikasi Kasir - " + currentUser.getUsername());
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        
        // Panel utama dengan CardLayout untuk ganti-ganti halaman
        mainContentPanel = new JPanel();
        cardLayout = new CardLayout();
        mainContentPanel.setLayout(cardLayout);
        
        // Membuat instance dari setiap halaman/panel

        JPanel pointOfSalePanel = new PointOfSalePanel(currentUser); 
        this.listPesananPanel = new ListPesananPanel();

        // Menambahkan panel-panel tersebut sebagai "kartu"
        mainContentPanel.add(pointOfSalePanel, TRANSAKSI_PANEL);
        mainContentPanel.add(this.listPesananPanel, LIST_PESANAN_PANEL);

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Tampilkan halaman transaksi sebagai default
        cardLayout.show(mainContentPanel, TRANSAKSI_PANEL);
    }

    // Metode untuk membuat sidebar
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 15));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(Color.decode("#212121"));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 20));
        
        // Panel untuk menu navigasi di bagian atas
        JPanel menuPanel = new JPanel(new GridLayout(10, 1, 0, 15));
        menuPanel.setOpaque(false); 

        JLabel title = new JLabel("Innobyte", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        JLabel kasirLabel = new JLabel("Kasir: " + currentUser.getUsername(), SwingConstants.CENTER);
        kasirLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        kasirLabel.setForeground(Color.WHITE);

        JLabel idLabel = new JLabel("ID: " + currentUser.getId(), SwingConstants.CENTER);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        idLabel.setForeground(Color.LIGHT_GRAY);

        menuPanel.add(title);
        menuPanel.add(kasirLabel);
        menuPanel.add(idLabel);
        menuPanel.add(new JSeparator());

        // Membuat tombol navigasi
        transaksiButton = createSidebarButton(TRANSAKSI_PANEL);
        listPesananButton = createSidebarButton(LIST_PESANAN_PANEL);
        menuPanel.add(transaksiButton);
        menuPanel.add(listPesananButton);

        // Panel bawah untuk logout dan copyright
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
        
        // Logika untuk berpindah halaman
        if (source == transaksiButton) {
            cardLayout.show(mainContentPanel, TRANSAKSI_PANEL);
        } else if (source == listPesananButton) {
            listPesananPanel.refreshData(); 
            cardLayout.show(mainContentPanel, LIST_PESANAN_PANEL);
        } else if (source == logoutButton) {
            int response = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin log out?", "Konfirmasi Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginPage().setVisible(true);
            }
        }
    }
}