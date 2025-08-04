package login.model;

public class Menu {
    private String id;
    private String nama;
    private int harga;

    // Constructor untuk membuat objek menu baru
    public Menu(String id, String nama, int harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    // --- Getters (untuk mengambil data) ---
    public String getId() { 
        return id; 
    }
    public String getNama() { 
        return nama; 
    }
    public int getHarga() { 
        return harga; 
    }

    // --- Setters (untuk fungsi edit) ---
    public void setNama(String nama) { 
        this.nama = nama; 
    }
    public void setHarga(int harga) { 
        this.harga = harga; 
    }
}