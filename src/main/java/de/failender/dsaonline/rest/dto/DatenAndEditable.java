package de.failender.dsaonline.rest.dto;

import de.failender.heldensoftware.xml.datenxml.Daten;

public class DatenAndEditable {

    private Daten daten;
    private boolean editable;

    public DatenAndEditable(Daten daten, boolean editable) {
        this.daten = daten;
        this.editable = editable;
    }

    public Daten getDaten() {
        return daten;
    }

    public boolean isEditable() {
        return editable;
    }
}
