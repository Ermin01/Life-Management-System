package ErmHam.MainMenu;

import ErmHam.Admin.*;
import ErmHam.LoginForm.Loginform;
import ErmHam.User.Podacioracunu;
import ErmHam.UserSession;

import javax.swing.*;
import java.awt.*;

public class MainMenuForm {

    private JPanel GlavniProzor;
    private JPanel adminPlaceholder;

    private JButton dodajKorisnikaButton;
    private JButton PracenjefinancijaButton;
    private JButton pregledKorisnikaButton;
    private JButton PracenjenavikaObroka;
    private JButton fitnesPlaniranjeButton;
    private JButton podaciORacunuButton;
    private JButton odjaviseButton;
    private JButton UcenjeButton;
    private String role;




    public MainMenuForm(String role) {
        this.role = role;
        applyRolePermissions();
        Akcije();
        dodajKorisnikaButton.setBackground(new Color(69, 104, 130));
        dodajKorisnikaButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));


        pregledKorisnikaButton.setBackground(new Color(69, 104, 130));
        pregledKorisnikaButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        podaciORacunuButton.setBackground(new Color(69, 104, 130));
        podaciORacunuButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));


        PracenjefinancijaButton.setBackground(new Color(69, 104, 130));
        PracenjefinancijaButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        PracenjenavikaObroka.setBackground(new Color(69, 104, 130));
        PracenjenavikaObroka.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        fitnesPlaniranjeButton.setBackground(new Color(69, 104, 130));
        fitnesPlaniranjeButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        UcenjeButton.setBackground(new Color(69, 104, 130));
        UcenjeButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        odjaviseButton.setBackground(new Color(69, 104, 130));
        odjaviseButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
    }

    private JPanel wrap(JButton button) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        p.setOpaque(false); // NE dira background
        p.add(button);
        return p;
    }

    private void applyRolePermissions() {

        adminPlaceholder.removeAll();
        adminPlaceholder.setLayout(new GridLayout(2, 3, 0, 0));

        if ("SUPERADMIN".equals(role)) {

            adminPlaceholder.add(wrap(dodajKorisnikaButton));
            adminPlaceholder.add(wrap(pregledKorisnikaButton));
            adminPlaceholder.add(wrap(podaciORacunuButton));

            adminPlaceholder.add(wrap(PracenjefinancijaButton));
            adminPlaceholder.add(wrap(PracenjenavikaObroka));
            adminPlaceholder.add(wrap(fitnesPlaniranjeButton));

            adminPlaceholder.add(wrap(UcenjeButton));

        } else { // USER

            adminPlaceholder.add(wrap(podaciORacunuButton));
            adminPlaceholder.add(wrap(PracenjefinancijaButton));
            adminPlaceholder.add(wrap(PracenjenavikaObroka));
            adminPlaceholder.add(wrap(fitnesPlaniranjeButton));
            adminPlaceholder.add(wrap(UcenjeButton));
        }

        adminPlaceholder.revalidate();
        adminPlaceholder.repaint();
    }


    public void Akcije() {
        dodajKorisnikaButton.addActionListener(e -> {
            new DodajKorisnika().setVisible(true);
        });
        pregledKorisnikaButton.addActionListener(e -> {
            new PregledKorisnika().setVisible(true);
        });
        podaciORacunuButton.addActionListener(e -> {
            new Podacioracunu().setVisible(true);
        });
        PracenjefinancijaButton.addActionListener(e -> {
            new Pracenjefinancija(role).setVisible(true);
        });
        PracenjenavikaObroka.addActionListener(e -> {

            if ("SUPERADMIN".equals(role)) {
                new DodajPlaniranjeObroka().setVisible(true);
            } else {
                new ErmHam.User.SedmicniplanerObrokaUser().setVisible(true);
            }

        });



        fitnesPlaniranjeButton.addActionListener(e -> {

            if("SUPERADMIN".equals(role)){
                new PlanerFitness().setVisible(true);
            }else{
                new ErmHam.User.FitnesplanerUser().setVisible(true);
            }
        });

        UcenjeButton.addActionListener(e -> {

            if("SUPERADMIN".equals(role)){
                new UcenjePlanerAdmin().setVisible(true);
            }else{
                new ErmHam.User.UcenjeplanerUser().setVisible(true);
            }
        });



        odjaviseButton.addActionListener(e -> odjaviSe());

    }



//    DESGIN BUTTON-NA


    private void odjaviSe() {
        int izbor = JOptionPane.showConfirmDialog(
                GlavniProzor,
                "Da li ste sigurni da se želite odjaviti?",
                "Odjava",
                JOptionPane.YES_NO_OPTION
        );

        if (izbor == JOptionPane.YES_OPTION) {

            // očisti session
            UserSession.clear();

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GlavniProzor);
            frame.setContentPane(new Loginform().Glavniprozor());
            frame.revalidate();
            frame.repaint();
        }
    }





    public JPanel getPanel() {
        return GlavniProzor;
    }
}