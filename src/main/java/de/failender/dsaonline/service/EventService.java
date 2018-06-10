//package de.failender.dsaonline.service;
//
//import de.failender.dsaonline.data.entity.EventEntity;
//import de.failender.dsaonline.data.repository.EventRepository;
//import de.failender.dsaonline.data.repository.HeldRepository;
//import de.failender.dsaonline.rest.event.CreateEventDto;
//import de.failender.dsaonline.rest.event.EventDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class EventService {
//
//
//	@Autowired
//	private EventRepository eventRepository;
//
//	@Autowired
//	private HeldRepository heldRepository;
//
//	@Autowired
//	private EntityManager em;
//
//	public Map<String, List<EventDto>> getEventsForGruppeAndHelden(int gruppe, int jahr, int monat) {
//		int dbJahr = jahr - 1000;
//		int dbMonat = dbJahr * 13 + monat;
//		Map<String, List<EventDto>> events = getEventsForGruppe(gruppe, dbMonat);
//		events.putAll(getEventsForHelden(gruppe, dbMonat));
//		return events;
//	}
//
//	private Map<String, List<EventDto>> getEventsForGruppe(int gruppe, int dbMonat) {
//		Map<String, List<EventDto>> eventMap = new HashMap<>();
//
//		return eventMap;
//	}
//
//	private Map<String, List<EventDto>> getEventsForHelden(int gruppe, int dbMonat) {
//		Map<String, List<EventDto>> eventMap = new HashMap<>();
//
//		return eventMap;
//	}
//
//	private EventDto toDto(EventEntity entity) {
//		return null;
//	}
//
//	private static final String INSERT_DATE_QUERY = "INSERT INTO EVENT_TO_DATE VALUES(?0, ?1)";
//
//	public void createEvent(CreateEventDto dto) {
//		EventEntity entity = new EventEntity();
//		entity.setName(dto.getName());
//		entity.setOwnerId(dto.getOwnerId());
//		entity.setType(dto.getType());
//		entity = eventRepository.save(entity);
//
//
//		for (int i = dto.getStartDate(); i <= dto.getEndDate(); i++) {
//			Query query = em.createNativeQuery(INSERT_DATE_QUERY);
//			query.setParameter(0, entity.getId());
//			query.setParameter(1, i);
//			query.executeUpdate();
//		}
//	}
//
//
//}
