
import javax.swing.*;

import affichage.Fenetre;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Fenetre frame = new Fenetre();
            frame.setVisible(true);
        });
    }
}
