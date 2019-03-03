package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.data.repository.EventRepository;
import de.failender.dsaonline.rest.dto.CreateEventDto;
import de.failender.dsaonline.rest.dto.DsaMonat;
import de.failender.dsaonline.rest.dto.EventDto;
import de.failender.dsaonline.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("api/events")
@RestController
public class EventController {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private EventService eventService;

	@GetMapping("{gruppe}/{jahr}/{monat}")
	public Map<String, List<EventDto>> getEventsForGruppe(@PathVariable int gruppe, @PathVariable int jahr, @PathVariable int monat) {
		DsaMonat dsaMonat = new DsaMonat(jahr, monat);
		return eventService.getEventsForGruppeAndHelden(gruppe, dsaMonat);
	}

	@PostMapping
	public void postEvent(@RequestBody CreateEventDto createEventDto) {
		eventService.createEvent(createEventDto);
	}

	@DeleteMapping("{id}")
	public void deleteEvent(@PathVariable int id) {
		this.eventRepository.deleteById(id);
	}
}
