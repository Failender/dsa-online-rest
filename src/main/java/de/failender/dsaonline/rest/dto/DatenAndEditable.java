package de.failender.dsaonline.rest.dto;

import de.failender.heldensoftware.xml.datenxml.Daten;

public class DatenAndEditable {

    private Daten daten;
    private boolean editable;
    private boolean xmlEditable;

    public DatenAndEditable(Daten daten, boolean editable, boolean xmlEditable) {
        this.daten = daten;
        this.editable = editable;
        this.xmlEditable = xmlEditable;
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
}
