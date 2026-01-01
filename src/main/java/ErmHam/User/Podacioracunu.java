package ErmHam.User;

import ErmHam.Database.Bazapodataka;
import ErmHam.UserSession;
import ErmHam.Users;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;

public class Podacioracunu extends JFrame {

    private JPanel GlavniPrPodacioracunu;
    private JTextField userName;
    private JTextField Role;
    private JPasswordField passwordUser;
    private JButton spremiPromjenuButton;
    private JButton nazadButton;
    private JLabel Korisnik;

    public Podacioracunu() {

        setTitle("Podaci o računu");
        setContentPane(GlavniPrPodacioracunu);
        setSize(550, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ucitajPodatke();
        Akcija();
    }

    private void ucitajPodatke() {

        Users user = UserSession.getUser();

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Nema prijavljenog korisnika!");
            dispose();
            return;
        }

        userName.setText(user.getUsername());
        Role.setText(user.getRole());
        passwordUser.setText(user.getPassword());

        // ✅ DOZVOLI IZMJENE
        userName.setEditable(true);
        passwordUser.setEditable(true);

        // ❌ ROLE OSTAJU ZAKLJUČANE
        Role.setEditable(false);
    }

    private void Akcija() {

        spremiPromjenuButton.addActionListener(e -> spremiPromjene());

        nazadButton.addActionListener(e -> dispose());
    }



    private void spremiPromjene() {

        Users user = UserSession.getUser();
        if (user == null) return;

        String noviUsername = userName.getText().trim();
        String novaSifra = new String(passwordUser.getPassword()).trim();

        if (noviUsername.isEmpty() || novaSifra.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username i lozinka ne smiju biti prazni!");
            return;
        }

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> usersCol = db.getCollection("users");

        Document filter = new Document("_id",
                new org.bson.types.ObjectId(user.getId()));

        Document update = new Document("$set",
                new Document("username", noviUsername)
                        .append("password", novaSifra)
        );

        usersCol.updateOne(filter, update);

        // 🔄 AŽURIRAJ SESSION
        UserSession.setUser(
                new Users(user.getId(), noviUsername, novaSifra, user.getRole())
        );

        JOptionPane.showMessageDialog(this, "Podaci su uspješno spremljeni!");
    }
}
