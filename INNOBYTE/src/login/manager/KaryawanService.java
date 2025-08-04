package login.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import login.AppService;
import login.DatabaseConnection;
import login.Role;
import login.User;
import login.model.Karyawan;

public class KaryawanService {

    // Mengambil semua karyawan dari database
    public List<Karyawan> getAllKaryawan() throws SQLException {
        List<Karyawan> daftarKaryawan = new ArrayList<>();
        String sql = "SELECT k.id_karyawan, k.nama_lengkap, k.posisi, u.* " +
                     "FROM karyawan k " +
                     "LEFT JOIN users u ON k.user_id = u.id ORDER BY k.id_karyawan";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = null;
                String userId = rs.getString("u.id");
                if (userId != null) {
                    user = new User(
                        userId, rs.getString("username"), 
                        rs.getString("password"), Role.valueOf(rs.getString("role"))
                    );
                }
                
                Karyawan karyawan = new Karyawan(
                    rs.getInt("id_karyawan"), rs.getString("nama_lengkap"),
                    rs.getString("posisi"), user
                );
                daftarKaryawan.add(karyawan);
            }
        }
        return daftarKaryawan;
    }

    // Menambah karyawan baru ke database
    public void tambahKaryawan(Karyawan karyawan) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            User akun = karyawan.getAkun();
            String userIdForKaryawan = null;

            if (akun != null) {
                userIdForKaryawan = akun.getId();
                // Panggil metode addUser dari AuthenticationService
                AppService.getAuthenticationService().addUser(akun);
            }

            String sqlKaryawan = "INSERT INTO karyawan (nama_lengkap, posisi, user_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmtKaryawan = conn.prepareStatement(sqlKaryawan, Statement.RETURN_GENERATED_KEYS)) {
                pstmtKaryawan.setString(1, karyawan.getNamaLengkap());
                pstmtKaryawan.setString(2, karyawan.getPosisi());
                
                if (userIdForKaryawan != null) {
                    pstmtKaryawan.setString(3, userIdForKaryawan);
                } else {
                    pstmtKaryawan.setNull(3, java.sql.Types.VARCHAR);
                }
                pstmtKaryawan.executeUpdate();
                
                // Ambil ID karyawan yang baru dibuat oleh database
                try (ResultSet generatedKeys = pstmtKaryawan.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        karyawan.setIdKaryawan(generatedKeys.getInt(1));
                    }
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // Mengupdate data karyawan di database
    public void updateKaryawan(Karyawan karyawan) throws SQLException {
        User akun = karyawan.getAkun();
        
        // Update data user jika ada
        if (akun != null) {
             AppService.getAuthenticationService().updateUser(akun);
        }

        // Update data karyawan
        String sql = "UPDATE karyawan SET nama_lengkap = ?, posisi = ?, user_id = ? WHERE id_karyawan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, karyawan.getNamaLengkap());
            pstmt.setString(2, karyawan.getPosisi());
            if (akun != null) {
                pstmt.setString(3, akun.getId());
            } else {
                pstmt.setNull(3, java.sql.Types.VARCHAR);
            }
            pstmt.setInt(4, karyawan.getIdKaryawan());
            pstmt.executeUpdate();
        }
    }

    // Menghapus karyawan dari database berdasarkan ID
    public void removeKaryawanById(int id) throws SQLException {
        Karyawan karyawan = getKaryawanById(id);
        
        // Hapus dari tabel karyawan dulu
        String sqlKaryawan = "DELETE FROM karyawan WHERE id_karyawan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlKaryawan)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }

        // Jika karyawan punya akun, hapus juga akunnya
        if (karyawan != null && karyawan.getAkun() != null) {
            AppService.getAuthenticationService().removeUserById(karyawan.getAkun().getId());
        }
    }
    
    // Mengambil satu karyawan berdasarkan ID
    public Karyawan getKaryawanById(int id) throws SQLException {
        String sql = "SELECT k.id_karyawan, k.nama_lengkap, k.posisi, u.* " +
                     "FROM karyawan k " +
                     "LEFT JOIN users u ON k.user_id = u.id WHERE k.id_karyawan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = null;
                    String userId = rs.getString("u.id");
                    if (userId != null) {
                        user = new User(
                            userId, rs.getString("username"), 
                            rs.getString("password"), Role.valueOf(rs.getString("role"))
                        );
                    }
                    return new Karyawan(
                        rs.getInt("id_karyawan"), rs.getString("nama_lengkap"),
                        rs.getString("posisi"), user
                    );
                }
            }
        }
        return null;
    }
}