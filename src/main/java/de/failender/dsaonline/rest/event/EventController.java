package de.failender.dsaonline.rest.event;

import de.failender.dsaonline.data.entity.EventEntity;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.repository.EventRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("api/events")
@RestController
public class EventController {

	@Autowired
	private EventRepository eventRepository;


	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private EventService eventService;

	@GetMapping("{gruppe}/{jahr}/{monat}")
	public Map<String, List<EventDto>> getEventsForGruppe(@PathVariable int gruppe, @PathVariable int jahr, @PathVariable int monat) {
		return eventService.getEventsForGruppeAndHelden(gruppe, jahr, monat);
	}

	@PostMapping
	public void postEvent(CreateEventDto createEventDto) {
		EventEntity eventEntity = new EventEntity();
		eventEntity.setEndDate(createEventDto.getEndDate());
		eventEntity.setStartDate(createEventDto.getStartDate());
		eventEntity.setName(createEventDto.getName());
		eventEntity.setOwnerId(createEventDto.getOwnerId());
		eventEntity.setType(createEventDto.getType());
		this.eventRepository.save(eventEntity);
	}

	@DeleteMapping("{id}")
	public void deleteEvent(@PathVariable int id) {
		this.eventRepository.deleteById(id);
	}
}
