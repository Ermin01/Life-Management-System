package ErmHam;

public class Transakcija {

    private String tip;
    private String kategorija;
    private double iznos;
    private String opis;

    private double saldo;

    public Transakcija(String tip, String kategorija, double iznos, String opis, double saldo) {
        this.tip = tip;
        this.kategorija = kategorija;
        this.iznos = iznos;
        this.opis = opis;
        this.saldo=saldo;
    }

    public String getTipTransakcijeBox() {
        return tip;
    }

    public String getKategorijaBox() {
        return kategorija;
    }

    public double getIznos() {
        return iznos;
    }

    public String getOpis() {
        return opis;
    }

    public double getSaldo() {
        return saldo;
    }
}
