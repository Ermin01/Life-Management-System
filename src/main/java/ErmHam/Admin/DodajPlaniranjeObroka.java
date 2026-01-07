package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import ErmHam.Main;
import ErmHam.Obrok;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DodajPlaniranjeObroka extends JFrame {

    private JPanel PracenjenavikaProzor;
    private JTextField nazivObroka;
    private JTable Obroktable;
    private JComboBox comboBoxtipObroka;
    private JButton obrisiObrokbutton;
    private JComboBox KategorijapracanjeNavika;
    private JComboBox tipobrokaD;
    private JComboBox kalorijaD;
    private JTextField pretrazivanjeObroka;
    private JTextField kalorije;
    private JTextField proteini;
    private JButton btnObroka;
    private JButton urediObrok;
    private JButton ocistiButton;

    private List<Obrok> obroci = new ArrayList<>();
    private TableRowSorter<DefaultTableModel> sorter;

    public DodajPlaniranjeObroka() {

        setTitle("Praćenje navika");
        setContentPane(PracenjenavikaProzor);
        setSize(1100, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        Image icon = new ImageIcon(
                Main.class.getResource("/imgdeskop.jpg")
        ).getImage();

        setIconImage(icon);

        comboBoxtipObroka.addItem("Odaberi");
        comboBoxtipObroka.addItem("Užina");
        comboBoxtipObroka.addItem("Doručak");
        comboBoxtipObroka.addItem("Ručak");
        comboBoxtipObroka.addItem("Večera");

        KategorijapracanjeNavika.addItem("Odaberi");
        KategorijapracanjeNavika.addItem("Zdravo");
        KategorijapracanjeNavika.addItem("Fitness");
        KategorijapracanjeNavika.addItem("Balansirano");
        KategorijapracanjeNavika.addItem("Cheat meal");
        KategorijapracanjeNavika.addItem("Vegetarijansko");
        KategorijapracanjeNavika.addItem("Vegan");
        KategorijapracanjeNavika.addItem("Dijetalno");

        // FILTERI
        tipobrokaD.addItem("Svi");
        tipobrokaD.addItem("Užina");
        tipobrokaD.addItem("Doručak");
        tipobrokaD.addItem("Ručak");
        tipobrokaD.addItem("Večera");

        kalorijaD.addItem("Sve");
        kalorijaD.addItem("< 300");
        kalorijaD.addItem("300 - 600");
        kalorijaD.addItem("> 600");

        ucitajTabeluDB();
        loadTable();
        popuniField();
        initFilters();


        btnObroka.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        btnObroka.setBackground(new Color(69, 104, 130));
        btnObroka.setForeground(Color.WHITE);

        obrisiObrokbutton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        obrisiObrokbutton.setBackground(new Color(69, 104, 130));
        obrisiObrokbutton.setForeground(Color.WHITE);

        ocistiButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        ocistiButton.setBackground(new Color(69, 104, 130));
        ocistiButton.setForeground(Color.WHITE);

        urediObrok.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        urediObrok.setBackground(new Color(69, 104, 130));
        urediObrok.setForeground(Color.WHITE);

        nazivObroka.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        comboBoxtipObroka.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        KategorijapracanjeNavika.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        kalorije.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        proteini.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnObroka.addActionListener(e -> sacuvajObrok());
        ocistiButton.addActionListener(e -> clearForm());
        obrisiObrokbutton.addActionListener(e -> obrisiObrok());
        urediObrok.addActionListener(e -> urediObrok());
    }

    // ================= FILTERI =================

    private void initFilters() {
        sorter = new TableRowSorter<>((DefaultTableModel) Obroktable.getModel());
        Obroktable.setRowSorter(sorter);

        tipobrokaD.addActionListener(e -> applyFilters());
        kalorijaD.addActionListener(e -> applyFilters());

        pretrazivanjeObroka.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
    }

    private void applyFilters() {
        List<RowFilter<Object,Object>> filters = new ArrayList<>();

        // pretraga po nazivu
        String text = pretrazivanjeObroka.getText();
        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + text, 0));
        }

        // tip obroka
        if (!tipobrokaD.getSelectedItem().equals("Svi")) {
            filters.add(RowFilter.regexFilter(tipobrokaD.getSelectedItem().toString(), 1));
        }

        // kalorije
        String kcal = kalorijaD.getSelectedItem().toString();
        if (!kcal.equals("Sve")) {
            filters.add(new RowFilter<>() {
                public boolean include(Entry<?, ?> e) {
                    int v = Integer.parseInt(e.getStringValue(3));
                    return switch (kcal) {
                        case "< 300" -> v < 300;
                        case "300 - 600" -> v >= 300 && v <= 600;
                        case "> 600" -> v > 600;
                        default -> true;
                    };
                }
            });
        }

        sorter.setRowFilter(RowFilter.andFilter(filters));
    }



    public void sacuvajObrok() {
        if (nazivObroka.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
            return;
        }

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("obroci");

        col.insertOne(new Document()
                .append("nazivObroka", nazivObroka.getText())
                .append("tipObroka", comboBoxtipObroka.getSelectedItem())
                .append("kategorija", KategorijapracanjeNavika.getSelectedItem())
                .append("kalorije", Integer.parseInt(kalorije.getText()))
                .append("proteini", Integer.parseInt(proteini.getText()))
                .append("active", true)
        );

        refresh();
    }

    private void urediObrok() {
        int row = Obroktable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Odaberi obrok za uređivanje!");
            return;
        }

        String stariNaziv = Obroktable.getValueAt(row, 0).toString();

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("obroci");

        col.updateOne(
                new Document("nazivObroka", stariNaziv),
                new Document("$set", new Document()
                        .append("nazivObroka", nazivObroka.getText())
                        .append("tipObroka", comboBoxtipObroka.getSelectedItem())
                        .append("kategorija", KategorijapracanjeNavika.getSelectedItem())
                        .append("kalorije", Integer.parseInt(kalorije.getText()))
                        .append("proteini", Integer.parseInt(proteini.getText()))
                )
        );

        refresh();
    }

    private void refresh() {
        ucitajTabeluDB();
        loadTable();
        clearForm();
        initFilters();
    }


    private void ucitajTabeluDB() {
        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> collection = db.getCollection("obroci");
        obroci.clear();

        for (Document d : collection.find()) {
            obroci.add(new Obrok(
                    d.getString("nazivObroka"),
                    d.getString("tipObroka"),
                    d.getString("kategorija"),
                    d.getInteger("kalorije"),
                    d.getInteger("proteini"),
                    d.getBoolean("active", true)
            ));
        }
    }


    private void loadTable(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("NAZIV OBROKA");
        model.addColumn("TIP OBROKA");
        model.addColumn("KATEGORIJA");
        model.addColumn("KALORIJE");
        model.addColumn("PROTEINI");
        model.addColumn("AKTIVNOST");


        for (Obrok t : obroci) {
            model.addRow(new Object[]{
                    t.getNazivObrok(),
                    t.getTipoObroka(),
                    t.getKategorija(),
                    t.getKalorije(),
                    t.getProteini(),
                    t.isActive()
            });
        }

        Obroktable.setModel(model);
    }


    private void popuniField() {
        Obroktable.getSelectionModel().addListSelectionListener(e -> {
            int r = Obroktable.getSelectedRow();
            if (r == -1) return;

            nazivObroka.setText(Obroktable.getValueAt(r,0).toString());
            comboBoxtipObroka.setSelectedItem(Obroktable.getValueAt(r,1));
            KategorijapracanjeNavika.setSelectedItem(Obroktable.getValueAt(r,2));
            kalorije.setText(Obroktable.getValueAt(r,3).toString());
            proteini.setText(Obroktable.getValueAt(r,4).toString());
        });
    }

    private void clearForm() {
        nazivObroka.setText("");
        comboBoxtipObroka.setSelectedIndex(0);
        KategorijapracanjeNavika.setSelectedIndex(0);
        kalorije.setText("");
        proteini.setText("");
    }

    private void obrisiObrok() {
        int row = Obroktable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Odaberi obrok koji želiš obrisati!",
                    "Upozorenje",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String naziv = Obroktable.getValueAt(row, 0).toString();

        int potvrda = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da želite obrisati obrok:\n\n" + naziv + " ?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (potvrda != JOptionPane.YES_OPTION) {
            return;
        }

        MongoDatabase db = Bazapodataka.getDatabase();
        db.getCollection("obroci")
                .deleteOne(new Document("nazivObroka", naziv));

        refresh();

        JOptionPane.showMessageDialog(
                this,
                "Obrok je uspješno obrisan.",
                "Obrisano",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
