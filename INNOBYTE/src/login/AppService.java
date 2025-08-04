package login;

import login.manager.KaryawanService;
import login.manager.MenuService;
import login.manager.laporan.TransaksiService;

public final class AppService {
    
    // Semua service dibuat tanpa dependensi di constructor.
    private static final AuthenticationService authService = new AuthenticationService();
    private static final MenuService menuService = new MenuService();
    private static final KaryawanService karyawanService = new KaryawanService(); // DIUBAH
    private static final TransaksiService transaksiService = new TransaksiService(); // DIUBAH

    private AppService() {}

    public static AuthenticationService getAuthenticationService() {
        return authService;
    }

    public static KaryawanService getKaryawanService() {
        return karyawanService;
    }
    
    public static MenuService getMenuService() {
        return menuService;
    }

    public static TransaksiService getTransaksiService() {
        return transaksiService;
    }
}