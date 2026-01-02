package ErmHam;

import ErmHam.LoginForm.Loginform;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Life Management System");
        frame.setContentPane(new Loginform().Glavniprozor());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}

//
//package ErmHam;
//
//        import ErmHam.LoginForm.Loginform;
//
//        import javax.swing.*;
//
//public class Main {
//
//    public static void main(String[] args) {
//
//        // 🔥 Moderan izgled aplikacije
//        try {
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (Exception ignored) {}
//
//        // ✅ Swing aplikacije uvijek pokretati ovako
//        SwingUtilities.invokeLater(() -> {
//
//            JFrame frame = new JFrame("Life Management System");
//
//            // Login panel
//            frame.setContentPane(new Loginform().Glavniprozor());
//
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(900, 550);
//            frame.setLocationRelativeTo(null); // centriraj
//            frame.setResizable(false);        // izgleda urednije
//            frame.setVisible(true);
//        });
//    }
//}
