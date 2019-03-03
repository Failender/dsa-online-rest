package de.failender.heldensoftware;

import de.failender.heldensoftware.xml.datenxml.Ereignis;

import java.util.List;

public class HeldenSoftwareUtil {

    public static void clearEreigniskontrolle(List<Ereignis> ereignisse) {
        if (ereignisse.isEmpty()) {
            return;
        }
        Ereignis ereignis = ereignisse.get(ereignisse.size() - 1);
        if (ereignis.getAktion() == null) {
            return;
        }
        if (ereignis.getAktion().equals("Änderungskontrolle")) {
            ereignisse.remove(ereignisse.size() - 1);
        }
    }
}
