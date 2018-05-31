package de.failender.dsaonline.rest.event;

import de.failender.dsaonline.data.entity.EventEntity;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.repository.EventRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
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

	@GetMapping("{gruppe}")
	public Map<String, List<EventDto>> getEventsForGruppe(@PathVariable int gruppe) {
		List<HeldEntity> helden = this.heldRepository.findByGruppeId(gruppe);
		List<EventEntity> gruppeEvents = eventRepository.findByOwnerIdAndType(BigInteger.valueOf(gruppe), EventEntity.Type.GRUPPE);

		Map<String, List<EventDto>> map = new HashMap<>();
		map.put("Gruppe", gruppeEvents
				.stream()
				.map(event -> new EventDto(event.getName(), event.getStartDate(), event.getEndDate(), event.getId())).collect(Collectors.toList()));

		helden.forEach(held -> {
			map.put(held.getName(), eventRepository.findByOwnerIdAndType(held.getId(), EventEntity.Type.HELD).stream()
					.map(event -> new EventDto(event.getName(), event.getStartDate(), event.getEndDate(), event.getId())).collect(Collectors.toList()));

		});
		return map;
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
