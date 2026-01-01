package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javax.swing.*;
public class DodajKorisnika extends JFrame {

    private JPanel glavniProzor;
    private JTextField userName;
    private JPasswordField userPassword;
    private JComboBox<String> comboBoxRole;
    private JButton sacuvajBtn;

    public DodajKorisnika() {

        setTitle("Dodaj korisnika");
        setContentPane(glavniProzor);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        comboBoxRole.addItem("Odaberi");
        comboBoxRole.addItem("USER");
        comboBoxRole.addItem("SUPERADMIN");

        sacuvajBtn.addActionListener(e -> sacuvajKorisnika());

    }



    private void sacuvajKorisnika() {

        String username = userName.getText().trim();
        String password = new String(userPassword.getPassword());
        String role = (String) comboBoxRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || role.equals("Odaberi")) {
            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
            return;
        }

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> users = db.getCollection("users");

        // provjera da li već postoji
        Document postoji = users.find(new Document("username", username)).first();
        if (postoji != null) {
            JOptionPane.showMessageDialog(this, "Korisnik već postoji!");
            return;
        }

        Document noviUser = new Document()
                .append("username", username)
                .append("password", password)
                .append("role", role);

        users.insertOne(noviUser);

        JOptionPane.showMessageDialog(this, "Korisnik uspješno sačuvan!");
        dispose(); // zatvori prozor
    }
}
