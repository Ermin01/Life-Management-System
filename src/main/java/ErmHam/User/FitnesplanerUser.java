package ErmHam.User;

import ErmHam.Database.Bazapodataka;
import ErmHam.UserSession;
import ErmHam.Users;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FitnesplanerUser extends JFrame {

    private JPanel FitnesUser;

    private JLabel korisnik;
    private JLabel status;
    private JLabel plantreninga;
    private JLabel fitnescilja;
    private JLabel fitnesnivoa;
    private JLabel visina;
    private JLabel tezina;

    private JButton zatvoriButton;

    private  JPanel leftimg;

    public FitnesplanerUser() {

        setTitle("Fitness plan – Moj plan");
        setContentPane(FitnesUser);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        zatvoriButton.addActionListener(e -> dispose());

        ucitajPlanZaUsera();



        try {
            Image bgImage = ImageIO.read(new File(
                    "C:\\Users\\Public\\Documents\\Life Management System\\src\\main\\java\\ErmHam\\Img\\fitnes.jpg"
            ));

            leftimg.setLayout(new BorderLayout());

            JPanel bgPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
            };

            leftimg.add(bgPanel, BorderLayout.CENTER);

        } catch (IOException e) {
            e.printStackTrace();
        }



        zatvoriButton.setBorder(BorderFactory.createEmptyBorder(13, 13, 13,13));
        zatvoriButton.setBackground(new Color(69, 104, 130));
        zatvoriButton.setForeground(Color.WHITE);

    }

    // ===================== GLAVNA LOGIKA =====================
    private void ucitajPlanZaUsera() {

        Users user = UserSession.getUser();

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Niste prijavljeni!",
                    "Greška",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        String username = user.getUsername();
        korisnik.setText("Korisnik: " + username);

        MongoDatabase db = Bazapodataka.getDatabase();
        MongoCollection<Document> col = db.getCollection("fitnes_planer");

        Document plan = col.find(
                new Document("username", username)
                        .append("aktivan", true)
        ).first();

        if (plan == null) {
            status.setText("Status: ❌ Fitness plan nije dostupan");
            plantreninga.setText("-");
            fitnescilja.setText("-");
            fitnesnivoa.setText("-");
            visina.setText("-");
            tezina.setText("-");
            return;
        }

        status.setText("Status: ✅ Aktivan plan");
        plantreninga.setText("Plan treninga: " + plan.getString("plan_treninga"));
        fitnescilja.setText("Fitness cilj: " + plan.getString("fitnes_cilj"));
        fitnesnivoa.setText("Fitness nivo: " + plan.getString("fitnes_nivo"));
        visina.setText("Visina: " + plan.getInteger("visina_cm") + " cm");
        tezina.setText("Težina: " + plan.getDouble("tezina_kg") + " kg");
    }
}

