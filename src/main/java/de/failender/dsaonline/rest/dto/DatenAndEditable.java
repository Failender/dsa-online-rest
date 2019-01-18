package de.failender.dsaonline.rest.dto;

import de.failender.heldensoftware.xml.datenxml.Daten;

public class DatenAndEditable {

    private Daten daten;
    private boolean editable;
    private boolean xmlEditable;
    private boolean ownHeld;

    public DatenAndEditable(Daten daten, boolean editable, boolean xmlEditable, boolean ownHeld) {
        this.daten = daten;
        this.editable = editable;
        this.xmlEditable = xmlEditable;
        this.ownHeld = ownHeld;
    }

    public Daten getDaten() {
        return daten;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isXmlEditable() {
        return xmlEditable;
    }

    public boolean isOwnHeld() {
        return ownHeld;
    }
}
