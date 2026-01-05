//package ErmHam.MainMenu;
//
//import ErmHam.Admin.DodajKorisnika;
//import ErmHam.Admin.DodajPlaniranjeObroka;
//import ErmHam.Admin.Pracenjefinancija;
//import ErmHam.Admin.PregledKorisnika;
//import ErmHam.LoginForm.Loginform;
//import ErmHam.User.Podacioracunu;
//import ErmHam.UserSession;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class MainMenuForm  {
//
//    private JPanel GlavniProzor;
//    private JPanel adminPlaceholder;
//
//    private JButton dodajKorisnikaButton;
//    private JButton PracenjefinancijaButton;
//    private JButton pregledKorisnikaButton;
//    private JButton Pracenjenavika;
//    private JButton planiranjeUcenjaButton;
//    private JButton podaciORacunuButton;
//    private JButton odjaviseButton;
//    private String role;
//
//
//
//
//    public MainMenuForm(String role) {
//        this.role = role;
//        applyRolePermissions();
//        Akcije();
//        dodajKorisnikaButton.setBackground(new Color(69, 104, 130));
//        dodajKorisnikaButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
//
//
//        pregledKorisnikaButton.setBackground(new Color(69, 104, 130));
//        pregledKorisnikaButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
//
//        podaciORacunuButton.setBackground(new Color(69, 104, 130));
//        podaciORacunuButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
//
//
//        PracenjefinancijaButton.setBackground(new Color(69, 104, 130));
//        PracenjefinancijaButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
//
//        Pracenjenavika.setBackground(new Color(69, 104, 130));
//        Pracenjenavika.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
//
//        planiranjeUcenjaButton.setBackground(new Color(69, 104, 130));
//        planiranjeUcenjaButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
//
//        odjaviseButton.setBackground(new Color(69, 104, 130));
//        odjaviseButton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//    }
//
//    private void applyRolePermissions() {
//        if ("SUPERADMIN".equals(role)) {
//            dodajKorisnikaButton.setVisible(true);
//            pregledKorisnikaButton.setVisible(true);
//            podaciORacunuButton.setVisible(true);
//            PracenjefinancijaButton.setVisible(true);
//
//        } else {
//            podaciORacunuButton.setVisible(true);
//            PracenjefinancijaButton.setVisible(true);
//            dodajKorisnikaButton.setVisible(false);
//            pregledKorisnikaButton.setVisible(false);
//        }
//    }
//
//    public void Akcije() {
//        dodajKorisnikaButton.addActionListener(e -> {
//            new DodajKorisnika().setVisible(true);
//        });
//        pregledKorisnikaButton.addActionListener(e -> {
//            new PregledKorisnika().setVisible(true);
//        });
//        podaciORacunuButton.addActionListener(e -> {
//            new Podacioracunu().setVisible(true);
//        });
//        PracenjefinancijaButton.addActionListener(e -> {
//            new Pracenjefinancija(role).setVisible(true);
//        });
//        Pracenjenavika.addActionListener(e -> {
//            new DodajPlaniranjeObroka().setVisible(true);
//        });
//        odjaviseButton.addActionListener(e -> odjaviSe());
//
//    }
//
//
////    DESGIN BUTTON-NA
//
//
//    private void odjaviSe() {
//        int izbor = JOptionPane.showConfirmDialog(
//                GlavniProzor,
//                "Da li ste sigurni da se želite odjaviti?",
//                "Odjava",
//                JOptionPane.YES_NO_OPTION
//        );
//
//        if (izbor == JOptionPane.YES_OPTION) {
//
//            // očisti session
//            UserSession.clear();
//
//            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GlavniProzor);
//            frame.setContentPane(new Loginform().Glavniprozor());
//            frame.revalidate();
//            frame.repaint();
//        }
//    }
//
//
//
//
//
//    public JPanel getPanel() {
//        return GlavniProzor;
//    }
////}
//
//package ErmHam.Admin;
//
//import ErmHam.Database.Bazapodataka;
//import ErmHam.Transakcija;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Pracenjefinancija extends JFrame {
//
//    private JPanel GlavniProzorPracenjeFina;
//    private JTable pregledTableFinacija;
//    private JComboBox<String> tipTransakcijeBox;
//    private JComboBox<String> katagroijaBox;
//    private JTextField Iznos;
//    private JTextField Opis;
//    private JTextField datum;
//    private JTextField SaldoField;
//    private JButton sacuvajBtn;
//    private JButton exportPDF;
//
//    private JLabel tipTransakcije;
//    private JLabel kategorija;
//    private JLabel iznos;
//    private JLabel opis;
//    private JLabel datumlabel;
//    private JLabel Saldo;
//    private JButton OcistiBtn;
//    private JPanel statistikaPanel;
//
//    private String role;
//    private List<Transakcija> transakcije = new ArrayList<>();
//
//    public Pracenjefinancija(String role) {
//        this.role = role;
//
//        setTitle("Praćenje financija");
//        setContentPane(GlavniProzorPracenjeFina);
//        setSize(900, 450);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        tipTransakcijeBox.addItem("Odaberi");
//        tipTransakcijeBox.addItem("Prihodi");
//        tipTransakcijeBox.addItem("Rashodi");
//
//        katagroijaBox.addItem("Odaberi");
//        katagroijaBox.addItem("Hrana");
//        katagroijaBox.addItem("Računi");
//        katagroijaBox.addItem("Ostalo");
//
//        SaldoField.setEditable(false); // 🔒 samo prikaz
//
//        sacuvajBtn.addActionListener(e -> dodajTransakciju());
//        OcistiBtn.addActionListener(e -> clearForm());
//
//        applyRoleView();
//        loadFromDB();
//        loadTable();
//        styleTable();
//        popuniField();
//        prikaziSaldo();
//
//
//        sacuvajBtn.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
//        sacuvajBtn.setBackground(new Color(69, 104, 130));
//        sacuvajBtn.setForeground(Color.WHITE);
//
//        OcistiBtn.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
//        OcistiBtn.setBackground(new Color(69, 104, 130));
//        OcistiBtn.setForeground(Color.WHITE);
//
//        exportPDF.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
//        exportPDF.setBackground(new Color(69, 104, 130));
//        exportPDF.setForeground(Color.WHITE);
//
//
//        Iznos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        Opis.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        datum.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        SaldoField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//    }
//
//    // ---------------- UI ----------------
//
//    private void styleTable() {
//        pregledTableFinacija.setRowHeight(28);
//        pregledTableFinacija.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        pregledTableFinacija.setShowVerticalLines(false);
//        pregledTableFinacija.setShowHorizontalLines(true);
//    }
//
//    private void applyRoleView() {
//        if ("USER".equals(role)) {
//            tipTransakcijeBox.setVisible(false);
//            katagroijaBox.setVisible(false);
//            Iznos.setVisible(false);
//            Opis.setVisible(false);
//            datum.setVisible(false);
//            sacuvajBtn.setVisible(false);
//            exportPDF.setVisible(false);
//            tipTransakcije.setText("");
//            kategorija.setText("");
//            iznos.setText("");
//            opis.setText("");
//            datumlabel.setText("");
//            datum.setVisible(false);
//            OcistiBtn.setVisible(false);
//        }
//    }
//
//
//    private void loadFromDB() {
//        MongoDatabase db = Bazapodataka.getDatabase();
//        MongoCollection<Document> col = db.getCollection("transakcije");
//
//        transakcije.clear();
//
//        for (Document d : col.find()) {
//            transakcije.add(new Transakcija(
//                    d.getString("tip"),
//                    d.getString("kategorija"),
//                    d.getDouble("iznos"),
//                    d.getString("opis"),
//                    0 // saldo se NE uzima iz baze
//            ));
//        }
//    }
//
//
//    private void loadTable() {
//        DefaultTableModel model = new DefaultTableModel();
//        model.addColumn("Tip");
//        model.addColumn("Kategorija");
//        model.addColumn("Iznos");
//        model.addColumn("Opis");
//
//        for (Transakcija t : transakcije) {
//            model.addRow(new Object[]{
//                    t.getTipTransakcijeBox(),
//                    t.getKategorijaBox(),
//                    t.getIznos(),
//                    t.getOpis()
//            });
//        }
//
//        pregledTableFinacija.setModel(model);
//    }
//
//    // ---------------- LOGIKA ----------------
//
//    private void dodajTransakciju() {
//
//        if (tipTransakcijeBox.getSelectedIndex() == 0 ||
//                katagroijaBox.getSelectedIndex() == 0 ||
//                Iznos.getText().isEmpty() ||
//                Opis.getText().isEmpty()) {
//
//            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
//            return;
//        }
//
//        try {
//            double iznos = Double.parseDouble(Iznos.getText());
//
//            MongoDatabase db = Bazapodataka.getDatabase();
//            MongoCollection<Document> col = db.getCollection("transakcije");
//
//            col.insertOne(new Document()
//                    .append("tip", tipTransakcijeBox.getSelectedItem())
//                    .append("kategorija", katagroijaBox.getSelectedItem())
//                    .append("iznos", iznos)
//                    .append("opis", Opis.getText())
//                    .append("datum", datum.getText())
//            );
//
//            loadFromDB();
//            loadTable();
//            prikaziSaldo();
//            clearForm();
//
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(this, "Iznos mora biti broj!");
//        }
//    }
//
//    private double izracunajSaldo() {
//        double saldo = 0;
//
//        for (Transakcija t : transakcije) {
//            if ("Prihodi".equals(t.getTipTransakcijeBox())) {
//                saldo += t.getIznos();
//            } else {
//                saldo -= t.getIznos();
//            }
//        }
//        return saldo;
//    }
//
//    private void prikaziSaldo() {
//        SaldoField.setText(String.format("%.2f", izracunajSaldo()));
//    }
//
//
//
//    private void popuniField() {
//        pregledTableFinacija.getSelectionModel().addListSelectionListener(e -> {
//            if (e.getValueIsAdjusting()) return;
//
//            int row = pregledTableFinacija.getSelectedRow();
//            if (row == -1) return;
//
//            tipTransakcijeBox.setSelectedItem(pregledTableFinacija.getValueAt(row, 0));
//            katagroijaBox.setSelectedItem(pregledTableFinacija.getValueAt(row, 1));
//            Iznos.setText(pregledTableFinacija.getValueAt(row, 2).toString());
//            Opis.setText(pregledTableFinacija.getValueAt(row, 3).toString());
//        });
//    }
//
//    private void clearForm() {
//        tipTransakcijeBox.setSelectedIndex(0);
//        katagroijaBox.setSelectedIndex(0);
//        Iznos.setText("");
//        Opis.setText("");
//        datum.setText("");
//    }
//
//}
//
////git add .
////git commit -m "Promjene na login formi"
//git push