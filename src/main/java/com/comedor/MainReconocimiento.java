package com.comedor;

import javax.swing.SwingUtilities;
import com.comedor.vista.LoginReconocimientoUI;

public class MainReconocimiento {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginReconocimientoUI().setVisible(true));
    }
}
