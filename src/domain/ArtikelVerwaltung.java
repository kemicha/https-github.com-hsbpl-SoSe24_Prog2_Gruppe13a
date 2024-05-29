package src.domain;

import src.persistence.ArtikelExistiertBereitsException;
import src.persistence.FilePersistenceManager;
import src.persistence.PersistenceManager;
import src.persistence.WarenkorbExistierBereitsException;
import src.valueObjects.*;
import src.valueObjects.Warenkorb;

import java.io.IOException;
import java.util.*;

public class ArtikelVerwaltung {


    private List<Artikel> artikelList = new ArrayList<>();
    private PersistenceManager pm = new FilePersistenceManager();
    private List<Warenkorb> warenkorbList = new ArrayList<>();
    private List<Rechnung> rechnung = new ArrayList<>();
    private List<Ereignis>  ereignisList= new ArrayList<>();


    public ArtikelVerwaltung() throws IOException {
        this.warenkorbList = warenkorbList;
        this.rechnung = rechnung;
        this.ereignisList= ereignisList;

    }


    public void liesDaten(String datei) throws IOException {
        try {
            artikelList = pm.leseArtikelList(datei);
        } catch (ArtikelExistiertBereitsException e) {
        }
    }

    public void schreibeDaten(String datei) throws IOException {
        pm.schreibeArtikelList(artikelList, datei);
    }

    public List<Artikel> getArtikelBestand() {
        return artikelList;
    }

    public List<Artikel> artikelList() {
        return artikelList;
    }


    public List<Artikel> sucheArtikelNachName(String bezeichnung) {
        List<Artikel> suche = new ArrayList<>();
        Iterator it = getArtikelBestand().iterator();
        while (it.hasNext()) {
            Artikel artikel = (Artikel) it.next();
            if (artikel.getBezeichnung().equals(bezeichnung)) {
                suche.add(artikel);
            }
        }
        return suche;
    }

    public List<Artikel> artikelNachBezeichnung() {
        artikelList.sort(Comparator.comparing(Artikel::getBezeichnung));
        return artikelList;
    }

    public void artikelBestandErhoehen(int atikelNummer, int menge) {

        for (Artikel artikels : getArtikelBestand()) {
            int bumber = artikels.getArtikelNummer();
            if (artikels.getArtikelNummer() != atikelNummer) {
                return;
            }

            artikels.setBestand(artikels.getBestand() + menge);

        }

    }

    public List<Artikel> artikelNachArtikelnummer() {
        artikelList.sort(Comparator.comparingInt(Artikel::getArtikelNummer));
        return artikelList;
    }

/*    public List<Artikel> sucheArtikelNachNummer(String artikelNummer) {
        List<Artikel> suche = new ArrayList<>();
        Iterator it = getBestand().iterator();
        while (it.hasNext()) {
            Artikel artikel = (Artikel) it.next();
            if (artikel.getArtikelNummer().equals(artikelNummer)) {
                suche.add(artikel);
            }
        }
        return suche;
    }*/


    public void loeschen(int artikelNummer) {
        artikelList.removeIf(Artikel -> Artikel.getArtikelNummer() == artikelNummer);
    }


// WarenkorbVerwaltung


    public List<Warenkorb> getWarenkorbList() {
        return warenkorbList;
    }

    public List<Ereignis> getEreignisListe() {
        return ereignisList;
    }

    public void liesWarenkorbDaten(String datei) throws IOException {
        try {
            warenkorbList = pm.leseWarenkorbList(datei);
        } catch (WarenkorbExistierBereitsException e) {
        }
    }

    public void schreibeDatenInWarenkorb(String datei) throws IOException {
        pm.schreibeInWarenkorblList(warenkorbList, datei);
    }

    public List<Warenkorb> getAlleArtikelMengeInwarenkorb() {
        return warenkorbList;
    }


