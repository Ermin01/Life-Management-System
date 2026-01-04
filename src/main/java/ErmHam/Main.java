//package ErmHam;
//
//import ErmHam.LoginForm.Loginform;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class Main {
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Life Management System");
//        frame.setContentPane(new Loginform().Glavniprozor());
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(900, 550);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//        frame.setResizable(false);
//
//        Image icon = new ImageIcon(
//                Main.class.getResource("/imgdeskop.jpg")
//        ).getImage();
//
//        frame.setIconImage(icon);
//
//
//
//    }
//}

package ErmHam;

import ErmHam.LoginForm.Loginform;
import ErmHam.User.UcenjeplanerUser;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Life Management System");
        frame.setContentPane(new Loginform().Glavniprozor());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 550);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);

        Image icon = new ImageIcon(
                Main.class.getResource("/imgdeskop.jpg")
        ).getImage();

        frame.setIconImage(icon);



    }
}


//import javax.swing.*;
//
//public class Main {
//    public static void main(String[] args) {
//
//        // Moderan izgled
//        try {
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (Exception ignored) {}
//
//        SwingUtilities.invokeLater(() -> {
//            new Splashscreen().setVisible(true);
//        });
//    }
//}

