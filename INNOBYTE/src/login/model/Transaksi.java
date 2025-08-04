package login.model;

import java.time.LocalDateTime;
import java.util.List;
import login.User;

public class Transaksi {
    private String id;
    private LocalDateTime waktu;
    private String namaCustomer;
    private List<ItemPesanan> items;
    private User kasir;
    private int total;
    private StatusPesanan status; 

    public Transaksi(String id, LocalDateTime waktu, String namaCustomer, List<ItemPesanan> items, User kasir) {
        this.id = id;
        this.waktu = waktu;
        this.namaCustomer = namaCustomer;
        this.items = items;
        this.kasir = kasir;
        this.total = items.stream().mapToInt(ItemPesanan::getSubtotal).sum();
        this.status = StatusPesanan.BARU_MASUK; // <-- Setiap transaksi baru, statusnya "BARU_MASUK"
    }

    // --- Getters ---
    public String getId() { return id; }
    public LocalDateTime getWaktu() { return waktu; }
    public String getNamaCustomer() { return namaCustomer; }
    public List<ItemPesanan> getItems() { return items; }
    public User getKasir() { return kasir; }
    // public int getTotal() { return total; }
    public int getTotal() {
        return items.stream()
                    .mapToInt(item -> item.getMenu().getHarga() * item.getJumlah())
                    .sum();
    }
    public StatusPesanan getStatus() { return status; } 

    // --- Setters ---
    public void setStatus(StatusPesanan status) { this.status = status; } 
}