package de.failender.dsaonline.scripting.helper;

import de.failender.dsaonline.service.UserHeldenService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

@Component
public class EventHelper extends ScriptHelper {

	public static Ereignis getLastEreinigsFromHeld(Daten daten) {
		return UserHeldenService.extractLastEreignis(daten.getEreignisse().getEreignis());
	}

	@Override
	public String getName() {
		return "eventHelper";
	}

	private static String[] methods;

	{
		methods = Stream.of(EventHelper.class.getMethods())
				.filter(method -> Modifier.isStatic(method.getModifiers()))
				.map(method -> method.getName())
				.toArray(s -> new String[s]);
	}

	@Override
	public String[] getMethods() {
		return methods;
	}
}
