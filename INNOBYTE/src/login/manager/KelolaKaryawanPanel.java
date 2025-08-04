package login.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import login.AppService;
import login.model.Karyawan;

public class KelolaKaryawanPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton tambahButton, editButton, hapusButton;

    public KelolaKaryawanPanel() {
        setLayout(new BorderLayout(10, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        add(createTopPanel(), BorderLayout.NORTH);

        String[] columnNames = {"ID", "Nama Karyawan", "Posisi", "Akses Akun"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        
        styleTable();
        addListeners();
        loadData();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(20, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Data Karyawan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);
        
        tambahButton = new JButton("Tambah Karyawan");
        styleActionButton(tambahButton, new Color(26, 170, 85));
        
        editButton = new JButton("Edit Karyawan");
        styleActionButton(editButton, Color.GRAY);
        editButton.setEnabled(false);
        
        hapusButton = new JButton("Hapus Karyawan");
        styleActionButton(hapusButton, Color.GRAY);
        hapusButton.setEnabled(false);

        actionsPanel.add(tambahButton);
        actionsPanel.add(editButton);
        actionsPanel.add(hapusButton);
        topPanel.add(actionsPanel, BorderLayout.CENTER);
        
        return topPanel;
    }

    private void addListeners() {
        // Listener untuk mendeteksi pemilihan baris di tabel
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean isRowSelected = table.getSelectedRow() != -1;
                editButton.setEnabled(isRowSelected);
                hapusButton.setEnabled(isRowSelected);
                styleActionButton(editButton, isRowSelected ? new Color(255, 184, 0) : Color.GRAY);
                styleActionButton(hapusButton, isRowSelected ? new Color(217, 30, 24) : Color.GRAY);
            }
        });

        // Action Listener untuk Tombol Tambah
        tambahButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            TambahEditKaryawanDialog dialog = new TambahEditKaryawanDialog(parentFrame);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        });

        // Action Listener untuk Tombol Edit
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;
            
            int id = (int) table.getValueAt(selectedRow, 0); // Ambil ID (int) dari kolom pertama
            try {
                Karyawan karyawanToEdit = AppService.getKaryawanService().getKaryawanById(id);
                if (karyawanToEdit != null) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    TambahEditKaryawanDialog dialog = new TambahEditKaryawanDialog(parentFrame, karyawanToEdit);
                    dialog.setVisible(true);
                    if (dialog.isSaved()) {
                        loadData();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengambil data untuk diedit: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Action Listener untuk Tombol Hapus
        hapusButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;

            int response = JOptionPane.showConfirmDialog(this, 
                "Menghapus karyawan juga akan menghapus akun login terkait (jika ada).\nApakah Anda yakin ingin melanjutkan?", "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                int id = (int) table.getValueAt(selectedRow, 0);
                try {
                    AppService.getKaryawanService().removeKaryawanById(id);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Data karyawan berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Karyawan> daftarKaryawan = AppService.getKaryawanService().getAllKaryawan();
            for (Karyawan karyawan : daftarKaryawan) {
                tableModel.addRow(new Object[]{
                    karyawan.getIdKaryawan(),
                    karyawan.getNamaLengkap(),
                    karyawan.getPosisi(),
                    (karyawan.getAkun() != null) ? "Ya" : "Tidak"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data karyawan: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleTable() {
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(240, 240, 240));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setPreferredWidth(300);
    }

    private void styleActionButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}