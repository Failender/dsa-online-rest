package de.failender.dsaonline.scripting.helper;

import de.failender.dsaonline.service.UserHeldenService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.failender.dsaonline.util.VersionService.extractLastEreignis;

@Component
public class EventHelper extends ScriptHelper {

	@DocumentedMethod(description = "Sucht das letzte Ereignis, welches AP gab, in einer Liste von Ereignissen", returnDescription = "Das gefundene Ereignis")
	public static Ereignis getLastApEreignisFromHeld(@DocumentedParameter(name ="daten", description = "Die Daten des zu durchsuchenden Helden") Daten daten) {
		return extractLastEreignis(daten.getEreignisse().getEreignis());
	}

	@DocumentedMethod(description = "Sucht alle Ereignisse, welche AP gaben, in einer Liste von Ereignissen", returnDescription = "Die gefundenen Ereignisse")
	public static List<Ereignis> getAllApEreignisFromHeld(@DocumentedParameter(name ="daten", description = "Die Daten des zu durchsuchenden Helden")Daten daten) {
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
}
