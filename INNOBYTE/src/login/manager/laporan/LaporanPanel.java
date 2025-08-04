package login.manager.laporan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import login.AppService;
import login.manager.CustomTableCellRenderer;
import login.model.ItemPesanan;
import login.model.Menu;
import login.model.Transaksi;

public class LaporanPanel extends JPanel {

    private JComboBox<String> filterComboBox;
    private JButton tampilkanButton;
    private JLabel totalPendapatanLabel, jumlahTransaksiLabel, menuTerlarisLabel;
    private JTable ringkasanMenuTable, riwayatTransaksiTable;
    private DefaultTableModel ringkasanMenuTableModel, riwayatTransaksiTableModel;
    private NumberFormat currencyFormatter;
    private JTabbedPane tabbedPane;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> ringkasanSorter;
    private TableRowSorter<DefaultTableModel> riwayatSorter;

    public LaporanPanel() {
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        setLayout(new BorderLayout(10, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 30, 30, 30));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);

        addListeners();
        updateLaporan();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Laporan Penjualan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new BorderLayout(20, 0));
        filterPanel.setBackground(Color.WHITE);
        
        JPanel leftFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilterPanel.setBackground(Color.WHITE);
        String[] filterOptions = {"Laporan Harian", "Laporan Bulanan", "Laporan All Time"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        tampilkanButton = new JButton("Tampilkan");
        leftFilterPanel.add(new JLabel("Pilih Jangka Waktu:"));
        leftFilterPanel.add(filterComboBox);
        leftFilterPanel.add(tampilkanButton);
        
        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        addPlaceholderStyle(searchField, "Cari Nama Menu / ID Transaksi / Kasir...");
        
        filterPanel.add(leftFilterPanel, BorderLayout.WEST);
        filterPanel.add(searchField, BorderLayout.EAST);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        return topPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        mainPanel.setBackground(Color.WHITE);
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(Color.WHITE);
        totalPendapatanLabel = new JLabel("Rp 0", SwingConstants.CENTER);
        jumlahTransaksiLabel = new JLabel("0 Transaksi", SwingConstants.CENTER);
        menuTerlarisLabel = new JLabel("-", SwingConstants.CENTER);
        summaryPanel.add(createSummaryCard("Total Pendapatan", totalPendapatanLabel));
        summaryPanel.add(createSummaryCard("Jumlah Transaksi", jumlahTransaksiLabel));
        summaryPanel.add(createSummaryCard("Menu Terlaris", menuTerlarisLabel));
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        String[] ringkasanMenuColumns = {"ID Menu", "Nama Menu", "Jumlah Terjual", "Total Pemasukan"};
        ringkasanMenuTableModel = new DefaultTableModel(ringkasanMenuColumns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        ringkasanMenuTable = new JTable(ringkasanMenuTableModel);
        styleTable(ringkasanMenuTable);
        ringkasanSorter = new TableRowSorter<>(ringkasanMenuTableModel);
        ringkasanMenuTable.setRowSorter(ringkasanSorter);
        tabbedPane.addTab("Ringkasan per Menu", new JScrollPane(ringkasanMenuTable));

        String[] riwayatTransaksiColumns = {"ID Transaksi", "Waktu", "Nama Customer", "Kasir", "Total Harga"};
        riwayatTransaksiTableModel = new DefaultTableModel(riwayatTransaksiColumns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        riwayatTransaksiTable = new JTable(riwayatTransaksiTableModel);
        styleTable(riwayatTransaksiTable);
        riwayatSorter = new TableRowSorter<>(riwayatTransaksiTableModel);
        riwayatTransaksiTable.setRowSorter(riwayatSorter);
        tabbedPane.addTab("Riwayat Transaksi", new JScrollPane(riwayatTransaksiTable));
        
        mainPanel.add(summaryPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void addListeners() {
        tampilkanButton.addActionListener(e -> updateLaporan());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTables(); }
            public void removeUpdate(DocumentEvent e) { filterTables(); }
            public void changedUpdate(DocumentEvent e) { filterTables(); }
        });
    }
    
    private void filterTables() {
        String text = searchField.getText();
        if (text.equals("Cari Nama Menu / ID Transaksi / Kasir...") || text.trim().length() == 0) {
            ringkasanSorter.setRowFilter(null);
            riwayatSorter.setRowFilter(null);
        } else {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String query = "(?i)" + text;
            if (selectedIndex == 0) {
                ringkasanSorter.setRowFilter(RowFilter.regexFilter(query, 1));
            } else if (selectedIndex == 1) {
                riwayatSorter.setRowFilter(RowFilter.regexFilter(query, 0, 2, 3));
            }
        }
    }

    private JPanel createSummaryCard(String title, JLabel contentLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(contentLabel, BorderLayout.CENTER);
        return card;
    }
    
    private void updateLaporan() {
        try {
            String selectedFilter = (String) filterComboBox.getSelectedItem();
            List<Transaksi> transaksiList = AppService.getTransaksiService().getTransaksi(selectedFilter);

            System.out.println("selectedFilter: " + selectedFilter);
            
            updateSummaryCards(transaksiList);
            updateTabelRingkasanMenu(transaksiList);
            updateTabelRiwayatTransaksi(transaksiList);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data laporan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSummaryCards(List<Transaksi> transaksiList) {
        int totalPendapatan = transaksiList.stream().mapToInt(Transaksi::getTotal).sum();
        totalPendapatanLabel.setText(currencyFormatter.format(totalPendapatan));
        jumlahTransaksiLabel.setText(transaksiList.size() + " Transaksi");
        menuTerlarisLabel.setText(hitungMenuTerlaris(transaksiList));
    }

    private String hitungMenuTerlaris(List<Transaksi> transaksiList) {
        if (transaksiList.isEmpty()) return "-";
        return transaksiList.stream()
            .flatMap(t -> t.getItems().stream())
            .collect(Collectors.groupingBy(item -> item.getMenu().getNama(), Collectors.summingInt(ItemPesanan::getJumlah)))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("-");
    }
    
    private void updateTabelRingkasanMenu(List<Transaksi> transaksiList) {
        ringkasanMenuTableModel.setRowCount(0);
        Map<Menu, Integer> jumlahTerjual = transaksiList.stream()
            .flatMap(t -> t.getItems().stream())
            .collect(Collectors.groupingBy(ItemPesanan::getMenu, Collectors.summingInt(ItemPesanan::getJumlah)));
        for (Map.Entry<Menu, Integer> entry : jumlahTerjual.entrySet()) {
            Menu menu = entry.getKey();
            int jumlah = entry.getValue();
            int totalPemasukan = menu.getHarga() * jumlah;
            ringkasanMenuTableModel.addRow(new Object[]{
                menu.getId(),
                menu.getNama(),
                jumlah,
                currencyFormatter.format(totalPemasukan)
            });
        }
    }

    private void updateTabelRiwayatTransaksi(List<Transaksi> transaksiList) {
        riwayatTransaksiTableModel.setRowCount(0);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss, dd-MM-yyyy");
        for (Transaksi transaksi : transaksiList) {
            riwayatTransaksiTableModel.addRow(new Object[]{
                transaksi.getId(),
                transaksi.getWaktu().format(timeFormatter),
                transaksi.getNamaCustomer(),
                transaksi.getKasir().getUsername(),
                currencyFormatter.format(transaksi.getTotal())
            });
        }
    }
    
    private void styleTable(JTable table) {
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void addPlaceholderStyle(JTextField textField, String placeholder) {
        Font originalFont = textField.getFont();
        Font placeholderFont = new Font(originalFont.getName(), Font.ITALIC, originalFont.getSize());
        textField.setText(placeholder);
        textField.setFont(placeholderFont);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setFont(originalFont);
                    textField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setFont(placeholderFont);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }
}