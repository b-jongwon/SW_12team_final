import ui.LoginFrame;

public class App {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}