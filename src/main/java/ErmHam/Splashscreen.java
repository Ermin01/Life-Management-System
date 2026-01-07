package ErmHam;

import ErmHam.LoginForm.Loginform;

import javax.swing.*;
import java.awt.*;

public class Splashscreen extends JFrame {

    private JPanel GlavniprozorPocetkogekrana;
    private JProgressBar progressBarUcitvanje;
    private JLabel Erminlabel;
    private JLabel lifesystemlabel;
    private JLabel verzija;
    private JLabel statusLabel;

    public Splashscreen() {

        setUndecorated(true);
        setSize(700, 400);
        setLocationRelativeTo(null);

        GlavniprozorPocetkogekrana = new JPanel(new BorderLayout());
        GlavniprozorPocetkogekrana.setBackground(new Color(69, 104, 130));
        add(GlavniprozorPocetkogekrana);


        Erminlabel = new JLabel("ErminHam", SwingConstants.CENTER);
        Erminlabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        Erminlabel.setForeground(Color.WHITE);

        lifesystemlabel = new JLabel("Life Management System", SwingConstants.CENTER);
        lifesystemlabel.setFont(new Font("Segoe UI", Font.PLAIN, 25));
        lifesystemlabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("Inicijalizacija sustava...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statusLabel.setForeground(Color.WHITE);

        verzija = new JLabel("v1.0", SwingConstants.CENTER);
        verzija.setFont(new Font("Segoe UI", Font.BOLD, 18));
        verzija.setForeground(new Color(220, 220, 220));

        JPanel center = new JPanel(new GridLayout(4, 1, 0, 1));
        center.setOpaque(false);
        center.add(Erminlabel);
        center.add(lifesystemlabel);
        center.add(statusLabel);
        center.add(verzija);

        GlavniprozorPocetkogekrana.add(center, BorderLayout.CENTER);

        // ===== PROGRESS BAR =====
        progressBarUcitvanje = new JProgressBar(0, 100);
        progressBarUcitvanje.setValue(0);
        progressBarUcitvanje.setStringPainted(true);
        progressBarUcitvanje.setForeground(new Color(52, 152, 219)); // PLAVA
        progressBarUcitvanje.setBackground(new Color(220, 230, 240));
        progressBarUcitvanje.setBorder(
                BorderFactory.createEmptyBorder(10, 30, 20, 30)
        );

        GlavniprozorPocetkogekrana.add(progressBarUcitvanje, BorderLayout.SOUTH);

        startLoading();
    }

    private void startLoading() {

        Timer timer = new Timer(40, null);

        timer.addActionListener(e -> {
            int value = progressBarUcitvanje.getValue() + 1;
            progressBarUcitvanje.setValue(value);

            if (value < 30) {
                statusLabel.setText("Inicijalizacija sustava...");
            } else if (value < 60) {
                statusLabel.setText("Učitavanje korisničkih modula...");
            } else if (value < 90) {
                statusLabel.setText("Povezivanje baze podataka...");
            } else {
                statusLabel.setText("Pokretanje aplikacije...");
            }

            if (value >= 100) {
                timer.stop();
                openLogin();
            }
        });

        timer.start();
    }


    private void openLogin() {
        dispose();

        JFrame frame = new JFrame("Life Management System");
        frame.setContentPane(new Loginform().Glavniprozor());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        Image icon = new ImageIcon(
                Main.class.getResource("/imgdeskop.jpg")
        ).getImage();
        frame.setIconImage(icon);

        frame.setVisible(true);
    }
}
