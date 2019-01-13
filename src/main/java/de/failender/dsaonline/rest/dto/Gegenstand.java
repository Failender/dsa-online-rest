package de.failender.dsaonline.rest.dto;

public class Gegenstand {
    private String gegenstand;
    private int anzahl;
    private boolean deletable;
    private String lagerort;

    public Gegenstand(String gegenstand, int anzahl, boolean deletable) {
        this.gegenstand = gegenstand;
        this.anzahl = anzahl;
        this.deletable = deletable;
    }

    public String getGegenstand() {
        return gegenstand;
    }

    public void setGegenstand(String gegenstand) {
        this.gegenstand = gegenstand;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
    }

    public boolean getDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public void setLagerort(String lagerort) {
        this.lagerort = lagerort;
    }

    public String getLagerort() {
        return lagerort;
    }
}
