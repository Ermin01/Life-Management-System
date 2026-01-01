package ErmHam.LoginForm;

import ErmHam.Database.Bazapodataka;
import ErmHam.MainMenu.MainMenuForm;
import ErmHam.UserSession;
import ErmHam.Users;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;

public class Loginform {

    private JPanel Glavniprozor;
    private JTextField Username;
    private JButton prijaviSeBtn;
    private JButton registarBtn;
    private JPasswordField Password;
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
        registarBtn.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        registarBtn.setBackground(new Color(69, 104, 130));
        registarBtn.setForeground(Color.WHITE);
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

        Document userDoc = users.find(
                new Document("username", username)
                        .append("password", password)
        ).first();

        if (userDoc == null) {
            JOptionPane.showMessageDialog(null, "Pogrešan username ili password");
            return;
        }

        Users user = new Users(
                userDoc.getObjectId("_id").toHexString(),
                username,
                password,
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

        users.insertOne(new Document()
                .append("username", username)
                .append("password", password)
                .append("role", "USER")
        );

        JOptionPane.showMessageDialog(null, "Registracija uspješna!");
        toggleMode(); // nazad na login
    }

    /* ================= TOGGLE MODE ================= */

    private void toggleMode() {
        if (mode == Mode.LOGIN) {
            mode = Mode.REGISTER;
            prijaviSeBtn.setText("Registruj se");
            registarBtn.setText("Nazad na login");
        } else {
            mode = Mode.LOGIN;
            prijaviSeBtn.setText("Prijavi se");
            registarBtn.setText("Registar");
        }
    }
}
