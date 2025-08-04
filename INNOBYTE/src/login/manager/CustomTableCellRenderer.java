package login.manager;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    
    private final Color evenRowColor = new Color(245, 245, 245); // Abu-abu sangat muda
    private final Color oddRowColor = Color.WHITE;
    private final Color selectionColor = new Color(51, 153, 255); // Biru

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
  
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // --- STYLING---
        // Atur warna teks default
        setForeground(Color.BLACK);
        // Hapus border default agar kita bisa set padding
        setBorder(null);
        // Beri padding (jarak tepi) di dalam sel
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Padding kiri-kanan
        
        // --- WARNA BARIS SELANG-SELING (ZEBRA-STRIPING) ---
        if (!isSelected) {
            setBackground(row % 2 == 0 ? oddRowColor : evenRowColor);
        } else {
            setBackground(selectionColor);
            setForeground(Color.WHITE);
        }

        // --- STYLING KHUSUS UNTUK KOLOM "Akses"  ---
        if (column == 4) {
            String akses = (String) value;
            if ("Tidak".equals(akses)) {
                // Jika tidak punya akses, set warna teks merah
                if (!isSelected) { 
                   setForeground(Color.RED);
                }
            }
        }
        
        return this;
    }
}