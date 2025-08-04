package login.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import login.AppService;
import login.model.Menu;

public class TambahEditMenuDialog extends JDialog {

    private JTextField idField, namaField, hargaField;
    private JButton simpanButton, batalButton;
    private boolean isSaved = false;
    private Menu menuToEdit;

    public TambahEditMenuDialog(Frame parent) {
        super(parent, "Tambah Menu Baru", true);
        this.menuToEdit = null;
        initComponents();
    }
    
    public TambahEditMenuDialog(Frame parent, Menu menuToEdit) {
        super(parent, "Edit Data Menu", true);
        this.menuToEdit = menuToEdit;
        initComponents();
        populateForm();
    }
    
    private void initComponents() {
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private void populateForm() {
        if (menuToEdit == null) return;
        idField.setText(menuToEdit.getId());
        idField.setEditable(false);
        idField.setBackground(new Color(230, 230, 230));
        namaField.setText(menuToEdit.getNama());
        hargaField.setText(String.valueOf(menuToEdit.getHarga()));
    }
    
    private void onSimpan() {
        if (idField.getText().trim().isEmpty() || namaField.getText().trim().isEmpty() || hargaField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        int harga = Integer.parseInt(hargaField.getText().trim());
        
        try {
            if (menuToEdit == null) { // Mode TAMBAH
                // Cek duplikasi ID
                if (AppService.getMenuService().idExists(id)) {
                    JOptionPane.showMessageDialog(this, "ID Menu telah terpakai", "Error: ID Duplikat", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                AppService.getMenuService().tambahMenu(new Menu(id, nama, harga));
            } else { // Mode EDIT
                menuToEdit.setNama(nama);
                menuToEdit.setHarga(harga);
                AppService.getMenuService().updateMenu(menuToEdit);
            }
            isSaved = true;
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSaved() {
        return isSaved;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;

        formPanel.add(new JLabel("ID Menu:"), gbc);
        gbc.gridx = 1; 
        idField = new JTextField(15);
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        formPanel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy++; formPanel.add(new JLabel("Nama Menu:"), gbc);
        gbc.gridx = 1; namaField = new JTextField(15); formPanel.add(namaField, gbc);

        gbc.gridx = 0; gbc.gridy++; formPanel.add(new JLabel("Harga:"), gbc);
        gbc.gridx = 1; hargaField = new JTextField(15);
        ((AbstractDocument) hargaField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        formPanel.add(hargaField, gbc);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton simpanButton = new JButton("Simpan");
        JButton batalButton = new JButton("Batal");
        buttonPanel.add(batalButton);
        buttonPanel.add(simpanButton);
        
        batalButton.addActionListener(e -> dispose());
        simpanButton.addActionListener(e -> onSimpan());
        
        return buttonPanel;
    }
}