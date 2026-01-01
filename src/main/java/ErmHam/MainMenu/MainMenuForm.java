package ErmHam.MainMenu;

import ErmHam.Admin.DodajKorisnika;
import ErmHam.Admin.DodajPlaniranjeObroka;
import ErmHam.Admin.Pracenjefinancija;
import ErmHam.Admin.PregledKorisnika;
import ErmHam.LoginForm.Loginform;
import ErmHam.User.Podacioracunu;
import ErmHam.UserSession;

import javax.swing.*;
import java.awt.*;

public class MainMenuForm {

    private JPanel GlavniProzor;
    private JButton dodajKorisnikaButton;
    private JButton PracenjefinancijaButton;
    private JButton pregledKorisnikaButton;
    private JButton Pracenjenavika;
    private JButton planiranjeUcenjaButton;
    private JButton podaciORacunuButton;
    private JButton odjaviseButton;
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

        Pracenjenavika.setBackground(new Color(69, 104, 130));
        Pracenjenavika.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        planiranjeUcenjaButton.setBackground(new Color(69, 104, 130));
        planiranjeUcenjaButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        odjaviseButton.setBackground(new Color(69, 104, 130));
        odjaviseButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
    }

    private void applyRolePermissions() {
        if ("SUPERADMIN".equals(role)) {
            dodajKorisnikaButton.setVisible(true);
            pregledKorisnikaButton.setVisible(true);
            podaciORacunuButton.setVisible(true);
            PracenjefinancijaButton.setVisible(true);
        } else {
            podaciORacunuButton.setVisible(true);
            PracenjefinancijaButton.setVisible(true);
            dodajKorisnikaButton.setVisible(false);
            pregledKorisnikaButton.setVisible(false);
        }
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
        Pracenjenavika.addActionListener(e -> {
            new DodajPlaniranjeObroka().setVisible(true);
        });
        odjaviseButton.addActionListener(e -> odjaviSe());
            //ovdje mora se napraviti logika
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
