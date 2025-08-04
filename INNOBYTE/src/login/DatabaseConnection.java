package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/innobyte";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() {
        try {
            // Memuat driver MySQL JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Mencoba membuat koneksi
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Koneksi ke database berhasil!");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL tidak ditemukan! Pastikan file JAR sudah ditambahkan ke proyek.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Gagal terhubung ke database: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}