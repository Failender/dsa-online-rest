package de.failender.dsaonline.restservice.helper;

import de.failender.heldensoftware.xml.datenxml.Ereignis;

public class EreignisBuilder {

    public static Ereignis ereignis(int ap, String kommentar) {
        Ereignis ereignis = new Ereignis();
        ereignis.setAp(ap);
        ereignis.setKommentar(kommentar);
        return ereignis;
    }

    public static Ereignis ereignis(HeldenContext heldenContext) {
        Ereignis ereignis = new Ereignis();
        ereignis.setAp(heldenContext.getLastEreignisAp());
        ereignis.setKommentar(heldenContext.getLastEreignis());
        return ereignis;
    }
}
