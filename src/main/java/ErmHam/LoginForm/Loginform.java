package ErmHam.LoginForm;

import ErmHam.Database.Bazapodataka;
import ErmHam.MainMenu.MainMenuForm;
import ErmHam.UserSession;
import ErmHam.Users;
import ErmHam.HashPassword;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;

public class Loginform extends JFrame {

    private JPanel Glavniprozor;
    private JTextField Username;
    private JButton prijaviSeBtn;
    private JButton registarBtn;
    private JPasswordField Password;
    private JPanel leftPanel;
    private JPanel Loginprozor;

    private enum Mode { LOGIN, REGISTER }
    private Mode mode = Mode.LOGIN;

    public Loginform() {

        prijaviSeBtn.addActionListener(e -> {
            if (mode == Mode.LOGIN) {
                login();
            } else {
                register();
            }
        });

        registarBtn.addActionListener(e -> toggleMode());

        // UI styling
        Username.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Password.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        prijaviSeBtn.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
        prijaviSeBtn.setBackground(new Color(69, 104, 130));
        prijaviSeBtn.setForeground(Color.WHITE);
        registarBtn.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
        registarBtn.setBackground(new Color(69, 104, 130));
        registarBtn.setForeground(Color.WHITE);

        try {
            Image bgImage = ImageIO.read(new File(
                    "C:\\Users\\Public\\Documents\\Life Management System\\src\\main\\java\\ErmHam\\Img\\Logoslika.png"
            ));

            leftPanel.setLayout(new BorderLayout());

            JLabel bgLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
            };

            leftPanel.add(bgLabel, BorderLayout.CENTER);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JPanel Glavniprozor() {
        return Glavniprozor;
    }

    /* ================= LOGIN ================= */

    private void login() {
        String username = Username.getText().trim();
        String password = new String(Password.getPassword());

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> users = db.getCollection("users");

        // 🔹 TRAŽIMO SAMO PO USERNAME
        Document userDoc = users.find(
                new Document("username", username)
        ).first();

        if (userDoc == null) {
            JOptionPane.showMessageDialog(null, "Pogrešan username ili password");
            return;
        }

        // 🔹 PROVJERA HASHA
        String storedHash = userDoc.getString("password");

        if (!HashPassword.verify(password, storedHash)) {
            JOptionPane.showMessageDialog(null, "Pogrešan username ili password");
            return;
        }

        Users user = new Users(
                userDoc.getObjectId("_id").toHexString(),
                userDoc.getString("username"),
                storedHash,
                userDoc.getString("role")
        );

        UserSession.setUser(user);

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(Glavniprozor);
        frame.setContentPane(new MainMenuForm(user.getRole()).getPanel());
        frame.revalidate();
        frame.repaint();
    }

    /* ================= REGISTER ================= */

    private void register() {
        String username = Username.getText().trim();
        String password = new String(Password.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Popuni sva polja");
            return;
        }

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> users = db.getCollection("users");

        if (users.find(new Document("username", username)).first() != null) {
            JOptionPane.showMessageDialog(null, "Korisnik već postoji");
            return;
        }

        // 🔐 HASH PASSWORD
        String hashedPassword = HashPassword.hash(password);

        users.insertOne(new Document()
                .append("username", username)
                .append("password", hashedPassword)
                .append("role", "USER")
        );

        JOptionPane.showMessageDialog(null, "Registracija uspješna!");
        toggleMode();
    }

    /* ================= TOGGLE MODE ================= */

    private void toggleMode() {
        if (mode == Mode.LOGIN) {
            mode = Mode.REGISTER;
            prijaviSeBtn.setText("REGISTRUJ SE");
            registarBtn.setText("NAZAD NA LOGIN");
        } else {
            mode = Mode.LOGIN;
            prijaviSeBtn.setText("PRIJAVI SE ");
            registarBtn.setText("REGISTRACIJA");
        }
    }

// "ermin.ham@gmail.com"
//   erkoerko



}
