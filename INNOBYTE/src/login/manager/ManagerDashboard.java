package login.manager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import login.LoginPage;
import login.User;
import login.manager.laporan.LaporanPanel;

public class ManagerDashboard extends JFrame implements ActionListener {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private User currentUser;
    private JButton profileButton, kelolaKaryawanButton, kelolaMenuButton, laporanButton, logoutButton;

    private final static String PROFILE_PANEL = "Profile";
    private final static String KARYAWAN_PANEL = "Kelola Karyawan";
    private final static String MENU_PANEL = "Kelola Menu";
    private final static String LAPORAN_PANEL = "Laporan";

    public ManagerDashboard(User user) {
        this.currentUser = user;
        
        setTitle("Dashboard Manager");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainContentPanel(), BorderLayout.CENTER);
        
        cardLayout.show(mainContentPanel, PROFILE_PANEL);
    }
    
    private JPanel createMainContentPanel() {
        mainContentPanel = new JPanel();
        cardLayout = new CardLayout();
        mainContentPanel.setLayout(cardLayout);

        mainContentPanel.add(new ProfilePanel(currentUser), PROFILE_PANEL);
        mainContentPanel.add(new KelolaKaryawanPanel(), KARYAWAN_PANEL);
        mainContentPanel.add(new KelolaMenuPanel(), MENU_PANEL);
        mainContentPanel.add(new LaporanPanel(), LAPORAN_PANEL);
        
        return mainContentPanel;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 15));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(Color.decode("#212121"));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 20));
        
        // Panel untuk menu navigasi
        JPanel menuPanel = new JPanel(new GridLayout(10, 1, 0, 15));
        menuPanel.setOpaque(false);

        JLabel title = new JLabel("Innobyte", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        menuPanel.add(title);
        menuPanel.add(new JSeparator());

        profileButton = createSidebarButton(PROFILE_PANEL);
        kelolaKaryawanButton = createSidebarButton(KARYAWAN_PANEL);
        kelolaMenuButton = createSidebarButton(MENU_PANEL);
        laporanButton = createSidebarButton(LAPORAN_PANEL);
        
        menuPanel.add(profileButton);
        menuPanel.add(kelolaKaryawanButton);
        menuPanel.add(kelolaMenuButton);
        menuPanel.add(laporanButton);
        
        // bagian bawah (logout)
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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == profileButton) {
            cardLayout.show(mainContentPanel, PROFILE_PANEL);
        } else if (source == kelolaKaryawanButton) {
            cardLayout.show(mainContentPanel, KARYAWAN_PANEL);
        } else if (source == kelolaMenuButton) {
            cardLayout.show(mainContentPanel, MENU_PANEL);
        } else if (source == laporanButton) {
            cardLayout.show(mainContentPanel, LAPORAN_PANEL);
        } else if (source == logoutButton) {
            int response = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin log out?", "Konfirmasi Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginPage().setVisible(true);
            }
        }
    }
}