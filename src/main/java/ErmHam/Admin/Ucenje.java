package ErmHam.Admin;

public class Ucenje {
    private String predmet;
    private String ciljevi;
    private String vrijemepodanu;
    private String Trajanje;
    private String napomena;
    private String prioritet;
    private String status;

    public Ucenje(String predmet, String ciljevi, String vrijemepodanu, String napomena, String prioritet, String status, String trajanje) {
        this.predmet = predmet;
        this.ciljevi = ciljevi;
        this.vrijemepodanu = vrijemepodanu;
        this.napomena = napomena;
        this.prioritet = prioritet;
        this.status = status;
        this.Trajanje = trajanje;
    }

    public String getPredmet() {
        return predmet;
    }

    public String getCiljevi() {
        return ciljevi;
    }

    public String getVrijemepodanu() {
        return vrijemepodanu;
    }

    public String getNapomena() {
        return napomena;
    }

    public String getPrioritet() {
        return prioritet;
    }

    public String getStatus() {
        return status;
    }
    public String getTrajanje(){
        return Trajanje;
    }
}
