package ErmHam;

public class Obrok {
    private String nazivObrok;
    private String tipoObroka;
    private String kategorija;
    private int kalorije;
    private int proteini;
    boolean active = true;

    public Obrok(String nazivObrok, String tipoObroka, String kategorija, int kalorije, int proteini, boolean active) {
        this.nazivObrok = nazivObrok;
        this.tipoObroka = tipoObroka;
        this.kategorija = kategorija;
        this.kalorije = kalorije;
        this.proteini = proteini;
        this.active = active;
    }

    public String getNazivObrok() {
        return nazivObrok;
    }

    public String getTipoObroka() {
        return tipoObroka;
    }

    public String getKategorija() {
        return kategorija;
    }

    public int getKalorije() {
        return kalorije;
    }

    public int getProteini() {
        return proteini;
    }

    public boolean isActive() {
        return active;
    }
}
