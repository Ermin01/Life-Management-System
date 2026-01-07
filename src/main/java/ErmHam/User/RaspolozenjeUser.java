package ErmHam.User;

import ErmHam.Database.Bazapodataka;
import ErmHam.Raspolozenje;
import ErmHam.UserSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RaspolozenjeUser extends JFrame {

    private JPanel RaspolozenjeTrackerProzor;
    private JTextField Datumfield;
    private JTable RasplozenjeTable;
    private JComboBox<String> RaspolozenjeBOX;
    private JTextField biljeska;
    private JButton OBRIsIButton;
    private JPanel Statistika;
    private JButton DodajRaspolozenja;

    private final List<Raspolozenje> raspolozenja = new ArrayList<>();

    public RaspolozenjeUser() {
        setTitle("Dodavanje Raspolozenja");
        setContentPane(RaspolozenjeTrackerProzor);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 🔧 FIX: Statistika panel mora imati veličinu
        Statistika.setPreferredSize(new Dimension(350, 250));
        Statistika.setMinimumSize(new Dimension(350, 250));
        Statistika.setBorder(BorderFactory.createTitledBorder("Statistika raspoloženja"));
        Statistika.setOpaque(true);
        Statistika.setBackground(Color.WHITE);

        // ComboBox
        RaspolozenjeBOX.addItem("Odaberi raspolozenje");
        RaspolozenjeBOX.addItem("😄 Sretan");
        RaspolozenjeBOX.addItem("🙂 Dobro");
        RaspolozenjeBOX.addItem("😐 Neutralno");
        RaspolozenjeBOX.addItem("😔 Tužno");
        RaspolozenjeBOX.addItem("😡 Ljuto");
        RaspolozenjeBOX.addItem("😴 Umorno");

        DodajRaspolozenja.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
        DodajRaspolozenja.setBackground(new Color(69, 104, 130));
        DodajRaspolozenja.setForeground(Color.WHITE);

        OBRIsIButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
        OBRIsIButton.setBackground(new Color(69, 104, 130));
        OBRIsIButton.setForeground(Color.WHITE);

        // 🔧 BITAN REDOSLIJED
        loadFromDB();
        ucitajTabelu();
        nacrtajHistogram();
        popuniField();

        DodajRaspolozenja.addActionListener(e -> dodajRaspolozenjeUsera());
    }

    private void ucitajTabelu() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Datum");
        model.addColumn("Raspoloženje");
        model.addColumn("Bilješka");

        for (Raspolozenje r : raspolozenja) {
            model.addRow(new Object[]{
                    r.getDatum(),
                    r.getRaspolozenje(),
                    r.getBiljeska()
            });
        }

        RasplozenjeTable.setModel(model);
    }

    private void loadFromDB() {
        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> collection = db.getCollection("raspolozenje");

        String userId = UserSession.getUser().getId();

        raspolozenja.clear();

        for (Document d : collection.find(new Document("userId", userId))) {
            raspolozenja.add(new Raspolozenje(
                    d.getString("datum"),
                    d.getString("raspolozenje"),
                    d.getString("biljeska")
            ));
        }
    }

    private void dodajRaspolozenjeUsera() {

        if (
                RaspolozenjeBOX.getSelectedIndex() == 0 ||
                        Datumfield.getText().trim().isEmpty() ||
                        biljeska.getText().trim().isEmpty()
        ) {
            JOptionPane.showMessageDialog(this, "Popuni sva polja!");
            return;
        }

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("raspolozenje");

        Document doc = new Document()
                .append("userId", UserSession.getUser().getId())
                .append("datum", Datumfield.getText())
                .append("raspolozenje", RaspolozenjeBOX.getSelectedItem().toString())
                .append("biljeska", biljeska.getText());

        col.insertOne(doc);

        loadFromDB();
        ucitajTabelu();
        nacrtajHistogram(); // 🔧 FIX
        clear();
    }

    private void popuniField() {
        RasplozenjeTable.getSelectionModel().addListSelectionListener(e -> {
            int r = RasplozenjeTable.getSelectedRow();
            if (r == -1) return;

            Datumfield.setText(RasplozenjeTable.getValueAt(r, 0).toString());
            RaspolozenjeBOX.setSelectedItem(RasplozenjeTable.getValueAt(r, 1));
            biljeska.setText(RasplozenjeTable.getValueAt(r, 2).toString());
        });
    }

    public void clear() {
        Datumfield.setText("");
        biljeska.setText("");
        RaspolozenjeBOX.setSelectedIndex(0);
    }

    private java.util.Map<String, Integer> izracunajStatistiku() {
        java.util.Map<String, Integer> mapa = new java.util.LinkedHashMap<>();

        mapa.put("😄 Sretan", 0);
        mapa.put("🙂 Dobro", 0);
        mapa.put("😐 Neutralno", 0);
        mapa.put("😔 Tužno", 0);
        mapa.put("😡 Ljuto", 0);
        mapa.put("😴 Umorno", 0);

        for (Raspolozenje r : raspolozenja) {
            mapa.put(r.getRaspolozenje(),
                    mapa.getOrDefault(r.getRaspolozenje(), 0) + 1);
        }
        return mapa;
    }

    private void nacrtajHistogram() {

        Statistika.removeAll();

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                var data = izracunajStatistiku();

                int width = getWidth();
                int height = getHeight();
                int barWidth = Math.max(40, width / data.size());
                int max = data.values().stream().max(Integer::compare).orElse(1);

                int x = 10;

                for (var entry : data.entrySet()) {
                    int value = entry.getValue();
                    int barHeight = (int) ((double) value / max * (height - 60));

                    g2.setColor(bojaZaRaspolozenje(entry.getKey()));
                    g2.fillRoundRect(x, height - barHeight - 30,
                            barWidth - 15, barHeight, 10, 10);

                    g2.setColor(Color.DARK_GRAY);
                    g2.drawString(entry.getKey(), x, height - 10);
                    g2.drawString(String.valueOf(value), x + 10, height - barHeight - 35);

                    x += barWidth;
                }
            }
        };

        chart.setBackground(Color.WHITE);
        chart.setOpaque(true);
        chart.setPreferredSize(new Dimension(320, 220));

        Statistika.setLayout(new BorderLayout());
        Statistika.add(chart, BorderLayout.CENTER);
        Statistika.revalidate();
        Statistika.repaint();
    }

    private Color bojaZaRaspolozenje(String r) {
        return switch (r) {
            case "😄 Sretan" -> new Color(76, 175, 80);
            case "🙂 Dobro" -> new Color(139, 195, 74);
            case "😐 Neutralno" -> new Color(255, 193, 7);
            case "😔 Tužno" -> new Color(33, 150, 243);
            case "😡 Ljuto" -> new Color(244, 67, 54);
            case "😴 Umorno" -> new Color(121, 85, 72);
            default -> Color.GRAY;
        };
    }
}
