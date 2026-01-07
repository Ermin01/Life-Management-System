package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import ErmHam.Main;
import ErmHam.Transakcija;
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
    private JButton OcistiBtn;
    private JPanel statistikaPanel;

    private JLabel tipTransakcije;
    private JLabel kategorija;
    private JLabel iznos;
    private JLabel opis;
    private JLabel datumlabel;
    private JLabel Saldo;

    private String role;
    private List<Transakcija> transakcije = new ArrayList<>();

    public Pracenjefinancija(String role) {
        this.role = role;

        setTitle("Praćenje financija");
        setContentPane(GlavniProzorPracenjeFina);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        Image icon = new ImageIcon(
                Main.class.getResource("/imgdeskop.jpg")
        ).getImage();

        setIconImage(icon);

        tipTransakcijeBox.addItem("Odaberi");
        tipTransakcijeBox.addItem("Prihodi");
        tipTransakcijeBox.addItem("Rashodi");





        katagroijaBox.addItem("Odaberi");
        katagroijaBox.addItem("Hrana");
        katagroijaBox.addItem("Računi");
        katagroijaBox.addItem("Ostalo");

        SaldoField.setEditable(false);

        sacuvajBtn.addActionListener(e -> dodajTransakciju());
        OcistiBtn.addActionListener(e -> clearForm());
        exportPDF.addActionListener(e -> exportToPDF());

        applyRoleView();
        loadFromDB();
        loadTable();
        styleTable();
        popuniField();
        prikaziSaldo();


        // STATISTIKA PANEL
        statistikaPanel.setLayout(new BorderLayout());
        statistikaPanel.add(new BarChartPanel(), BorderLayout.CENTER);

        sacuvajBtn.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        sacuvajBtn.setBackground(new Color(69, 104, 130));
        sacuvajBtn.setForeground(Color.WHITE);

        OcistiBtn.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        OcistiBtn.setBackground(new Color(69, 104, 130));
        OcistiBtn.setForeground(Color.WHITE);

        exportPDF.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        exportPDF.setBackground(new Color(69, 104, 130));
        exportPDF.setForeground(Color.WHITE);


        Iznos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Opis.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        datum.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        SaldoField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }


    private void styleTable() {
        pregledTableFinacija.setRowHeight(28);
        pregledTableFinacija.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pregledTableFinacija.setShowVerticalLines(false);
    }

    private void applyRoleView() {
        if ("USER".equals(role)) {
            tipTransakcijeBox.setVisible(false);
            katagroijaBox.setVisible(false);
            Iznos.setVisible(false);
            Opis.setVisible(false);
            datum.setVisible(false);
            sacuvajBtn.setVisible(false);
            exportPDF.setVisible(false);
            OcistiBtn.setVisible(false);
            tipTransakcije.setText("");
            kategorija.setText("");
            iznos.setText("");
            opis.setText("");
            datum.setText("");

        }
    }


    private void loadFromDB() {
        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("transakcije");

        transakcije.clear();

        for (Document d : col.find()) {
            transakcije.add(new Transakcija(
                    d.getString("tip"),
                    d.getString("kategorija"),
                    d.getDouble("iznos"),
                    d.getString("opis"),
                    0 // saldo se NE uzima iz baze
            ));
        }
    }


    private void loadTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Tip");
        model.addColumn("Kategorija");
        model.addColumn("Iznos");
        model.addColumn("Opis");

        for (Transakcija t : transakcije) {
            model.addRow(new Object[]{
                    t.getTipTransakcijeBox(),
                    t.getKategorijaBox(),
                    t.getIznos(),
                    t.getOpis()
            });
        }

        pregledTableFinacija.setModel(model);
    }



        private void dodajTransakciju() {

        if (tipTransakcijeBox.getSelectedIndex() == 0 ||
                katagroijaBox.getSelectedIndex() == 0 ||
                Iznos.getText().isEmpty() ||
                Opis.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
            return;
        }

        try {
            double iznos = Double.parseDouble(Iznos.getText());

            MongoDatabase db = Bazapodataka.getDatabase();
            MongoCollection<Document> col = db.getCollection("transakcije");

            col.insertOne(new Document()
                    .append("tip", tipTransakcijeBox.getSelectedItem())
                    .append("kategorija", katagroijaBox.getSelectedItem())
                    .append("iznos", iznos)
                    .append("opis", Opis.getText())
                    .append("datum", datum.getText())
            );

            loadFromDB();
            loadTable();
            prikaziSaldo();
            clearForm();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Iznos mora biti broj!");
        }
    }


    private double izracunajSaldo() {
        double saldo = 0;
        for (Transakcija t : transakcije) {
            if ("Prihodi".equals(t.getTipTransakcijeBox()))
                saldo += t.getIznos();
            else
                saldo -= t.getIznos();
        }
        return saldo;
    }

    private void prikaziSaldo() {
        SaldoField.setText(String.format("%.2f", izracunajSaldo()));
    }

    private double ukupniPrihodi() {
        return transakcije.stream()
                .filter(t -> "Prihodi".equals(t.getTipTransakcijeBox()))
                .mapToDouble(Transakcija::getIznos)
                .sum();
    }

    private double ukupniRashodi() {
        return transakcije.stream()
                .filter(t -> "Rashodi".equals(t.getTipTransakcijeBox()))
                .mapToDouble(Transakcija::getIznos)
                .sum();
    }

    private void popuniField() {
        pregledTableFinacija.getSelectionModel().addListSelectionListener(e -> {
            int row = pregledTableFinacija.getSelectedRow();
            if (row == -1) return;

            tipTransakcijeBox.setSelectedItem(pregledTableFinacija.getValueAt(row, 0));
            katagroijaBox.setSelectedItem(pregledTableFinacija.getValueAt(row, 1));
            Iznos.setText(pregledTableFinacija.getValueAt(row, 2).toString());
            Opis.setText(pregledTableFinacija.getValueAt(row, 3).toString());
        });
    }

    private void clearForm() {
        tipTransakcijeBox.setSelectedIndex(0);
        katagroijaBox.setSelectedIndex(0);
        Iznos.setText("");
        Opis.setText("");
        datum.setText("");
    }



    class BarChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            double prihodi = ukupniPrihodi();
            double rashodi = ukupniRashodi();
            double max = Math.max(prihodi, rashodi);
            if (max == 0) max = 1;

            int h = getHeight() - 50;
            int w = 80;

            int ph = (int) ((prihodi / max) * h);
            int rh = (int) ((rashodi / max) * h);

            g.setColor(new Color(46, 204, 113));
            g.fillRect(80, getHeight() - ph - 30, w, ph);
            g.drawString("Prihodi", 90, getHeight() - 10);

            g.setColor(new Color(231, 76, 60));
            g.fillRect(220, getHeight() - rh - 30, w, rh);
            g.drawString("Rashodi", 230, getHeight() - 10);
        }
    }

    private void exportToPDF() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Sačuvaj PDF izvještaj");
        System.out.println("EXPORT PDF POZVAN");


        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String path = chooser.getSelectedFile().getAbsolutePath();
        if (!path.endsWith(".pdf")) {
            path += ".pdf";
        }

        try {
            com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(
                    pdfDoc,
                    new java.io.FileOutputStream(path)
            );

            pdfDoc.open();

            // NASLOV
            com.itextpdf.text.Font titleFont =
                    new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            18,
                            com.itextpdf.text.Font.BOLD
                    );

            pdfDoc.add(new com.itextpdf.text.Paragraph(
                    "Izvještaj – Pracenje financija\n\n", titleFont
            ));

            // TABELA
            com.itextpdf.text.pdf.PdfPTable table =
                    new com.itextpdf.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);

            table.addCell("Tip");
            table.addCell("Kategorija");
            table.addCell("Iznos");
            table.addCell("Opis");

            for (Transakcija t : transakcije) {
                table.addCell(t.getTipTransakcijeBox());
                table.addCell(t.getKategorijaBox());
                table.addCell(String.valueOf(t.getIznos()));
                table.addCell(t.getOpis());
            }

            pdfDoc.add(table);

            // SALDO
            pdfDoc.add(new com.itextpdf.text.Paragraph("\n"));
            pdfDoc.add(new com.itextpdf.text.Paragraph(
                    "Ukupni prihodi: " + ukupniPrihodi()
            ));
            pdfDoc.add(new com.itextpdf.text.Paragraph(
                    "Ukupni rashodi: " + ukupniRashodi()
            ));
            pdfDoc.add(new com.itextpdf.text.Paragraph(
                    "Saldo: " + izracunajSaldo()
            ));

            pdfDoc.close();

            JOptionPane.showMessageDialog(
                    this,
                    "PDF uspješno sačuvan!",
                    "Uspjeh",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Greška pri kreiranju PDF-a",
                    "Greška",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
