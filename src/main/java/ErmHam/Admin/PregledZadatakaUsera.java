package ErmHam.Admin;

import ErmHam.Database.Bazapodataka;
import ErmHam.Main;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PregledZadatakaUsera extends JFrame {

    private JPanel GlavniProzorPregledZadatakaUsera;
    private JTable tablePregledKorisnika;
    private JProgressBar progressBar1;
    private JLabel OdkolikodoKolikoZavresnihzadataka;
    private JLabel aktivnoKorisnika;
    private JLabel aktivnozadataka;
    private JPanel Histogram;

    // BROJAČI


    private int countAktivno = 0;
    private int countUToku = 0;
    private int countZavrseno = 0;

    public PregledZadatakaUsera() {
        setTitle("Pregled zadataka korisnika");
        setContentPane(GlavniProzorPregledZadatakaUsera);
        setSize(1100, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = new ImageIcon(
                Main.class.getResource("/imgdeskop.jpg")
        ).getImage();

        setIconImage(icon);

        styleProgressBar();
        initHistogram();
        ucitajTabelu();
        bojastatusa();
    }

    private void styleProgressBar() {
        progressBar1.setStringPainted(true);
        progressBar1.setForeground(new Color(76, 175, 80));
        progressBar1.setBackground(new Color(220, 220, 220));
    }

    private void ucitajTabelu() {

        countAktivno = 0;
        countUToku = 0;
        countZavrseno = 0;

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Korisnik ");
        model.addColumn("Naziv zadatka");
        model.addColumn("Datum");
        model.addColumn("Prioritet");
        model.addColumn("Status");

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("planiranje_zadataka");
        MongoCollection<Document> usersCol = db.getCollection("users");

        int ukupno = 0;

        for (Document d : col.find()) {
            ukupno++;

            String status = d.getString("status");

            if ("Aktivno".equals(status)) countAktivno++;
            else if ("U toku".equals(status)) countUToku++;
            else if ("Završeno".equals(status)) countZavrseno++;

            // 👉 USERNAME
            String userId = d.getString("userId");
            String username = "Nepoznat";

            Document userDoc = null;

            try {
                userDoc = usersCol.find(
                        new Document("_id", new ObjectId(userId))
                ).first();
            } catch (Exception e) {
                System.out.println("Neispravan userId: " + userId);
            }

            if (userDoc != null) {
                username = userDoc.getString("username");
            }


            model.addRow(new Object[]{
                    username,
                    d.getString("nazivZadatka"),
                    d.getString("datum"),
                    d.getString("prioritet"),
                    status
            });
        }


        tablePregledKorisnika.setModel(model);
        updateStatistika(ukupno, countZavrseno);
        Histogram.repaint();
    }

    private void updateStatistika(int ukupno, int zavrseno) {

        int aktivni = ukupno - zavrseno;

        aktivnozadataka.setText("Ukupno zadataka: " + ukupno);
        aktivnoKorisnika.setText("Aktivni zadaci: " + aktivni);
        OdkolikodoKolikoZavresnihzadataka.setText(
                "Završeni: " + zavrseno + " / " + ukupno
        );

        int procenat = ukupno == 0 ? 0 : (zavrseno * 100) / ukupno;
        progressBar1.setValue(procenat);
        progressBar1.setString(procenat + "% završeno");
    }

    private void bojastatusa() {

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {

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
                            c.setBackground(new Color(255, 193, 7)); // 🟠
                            c.setForeground(Color.BLACK);
                            break;

                        case "U toku":
                            c.setBackground(new Color(220, 53, 69)); // 🔴
                            c.setForeground(Color.WHITE);
                            break;

                        case "Završeno":
                            c.setBackground(new Color(40, 167, 69)); // 🟢
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
        };

        tablePregledKorisnika.getColumnModel()
                .getColumn(4)
                .setCellRenderer(renderer);
    }

    private void initHistogram() {

        Histogram.setLayout(new BorderLayout());

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int width = getWidth();
                int height = getHeight();

                int max = Math.max(countAktivno,
                        Math.max(countUToku, countZavrseno));
                if (max == 0) return;

                int barWidth = 80;
                int baseY = height - 40;

                drawBar(g2, 80, baseY, countAktivno, max,
                        new Color(255, 193, 7), "Aktivno");

                drawBar(g2, 200, baseY, countUToku, max,
                        new Color(220, 53, 69), "U toku");

                drawBar(g2, 320, baseY, countZavrseno, max,
                        new Color(40, 167, 69), "Završeno");
            }
        };

        chart.setBackground(Color.WHITE);
        Histogram.add(chart, BorderLayout.CENTER);
    }

    private void drawBar(Graphics2D g2, int x, int baseY,
                         int value, int max, Color color, String label) {

        int height = (value * 180) / max;

        g2.setColor(color);
        g2.fillRoundRect(x, baseY - height, 60, height, 12, 12);

        g2.setColor(Color.BLACK);
        g2.drawString(label + " (" + value + ")", x - 5, baseY + 15);
    }
}
