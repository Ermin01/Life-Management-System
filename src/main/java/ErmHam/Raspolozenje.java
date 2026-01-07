package ErmHam;

public class Raspolozenje {

    private String datum;
    private String raspolozenje;
    private String biljeska;

    public Raspolozenje(String datum, String raspolozenje, String biljeska) {
        this.datum = datum;
        this.raspolozenje = raspolozenje;
        this.biljeska = biljeska;
    }

    public String getDatum() {
        return datum;
    }

    public String getRaspolozenje() {
        return raspolozenje;
    }

    public String getBiljeska() {
        return biljeska;
    }
}
