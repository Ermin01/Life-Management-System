package ErmHam;

import ErmHam.LoginForm.Loginform;

import javax.swing.*;
import java.awt.*;

public class Splashscreen extends JFrame {

    private JProgressBar progressBar;
    private JLabel statusLabel;

    public Splashscreen() {

        setUndecorated(true); // bez okvira
        setSize(600, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ====== BACKGROUND PANEL ======
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Gradient background
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(52, 152, 219),
                        0, getHeight(), new Color(41, 128, 185)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        add(mainPanel);

        // ====== TITLE ======
        JLabel title = new JLabel("Life Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBounds(130, 40, 400, 40);
        mainPanel.add(title);

        // ====== SUBTITLE ======
        JLabel subtitle = new JLabel("Organize • Track • Improve");
        subtitle.setForeground(new Color(220, 230, 240));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setBounds(200, 85, 300, 30);
        mainPanel.add(subtitle);

        // ====== STATUS TEXT ======
        statusLabel = new JLabel("Loading modules...");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setBounds(220, 200, 250, 25);
        mainPanel.add(statusLabel);

        // ====== PROGRESS BAR ======
        progressBar = new JProgressBar();
        progressBar.setBounds(120, 230, 360, 22);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setBorderPainted(false);
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(69, 104, 130));
        mainPanel.add(progressBar);

        startLoading();
    }

    // ====== LOADING LOGIC ======
    private void startLoading() {

        Timer timer = new Timer(40, null);

        timer.addActionListener(e -> {
            int value = progressBar.getValue() + 1;
            progressBar.setValue(value);

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

    // ====== OPEN LOGIN ======
    private void openLogin() {
        dispose(); // zatvori splash

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