    public List<Warenkorb> sucheArtikelInWarenkorb(Artikel artikel, int menge) {
        List<Warenkorb> suche = new ArrayList<>();
        Iterator it = getAlleArtikelMengeInwarenkorb().iterator();
        while (it.hasNext()) {
            Warenkorb warenkorb = (Warenkorb) it.next();
            warenkorb.setMenge(warenkorb.getMenge() + menge);
            if (warenkorb.getArtikel().equals(artikel) && warenkorb.getMenge() == menge) {
                suche.add(warenkorb);
            }
        }
        return suche;
    }


    public void loeschenArtikelInWarenkorb(int artikelNummer) {
        warenkorbList.removeIf(warenkorb -> warenkorb.getArtikel().getArtikelNummer() == artikelNummer);
    }

    public void ändernWarenkorb(String bezeichnung, int menge) {
        for (Warenkorb warenkorb : warenkorbList) {
            if (warenkorb.getArtikel().getBezeichnung() == bezeichnung) {
                warenkorb.setMenge(menge);
                return;
            }
        }
    }



    public void gekauftArtikel(Benutzer benutzer) {
        List<Warenkorb> warenkorbList = getWarenkorbList();
        double gesamtpreis = 0;
        for (Warenkorb warenkorb : warenkorbList) {
            Artikel artikel = warenkorb.getArtikel();
            int menge = warenkorb.getMenge();
            artikel.setBestand(artikel.getBestand() - menge);
            gesamtpreis += artikel.getPreis() * menge;
        }
        // die Recchnung
        Rechnung rechnung = new Rechnung(benutzer, new Date(), warenkorbList, gesamtpreis);
        rechnung.drueckeRechnung();
        //get Warenkorb Leer
        warenkorbList.clear();
        // speicher in Ereignis
        ereignisErfassen((Kunde) benutzer, warenkorbList);
    }


  /*  private void ereignisErfassen(Benutzer benutzer, List<Warenkorb> gekaufteArtikel) {
        int datum = new Date().getDay();
        for (Warenkorb warenkorb : gekaufteArtikel) {
            Artikel artikel = warenkorb.getArtikel();
            int menge = warenkorb.getMenge();

        }
    }*/

    public void ereignisErfassen(Benutzer benutzer, List<Warenkorb> warenkorbList) {
        Date datum = new Date();
        for (Warenkorb warenkorb : warenkorbList) {
            Artikel artikel = warenkorb.getArtikel();
            int menge = warenkorb.getMenge();
            Ereignis ereignis = new Ereignis(benutzer,datum,artikel,menge);
            ereignisList.add(ereignis);
        }
    }


    public boolean artikelAusWarenkorbKaufen () {
        if (artikelList.isEmpty()) {
            System.out.println("Der Warenkorb ist leer. Es gibt keine Artikel zum Kaufen.");
            return false;
        } else {
            // gesamt preis initialisieren
            double gesamtPreis = 0.0;
            Kunde kunde = null;
            rechnung = new Rechnung (benutzer, artikelList);
            artikelList.clear();
            System.out.println("Die Artikel im Warenkorb wurden gekauft.");
            rechnung.showRechnung();
        }
        return true;
    }



