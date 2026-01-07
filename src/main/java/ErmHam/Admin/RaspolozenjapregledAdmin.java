package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import ErmHam.Raspolozenje;
import ErmHam.UserSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RaspolozenjapregledAdmin extends JFrame {

    private JPanel glavniProzorraspolozenjapregled;
    private JTable pregledAdminrasplo;
    private JButton Obrisibutton;

    private final List<Raspolozenje> raspolozenja = new ArrayList<>();

    public RaspolozenjapregledAdmin() {
        setTitle("Pregled raspoloženja (SUPERADMIN)");
        setContentPane(glavniProzorraspolozenjapregled);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadFromDB();
        ucitajTabelu();

        Obrisibutton.addActionListener(e -> obrisiRaspolozenje());


        Obrisibutton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        Obrisibutton.setBackground(new Color(69, 104, 130));
        Obrisibutton.setForeground(Color.WHITE);

    }

    private void ucitajTabelu() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Datum");
        model.addColumn("Raspoloženje");
        model.addColumn("Bilješka");

        for (Raspolozenje r : raspolozenja) {
            model.addRow(new Object[]{
                    r.getDatum(),
                    r.getRaspolozenje(),
                    r.getBiljeska()
            });
        }

        pregledAdminrasplo.setModel(model);
    }

    private void loadFromDB() {
        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> collection = db.getCollection("raspolozenje");

        raspolozenja.clear();

        // ✅ SUPERADMIN vidi SVE
        for (Document d : collection.find()) {
            raspolozenja.add(new Raspolozenje(
                    d.getString("datum"),
                    d.getString("raspolozenje"),
                    d.getString("biljeska")
            ));
        }
    }

    private void obrisiRaspolozenje() {

        int row = pregledAdminrasplo.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Odaberi red za brisanje!");
            return;
        }

        int potvrda = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da želite obrisati raspoloženje?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION
        );

        if (potvrda != JOptionPane.YES_OPTION) return;

        String datum = pregledAdminrasplo.getValueAt(row, 0).toString();
        String raspolozenje = pregledAdminrasplo.getValueAt(row, 1).toString();
        String biljeska = pregledAdminrasplo.getValueAt(row, 2).toString();

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> collection = db.getCollection("raspolozenje");

        collection.deleteOne(new Document()
                .append("datum", datum)
                .append("raspolozenje", raspolozenje)
                .append("biljeska", biljeska)
        );

        loadFromDB();
        ucitajTabelu();

        JOptionPane.showMessageDialog(this, "Raspoloženje obrisano!");
    }
}
