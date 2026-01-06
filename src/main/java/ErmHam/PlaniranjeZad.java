package ErmHam;

public class PlaniranjeZad {

    private String nazivZadatka;
    private String dodajOpisZadatka;
    private String datum;
    private String  prioritet;
    private String status;

    public PlaniranjeZad(String nazivZadatka, String dodajOpisZadatka, String datum, String prioritet, String status) {
        this.nazivZadatka = nazivZadatka;
        this.dodajOpisZadatka = dodajOpisZadatka;
        this.datum = datum;
        this.prioritet = prioritet;
        this.status = status;
    }

    public String getNazivZadatka() {
        return nazivZadatka;
    }

    public String getDodajOpisZadatka() {
        return dodajOpisZadatka;
    }

    public String getDatum() {
        return datum;
    }

    public String getPrioritet() {
        return prioritet;
    }

    public String getStatus() {
        return status;
    }
}