    public void showAlleEreignisse()
    {
        for (Ereignis er : ereignisList)
        {
            System.out.println("> " + String.valueOf(er.getDatum())
                    +" " + String.valueOf(er.getBenutzer())
                    +" " + String.valueOf(er.getErgeinis())
                    +" Artikel: " + String.valueOf(er.getArtikel().getBezeichnung())
                    +" Stükzahl: " + String.valueOf(er.getAnzahl()));
        }
    }
}





































    /*public ArtikelVerwaltung() {
        Artikel a1 = new Artikel("shuhe", 5, 19.5, 123);
        Artikel a2 = new Artikel("hose", 2, 18.5, 124);
        Artikel a3 = new Artikel("kleid", 3, 17.5, 113);
        this.artikelListe = new ArrayList<>(Arrays.asList(new Artikel []{a1,a2,a3}));
        artikelListe.add(a1);
        artikelListe.add(a2);
        artikelListe.add(a3);
    }

    public List<Artikel> artikelAusgeben() {
        if (artikelListe.isEmpty()) {
            System.out.println("Die Artikelliste ist leer.");
        } else {
            System.out.println("Artikelliste:");
            for (Artikel artikel : artikelListe) {
                System.out.println(artikel);
            }
        }
        return null;
    }

    public List <Artikel > getArtikelBestand(){
        artikelListe .sort(Comparator.comparing(Artikel ::getArtikelNummer));
        return artikelListe;
    }

    // Methode zum Hinzufügen eines Artikels
    public Artikel artikelHinzufuegen(String  bezeichnung,int bestand,double preis,int artikelNummer) {
        Artikel neuerArtikel = new Artikel(bezeichnung,bestand,preis,artikelNummer);
        artikelListe.add(neuerArtikel);
        System.out.println("Neuer Artikel wurde erfolgreich angelegt: " + neuerArtikel.toString());
        return neuerArtikel;
    }


    // Methode zum Entfernen eines Artikels
    public void artikelEntfernen(Artikel artikel) {
        artikelListe.remove(artikel);
    }

    public Artikel artikelSuchen(int artikelnummer) {
        for (Artikel artikel : artikelListe) {
            if (artikel.getArtikelNummer() == artikelnummer) {
                return artikel;
            }
        }
        return null;
    }

    public void alleArtikelAnzeigen() {
        for (Artikel a : artikelListe) {
        }
    }
    public void erhoeheBestand(String bezeichnung, int menge) {
        if (artikelListe == null) {
            return;
        }
        Artikel artikel = artikelListe.stream()
                .filter(a -> a.getBezeichnung().equals(bezeichnung))
                .findFirst()
                .orElse(null);

        if (artikel != null) {
            artikel.setBestand(artikel.getBestand() + menge);
            ereignisList.add(new Ereignis("Mitarbeiter",0, "Bestand Erhöhen",artikel,menge ));
        } else {
            System.out.println("Artikel unbekannt!");
            return;
        }
    }

    public void bestandVerringern(String bezeichnung, int menge) {

        Artikel artikel = artikelListe.stream()
                .filter(a -> a.getBezeichnung().equals(bezeichnung))
                .findFirst()
                .orElse(null);

        if (artikel != null) {
            int aktuellerBestand = artikel.getBestand();

            if (aktuellerBestand >= menge) {
                artikel.setBestand(aktuellerBestand - menge);
                ereignisList.add(new Ereignis("Mitarbeiter",0, "Bestand Verringern",artikel,menge ));
                System.out.println("Der Bestand von Artikel " + artikel.getBezeichnung() + " wurde um " + menge + " Stück(e) verringert.");
            }
            else {
                System.out.println("Nicht genug Bestand von Artikel " + artikel.getBezeichnung() + " vorhanden.");
            }
        } else {
            System.out.println("Artikel " + bezeichnung + " wurde nicht gefunden.");
        }
    }

    public boolean artikelExistiert(Artikel artikel) {
        for (Artikel a : artikelListe) {
            if (a.equals(artikel)) {
                return true;
            }
        }
        return false;
    }

    public Artikel artikelNachNummer(int artikelNummer) {
        Collections.sort(artikelListe, Comparator.comparingInt(Artikel::getArtikelNummer));
        System.out.println("Artikelliste wurde nach Artikelnummer sortiert.");
        return null;
    }

    public Artikel artikelNachBezeichnung(String bezeichnung) {
        artikelListe.sort(Comparator.comparing(Artikel::getBezeichnung));
        System.out.println("Artikelliste wurde nach Bezeichnung sortiert.");
        return (Artikel) Collections.singletonList(artikelListe);
    }


    public List<Artikel> artikelListe() {
        return artikelListe;
    }
    public void aktualisiereBestand (Artikel artikel, int verkaufteMenge) {
        if (artikel != null && artikel.getBestand() >= verkaufteMenge) {
            int neuerBestand = artikel.getBestand() - verkaufteMenge;
            artikel.setBestand((neuerBestand));
        }
    }
    //Warenkorb



    public void artikelImWarenkorbReinlegen(Artikel artikel, int menge) {
        if (artikelMap.containsKey(artikel)) {
            int vorhandeneMenge = artikelMap.get(artikel);
            artikelMap.put(artikel, vorhandeneMenge + menge);
        } else {
            artikelMap.put(artikel, menge);
        }
        ereignisList.add(new Ereignis("Kunde",0, "Bestand Erhöhen",artikel,menge ));
    }

    // Methode zum Entfernen eines Artikels mit bestimmter Menge aus dem Warenkorb
    public boolean artikelImWarenkorbRaus(Artikel artikel, int menge) {
        boolean isOk = false;
        if (artikelMap.containsKey(artikel)) {
            int vorhandeneMenge = artikelMap.get(artikel);

            if (menge >= vorhandeneMenge) {
                artikelMap.remove(artikel);
                System.out.println("(" + vorhandeneMenge + ") Stück (e) von Artikel " + artikel.getBezeichnung() + " wurden aus dem Warenkorb entfernt.");

                ereignisList.add(new Ereignis("Kunde",0, "Bestand entfernt",artikel,menge ));
                isOk = true;
            } else {
                artikelMap.put(artikel, vorhandeneMenge - menge);
                System.out.println(menge + " Stück(e) von Artikel " + artikel.getBezeichnung() + " wurden aus dem Warenkorb entfernt.");

                ereignisList.add(new Ereignis("Kunde",0, "Bestand verringert",artikel,menge ));
                isOk = true;
            }
        } else {
            System.out.println("Artikel " + artikel.getBezeichnung() + " befindet sich nicht im Warenkorb.");
        }
        return isOk;
    }

    public void artikelImWarenkorbAnzeigen() {
        if (artikelMap.isEmpty()) {
            System.out.println("Der Warenkorb ist leer.");
        }
        else {
            System.out.println("Artikel im Warenkorb:");
            for (Map.Entry<Artikel, Integer> entry : artikelMap.entrySet()) {
                Artikel artikel = entry.getKey();
                int menge = entry.getValue();
                System.out.println(artikel.toString() + " - Menge: " + menge);
            }
        }
    }

    public boolean artikelAusWarenkorbKaufen () {
        if (artikelMap.isEmpty()) {
            System.out.println("Der Warenkorb ist leer. Es gibt keine Artikel zum Kaufen.");
            return false;
        } else {
            double gesamtPreis = 0.0;
            Kunde Benutzer = null;
            rechnung = new Rechnung(Benutzer, artikelMap);
            artikelMap.clear();
            System.out.println("Die Artikel im Warenkorb wurden gekauft.");
            rechnung.showRechnung();
        }
        return true;
    }
    
    public void showAlleEreignisse()
    {
        for (Ereignis er : ereignisList)
        {
            System.out.println("> " + String.valueOf(er.getDatum())
                    +" " + String.valueOf(er.getBenutzer())
                    +" " + String.valueOf(er.getErgeinis())
                    +" Artikel: " + String.valueOf(er.getArtikel().getBezeichnung())
                    +" Stükzahl: " + String.valueOf(er.getAnzahl()));
        }
    }


    public Artikel sucheArtikel(int artikelNummer) {
        for (Artikel artikel : artikelListe()) {
            if (artikel.getArtikelNummer() == artikelNummer) {
                return artikel;
            }
        }
        return null;
    }
    public List<Artikel> sucheArtikelByBezeichnung(String bezeichnung) {
        List<Artikel> foundArtikels = new ArrayList<>();
        for (Artikel artikel : artikelListe()) {
            if (artikel.getBezeichnung().contains(bezeichnung)) {
                foundArtikels.add(artikel);
            }
        }
        return foundArtikels;
    }*/

