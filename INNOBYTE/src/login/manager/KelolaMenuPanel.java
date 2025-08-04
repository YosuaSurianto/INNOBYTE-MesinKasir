package login.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import login.AppService;
import login.model.Menu;

public class KelolaMenuPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton tambahButton, editButton, hapusButton;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public KelolaMenuPanel() {
        setLayout(new BorderLayout(10, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        add(createTopPanel(), BorderLayout.NORTH);

        String[] columnNames = {"ID", "Nama Menu", "Harga"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
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
        JLabel titleLabel = new JLabel("Data Menu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(Color.WHITE);
        
        tambahButton = new JButton("Tambah Menu");
        styleActionButton(tambahButton, new Color(26, 170, 85));
        
        editButton = new JButton("Edit Menu");
        styleActionButton(editButton, Color.GRAY);
        editButton.setEnabled(false);
        
        hapusButton = new JButton("Hapus Menu");
        styleActionButton(hapusButton, Color.GRAY);
        hapusButton.setEnabled(false);

        actionsPanel.add(tambahButton);
        actionsPanel.add(editButton);
        actionsPanel.add(hapusButton);
        topPanel.add(actionsPanel, BorderLayout.CENTER);
        
        searchField = new JTextField(20);
        addPlaceholderStyle(searchField, "Cari Menu...");
        topPanel.add(searchField, BorderLayout.EAST);
        
        return topPanel;
    }

    private void addListeners() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean isRowSelected = table.getSelectedRow() != -1;
                editButton.setEnabled(isRowSelected);
                hapusButton.setEnabled(isRowSelected);
                styleActionButton(editButton, isRowSelected ? new Color(255, 184, 0) : Color.GRAY);
                styleActionButton(hapusButton, isRowSelected ? new Color(217, 30, 24) : Color.GRAY);
            }
        });

        tambahButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            TambahEditMenuDialog dialog = new TambahEditMenuDialog(parentFrame);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;
            
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String id = (String) tableModel.getValueAt(modelRow, 0);

            try {
                Menu menuToEdit = AppService.getMenuService().getMenuById(id);
                if (menuToEdit != null) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    TambahEditMenuDialog dialog = new TambahEditMenuDialog(parentFrame, menuToEdit);
                    dialog.setVisible(true);
                    if (dialog.isSaved()) {
                        loadData();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengambil data untuk diedit: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        hapusButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;

            int response = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus menu ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                String id = (String) tableModel.getValueAt(modelRow, 0);
                try {
                    AppService.getMenuService().removeMenuById(id);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Menu berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus menu: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
    }

    private void filterTable() {
        String text = searchField.getText();
        if (text.equals("Cari Menu...") || text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
    
    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Menu> daftarMenu = AppService.getMenuService().getAllMenu();
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            for (Menu menu : daftarMenu) {
                tableModel.addRow(new Object[]{
                    menu.getId(),
                    menu.getNama(),
                    currencyFormatter.format(menu.getHarga())
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data menu: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(400);
    }

    private void styleActionButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void addPlaceholderStyle(JTextField textField, String placeholder) {
        Font originalFont = textField.getFont();
        Font placeholderFont = new Font(originalFont.getName(), Font.ITALIC, originalFont.getSize());
        
        textField.setText(placeholder);
        textField.setFont(placeholderFont);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setFont(originalFont);
                    textField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setFont(placeholderFont);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }
}