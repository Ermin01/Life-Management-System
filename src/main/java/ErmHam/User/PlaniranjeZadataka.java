package ErmHam.User;

import ErmHam.Database.Bazapodataka;
import ErmHam.PlaniranjeZad;
import ErmHam.UserSession;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

import java.util.List;
public class PlaniranjeZadataka extends  JFrame {
    private JPanel GlavniProzorPlaniranjaZadataka;
    private JTextField nazivZadatkaField;
    private JTextField OpisZadatkaa;
    private JTextField Datum;
    private JComboBox prioritetCombobox;
    private JButton dodajZadatakButton;
    private JTable planiranjezadatkaTable;
    private JButton aktivnoButton;
    private JTextField pretrazivanje;
    private JButton uTokubutton;
    private JButton zavreseniButton;
    private JButton SortNovijeStarije;
    private JComboBox StatusCombobox;
    private JButton azurirajButton;
    private JLabel AktivinizadaciRacunaj;
    private JLabel ZdrastveniZadaciRacunaj;
    private JButton ExportPDF;
    private JLabel serach;

    private final List<PlaniranjeZad> planiranjeZad = new ArrayList<>();


    public PlaniranjeZadataka(){
        setTitle("Planiranje Zadataka");
        setContentPane(GlavniProzorPlaniranjaZadataka);
        setSize(1100, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);



        dodajZadatakButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        dodajZadatakButton.setBackground(new Color(69, 104, 130));
        dodajZadatakButton.setForeground(Color.WHITE);

        uTokubutton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        uTokubutton.setBackground(new Color(69, 104, 130));
        uTokubutton.setForeground(Color.WHITE);

        zavreseniButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        zavreseniButton.setBackground(new Color(69, 104, 130));
        zavreseniButton.setForeground(Color.WHITE);

        SortNovijeStarije.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        SortNovijeStarije.setBackground(new Color(69, 104, 130));
        SortNovijeStarije.setForeground(Color.WHITE);

        aktivnoButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        aktivnoButton.setBackground(new Color(69, 104, 130));
        aktivnoButton.setForeground(Color.WHITE);

        azurirajButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        azurirajButton.setBackground(new Color(69, 104, 130));
        azurirajButton.setForeground(Color.WHITE);

        ExportPDF.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        ExportPDF.setBackground(new Color(69, 104, 130));
        ExportPDF.setForeground(Color.WHITE);





        prioritetCombobox.addItem("Odaberi prioritet");
        prioritetCombobox.addItem("Nizak");
        prioritetCombobox.addItem("Srednji");
        prioritetCombobox.addItem("Visok");
        prioritetCombobox.addItem("Hitno");

        StatusCombobox.addItem("Odaberi status");
        StatusCombobox.addItem("Aktivno");
        StatusCombobox.addItem("U toku");
        StatusCombobox.addItem("Završeno");

        Datum.putClientProperty("JTextField.placeholderText", "dd-MM-yyyy");


        dodajZadatakButton.addActionListener(e -> dodajZadatak());
        azurirajButton.addActionListener(e -> azurirajZadatak());

        serach.setIcon(
                new FlatSVGIcon("icons/search.svg", 24, 24)
        );

        loadFromDB();
        UcitajtabeluZadaci();
        popuniField();
        updateCounters();

        bojastatusa();

    }


    public void UcitajtabeluZadaci(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Naziv zadatka");
        model.addColumn("Opis zadatka");
        model.addColumn("Datum");
        model.addColumn("Prioritet");
        model.addColumn("Status");
        for (PlaniranjeZad t : planiranjeZad) {
            model.addRow(new Object[]{
                    t.getNazivZadatka(),
                    t.getDodajOpisZadatka(),
                    t.getDatum(),
                    t.getPrioritet(),
                    t.getStatus()
            });
        }
        planiranjezadatkaTable.setModel(model);
    }

    private void loadFromDB() {
        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("planiranje_zadataka");

        String userId = UserSession.getUser().getId();

        planiranjeZad.clear();

        for (Document d : col.find(new Document("userId", userId))) {
            planiranjeZad.add(new PlaniranjeZad(
                    d.getString("nazivZadatka"),
                    d.getString("dodajOpisZadatka"),
                    d.getString("datum"),
                    d.getString("prioritet"),
                    d.getString("status")
            ));
        }
    }

