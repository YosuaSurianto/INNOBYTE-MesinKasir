package login.manager.laporan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import login.DatabaseConnection;
import login.Role;
import login.User;
import login.model.ItemPesanan;
import login.model.Menu;
import login.model.StatusPesanan;
import login.model.Transaksi;

public class TransaksiService {

    private final DateTimeFormatter idFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // Metode utama untuk mengambil data transaksi berdasarkan filter
    public List<Transaksi> getTransaksi(String filter) throws SQLException {
        Map<String, Transaksi> transaksiMap = new HashMap<>();
        String sql = "SELECT t.id_transaksi, t.waktu_transaksi, t.nama_customer, t.total_harga, t.status, " +
                     "u.id as id_kasir, u.username as kasir_username, u.role as kasir_role, " +
                     "dt.id_menu, dt.jumlah, dt.subtotal, " +
                     "m.nama_menu, m.harga as harga_menu " +
                     "FROM transaksi t " +
                     "JOIN users u ON t.id_kasir = u.id " +
                     "JOIN detail_transaksi dt ON t.id_transaksi = dt.id_transaksi " +
                     "JOIN menu m ON dt.id_menu = m.id_menu ";

        switch (filter) {
            case "Laporan Harian":
                sql += "WHERE DATE(t.waktu_transaksi) = CURDATE() ";
                break;
            case "Laporan Bulanan":
                sql += "WHERE MONTH(t.waktu_transaksi) = MONTH(CURDATE()) AND YEAR(t.waktu_transaksi) = YEAR(CURDATE()) ";
                break;
        }
        sql += "ORDER BY t.waktu_transaksi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String transaksiId = rs.getString("id_transaksi");
                Transaksi transaksi = transaksiMap.get(transaksiId);

                if (transaksi == null) {
                    User kasir = new User(
                        rs.getString("id_kasir"),
                        rs.getString("kasir_username"),
                        null, 
                        Role.valueOf(rs.getString("kasir_role"))
                    );
                    transaksi = new Transaksi(
                        transaksiId,
                        rs.getTimestamp("waktu_transaksi").toLocalDateTime(),
                        rs.getString("nama_customer"),
                        new ArrayList<>(),
                        kasir
                    );
                    transaksi.setStatus(StatusPesanan.valueOf(rs.getString("status")));
                    transaksiMap.put(transaksiId, transaksi);
                }

                Menu menu = new Menu(
                    rs.getString("id_menu"),
                    rs.getString("nama_menu"),
                    rs.getInt("harga_menu")
                );
                ItemPesanan item = new ItemPesanan(menu, rs.getInt("jumlah"));
                transaksi.getItems().add(item);
            }
        }
        return new ArrayList<>(transaksiMap.values());
    }

    // Metode untuk menambah transaksi baru (dari kasir)
    public void addTransaksi(Transaksi transaksi) throws SQLException {
        Connection conn = null;
        String sqlTransaksi = "INSERT INTO transaksi (id_transaksi, waktu_transaksi, nama_customer, total_harga, id_kasir, status) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_menu, jumlah, subtotal) VALUES (?, ?, ?, ?)";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sqlTransaksi)) {
                pstmt.setString(1, transaksi.getId());
                pstmt.setTimestamp(2, Timestamp.valueOf(transaksi.getWaktu()));
                pstmt.setString(3, transaksi.getNamaCustomer());
                pstmt.setInt(4, transaksi.getTotal());
                pstmt.setString(5, transaksi.getKasir().getId());
                pstmt.setString(6, transaksi.getStatus().toString());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetail)) {
                for (ItemPesanan item : transaksi.getItems()) {
                    pstmt.setString(1, transaksi.getId());
                    pstmt.setString(2, item.getMenu().getId());
                    pstmt.setInt(3, item.getJumlah());
                    pstmt.setInt(4, item.getSubtotal());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
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

    // Membuat ID Transaksi baru yang unik
    public String generateNewId(LocalDateTime waktu) {
        String timestampPart = waktu.format(idFormatter);
        return "TRX-" + timestampPart;
    }

    // Mengubah status pesanan di database
    public void updateStatusPesanan(String transaksiId, StatusPesanan newStatus) throws SQLException {
        String sql = "UPDATE transaksi SET status = ? WHERE id_transaksi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.toString());
            pstmt.setString(2, transaksiId);
            pstmt.executeUpdate();
        }
    }
}