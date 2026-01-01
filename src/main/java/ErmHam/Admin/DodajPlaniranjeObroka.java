package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import ErmHam.Obrok;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DodajPlaniranjeObroka extends  JFrame {
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
    private JLabel Ukupnoobroka;
    private List<Obrok> obroci = new ArrayList<>();



    public DodajPlaniranjeObroka(){
        setTitle("Praćenje navika");
        setContentPane(PracenjenavikaProzor);
        setSize(1100, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


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

        ucitajTabeluDB();
        loadTable();
        clearForm();
        popuniField();

        Obroktable.setRowHeight(28);
        Obroktable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        Obroktable.setGridColor(new Color(65, 92, 112));
        Obroktable.setShowVerticalLines(false);
        Obroktable.setShowHorizontalLines(true);

        btnObroka.addActionListener(e-> sacuvajObrok());
        ocistiButton.addActionListener(e-> clearForm());
        obrisiObrokbutton.addActionListener(e-> obrisiObrok());


        ocistiButton.setText("Očisti");
        ocistiButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        ocistiButton.setBackground(new Color(0,120,208));
        ocistiButton.setForeground(Color.WHITE);
        ocistiButton.setOpaque(true);
        ocistiButton.setContentAreaFilled(true);
        ocistiButton.setFocusPainted(false);
        ocistiButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ocistiButton.setMargin(new Insets(12, 30, 12, 30));
        ocistiButton.setBorder(BorderFactory.createLineBorder(new Color(0,120,208), 2, true));


        btnObroka.setText("Sačuvaj");
        btnObroka.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnObroka.setBackground(new Color(0,120,208));
        btnObroka.setForeground(Color.WHITE);
        btnObroka.setOpaque(true);
        btnObroka.setContentAreaFilled(true);
        btnObroka.setFocusPainted(false);
        btnObroka.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnObroka.setMargin(new Insets(12, 30, 12, 30));
        btnObroka.setBorder(BorderFactory.createLineBorder(new Color(0,120,208), 2, true));




        obrisiObrokbutton.setText("Obriši");
        obrisiObrokbutton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        obrisiObrokbutton.setBackground(new Color(0,120,208));
        obrisiObrokbutton.setForeground(Color.WHITE);
        obrisiObrokbutton.setOpaque(true);
        obrisiObrokbutton.setContentAreaFilled(true);
        obrisiObrokbutton.setFocusPainted(false);
        obrisiObrokbutton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        obrisiObrokbutton.setMargin(new Insets(12, 30, 12, 30));
        obrisiObrokbutton.setBorder(BorderFactory.createLineBorder(new Color(0,120,208), 2, true));



        urediObrok.setText("Uredi");
        urediObrok.setFont(new Font("Segoe UI", Font.BOLD, 16));
        urediObrok.setBackground(new Color(0,120,208));
        urediObrok.setForeground(Color.WHITE);
        urediObrok.setOpaque(true);
        urediObrok.setContentAreaFilled(true);
        urediObrok.setFocusPainted(false);
        urediObrok.setCursor(new Cursor(Cursor.HAND_CURSOR));
        urediObrok.setMargin(new Insets(12, 30, 12, 30));
        urediObrok.setBorder(BorderFactory.createLineBorder(new Color(0,120,208), 2, true));
    }

    public void sacuvajObrok(){
        if (nazivObroka.getText().isEmpty()||
                comboBoxtipObroka.getSelectedIndex() == 0 ||
                KategorijapracanjeNavika.getSelectedIndex() == 0 ||
                kalorije.getText().isEmpty()||
                proteini.getText().isEmpty())

        {
            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
            return;

        }
        try{
            MongoDatabase db = Bazapodataka.getDatabase();
            MongoCollection<Document> col = db.getCollection("obroci");

            col.insertOne(new Document()
                    .append("nazivObroka", nazivObroka.getText())
                    .append("tipObroka", comboBoxtipObroka.getSelectedItem().toString())
                    .append("kategorija", KategorijapracanjeNavika.getSelectedItem().toString())
                    .append("kalorije", Integer.parseInt(kalorije.getText()))
                    .append("proteini", Integer.parseInt(proteini.getText()))
                    .append("active", true)
            );

            ucitajTabeluDB();
            loadTable();
            clearForm();
        }catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Iznos i proteina kalorija moraju biti brojevi!");
        }

    }
    private void ucitajTabeluDB() {
        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> collection = db.getCollection("obroci");
        obroci.clear();

        for (Document d : collection.find()) {
            Obrok u = new Obrok(
                    d.getString("nazivObroka"),
                    String.valueOf(d.get("tipObroka")),
                    String.valueOf(d.get("kategorija")),
                    Integer.parseInt(d.get("kalorije").toString()),
                    Integer.parseInt(d.get("proteini").toString()),
                    d.getBoolean("active", true)
            );
            obroci.add(u);
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

    public void popuniField(){
        Obroktable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())return;

            int row = Obroktable.getSelectedRow();
            if(row == -1)return;

            nazivObroka.setText(Obroktable.getValueAt(row, 0).toString());
            comboBoxtipObroka.setSelectedItem(Obroktable.getValueAt(row,1).toString());
            KategorijapracanjeNavika.setSelectedItem(Obroktable.getValueAt(row,2).toString());
            kalorije.setText(Obroktable.getValueAt(row,3).toString());
            proteini.setText(Obroktable.getValueAt(row,4).toString());
        });
    }

    public void clearForm(){
        nazivObroka.setText("");
        comboBoxtipObroka.setSelectedIndex(0);
        KategorijapracanjeNavika.setSelectedIndex(0);
        kalorije.setText("");
        proteini.setText("");
    }

    public void obrisiObrok() {
        int row = Obroktable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Odaberi obrok za brisanje!");
            return;
        }

        int potvrda = JOptionPane.showConfirmDialog(
                this,
                "Da li si siguran da želiš obrisati obrok?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION
        );

        if (potvrda != JOptionPane.YES_OPTION) return;

        String naziv = Obroktable.getValueAt(row, 0).toString();

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("obroci");

        col.deleteOne(new Document("nazivObroka", naziv));

        ucitajTabeluDB();
        loadTable();
        clearForm();

        JOptionPane.showMessageDialog(this, "Obrok je obrisan.");
    }

}
