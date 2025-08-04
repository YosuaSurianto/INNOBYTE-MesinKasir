import javax.swing.SwingUtilities;
import login.LoginPage;

public class App {

    public static void main(String[] args) {
        // Menjalankan GUI di Event Dispatch Thread (EDT) untuk keamanan thread Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Membuat instance dari LoginPage dan menampilkannya
                new LoginPage().setVisible(true);
            }
        });
    }
}