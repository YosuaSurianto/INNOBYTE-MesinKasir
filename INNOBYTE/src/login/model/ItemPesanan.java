package login.model;

import login.model.Menu; 

public class ItemPesanan {
    private Menu menu;
    private int jumlah;

    public ItemPesanan(Menu menu, int jumlah) {
        this.menu = menu;
        this.jumlah = jumlah;
    }

    public Menu getMenu() { 
        return menu; 
    }
    
    public int getJumlah() { 
        return jumlah; 
    }
    
    public int getSubtotal() {
        return menu.getHarga() * jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    // Metode helper yang kita tambahkan untuk kasir
    public void tambahJumlah() {
        this.jumlah++;
    }

    public void kurangJumlah() {
        this.jumlah--;
    }
}