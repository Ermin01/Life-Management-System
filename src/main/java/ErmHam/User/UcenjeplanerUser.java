package ErmHam.User;

import ErmHam.Admin.Ucenje;
import ErmHam.Database.Bazapodataka;
import ErmHam.UserSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UcenjeplanerUser extends JFrame {

    private JPanel GlavniProzorUserucenja;
    private JComboBox<String> predmetiCombobox;
    private JComboBox<String> ciljeviCombobox;
    private JComboBox<String> vrijemepodanuCombobox;
    private JComboBox<String> Trajanjecombobx;
    private JComboBox<String> PrioreitetCombox;
    private JTextField Napomena;
    private JTable UcenjePlanerUserTable;
    private JButton sacuvajButton;
    private JButton poceoButton;
    private JButton zavrsioButton;

    // STATISTIKA
    private JLabel Ukupnoplanova;
    private JLabel zavresno;
    private JLabel uToku;
    private JLabel nijeZapoceto;
    private JProgressBar progressBar1;

    private final List<Ucenje> ucenje = new ArrayList<>();

    public UcenjeplanerUser() {

        setTitle("Planer učenja");
        setContentPane(GlavniProzorUserucenja);
        setSize(1100, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComboboxes();
        refreshUI();
        popuniField();


        Napomena.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        sacuvajButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        sacuvajButton.setBackground(new Color(69, 104, 130));
        sacuvajButton.setForeground(Color.WHITE);

        poceoButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        poceoButton.setBackground(new Color(69, 104, 130));
        poceoButton.setForeground(Color.WHITE);

        zavrsioButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        zavrsioButton.setBackground(new Color(69, 104, 130));
        zavrsioButton.setForeground(Color.WHITE);


        sacuvajButton.addActionListener(e -> dodajPlanucnjeaUsera());
        poceoButton.addActionListener(e -> promijeniStatus("POCEO"));
        zavrsioButton.addActionListener(e -> promijeniStatus("ZAVRSENO"));
    }


    private void initComboboxes() {
        predmetiCombobox.addItem("Odaberi predmet");
        predmetiCombobox.addItem("Bosanski jezik");
        predmetiCombobox.addItem("Engleski jezik");
        predmetiCombobox.addItem("Njemački jezik");
        predmetiCombobox.addItem("Matematika");
        predmetiCombobox.addItem("Informatika");

        ciljeviCombobox.addItem("Odaberi cilj");
        ciljeviCombobox.addItem("Naučiti novo gradivo");
        ciljeviCombobox.addItem("Ponoviti gradivo");
        ciljeviCombobox.addItem("Vježbati zadatke");

        vrijemepodanuCombobox.addItem("Odaberi vrijeme");
        vrijemepodanuCombobox.addItem("Jutro");
        vrijemepodanuCombobox.addItem("Popodne");
        vrijemepodanuCombobox.addItem("Večer");

        Trajanjecombobx.addItem("Odaberi trajanje");
        Trajanjecombobx.addItem("30 minuta");
        Trajanjecombobx.addItem("1 sat");
        Trajanjecombobx.addItem("1 sat 30 min");

        PrioreitetCombox.addItem("Odaberi prioritet");
        PrioreitetCombox.addItem("Nizak");
        PrioreitetCombox.addItem("Srednji");
        PrioreitetCombox.addItem("Visok");
    }



    private void refreshUI() {
        loadFromDB();
        UcitajtabeluUsersucenja();
        osvjeziStatistiku();
    }

    public void UcitajtabeluUsersucenja(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Predmet");
        model.addColumn("Ciljevi");
        model.addColumn("Vrijeme po danu");
        model.addColumn("Trajanje");
        model.addColumn("Prioritet");
        model.addColumn("Status");
        for (Ucenje t : ucenje) {
            model.addRow(new Object[]{
                    t.getPredmet(),
                    t.getCiljevi(),
                    t.getVrijemepodanu(),
                    t.getTrajanje(),
                    t.getPrioritet(),
                    t.getStatus()
            });
        }
        UcenjePlanerUserTable.setModel(model);
    }



    private void loadFromDB() {
        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("userucenja_paner");

        String userId = UserSession.getUser().getId();

        ucenje.clear();

        for (Document d : col.find(new Document("userId", userId))) {
            ucenje.add(new Ucenje(
                    d.getString("predmet"),
                    d.getString("ciljevi"),
                    d.getString("vrijemepodanu"),
                    d.getString("napomena"),
                    d.getString("prioritet"),
                    d.getString("status"),
                    d.getString("trajanje")
            ));
        }
    }

    private void dodajPlanucnjeaUsera() {

        if (predmetiCombobox.getSelectedIndex() == 0 ||
                ciljeviCombobox.getSelectedIndex() == 0 ||
                vrijemepodanuCombobox.getSelectedIndex() == 0 ||
                Trajanjecombobx.getSelectedIndex() == 0 ||
                PrioreitetCombox.getSelectedIndex() == 0 ||
                Napomena.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
            return;
        }

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("userucenja_paner");

        Document doc = new Document()
                .append("userId", UserSession.getUser().getId())
                .append("username", UserSession.getUser().getUsername()) // ✅ DODANO
                .append("predmet", predmetiCombobox.getSelectedItem())
                .append("ciljevi", ciljeviCombobox.getSelectedItem())
                .append("vrijemepodanu", vrijemepodanuCombobox.getSelectedItem())
                .append("trajanje", Trajanjecombobx.getSelectedItem())
                .append("napomena", Napomena.getText())
                .append("prioritet", PrioreitetCombox.getSelectedItem())
                .append("status", "NIJE_ZAPOCETO");

        col.insertOne(doc);
        refreshUI();
    }

    private void promijeniStatus(String noviStatus) {

        int row = UcenjePlanerUserTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Odaberi plan u tabeli!");
            return;
        }

        String predmet = UcenjePlanerUserTable.getValueAt(row, 0).toString();
        String cilj = UcenjePlanerUserTable.getValueAt(row, 1).toString();
        String vrijeme = UcenjePlanerUserTable.getValueAt(row, 2).toString();

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("userucenja_paner");

        col.updateOne(
                new Document("userId", UserSession.getUser().getId())
                        .append("predmet", predmet)
                        .append("ciljevi", cilj)
                        .append("vrijemepodanu", vrijeme),
                new Document("$set", new Document("status", noviStatus))
        );

        refreshUI();
    }

    // ================= STATISTIKA =================

    private void osvjeziStatistiku() {

        int ukupno = ucenje.size();
        int zavrsenoCount = 0;
        int uTokuCount = 0;
        int nijeZapocetoCount = 0;

        for (Ucenje u : ucenje) {
            if (u.getStatus() == null) continue;

            switch (u.getStatus()) {
                case "ZAVRSENO": zavrsenoCount++; break;
                case "POCEO": uTokuCount++; break;
                case "NIJE_ZAPOCETO": nijeZapocetoCount++; break;
            }
        }

        Ukupnoplanova.setText(String.valueOf(ukupno));
        zavresno.setText(String.valueOf(zavrsenoCount));
        uToku.setText(String.valueOf(uTokuCount));
        nijeZapoceto.setText(String.valueOf(nijeZapocetoCount));

        int procenat = ukupno == 0 ? 0 : (int) ((zavrsenoCount * 100.0) / ukupno);
        progressBar1.setValue(procenat);
        progressBar1.setStringPainted(true);
        progressBar1.setString(procenat + "% završeno");
    }


    private void popuniField() {
        UcenjePlanerUserTable.getSelectionModel().addListSelectionListener(e -> {
            clearForm();
            int row = UcenjePlanerUserTable.getSelectedRow();

            if (row == -1) return;

            predmetiCombobox.setSelectedItem(UcenjePlanerUserTable.getValueAt(row, 0));
            ciljeviCombobox.setSelectedItem(UcenjePlanerUserTable.getValueAt(row, 1));
            vrijemepodanuCombobox.setSelectedItem(UcenjePlanerUserTable.getValueAt(row, 2));
            Trajanjecombobx.setSelectedItem(UcenjePlanerUserTable.getValueAt(row, 3));
            PrioreitetCombox.setSelectedItem(UcenjePlanerUserTable.getValueAt(row, 4));
            Napomena.setText(UcenjePlanerUserTable.getValueAt(row, 5).toString());


        });



    }
    private void clearForm() {
        predmetiCombobox.setSelectedIndex(0);
        ciljeviCombobox.setSelectedIndex(0);
        vrijemepodanuCombobox.setSelectedIndex(0);
        Trajanjecombobx.setSelectedIndex(0);
        PrioreitetCombox.setSelectedIndex(0);
        Napomena.setText("");

    }
}
