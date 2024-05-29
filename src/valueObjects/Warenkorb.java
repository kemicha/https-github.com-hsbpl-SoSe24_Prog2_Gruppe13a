package src.valueObjects;

public class Warenkorb {
    private Artikel artikel;
    private int menge;


    public Warenkorb(Artikel artikel, int menge) {
        this.artikel = artikel;
        this.menge = menge;
    }

    public Artikel getArtikel() {
        return artikel;
    }

    public void setArtikel(Artikel artikel) {
        this.artikel = artikel;
    }

    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("\n Warenkorb: ")
                .append("Artikel")
                .append(artikel)
                .append("\n menge = ")
                .append(menge)
                .toString();
    }

}