    private void dodajZadatak() {

        String naziv = nazivZadatkaField.getText().trim();
        String opis = OpisZadatkaa.getText().trim();
        String datum = Datum.getText().trim();

        if (naziv.isEmpty() || datum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
            return;
        }

        if (!validComboBox()) return; // 🔒 BLOKADA

        String prioritet = (String) prioritetCombobox.getSelectedItem();
        String status = (String) StatusCombobox.getSelectedItem();

        MongoCollection<Document> col =
                Bazapodataka.getDatabase().getCollection("planiranje_zadataka");

        col.insertOne(new Document()
                .append("userId", UserSession.getUser().getId())
                .append("nazivZadatka", naziv)
                .append("dodajOpisZadatka", opis)
                .append("datum", datum)
                .append("prioritet", prioritet)
                .append("status", status)
        );

        loadFromDB();
        UcitajtabeluZadaci();
        updateCounters();
        clearForm();
        bojastatusa();

        JOptionPane.showMessageDialog(this, "Zadatak uspješno dodan!");
    }



    private void popuniField() {
        planiranjezadatkaTable.getSelectionModel().addListSelectionListener(e -> {
            clearForm();
            int row = planiranjezadatkaTable.getSelectedRow();
            if (row == -1) return;

            nazivZadatkaField.setText(planiranjezadatkaTable.getValueAt(row, 0).toString());
            OpisZadatkaa.setText(planiranjezadatkaTable.getValueAt(row, 1).toString());
            Datum.setText(planiranjezadatkaTable.getValueAt(row, 2).toString());
            prioritetCombobox.setSelectedItem(planiranjezadatkaTable.getValueAt(row, 3));
            StatusCombobox.setSelectedItem(planiranjezadatkaTable.getValueAt(row, 4));
        });
    }


    private void clearForm(){
        prioritetCombobox.setSelectedIndex(0);
        StatusCombobox.setSelectedIndex(0);
        nazivZadatkaField.setText("");
        OpisZadatkaa.setText("");
        Datum.setText("");
    }

    private void azurirajZadatak() {

        int row = planiranjezadatkaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Izaberi zadatak iz tabele!");
            return;
        }

        if (!validComboBox()) return; // 🔒 BLOKADA

        String stariNaziv = planiranjezadatkaTable.getValueAt(row, 0).toString();

        MongoCollection<Document> col =
                Bazapodataka.getDatabase().getCollection("planiranje_zadataka");

        col.updateOne(
                new Document("userId", UserSession.getUser().getId())
                        .append("nazivZadatka", stariNaziv),
                new Document("$set", new Document()
                        .append("nazivZadatka", nazivZadatkaField.getText())
                        .append("dodajOpisZadatka", OpisZadatkaa.getText())
                        .append("datum", Datum.getText())
                        .append("prioritet", prioritetCombobox.getSelectedItem())
                        .append("status", StatusCombobox.getSelectedItem())
                )
        );

        loadFromDB();
        UcitajtabeluZadaci();
        updateCounters();
        bojastatusa();

        JOptionPane.showMessageDialog(this, "Zadatak ažuriran!");
    }

    private void updateCounters() {
        long aktivni = planiranjeZad.stream()
                .filter(z -> !"Završeno".equals(z.getStatus()))
                .count();

        long zavrseni = planiranjeZad.stream()
                .filter(z -> "Završeno".equals(z.getStatus()))
                .count();

        AktivinizadaciRacunaj.setText("Aktivni zadaci: " + aktivni);
        ZdrastveniZadaciRacunaj.setText("Završeni zadaci: " + zavrseni);
    }



    private boolean validComboBox() {
        if (prioritetCombobox.getSelectedIndex() == 0 ||
                StatusCombobox.getSelectedIndex() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Molimo odaberite PRIORITET i STATUS!",
                    "Greška",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }


    private void bojastatusa() {
        planiranjezadatkaTable.getColumnModel()
                .getColumn(4)
                .setCellRenderer(new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {

                        Component c = super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);

                        String status = value.toString();

                        if (!isSelected) {
                            switch (status) {
                                case "Aktivno":
                                    c.setBackground(new Color(255, 193, 7)); // 🟠 ORANGE
                                    c.setForeground(Color.BLACK);
                                    break;

                                case "U toku":
                                    c.setBackground(new Color(220, 53, 69)); // 🔴 CRVENA
                                    c.setForeground(Color.WHITE);
                                    break;

                                case "Završeno":
                                    c.setBackground(new Color(40, 167, 69)); // 🟢 ZELENA
                                    c.setForeground(Color.WHITE);
                                    break;

                                default:
                                    c.setBackground(Color.WHITE);
                                    c.setForeground(Color.BLACK);
                            }
                        } else {
                            c.setBackground(table.getSelectionBackground());
                            c.setForeground(table.getSelectionForeground());
                        }

                        setHorizontalAlignment(SwingConstants.CENTER);
                        return c;
                    }
                });
    }

}


