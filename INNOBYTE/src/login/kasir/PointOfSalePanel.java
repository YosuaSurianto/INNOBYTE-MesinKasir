package login.kasir;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import login.AppService;
import login.User;
import login.model.ItemPesanan;
import login.model.Menu;
import login.model.Transaksi;

public class PointOfSalePanel extends JPanel {

    private User currentUser;
    private List<ItemPesanan> keranjangList;
    private DefaultTableModel keranjangTableModel;
    private JTable keranjangTable;
    private JLabel totalHargaLabel;
    private NumberFormat formatter;
    private JPanel menuGridPanel;

    public PointOfSalePanel(User user) {
        this.currentUser = user;
        this.keranjangList = new ArrayList<>();
        this.formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        setLayout(new BorderLayout());

        add(createMenuPanel(), BorderLayout.CENTER);
        add(createKeranjangPanel(), BorderLayout.EAST);
        
        loadMenu();
    }
    
    private JScrollPane createMenuPanel() {
        menuGridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        menuGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(menuGridPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        return scrollPane;
    }
    
    private void loadMenu() {
        try {
            List<Menu> daftarMenu = AppService.getMenuService().getAllMenu();
            for (Menu menu : daftarMenu) {
                String buttonText = String.format("<html><center>%s<br>%s</center></html>", menu.getNama(), formatter.format(menu.getHarga()));
                JButton menuButton = new JButton(buttonText);
                menuButton.setFont(new Font("Arial", Font.BOLD, 14));
                menuButton.setPreferredSize(new Dimension(180, 80));
                menuButton.addActionListener(e -> addItemToKeranjang(menu));
                menuGridPanel.add(menuButton);
            }
        } catch (SQLException e) {
            menuGridPanel.add(new JLabel("Gagal memuat menu: " + e.getMessage()));
        }
    }

    private JPanel createKeranjangPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(450, 0));

        JLabel titleLabel = new JLabel("Keranjang Pesanan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        String[] columnNames = {"Nama Item", "Jumlah", "Subtotal", "Aksi"};
        keranjangTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 3; }
        };
        keranjangTable = new JTable(keranjangTableModel);
        keranjangTable.setRowHeight(40);
        keranjangTable.getColumn("Aksi").setCellRenderer(new KeranjangActionRenderer());
        keranjangTable.getColumn("Aksi").setCellEditor(new KeranjangActionEditor());
        
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        totalHargaLabel = new JLabel("Total: Rp 0");
        totalHargaLabel.setFont(new Font("Arial", Font.BOLD, 22));
        
        JPanel actionButtonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton batalButton = new JButton("Batal");
        batalButton.setBackground(Color.ORANGE);
        JButton prosesButton = new JButton("Proses Transaksi");
        prosesButton.setBackground(new Color(26, 170, 85));
        
        batalButton.addActionListener(e -> {
            keranjangList.clear();
            updateKeranjangTable();
        });
        
        prosesButton.addActionListener(e -> prosesTransaksi());
        actionButtonsPanel.add(batalButton);
        actionButtonsPanel.add(prosesButton);
        
        bottomPanel.add(totalHargaLabel, BorderLayout.NORTH);
        bottomPanel.add(actionButtonsPanel, BorderLayout.CENTER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(keranjangTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addItemToKeranjang(Menu menu) {
        for (ItemPesanan item : keranjangList) {
            if (item.getMenu().getId().equals(menu.getId())) {
                item.setJumlah(item.getJumlah() + 1);
                updateKeranjangTable();
                return;
            }
        }
        keranjangList.add(new ItemPesanan(menu, 1));
        updateKeranjangTable();
    }
    
    private void updateKeranjangTable() {
        if (keranjangTable != null && keranjangTable.isEditing()) {
            keranjangTable.getCellEditor().stopCellEditing();
        }
        keranjangTableModel.setRowCount(0);
        int totalHarga = 0;
        for (ItemPesanan item : keranjangList) {
            Object[] rowData = {
                item.getMenu().getNama(),
                item.getJumlah(),
                formatter.format(item.getSubtotal()),
                "Aksi"
            };
            keranjangTableModel.addRow(rowData);
            totalHarga += item.getSubtotal();
        }
        totalHargaLabel.setText("Total: " + formatter.format(totalHarga));
    }
    
    private void prosesTransaksi() {
        if (keranjangList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String namaCustomer = JOptionPane.showInputDialog(this, "Masukkan Nama Customer:", "Input Nama Customer", JOptionPane.PLAIN_MESSAGE);
        if (namaCustomer == null || namaCustomer.trim().isEmpty()) {
            return;
        }

        int response = JOptionPane.showConfirmDialog(this, 
            "Proses transaksi untuk customer: " + namaCustomer.trim() + "?", 
            "Konfirmasi Transaksi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            try {
                String newTransactionId = AppService.getTransaksiService().generateNewId(LocalDateTime.now());
                Transaksi newTransaction = new Transaksi(
                    newTransactionId, LocalDateTime.now(), namaCustomer.trim(),
                    new ArrayList<>(keranjangList), currentUser);
                
                AppService.getTransaksiService().addTransaksi(newTransaction);
                keranjangList.clear();
                updateKeranjangTable();
                JOptionPane.showMessageDialog(this, "Transaksi berhasil diproses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void tambahJumlahItem(int rowIndex) {
        keranjangList.get(rowIndex).setJumlah(keranjangList.get(rowIndex).getJumlah() + 1);
        updateKeranjangTable();
    }

    private void kurangiJumlahItem(int rowIndex) {
        ItemPesanan item = keranjangList.get(rowIndex);
        if (item.getJumlah() > 1) {
            item.setJumlah(item.getJumlah() - 1);
        } else {
            hapusItem(rowIndex);
        }
        updateKeranjangTable();
    }

    private void hapusItem(int rowIndex) {
        keranjangList.remove(rowIndex);
        updateKeranjangTable();
    }

    class KeranjangActionPanel extends JPanel {
        public JButton plusButton, minusButton, deleteButton;
        public KeranjangActionPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            plusButton = new JButton("+");
            minusButton = new JButton("-");
            deleteButton = new JButton("x");
            plusButton.setMargin(new Insets(2, 5, 2, 5));
            minusButton.setMargin(new Insets(2, 5, 2, 5));
            deleteButton.setMargin(new Insets(2, 5, 2, 5));
            deleteButton.setForeground(Color.RED);
            add(minusButton);
            add(plusButton);
            add(deleteButton);
        }
    }

    class KeranjangActionRenderer extends KeranjangActionPanel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    class KeranjangActionEditor extends AbstractCellEditor implements TableCellEditor {
        private KeranjangActionPanel actionPanel = new KeranjangActionPanel();
        private int editingRow;

        public KeranjangActionEditor() {
            actionPanel.plusButton.addActionListener(e -> {
                if (editingRow != -1) tambahJumlahItem(keranjangTable.convertRowIndexToModel(editingRow));
                fireEditingStopped();
            });
            actionPanel.minusButton.addActionListener(e -> {
                if (editingRow != -1) kurangiJumlahItem(keranjangTable.convertRowIndexToModel(editingRow));
                fireEditingStopped();
            });
            actionPanel.deleteButton.addActionListener(e -> {
                if (editingRow != -1) hapusItem(keranjangTable.convertRowIndexToModel(editingRow));
                fireEditingStopped();
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.editingRow = row;
            actionPanel.setBackground(UIManager.getColor("Button.background"));
            return actionPanel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}