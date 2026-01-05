package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import ErmHam.Users;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PregledKorisnika extends JFrame {

    private JPanel PregledProzora;
    private JPanel ProzorPrgleda;
    private JTextField pretrazivanje;
    private JTable pregledTable;
    private JButton IzvjestajbtnPDF;
    private JTable pregledTabele;
    private JTextField updateID;
    private JComboBox updateRole;
    private JTextField updateIme;
    private JTextField updatePassword;
    private JButton UrediButton;
    private JButton obrisiKorisnika;
    private List<Users> users = new ArrayList<>();

    public PregledKorisnika() {

        setTitle("Pregled korisnika");
        setContentPane(PregledProzora);
        setSize(900, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        updateRole.addItem("Odaberi");
        updateRole.addItem("USER");
        updateRole.addItem("SUPERADMIN");

        loadUsersFromDB(); // ⬅ učitaj iz baze
        loadTable();       // ⬅ napuni tabelu
        initTableClick();

        pregledTabele.setRowHeight(28);
        pregledTabele.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pregledTabele.setGridColor(new Color(65, 92, 112));
        pregledTabele.setShowVerticalLines(false);
        pregledTabele.setShowHorizontalLines(true);

        IzvjestajbtnPDF.addActionListener(e -> exportToPDF());


        updateID.setEditable(false);
        updateID.setBackground(Color.WHITE);

        updatePassword.setEditable(false);
        updatePassword.setBackground(new Color(240, 240, 240));

        updateIme.setEditable(true);
        updateRole.setEnabled(true);
        UrediButton.addActionListener(e -> urediKorisnika());



        UrediButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        UrediButton.setBackground(new Color(69, 104, 130));
        UrediButton.setForeground(Color.WHITE);

        IzvjestajbtnPDF.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        IzvjestajbtnPDF.setBackground(new Color(69, 104, 130));
        IzvjestajbtnPDF.setForeground(Color.WHITE);

        obrisiKorisnika.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        obrisiKorisnika.setBackground(new Color(69, 104, 130));
        obrisiKorisnika.setForeground(Color.WHITE);

        pretrazivanje.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


    }

    private void loadUsersFromDB() {

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> collection = db.getCollection("users");

        users.clear();

        for (Document d : collection.find()) {
            Users u = new Users(
                    d.getObjectId("_id").toHexString(),
                    d.getString("username"),
                    d.getString("password"),
                    d.getString("role")
            );
            users.add(u);
        }
    }


    private void loadTable() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Role");

        for (Users t : users) {
            model.addRow(new Object[]{
                    t.getId(),
                    t.getUsername(),
                    t.getPassword(),
                    t.getRole()
            });
        }

        pregledTabele.setModel(model);
    }


    private void initTableClick() {
        pregledTabele.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            int row = pregledTabele.getSelectedRow();
            if (row == -1) return;

            updateID.setText(pregledTabele.getValueAt(row, 0).toString());
            updateIme.setText(pregledTabele.getValueAt(row, 1).toString());
            updatePassword.setText(pregledTabele.getValueAt(row, 2).toString());
            updateRole.setSelectedItem(
                    pregledTabele.getValueAt(row, 3).toString());
        });
    }



    private void exportToPDF() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Sačuvaj PDF izvještaj");

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

            // Naslov
            com.itextpdf.text.Font titleFont =
                    new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            18,
                            com.itextpdf.text.Font.BOLD
                    );

            pdfDoc.add(new com.itextpdf.text.Paragraph("Pregled korisnika\n\n", titleFont));

            // Tabela
            com.itextpdf.text.pdf.PdfPTable table =
                    new com.itextpdf.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);

            table.addCell("ID");
            table.addCell("Username");
            table.addCell("Password");
            table.addCell("Role");

            for (Users u : users) {
                table.addCell(u.getId());
                table.addCell(u.getUsername());
                table.addCell(u.getPassword());
                table.addCell(u.getRole());
            }

            pdfDoc.add(table);
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
    private void urediKorisnika() {

        if (updateID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Odaberi korisnika iz tabele!");
            return;
        }

        String novoIme = updateIme.getText().trim();
        String novaRole = updateRole.getSelectedItem().toString();

        if (novoIme.isEmpty() || novaRole.equals("Odaberi")) {
            JOptionPane.showMessageDialog(this, "Ime i role su obavezni!");
            return;
        }

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("users");

        Document filter = new Document("_id",
                new org.bson.types.ObjectId(updateID.getText()));

        Document update = new Document("$set",
                new Document("username", novoIme)
                        .append("role", novaRole)
        );

        col.updateOne(filter, update);

        // REFRESH
        loadUsersFromDB();
        loadTable();

        JOptionPane.showMessageDialog(this, "Korisnik uspješno izmijenjen!");
    }



}
