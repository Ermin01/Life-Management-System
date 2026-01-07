package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import ErmHam.Main;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlanerFitness extends JFrame {
    private JPanel PlanerFitn;

    private JComboBox comboBoxFitnes;
    private JComboBox comboBoxPlantreniga;
    private JComboBox comboBoxfitnescilj;
    private JComboBox comboBoxFitnesnivo;
    private JTextField visina;
    private JCheckBox aktivanPlanCheckBox;
    private JButton sacuvajButton;
    private JTextField tezina;
    private JTextField datum;

    public PlanerFitness(){
        setTitle("FITNES");
        setContentPane(PlanerFitn);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        Image icon = new ImageIcon(
                Main.class.getResource("/imgdeskop.jpg")
        ).getImage();

        setIconImage(icon);

//        comboBOX
        comboBoxPlantreniga.addItem("Odaberi");
        comboBoxPlantreniga.addItem("Full Body (Cijelo tijelo)");
        comboBoxPlantreniga.addItem("Upper / Lower Split");
        comboBoxPlantreniga.addItem("Push Pull Legs (PPL)");
        comboBoxPlantreniga.addItem("Kardio plan");
        comboBoxPlantreniga.addItem("HIIT plan");
        comboBoxPlantreniga.addItem("Snaga - osnovi");
        comboBoxPlantreniga.addItem("Snaga – napredni");
        comboBoxPlantreniga.addItem("Mršavljenje");
        comboBoxPlantreniga.addItem("Rehabilitacioni plan");
        comboBoxPlantreniga.addItem("Kućni trening");

        comboBoxfitnescilj.addItem("Odaberi");
        comboBoxfitnescilj.addItem("Mršavljenje");
        comboBoxfitnescilj.addItem("Povećanje mišićne mase");
        comboBoxfitnescilj.addItem("Snaga");
        comboBoxfitnescilj.addItem("Kondicija");
        comboBoxfitnescilj.addItem("Definicija");
        comboBoxfitnescilj.addItem("Zdravlje i forma");
        comboBoxfitnescilj.addItem("Rehabilitacija");
        comboBoxfitnescilj.addItem("Održavanje forme");

        comboBoxFitnesnivo.addItem("Odaberi");
        comboBoxFitnesnivo.addItem("Početnik");
        comboBoxFitnesnivo.addItem("Srednji");
        comboBoxFitnesnivo.addItem("Napredni");
        comboBoxFitnesnivo.addItem("Profesionalni");

        sacuvajButton.setBackground(new Color(69, 104, 130));
        sacuvajButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));


        sacuvajButton.addActionListener(e -> sacuvajPlanFitnes());





        ucitajKorisnikeIzBaze();
    }

    private void ucitajKorisnikeIzBaze() {

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("users");

        comboBoxFitnes.removeAllItems();
        comboBoxFitnes.addItem("Odaberi korisnika");
        System.out.println(comboBoxFitnes);

        // SAMO USER, bez active
        Document filter = new Document("role", "USER");

        for (Document d : col.find(filter)) {
            String username = d.getString("username");
            comboBoxFitnes.addItem(username);
        }
    }

    public void sacuvajPlanFitnes() {

        if (comboBoxFitnes.getSelectedIndex() == 0 ||
                comboBoxPlantreniga.getSelectedIndex() == 0 ||
                comboBoxfitnescilj.getSelectedIndex() == 0 ||
                comboBoxFitnesnivo.getSelectedIndex() == 0 ||
                visina.getText().isEmpty() ||
                tezina.getText().isEmpty() ||
                datum.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
            return;
        }

        try {
            int visinaCm = Integer.parseInt(visina.getText());
            double tezinaKg = Double.parseDouble(tezina.getText());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date datumDodjele = sdf.parse(datum.getText());

            MongoDatabase db = Bazapodataka.getDatabase();
            MongoCollection<Document> col = db.getCollection("fitnes_planer");

            Document plan = new Document()
                    .append("username", comboBoxFitnes.getSelectedItem())
                    .append("plan_treninga", comboBoxPlantreniga.getSelectedItem())
                    .append("fitnes_cilj", comboBoxfitnescilj.getSelectedItem())
                    .append("fitnes_nivo", comboBoxFitnesnivo.getSelectedItem())
                    .append("visina_cm", visinaCm)
                    .append("tezina_kg", tezinaKg)
                    .append("aktivan", aktivanPlanCheckBox.isSelected())
                    .append("datum_dodjele", datumDodjele);

            col.insertOne(plan);

            JOptionPane.showMessageDialog(this, "Fitness plan uspješno sačuvan ✅");
            ocistiFormu();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Visina i težina moraju biti brojevi!");
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Datum mora biti u formatu yyyy-MM-dd!");
        }
    }

    private void ocistiFormu(){
        comboBoxFitnes.setSelectedIndex(0);
        comboBoxPlantreniga.setSelectedIndex(0);
        comboBoxfitnescilj.setSelectedIndex(0);
        comboBoxFitnesnivo.setSelectedIndex(0);
        visina.setText("");
        tezina.setText("");
        aktivanPlanCheckBox.setText("");
        datum.setText("");
        aktivanPlanCheckBox.setSelected(false);


    }


}