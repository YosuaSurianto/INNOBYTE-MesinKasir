package login.model;

import login.User; 

public class Karyawan {
    private int idKaryawan;
    private String namaLengkap;
    private String posisi;
    private User akun;

    // Constructor untuk mengambil data dari database
    public Karyawan(int idKaryawan, String namaLengkap, String posisi, User akun) {
        this.idKaryawan = idKaryawan;
        this.namaLengkap = namaLengkap;
        this.posisi = posisi;
        this.akun = akun;
    }
    
    // Constructor untuk membuat karyawan baru (ID belum ada)
    public Karyawan(String namaLengkap, String posisi, User akun) {
        this.namaLengkap = namaLengkap;
        this.posisi = posisi;
        this.akun = akun;
    }

    // Getters dan Setters
    public int getIdKaryawan() { return idKaryawan; }
    public void setIdKaryawan(int id) { this.idKaryawan = id; }
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String nama) { this.namaLengkap = nama; }
    public String getPosisi() { return posisi; }
    public void setPosisi(String posisi) { this.posisi = posisi; }
    public User getAkun() { return akun; }
    public void setAkun(User akun) { this.akun = akun; }
}