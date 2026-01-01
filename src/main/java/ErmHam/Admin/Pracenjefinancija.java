package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import ErmHam.Transakcija;
import ErmHam.Users;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Pracenjefinancija extends JFrame {

    private JPanel GlavniProzorPracenjeFina;
    private JTable pregledTableFinacija;
    private JComboBox<String> tipTransakcijeBox;
    private JComboBox<String> katagroijaBox;
    private JTextField Iznos;
    private JTextField Opis;
    private JTextField datum;
    private JTextField SaldoField;
    private JButton sacuvajBtn;
    private JButton exportPDF;

    private JLabel tipTransakcije;
    private JLabel kategorija;
    private JLabel iznos;
    private JLabel opis;
    private JLabel datumlabel;
    private JLabel Saldo;
    private JButton OcistiBtn;

    private String role;
    private List<Transakcija> transakcije = new ArrayList<>();

    public Pracenjefinancija(String role) {
        this.role = role;

        setTitle("Praćenje financija");
        setContentPane(GlavniProzorPracenjeFina);
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tipTransakcijeBox.addItem("Odaberi");
        tipTransakcijeBox.addItem("Prihodi");
        tipTransakcijeBox.addItem("Rashodi");

        katagroijaBox.addItem("Odaberi");
        katagroijaBox.addItem("Hrana");
        katagroijaBox.addItem("Računi");
        katagroijaBox.addItem("Ostalo");

        sacuvajBtn.addActionListener(e -> dodajTransakciju());

        applyRoleView();
        loadFromDB();
        loadTable();
        styleTable();
        popuniField();
        izracunajSaldo();
        prikaziSaldo();

        OcistiBtn.addActionListener(e -> clearForm());
    }

    private void styleTable() {
        pregledTableFinacija.setRowHeight(28);
        pregledTableFinacija.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pregledTableFinacija.setShowVerticalLines(false);
        pregledTableFinacija.setShowHorizontalLines(true);
    }

    private void applyRoleView() {
        if ("USER".equals(role)) {
            tipTransakcijeBox.setVisible(false);
            katagroijaBox.setVisible(false);
            Iznos.setVisible(false);
            Opis.setVisible(false);
            datum.setVisible(false);
            SaldoField.setVisible(false);
            sacuvajBtn.setVisible(false);
            exportPDF.setVisible(false);
        }
    }

    private void loadFromDB() {
        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("transakcije");

        transakcije.clear();

        for (Document d : col.find()) {

            Number iznosNum = d.get("iznos", Number.class);
            Number saldoNum = d.get("saldo", Number.class);

            double iznos = iznosNum != null ? iznosNum.doubleValue() : 0;
            double saldo = saldoNum != null ? saldoNum.doubleValue() : 0;

            transakcije.add(new Transakcija(
                    d.getString("tip"),
                    d.getString("kategorija"),
                    iznos,
                    d.getString("opis"),
                    saldo
            ));
        }
    }


        private void loadTable() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Tip transakcije");
        model.addColumn("Kategorija");
        model.addColumn("Iznos");
        model.addColumn("Opis");

         for (Transakcija t : transakcije) {
               model.addRow(new Object[]{
                t.getTipTransakcijeBox(),
                    t.getKategorijaBox(),
                    t.getIznos(),
                    t.getOpis(),
        });
       }
          pregledTableFinacija.setModel(model);
    }


    private void dodajTransakciju() {

        if (tipTransakcijeBox.getSelectedIndex() == 0 ||
                katagroijaBox.getSelectedIndex() == 0 ||
                Iznos.getText().isEmpty() ||
                SaldoField.getText().isEmpty() ||
                Opis.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
            return;
        }

        try {
            double iznos = Double.parseDouble(Iznos.getText());
            double saldo = Double.parseDouble(SaldoField.getText());

            MongoDatabase db = Bazapodataka.getDatabase();
            MongoCollection<Document> col = db.getCollection("transakcije");

            col.insertOne(new Document()
                    .append("tip", tipTransakcijeBox.getSelectedItem())
                    .append("kategorija", katagroijaBox.getSelectedItem())
                    .append("iznos", iznos)
                    .append("opis", Opis.getText())
                    .append("saldo", saldo)
                    .append("datum", datum.getText())
            );

            loadFromDB();
            loadTable();
            clearForm();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Iznos i saldo moraju biti brojevi!");
        }
    }

    public void popuniField(){
        pregledTableFinacija.getSelectionModel().addListSelectionListener(e ->{
            if (e.getValueIsAdjusting()) return;

            int row = pregledTableFinacija.getSelectedRow();
            if (row == -1) return;

            tipTransakcijeBox.setSelectedItem(pregledTableFinacija.getValueAt(row, 0).toString());
            katagroijaBox.setSelectedItem(pregledTableFinacija.getValueAt(row, 1).toString());
            Iznos.setText(pregledTableFinacija.getValueAt(row,  2).toString());
            Opis.setText(pregledTableFinacija.getValueAt(row,  3).toString());
        });
    }


    private double izracunajSaldo() {
        double saldo = 0;

        for (Transakcija t : transakcije) {
            if ("Prihodi".equals(t.getTipTransakcijeBox())) {
                saldo += t.getIznos();
            } else if ("Rashodi".equals(t.getTipTransakcijeBox())) {
                saldo -= t.getIznos();
            }
        }

        return saldo;
    }
    private void prikaziSaldo() {
        double saldo = izracunajSaldo();
        SaldoField.setText(String.format("%.2f", saldo));
    }


    private void clearForm() {
        tipTransakcijeBox.setSelectedIndex(0);
        katagroijaBox.setSelectedIndex(0);
        Iznos.setText("");
        Opis.setText("");
        datum.setText("");
        SaldoField.setText("");
    }
}
