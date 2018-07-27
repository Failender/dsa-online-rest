package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.service.HeldRepositoryService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.dsaonline.service.UserService;
import de.failender.dsaonline.util.DevInsertTestData;
import de.failender.dsaonline.util.JaxbUtil;
import de.failender.heldensoftware.api.CacheHandler;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.api.requests.ApiRequest;
import de.failender.heldensoftware.api.requests.ConvertingRequest;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class HeldenTest extends DsaOnlineTest {

	@Autowired protected  HeldRepository heldRepository;
	@Autowired protected  UserRepository userRepository;
	@Autowired protected  HeldRepositoryService heldRepositoryService;
	@Autowired protected GruppeRepository gruppeRepository;
	@Autowired protected  HeldenApi heldenApi;
	protected UserService userService;

	protected static final String TEST_TOKEN = "token";
	protected static final TokenAuthentication TEST_AUTH= new TokenAuthentication(TEST_TOKEN);
	protected static final String TEST_GRUPPE = "Der Runde Tisch";
	protected static final String TEST_NAME= "name";
	protected static final BigInteger TEST_HELDID= BigInteger.valueOf(0L);


	protected UserHeldenService userHeldenService;


	@Before
	public void before() {
		heldenApi = Mockito.spy(heldenApi);
		heldenApi.setCacheHandler(Mockito.mock(CacheHandler.class));
		Answer<Mono<InputStream>> answer = invocationOnMock -> {
			ApiRequest _request = invocationOnMock.getArgument(0);
			String body = heldenApi.buildBody(_request.writeRequest());
			System.out.println(_request.url());
			System.out.println(body);
			if(_request instanceof ConvertingRequest) {
				ConvertingRequest request = (ConvertingRequest) _request;
				if(request.getFormat() == HeldenApi.Format.datenxml) {
					return handlePdfConvertingRequest(request);
				} else {
					return Mono.just(new NearlyEmptyInputStream());
				}
			}
			return null;
		};
		Mockito.doAnswer(answer).when(heldenApi).doRequest(Mockito.any());
		userHeldenService = new UserHeldenService(heldRepository, userRepository, heldenApi, heldRepositoryService);
		userService = new UserService(userRepository, gruppeRepository, userHeldenService);
		userService.createUsers(Arrays.asList(new DevInsertTestData.UserData(TEST_NAME, TEST_TOKEN, null, null, TEST_GRUPPE, null)));


	}

	private Mono<InputStream> handlePdfConvertingRequest(ConvertingRequest request) throws JAXBException {
		Ereignis ereignis = new Ereignis();
		ereignis.setAp(1);
		ereignis.setKommentar("Test");
		Daten daten = DatenBuilder.builder().addEreignis(ereignis).build();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JaxbUtil.getMarshaller(Daten.class).marshal(daten, bos);
		ReusableByteArrayStream stream = new ReusableByteArrayStream(bos.toByteArray());
		return Mono.just(stream);
	}

	@FlywayTest
	@Test
	public void testRegistration() {
		Assertions.assertThat(userRepository.count()).isEqualTo(2);
	}
}
