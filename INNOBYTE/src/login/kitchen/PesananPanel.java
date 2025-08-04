package login.kitchen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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

public class PesananPanel extends JPanel {

    private JPanel gridPanel;

    public PesananPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Daftar Pesanan Aktif");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(titleLabel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        gridPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(gridPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);

        refreshPesanan();
    }

    public void refreshPesanan() {
        gridPanel.removeAll(); 

        try {
            // Ambil semua transaksi, lalu filter yang statusnya belum SELESAI
            List<Transaksi> pesananAktif = AppService.getTransaksiService().getTransaksi("Laporan All Time")
                .stream()
                .filter(trx -> trx.getStatus() != StatusPesanan.SELESAI)
                .collect(Collectors.toList());

            if (pesananAktif.isEmpty()) {
                gridPanel.setLayout(new BorderLayout());
                JLabel emptyLabel = new JLabel("Tidak ada pesanan aktif.", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Arial", Font.ITALIC, 18));
                gridPanel.add(emptyLabel, BorderLayout.CENTER);
            } else {
                gridPanel.setLayout(new GridLayout(0, 4, 20, 20));
                for (Transaksi trx : pesananAktif) {
                    JPanel card = createPesananCard(trx);
                    gridPanel.add(card);
                }
            }
        } catch (SQLException e) {
            gridPanel.setLayout(new BorderLayout());
            JLabel errorLabel = new JLabel("Gagal memuat data pesanan: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            gridPanel.add(errorLabel, BorderLayout.CENTER);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    private JPanel createPesananCard(Transaksi trx) {
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBackground(new Color(255, 253, 231));
        cardPanel.setBorder(new CompoundBorder(
            new LineBorder(Color.GRAY),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Panel untuk info (nama dan detail pesanan)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel namaLabel = new JLabel(trx.getNamaCustomer());
        namaLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        StringBuilder pesananText = new StringBuilder("<html><hr>");
        for(ItemPesanan item : trx.getItems()) {
            pesananText.append(item.getJumlah()).append("x ").append(item.getMenu().getNama()).append("<br>");
        }
        pesananText.append("</html>");
        JLabel detailPesananLabel = new JLabel(pesananText.toString());
        detailPesananLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        infoPanel.add(namaLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(detailPesananLabel);

        // Panel untuk status dan tombol aksi di bagian bawah
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel statusLabel = new JLabel(trx.getStatus().toString().replace("_", " "));
        statusLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));
        
        // Tentukan warna status dan tombol yang akan ditampilkan
        if (trx.getStatus() == StatusPesanan.BARU_MASUK) {
            statusLabel.setForeground(Color.BLUE);
            JButton mulaiMasakButton = new JButton("Mulai Masak");
            styleActionButton(mulaiMasakButton, new Color(230, 126, 34)); // Oranye
            mulaiMasakButton.addActionListener(e -> updateStatus(trx, StatusPesanan.SEDANG_DIMASAK));
            bottomPanel.add(mulaiMasakButton, BorderLayout.EAST);
        } else if (trx.getStatus() == StatusPesanan.SEDANG_DIMASAK) {
            statusLabel.setForeground(new Color(230, 126, 34));
            JButton selesaiButton = new JButton("Pesanan Selesai");
            styleActionButton(selesaiButton, new Color(39, 174, 96)); // Hijau
            selesaiButton.addActionListener(e -> updateStatus(trx, StatusPesanan.SELESAI));
            bottomPanel.add(selesaiButton, BorderLayout.EAST);
        }
        
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(bottomPanel, BorderLayout.SOUTH);

        return cardPanel;
    }

    // Metode helper untuk mengubah status
    private void updateStatus(Transaksi trx, StatusPesanan newStatus) {
        try {
            AppService.getTransaksiService().updateStatusPesanan(trx.getId(), newStatus);
            refreshPesanan();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Metode helper untuk styling tombol
    private void styleActionButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}