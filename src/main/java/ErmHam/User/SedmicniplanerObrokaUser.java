package ErmHam.User;

import ErmHam.Database.Bazapodataka;
import ErmHam.UserSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class SedmicniplanerObrokaUser extends JFrame {

    private JPanel SedmicnipanelGlava;
    private JButton sacuvajButton;
    private JButton Clearbutton;
    private JButton updateButton;

    // ====== PONEDJELJAK ======
    private JComboBox pon_uzina, pon_dorucak, pon_rucak, pon_vecera;

    // ====== UTORAK ======
    private JComboBox uto_uzina, uto_dorucak, uto_rucak, uto_vecera;

    // ====== SRIJEDA ======
    private JComboBox sri_uzina, sri_dorucak, sri_rucak, sri_vecera;

    // ====== ČETVRTAK ======
    private JComboBox cet_uzina, cet_dorucak, cet_rucak, cet_vecera;

    // ====== PETAK ======
    private JComboBox pet_uzina, pet_dorucak, pet_rucak, pet_vecera;

    // ====== SUBOTA ======
    private JComboBox sub_uzina, sub_dorucak, sub_rucak, sub_vecera;

    // ====== NEDELJA ======
    private JComboBox ned_uzina, ned_dorucak, ned_rucak, ned_vecera;
    private JButton ExportPDF;

    private JComboBox[] sviComboBoxovi;
    private Map<String, JComboBox[]> daniMap;

    public SedmicniplanerObrokaUser() {

        setTitle("Sedmični planer obroka");
        setContentPane(SedmicnipanelGlava);
        setSize(1100, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);



        Clearbutton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        Clearbutton.setBackground(new Color(69, 104, 130));
        Clearbutton.setForeground(Color.WHITE);

        updateButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        updateButton.setBackground(new Color(69, 104, 130));
        updateButton.setForeground(Color.WHITE);

        sacuvajButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        sacuvajButton.setBackground(new Color(69, 104, 130));
        sacuvajButton.setForeground(Color.WHITE);

        ExportPDF.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        ExportPDF.setBackground(new Color(69, 104, 130));
        ExportPDF.setForeground(Color.WHITE);

        inicijalizujMapu();

        sviComboBoxovi = daniMap.values()
                .stream()
                .flatMap(arr -> java.util.Arrays.stream(arr))
                .toArray(JComboBox[]::new);

        ucitajObrokeIzBaze();
        ucitajSacuvaniPlan();

        sacuvajButton.addActionListener(e -> sacuvajSedmicniPlan());
        Clearbutton.addActionListener(e -> ocistiComboBoxove());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sacuvajSedmicniPlan();
            }
        });
    }

    private void inicijalizujMapu() {

        daniMap = new LinkedHashMap<>();

        daniMap.put("ponedjeljak", new JComboBox[]{pon_uzina, pon_dorucak, pon_rucak, pon_vecera});
        daniMap.put("utorak",      new JComboBox[]{uto_uzina, uto_dorucak, uto_rucak, uto_vecera});
        daniMap.put("srijeda",     new JComboBox[]{sri_uzina, sri_dorucak, sri_rucak, sri_vecera});
        daniMap.put("cetvrtak",    new JComboBox[]{cet_uzina, cet_dorucak, cet_rucak, cet_vecera});
        daniMap.put("petak",       new JComboBox[]{pet_uzina, pet_dorucak, pet_rucak, pet_vecera});
        daniMap.put("subota",      new JComboBox[]{sub_uzina, sub_dorucak, sub_rucak, sub_vecera});
        daniMap.put("nedelja",     new JComboBox[]{ned_uzina, ned_dorucak, ned_rucak, ned_vecera});
    }

    private void ucitajObrokeIzBaze() {

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("obroci");

        for (JComboBox cb : sviComboBoxovi) {
            cb.removeAllItems();
            cb.addItem("Odaberi");
        }

        for (Document d : col.find(new Document("active", true))) {
            String naziv = d.getString("nazivObroka");
            for (JComboBox cb : sviComboBoxovi) {
                cb.addItem(naziv);
            }
        }
    }

    private void ucitajSacuvaniPlan() {

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("sedmicni_planer");

        Document plan = col.find(
                new Document("userId", UserSession.getUser().getId())
        ).first();

        if (plan == null) return;

        for (String dan : daniMap.keySet()) {

            Document d = plan.get(dan, Document.class);
            if (d == null) continue;

            JComboBox[] cb = daniMap.get(dan);

            cb[0].setSelectedItem(d.getString("uzina"));
            cb[1].setSelectedItem(d.getString("dorucak"));
            cb[2].setSelectedItem(d.getString("rucak"));
            cb[3].setSelectedItem(d.getString("vecera"));
        }
    }

    // ================= SAVE / UPDATE =================
    private void sacuvajSedmicniPlan() {

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("sedmicni_planer");

        Document data = new Document("userId", UserSession.getUser().getId());

        for (String dan : daniMap.keySet()) {

            JComboBox[] cb = daniMap.get(dan);

            data.append(dan, new Document()
                    .append("uzina", cb[0].getSelectedItem())
                    .append("dorucak", cb[1].getSelectedItem())
                    .append("rucak", cb[2].getSelectedItem())
                    .append("vecera", cb[3].getSelectedItem())
            );
        }

        data.append("updatedAt", System.currentTimeMillis());

        col.updateOne(
                new Document("userId", UserSession.getUser().getId()),
                new Document("$set", data),
                new com.mongodb.client.model.UpdateOptions().upsert(true)
        );

        JOptionPane.showMessageDialog(
                this,
                "Sedmični plan je uspješno sačuvan ✔",
                "Sačuvano",
                JOptionPane.INFORMATION_MESSAGE
        );
    }



    private void ocistiComboBoxove() {

        int izbor = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da želite očistiti sve obroke?",
                "Potvrda",
                JOptionPane.YES_NO_OPTION
        );

        if (izbor == JOptionPane.YES_OPTION) {
            for (JComboBox cb : sviComboBoxovi) {
                cb.setSelectedIndex(0);
            }
        }
    }
}
