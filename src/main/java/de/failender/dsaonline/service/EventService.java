package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.EventEntity;
import de.failender.dsaonline.data.repository.EventRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.rest.event.CreateEventDto;
import de.failender.dsaonline.rest.event.DsaMonat;
import de.failender.dsaonline.rest.event.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventService {


	private final EventRepository eventRepository;


	private final HeldRepository heldRepository;

	public static final String GRUPPE_KEY = "Gruppe";

	public EventService(EventRepository eventRepository, HeldRepository heldRepository) {
		this.eventRepository = eventRepository;
		this.heldRepository = heldRepository;
	}


	public Map<String, List<EventDto>> getEventsForGruppeAndHelden(int gruppe, DsaMonat dsaMonat) {
		log.info("Finding events between {} and {} for gruppe {}", dsaMonat.getStartTag(), dsaMonat.getEndTag(), gruppe);
		Map<String, List<EventDto>> events = getEventsForGruppe(gruppe, dsaMonat);
		events.putAll(getEventsForHelden(gruppe, dsaMonat));
		return events;
	}

	private Map<String, List<EventDto>> getEventsForGruppe(int gruppe, DsaMonat dsaMonat) {
		Map<String, List<EventDto>> eventMap = new HashMap<>();
		List<EventEntity> events = eventRepository.findByOwnerAndTypeAndDateBetween(BigInteger.valueOf(gruppe), EventEntity.Type.GRUPPE, dsaMonat.getStartTag(), dsaMonat.getEndTag());
		eventMap.put(GRUPPE_KEY, events.stream().map(this::toDto).collect(Collectors.toList()));
		return eventMap;
	}


	private Map<String, List<EventDto>> getEventsForHelden(int gruppe, DsaMonat dsaMonat) {
		Map<String, List<EventDto>> eventMap = new HashMap<>();
		heldRepository.findByGruppeId(gruppe)
				.stream()
				.forEach(held -> {
					List<EventEntity> events = eventRepository.findByOwnerAndTypeAndDateBetween(held.getId(), EventEntity.Type.HELD, dsaMonat.getStartTag(), dsaMonat.getEndTag());
					if (!events.isEmpty()) {
						eventMap.put(held.getName(), events.stream().map(this::toDto).collect(Collectors.toList()));
					}

				});

		return eventMap;
	}

	private EventDto toDto(EventEntity entity) {
		return new EventDto(entity.getName(), entity.getDate(), entity.getId());
	}

	public void createEvent(CreateEventDto dto) {
		EventEntity entity = new EventEntity();
		entity.setName(dto.getName());
		entity.setOwnerId(dto.getOwnerId());
		entity.setType(dto.getType());
		entity.setDate(dto.getDate());
		log.info("Saving new event {} at date {}", dto.getName(), dto.getDate());
		eventRepository.save(entity);


	}


}
