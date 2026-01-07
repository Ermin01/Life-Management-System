package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import ErmHam.Main;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class UcenjePlanerAdmin extends JFrame {

    private JPanel UcenjeAdminplaner;
    private JTable PrikazUcenja;
    private JTextField pretrazivnje;
    private JButton obrisiButton;
    private JPanel prozorNeki;
    private JLabel Slika;

    public UcenjePlanerAdmin() {

        setTitle("Pregled učenja – Admin");
        setContentPane(UcenjeAdminplaner);
        setSize(1100, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pretrazivnje.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        obrisiButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        obrisiButton.setBackground(new Color(69, 104, 130));
        obrisiButton.setForeground(Color.WHITE);

        Slika = new JLabel(
                new ImageIcon(this.getClass().getResource("/Pretrazivanje.png"))
        );

        Image icon = new ImageIcon(
                Main.class.getResource("/imgdeskop.jpg")
        ).getImage();

        setIconImage(icon);

        ucitajSvePlanove();

        pretrazivnje.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pretrazi();
            }
        });
        obrisiButton.addActionListener(e -> obrisiPlanUcenja());
    }


    private void obrisiPlanUcenja() {

        int selectedRow = PrikazUcenja.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Molimo označite jedan red u tabeli!",
                    "Upozorenje",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Ako je sorter aktivan – moramo konvertovati indeks
        int modelRow = PrikazUcenja.convertRowIndexToModel(selectedRow);

        DefaultTableModel model =
                (DefaultTableModel) PrikazUcenja.getModel();

        String korisnik = model.getValueAt(modelRow, 0).toString();
        String predmet  = model.getValueAt(modelRow, 1).toString();
        String vrijeme  = model.getValueAt(modelRow, 3).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da želite obrisati plan?\n\n"
                        + "Korisnik: " + korisnik + "\n"
                        + "Predmet: " + predmet,
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        MongoCollection<Document> col =
                Bazapodataka.getDatabase()
                        .getCollection("userucenja_paner");

        // ⚠️ Filter – biramo JEDINSTVENU kombinaciju
        Document filter = new Document()
                .append("predmet", predmet)
                .append("vrijemepodanu", vrijeme);

        col.deleteOne(filter);

        // 🔄 REFRESH
        model.removeRow(modelRow);

        JOptionPane.showMessageDialog(
                this,
                "Plan učenja uspješno obrisan!",
                "Uspjeh",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void ucitajSvePlanove() {

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> planoviCol = db.getCollection("userucenja_paner");
        MongoCollection<Document> usersCol = db.getCollection("users");

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Korisnik");
        model.addColumn("Predmet");
        model.addColumn("Ciljevi");
        model.addColumn("Vrijeme");
        model.addColumn("Trajanje");
        model.addColumn("Prioritet");
        model.addColumn("Status");

        for (Document d : planoviCol.find()) {

            String username = d.getString("username");

            // 🔴 AKO JE PRAZNO – TRAŽIMO U USERS
            if (username == null || username.isEmpty()) {

                String userId = d.getString("userId");

                try {
                    Document user = usersCol.find(
                            new Document("_id", new ObjectId(userId))
                    ).first();

                    if (user != null) {
                        username = user.getString("username");
                    } else {
                        username = "Nepoznat korisnik";
                    }

                } catch (Exception e) {
                    username = "Neispravan ID";
                }
            }

            model.addRow(new Object[]{
                    username,
                    d.getString("predmet"),
                    d.getString("ciljevi"),
                    d.getString("vrijemepodanu"),
                    d.getString("trajanje"),
                    d.getString("prioritet"),
                    d.getString("status")
            });
        }

        PrikazUcenja.setModel(model);
    }

    // ================= PRETRAGA =================

    private void pretrazi() {

        String text = pretrazivnje.getText().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) PrikazUcenja.getModel();

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(model);

        PrikazUcenja.setRowSorter(sorter);

        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

}
