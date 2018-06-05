package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.EventEntity;
import de.failender.dsaonline.data.repository.EventRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.rest.event.EventDto;
import org.hibernate.mapping.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventService {


	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private HeldRepository heldRepository;


	public Map<String, List<EventDto>> getEventsForGruppeAndHelden(int gruppe, int jahr, int monat) {
		int dbJahr = jahr - 1000;
		int dbMonat = dbJahr * 13 + monat;
		Map<String, List<EventDto>> events = getEventsForGruppe(gruppe, dbMonat);
		events.putAll(getEventsForHelden(gruppe, dbMonat));
		return events;
	}

	private Map<String, List<EventDto>> getEventsForGruppe(int gruppe, int dbMonat) {
		Map<String, List<EventDto>> eventMap = new HashMap<>();
		List<EventEntity> events = eventRepository.findByOwnerIdAndTypeAndEndMonat(BigInteger.valueOf(gruppe), EventEntity.Type.GRUPPE, dbMonat);
		eventMap.put("gruppe", events.stream().map(this::toDto).collect(Collectors.toList()));
		return eventMap;
	}

	private Map<String, List<EventDto>> getEventsForHelden(int gruppe, int dbMonat) {
		Map<String, List<EventDto>> eventMap = new HashMap<>();

		heldRepository.findByGruppeId(gruppe)
				.forEach((held -> {
					List<EventEntity> events = eventRepository.findByOwnerIdAndTypeAndEndMonat(held.getId(), EventEntity.Type.HELD, dbMonat);
					events.addAll(eventRepository.findByOwnerIdAndTypeAndStartMonat(held.getId(), EventEntity.Type.HELD, dbMonat));
					eventMap.put(held.getName(), events.stream().map(this::toDto).collect(Collectors.toList()));
				}));

		return eventMap;
	}

	private EventDto toDto(EventEntity entity) {
		EventDto dto = new EventDto(entity.getName(), entity.getStartDate(), entity.getEndDate(), entity.getId());
		return dto;
	}
}
