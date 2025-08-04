package login.kasir;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import login.AppService;
import login.model.ItemPesanan;
import login.model.StatusPesanan;
import login.model.Transaksi;

public class ListPesananPanel extends JPanel {

    private JPanel gridPanel;

    public ListPesananPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("List Pesanan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(titleLabel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(gridPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        gridPanel.removeAll(); 

        try {
            List<Transaksi> daftarTransaksi = AppService.getTransaksiService().getTransaksi("Laporan All Time");

            if (daftarTransaksi.isEmpty()) {
                gridPanel.setLayout(new BorderLayout());
                JLabel emptyLabel = new JLabel("Belum ada pesanan.", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Arial", Font.ITALIC, 18));
                gridPanel.add(emptyLabel, BorderLayout.CENTER);
            } else {
                gridPanel.setLayout(new GridLayout(0, 3, 20, 20));
                for (Transaksi trx : daftarTransaksi) {
                    JPanel card = createPesananCard(trx);
                    gridPanel.add(card);
                }
            }
        } catch (SQLException e) {
            gridPanel.setLayout(new BorderLayout());
            JLabel errorLabel = new JLabel("Gagal memuat data: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            gridPanel.add(errorLabel, BorderLayout.CENTER);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    private JPanel createPesananCard(Transaksi trx) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(245, 245, 245));
        cardPanel.setBorder(new CompoundBorder(
            new LineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel namaLabel = new JLabel("Nama: " + trx.getNamaCustomer());
        namaLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel statusLabel = new JLabel("Status: " + trx.getStatus().toString().replace("_", " "));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        
        if(trx.getStatus() == StatusPesanan.BARU_MASUK) {
            statusLabel.setForeground(Color.BLUE);
        } else if (trx.getStatus() == StatusPesanan.SEDANG_DIMASAK) {
            statusLabel.setForeground(new Color(230, 126, 34)); // Orange
        } else {
            statusLabel.setForeground(new Color(39, 174, 96)); // Green
        }

        JButton lihatButton = new JButton("Lihat Pesanan");
        lihatButton.setBackground(new Color(52, 152, 219));
        lihatButton.setForeground(Color.WHITE);
        lihatButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        lihatButton.addActionListener(e -> {
            StringBuilder details = new StringBuilder("<html><b>Detail Pesanan untuk " + trx.getNamaCustomer() + "</b><br><hr>");
            for (ItemPesanan item : trx.getItems()) {
                details.append(item.getJumlah()).append("x ").append(item.getMenu().getNama()).append("<br>");
            }
            details.append("</html>");
            JOptionPane.showMessageDialog(this, new JLabel(details.toString()), "Rincian Pesanan", JOptionPane.INFORMATION_MESSAGE);
        });

        namaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lihatButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cardPanel.add(namaLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(statusLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(lihatButton);

        return cardPanel;
    }
}