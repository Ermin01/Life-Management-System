package ErmHam;

import ErmHam.LoginForm.Loginform;
import ErmHam.User.UcenjeplanerUser;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {



        SwingUtilities.invokeLater(() -> {
            new Splashscreen().setVisible(true);
        });



    }
}


//public class Main {
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Life Management System");
//        frame.setContentPane(new Loginform().Glavniprozor());
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(1000, 550);
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