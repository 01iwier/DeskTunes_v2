import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlayerGUI().setVisible(true));
    }
}
