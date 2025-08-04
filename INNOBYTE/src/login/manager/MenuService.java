package login.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import login.DatabaseConnection;
import login.model.Menu;

public class MenuService {

    // Mengambil semua data menu dari database
    public List<Menu> getAllMenu() throws SQLException {
        List<Menu> daftarMenu = new ArrayList<>();
        String sql = "SELECT * FROM menu ORDER BY id_menu ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                daftarMenu.add(new Menu(
                    rs.getString("id_menu"),
                    rs.getString("nama_menu"),
                    rs.getInt("harga")
                ));
            }
        }
        return daftarMenu;
    }

    // Menambah menu baru ke database
    public void tambahMenu(Menu menu) throws SQLException {
        String sql = "INSERT INTO menu (id_menu, nama_menu, harga) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, menu.getId());
            pstmt.setString(2, menu.getNama());
            pstmt.setInt(3, menu.getHarga());
            pstmt.executeUpdate();
        }
    }

    // Mengedit menu yang ada di database
    public void updateMenu(Menu menu) throws SQLException {
        String sql = "UPDATE menu SET nama_menu = ?, harga = ? WHERE id_menu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, menu.getNama());
            pstmt.setInt(2, menu.getHarga());
            pstmt.setString(3, menu.getId());
            pstmt.executeUpdate();
        }
    }
    
    // Menghapus menu dari database berdasarkan ID
    public void removeMenuById(String id) throws SQLException {
        String sql = "DELETE FROM menu WHERE id_menu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    // Mengecek apakah ID menu sudah ada di database
    public boolean idExists(String id) throws SQLException {
        String sql = "SELECT 1 FROM menu WHERE id_menu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true jika ada hasilnya
            }
        }
    }
    
    // Mengambil satu menu berdasarkan ID dari database
    public Menu getMenuById(String id) throws SQLException {
        String sql = "SELECT * FROM menu WHERE id_menu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    return new Menu(
                        rs.getString("id_menu"), 
                        rs.getString("nama_menu"), 
                        rs.getInt("harga")
                    );
                }
            }
        }
        return null;
    }
}