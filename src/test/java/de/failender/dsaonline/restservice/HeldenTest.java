package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.data.repository.VersionRepository;
import de.failender.dsaonline.restservice.helper.DatenBuilder;
import de.failender.dsaonline.restservice.helper.HeldenContext;
import de.failender.dsaonline.restservice.helper.JaxbHelper;
import de.failender.dsaonline.service.HeldRepositoryService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.dsaonline.service.UserService;
import de.failender.dsaonline.util.DevInsertTestData;
import de.failender.heldensoftware.api.CacheHandler;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.api.requests.*;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

import static de.failender.dsaonline.restservice.helper.HeldXmlHelper.heldxml;
import static de.failender.dsaonline.restservice.helper.HeldenListenBuilder.heldenliste;
import static org.assertj.core.api.Assertions.assertThat;

public class HeldenTest extends DsaOnlineTest {

	@Autowired protected  HeldRepository heldRepository;
	@Autowired protected  UserRepository userRepository;
	@Autowired protected  HeldRepositoryService heldRepositoryService;
	@Autowired protected GruppeRepository gruppeRepository;
	@Autowired protected  HeldenApi heldenApi;
	@Autowired protected VersionRepository versionRepository;
	protected UserService userService;
	protected final HeldenContext heldenContext = new HeldenContext();

	protected static final String TEST_TOKEN = "token";
	protected static final TokenAuthentication TEST_AUTH= new TokenAuthentication(TEST_TOKEN);
	protected static final String TEST_GRUPPE = "Der Runde Tisch";
	protected static final String TEST_USER_NAME = "name";
	protected static final String TEST_HELD_NAME = "heldname";
	protected static final BigInteger TEST_HELD_ID = BigInteger.valueOf(0L);


	protected UserHeldenService userHeldenService;


	@Before
	public void before() {
		heldenContext.setHeldid(TEST_HELD_ID);
		heldenContext.setName(TEST_HELD_NAME);
		heldenContext.setGesamtAp(500L);
		heldenContext.setStand(1000L);
		heldenContext.setLastEreignis("Test");
		heldenContext.setLastEreignisAp(50);
		heldenApi = Mockito.spy(heldenApi);
		heldenApi.setCacheHandler(Mockito.mock(CacheHandler.class));
		Answer<Mono<InputStream>> answer = invocationOnMock -> {
			ApiRequest _request = invocationOnMock.getArgument(0);
			String body = heldenApi.buildBody(_request.writeRequest());
			System.out.println(_request.url());
			System.out.println(body);
			if(_request instanceof ConvertingRequest) {
				System.err.println("IMPLEMENT CONVERTING");
				ConvertingRequest request = (ConvertingRequest) _request;
//				if(request.getFormat() == HeldenApi.Format.datenxml) {
//					return handlePdfConvertingRequest(request);
//				} else {
//					return Mono.just(new NearlyEmptyInputStream());
//				}
			} else if(_request instanceof ReturnHeldXmlRequest) {
				return Mono.just(handleRequest((ReturnHeldXmlRequest) _request));
			} else if(_request instanceof ReturnHeldDatenWithEreignisseRequest) {
				return Mono.just(handleRequest((ReturnHeldDatenWithEreignisseRequest) _request));
			} else if(_request instanceof ReturnHeldPdfRequest) {
				return Mono.just(handleRequest((ReturnHeldPdfRequest)_request));
			}

			else if(_request instanceof GetAllHeldenRequest) {
				return Mono.just(handleRequest((GetAllHeldenRequest)_request));
			} else {
				System.err.println("Uncaught request " + _request.getClass());
			}
			return null;
		};
		Mockito.doAnswer(answer).when(heldenApi).doRequest(Mockito.any());
		userHeldenService = new UserHeldenService(heldRepository, userRepository, heldenApi, heldRepositoryService);
		userService = new UserService(userRepository, gruppeRepository, userHeldenService);
		userService.createUsers(Arrays.asList(new DevInsertTestData.UserData(TEST_USER_NAME, TEST_TOKEN, null, null, TEST_GRUPPE, null)));


	}

	private InputStream handleRequest(ReturnHeldPdfRequest request) {
		return new NearlyEmptyInputStream();
	}

	private InputStream handleRequest(ReturnHeldDatenWithEreignisseRequest request) throws JAXBException {
		Ereignis ereignis = new Ereignis();
		ereignis.setAp(heldenContext.getLastEreignisAp());
		ereignis.setKommentar(heldenContext.getLastEreignis());
		ereignis.setAktion("Abenteuer");
		Daten daten = DatenBuilder.builder()
				.addEreignis(ereignis)
				.apGesamt(heldenContext.getGesamtAp())
				.build();
		InputStream stream = JaxbHelper.marshall(daten);
		return stream;
	}

	private InputStream handleRequest(GetAllHeldenRequest request) throws JAXBException {
		return JaxbHelper.marshall(heldenliste()
		.held(heldenContext.getHeldid(), heldenContext.getName(), heldenContext.getHeldid(), heldenContext.getStand())
		.build());
	}

	private InputStream handleRequest(ReturnHeldXmlRequest request) throws TransformerException, UnsupportedEncodingException {
		return new ReusableByteArrayStream(heldxml()
				.stand(heldenContext.getStand())
				.build());
	}



	@FlywayTest
	@Test
	public void testRegistration() {
		assertThat(userRepository.count()).isEqualTo(2);
		assertThat(userRepository.findAll()
				.stream()
				.map(UserEntity::getName)
				.toArray(String[]::new))
				.isEqualTo(new String[]{"Admin", TEST_USER_NAME});
		assertThat(heldRepository.count()).isEqualTo(1);
		assertThat(heldRepository.findAll().get(0).getName()).isEqualTo(TEST_HELD_NAME);
		assertThat(heldRepository.findAll().get(0).getId()).isEqualTo(TEST_HELD_ID);
		assertThat(versionRepository.count()).isEqualTo(1);
		assertThat(versionRepository.findAll().iterator().next().getVersion()).isEqualTo(1);
		assertThat(versionRepository.findAll().iterator().next().getLastEvent()).isEqualTo(heldenContext.getLastEreignis());

	}
}
