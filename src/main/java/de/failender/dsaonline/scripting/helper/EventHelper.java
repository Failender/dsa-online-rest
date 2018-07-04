package de.failender.dsaonline.scripting.helper;

import de.failender.dsaonline.service.UserHeldenService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class EventHelper extends ScriptHelper {

	public static Ereignis getLastApEreignisFromHeld(Daten daten) {
		return UserHeldenService.extractLastEreignis(daten.getEreignisse().getEreignis());
	}

	public static List<Ereignis> getAllApEreignisFromHeld(Daten daten) {
		return daten.getEreignisse().getEreignis()
				.stream()
				.filter(ereignis -> ereignis.getAp() > 0)
				.collect(Collectors.toList());
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
