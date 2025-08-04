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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import login.AppService;
import login.Role;
import login.User;
import login.model.Karyawan;

public class TambahEditKaryawanDialog extends JDialog {

    private JTextField idField, namaField, usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> posisiComboBox;
    private JCheckBox punyaAkunCheckBox;
    private JButton simpanButton, batalButton;

    private boolean isSaved = false;
    private Karyawan karyawanToEdit;

    public TambahEditKaryawanDialog(Frame parent) {
        super(parent, "Tambah Karyawan Baru", true);
        this.karyawanToEdit = null;
        initComponents();
    }
    
    public TambahEditKaryawanDialog(Frame parent, Karyawan karyawanToEdit) {
        super(parent, "Edit Data Karyawan", true);
        this.karyawanToEdit = karyawanToEdit;
        initComponents();
        populateForm();
    }
    
    private void initComponents() {
        setSize(450, 400);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private void populateForm() {
        if (karyawanToEdit == null) return;
        
        idField.setText(String.valueOf(karyawanToEdit.getIdKaryawan()));
        idField.setEditable(false);
        idField.setBackground(new Color(230, 230, 230));

        namaField.setText(karyawanToEdit.getNamaLengkap());
        posisiComboBox.setSelectedItem(karyawanToEdit.getPosisi());

        User akun = karyawanToEdit.getAkun();
        if (akun != null) {
            punyaAkunCheckBox.setSelected(true);
            punyaAkunCheckBox.setEnabled(false);
            usernameField.setText(akun.getUsername());
            passwordField.setText(akun.getPassword());
        } else {
            punyaAkunCheckBox.setSelected(false);
        }
    }
    
    private void onSimpan() {
        if (namaField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap tidak boleh kosong!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nama = namaField.getText().trim();
        String posisi = (String) posisiComboBox.getSelectedItem();
        User akunBaru = null;
        
        if (punyaAkunCheckBox.isSelected()) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username dan Password Akun tidak boleh kosong!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                if (karyawanToEdit == null || karyawanToEdit.getAkun() == null || !karyawanToEdit.getAkun().getUsername().equalsIgnoreCase(username)) {
                    if (AppService.getAuthenticationService().usernameExists(username)) {
                        JOptionPane.showMessageDialog(this, "Username telah terpakai", "Error: Username Duplikat", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memvalidasi username: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String userId = (karyawanToEdit != null && karyawanToEdit.getAkun() != null) ? karyawanToEdit.getAkun().getId() : JOptionPane.showInputDialog(this, "Masukkan ID untuk Akun Baru (contoh: 004):");
            if(userId == null || userId.trim().isEmpty()){
                JOptionPane.showMessageDialog(this, "ID Akun tidak boleh kosong!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Role role = posisi.equalsIgnoreCase("Manager") ? Role.MANAGER_FBM : 
                        posisi.equalsIgnoreCase("Kasir") ? Role.KASIR : Role.KITCHEN;
            akunBaru = new User(userId.trim(), username, password, role);
        }

        try {
            if (karyawanToEdit == null) {
                Karyawan karyawanBaru = new Karyawan(nama, posisi, akunBaru);
                AppService.getKaryawanService().tambahKaryawan(karyawanBaru);
            } else {
                karyawanToEdit.setNamaLengkap(nama);
                karyawanToEdit.setPosisi(posisi);
                karyawanToEdit.setAkun(akunBaru);
                AppService.getKaryawanService().updateKaryawan(karyawanToEdit);
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

        formPanel.add(new JLabel("ID Karyawan:"), gbc);
        gbc.gridx = 1; 
        idField = new JTextField(20);
        idField.setText("(Otomatis oleh Database)");
        idField.setEditable(false);
        idField.setBackground(new Color(230, 230, 230));
        formPanel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy++; formPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; namaField = new JTextField(20); formPanel.add(namaField, gbc);

        gbc.gridx = 0; gbc.gridy++; formPanel.add(new JLabel("Posisi:"), gbc);
        gbc.gridx = 1; 
        String[] posisi = {"Kasir", "Kitchen", "Cook", "Waitress", "Cleaning Service", "Manager"}; 
        posisiComboBox = new JComboBox<>(posisi); 
        formPanel.add(posisiComboBox, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; 
        punyaAkunCheckBox = new JCheckBox("Buatkan Akun Login untuk Karyawan ini"); 
        formPanel.add(punyaAkunCheckBox, gbc);
        gbc.gridwidth = 1;

        JLabel usernameLabel = new JLabel("Username Akun:");
        JLabel passwordLabel = new JLabel("Password Akun:");
        gbc.gridx = 0; gbc.gridy++; formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; usernameField = new JTextField(20); formPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy++; formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; passwordField = new JPasswordField(20); formPanel.add(passwordField, gbc);

        usernameLabel.setEnabled(false); usernameField.setEnabled(false);
        passwordLabel.setEnabled(false); passwordField.setEnabled(false);
        punyaAkunCheckBox.addItemListener(e -> {
            boolean isSelected = e.getStateChange() == 1;
            usernameLabel.setEnabled(isSelected);
            usernameField.setEnabled(isSelected);
            passwordLabel.setEnabled(isSelected);
            passwordField.setEnabled(isSelected);
        });
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        simpanButton = new JButton("Simpan");
        batalButton = new JButton("Batal");
        buttonPanel.add(batalButton);
        buttonPanel.add(simpanButton);
        
        batalButton.addActionListener(e -> dispose());
        simpanButton.addActionListener(e -> onSimpan());
        
        return buttonPanel;
    }
}