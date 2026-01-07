package ErmHam.User;

import ErmHam.Database.Bazapodataka;
import ErmHam.Main;
import ErmHam.UserSession;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class SedmicniplanerObrokaUser extends JFrame {

    private JPanel SedmicnipanelGlava;
    private JButton sacuvajButton;
    private JButton ExportPDF;
    private JButton Clearbutton;

    // ====== LABELI ======
    private JLabel KalorijaLabel;
    private JLabel ProteiniLabel;

    // ====== COMBOBOXOVI ======
    private JComboBox pon_uzina, pon_dorucak, pon_rucak, pon_vecera;
    private JComboBox uto_uzina, uto_dorucak, uto_rucak, uto_vecera;
    private JComboBox sri_uzina, sri_dorucak, sri_rucak, sri_vecera;
    private JComboBox cet_uzina, cet_dorucak, cet_rucak, cet_vecera;
    private JComboBox pet_uzina, pet_dorucak, pet_rucak, pet_vecera;
    private JComboBox sub_uzina, sub_dorucak, sub_rucak, sub_vecera;
    private JComboBox ned_uzina, ned_dorucak, ned_rucak, ned_vecera;

    private JComboBox[] sviComboBoxovi;
    private Map<String, JComboBox[]> daniMap;

    public SedmicniplanerObrokaUser() {

        setTitle("Sedmični  obrok");
        setContentPane(SedmicnipanelGlava);
        setSize(1100, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = new ImageIcon(
                Main.class.getResource("/imgdeskop.jpg")
        ).getImage();

        setIconImage(icon);

        KalorijaLabel.setText("Ukupno kalorija: 0 kcal");
        ProteiniLabel.setText("Ukupno proteina: 0 g");


        sacuvajButton.setBackground(new Color(69, 104, 130));
        sacuvajButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        ExportPDF.setBackground(new Color(69, 104, 130));
        ExportPDF.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));


        Clearbutton.setBackground(new Color(69, 104, 130));
        Clearbutton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        inicijalizujMapu();

        sviComboBoxovi = daniMap.values()
                .stream()
                .flatMap(arr -> java.util.Arrays.stream(arr))
                .toArray(JComboBox[]::new);

        ucitajObrokeIzBaze();
        ucitajSacuvaniPlan();
        izracunajKalorijeIProteine();

        for (JComboBox cb : sviComboBoxovi) {
            cb.addActionListener(e -> izracunajKalorijeIProteine());
        }

        sacuvajButton.addActionListener(e -> sacuvajSedmicniPlan());
        Clearbutton.addActionListener(e -> ocistiComboBoxove());

        ExportPDF.addActionListener(e -> exportToPDF());
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

    // ================= UČITAVANJE OBROKA =================
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
                new UpdateOptions().upsert(true)
        );

        JOptionPane.showMessageDialog(this, "Uspješno sačuvano ✔");
    }

    // ================= IZRAČUN =================
    private void izracunajKalorijeIProteine() {

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("obroci");

        int kalorije = 0;
        int proteini = 0;

        for (String dan : daniMap.keySet()) {

            for (JComboBox cb : daniMap.get(dan)) {

                String naziv = (String) cb.getSelectedItem();
                if (naziv == null || naziv.equals("Odaberi")) continue;

                Document obrok = col.find(new Document("nazivObroka", naziv)).first();
                if (obrok != null) {
                    kalorije += obrok.getInteger("kalorije", 0);
                    proteini += obrok.getInteger("proteini", 0);
                }
            }
        }

        KalorijaLabel.setText("Ukupno kalorija: " + kalorije + " kcal");
        ProteiniLabel.setText("Ukupno proteina: " + proteini + " g");
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
            izracunajKalorijeIProteine();
        }
    }




    private void exportToPDF() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Sačuvaj sedmični planer (PDF)");

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String path = chooser.getSelectedFile().getAbsolutePath();
        if (!path.endsWith(".pdf")) {
            path += ".pdf";
        }

        try {
            com.itextpdf.text.Document pdf =
                    new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(
                    pdf,
                    new FileOutputStream(path)
            );

            pdf.open();

            // NASLOV
            com.itextpdf.text.Font titleFont =
                    new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            18,
                            com.itextpdf.text.Font.BOLD
                    );

            pdf.add(new com.itextpdf.text.Paragraph(
                    "Sedmični planer obroka\n\n", titleFont
            ));

            pdf.add(new com.itextpdf.text.Paragraph(
                    "Korisnik: " + UserSession.getUser().getUsername() + "\n\n"
            ));

            // TABELA
            com.itextpdf.text.pdf.PdfPTable table =
                    new com.itextpdf.text.pdf.PdfPTable(5);
            table.setWidthPercentage(100);

            table.addCell("Dan");
            table.addCell("Užina");
            table.addCell("Doručak");
            table.addCell("Ručak");
            table.addCell("Večera");

            for (String dan : daniMap.keySet()) {
                JComboBox[] cb = daniMap.get(dan);

                table.addCell(dan);
                table.addCell(cb[0].getSelectedItem().toString());
                table.addCell(cb[1].getSelectedItem().toString());
                table.addCell(cb[2].getSelectedItem().toString());
                table.addCell(cb[3].getSelectedItem().toString());
            }

            pdf.add(table);

            pdf.add(new com.itextpdf.text.Paragraph("\n"));
            pdf.add(new com.itextpdf.text.Paragraph(
                    KalorijaLabel.getText()
            ));
            pdf.add(new com.itextpdf.text.Paragraph(
                    ProteiniLabel.getText()
            ));

            pdf.close();

            JOptionPane.showMessageDialog(
                    this,
                    "PDF uspješno sačuvan ✔",
                    "Export",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Greška pri exportu PDF-a",
                    "Greška",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }



}
