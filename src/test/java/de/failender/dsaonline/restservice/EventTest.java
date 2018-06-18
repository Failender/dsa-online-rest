package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.EventEntity;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.repository.EventRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.rest.event.CreateEventDto;
import de.failender.dsaonline.rest.event.DsaMonat;
import de.failender.dsaonline.rest.event.EventDto;
import de.failender.dsaonline.service.EventService;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EventTest extends DsaOnlineTest {


	private EventService eventService;

	@Autowired
	private EventRepository eventRepository;

	@Before
	public void setup() {
		HeldRepository heldRepository = Mockito.mock(HeldRepository.class);


		HeldEntity mockedHeld = new HeldEntity();
		mockedHeld.setName(TEST_HELD_NAME);
		mockedHeld.setId(TEST_HELD);
		List<HeldEntity> mockedList = Arrays.asList(mockedHeld);

		Mockito.when(heldRepository.findByGruppeId(Mockito.any(Integer.class))).thenReturn(mockedList);
		eventService = new EventService(eventRepository, heldRepository);

	}

	private static final BigInteger TEST_GRUPPE = BigInteger.valueOf(1L);
	private static final BigInteger TEST_HELD = BigInteger.valueOf(36222L);
	private static final String TEST_HELD_NAME = "test";


	@FlywayTest
	@Test
	public void testEvents() {

		DsaMonat dsaMonat = new DsaMonat(1003, 12);

		CreateEventDto createEventDto = new CreateEventDto();
		createEventDto.setDate(dsaMonat.getStartTag() + 15);
		createEventDto.setName("Test-Event");
		createEventDto.setOwnerId(TEST_GRUPPE);
		createEventDto.setType(EventEntity.Type.GRUPPE);
		eventService.createEvent(createEventDto);
		Map<String, List<EventDto>> eventMap = eventService.getEventsForGruppeAndHelden(TEST_GRUPPE.intValue(), dsaMonat);

		Assertions.assertThat(eventMap.size()).isEqualTo(1);
		Assertions.assertThat(eventMap.get(EventService.GRUPPE_KEY).size()).isEqualTo(1);

		createEventDto = new CreateEventDto();
		createEventDto.setDate(dsaMonat.getStartTag() + 15);
		createEventDto.setName("Test-Event-Held");
		createEventDto.setOwnerId(TEST_HELD);
		createEventDto.setType(EventEntity.Type.HELD);
		eventService.createEvent(createEventDto);

		eventMap = eventService.getEventsForGruppeAndHelden(TEST_GRUPPE.intValue(), dsaMonat);
		Assertions.assertThat(eventMap.size()).isEqualTo(2);
		Assertions.assertThat(eventMap.get(TEST_HELD_NAME).size()).isEqualTo(1);


	}


}
