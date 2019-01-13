package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.LagerortRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.data.service.HeldRepositoryService;
import de.failender.dsaonline.data.service.VersionRepositoryService;
import de.failender.dsaonline.rest.dto.UserData;
import de.failender.dsaonline.restservice.helper.HeldenContext;
import de.failender.dsaonline.restservice.helper.JaxbHelper;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.dsaonline.service.UserService;
import de.failender.dsaonline.util.VersionService;
import de.failender.heldensoftware.api.CacheHandler;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.api.requests.*;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import org.junit.Before;
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

import static de.failender.dsaonline.restservice.helper.DatenBuilder.daten;
import static de.failender.dsaonline.restservice.helper.HeldXmlBuilder.heldxml;
import static de.failender.dsaonline.restservice.helper.HeldenListenBuilder.heldenliste;

public abstract class HeldenTest extends DsaOnlineTest {

	@Autowired protected  HeldRepository heldRepository;
	@Autowired protected  UserRepository userRepository;
	@Autowired protected  HeldRepositoryService heldRepositoryService;
	@Autowired protected GruppeRepository gruppeRepository;
	@Autowired protected  HeldenApi heldenApi;
	@Autowired protected VersionRepositoryService versionRepositoryService;
	@Autowired protected VersionService versionService;
	@Autowired protected SecurityUtils securityUtils;
	@Autowired protected LagerortRepository lagerortRepository;

	protected final HeldenContext heldenContext = new HeldenContext();

	protected static final String TEST_TOKEN = "token";
	protected static final TokenAuthentication TEST_AUTH= new TokenAuthentication(TEST_TOKEN);
	protected static final String TEST_GRUPPE = "Der Runde Tisch";
	protected static final String TEST_USER_NAME = "name";
	protected static final String TEST_HELD_NAME = "heldname";
	protected static final BigInteger TEST_HELD_ID = BigInteger.valueOf(0L);
	protected HeldEntity testHeld;

	protected UserService userService;
	protected UserHeldenService userHeldenService;
	protected HeldenService heldenService;


	@Before
	public void before() {
		heldenContext.setHeldid(TEST_HELD_ID);
		heldenContext.setName(TEST_HELD_NAME);
		heldenContext.setGesamtAp(500L);
		heldenContext.setStand(10000L);
		heldenContext.setLastEreignis("Test");
		heldenContext.setLastEreignisAp(50);
		heldenApi = Mockito.spy(heldenApi);
		heldenApi.setCacheHandler(Mockito.mock(CacheHandler.class));
		Answer<Mono<InputStream>> answer = invocationOnMock -> {
			ApiRequest _request = invocationOnMock.getArgument(0);
			String body = heldenApi.buildBody(_request);
			System.out.println(_request.url());
			System.out.println(body);
			if(_request instanceof ConvertingRequest) {

				return Mono.just(handleRequest((ConvertingRequest)_request));
			} else if(_request instanceof ReturnHeldXmlRequest) {
				return Mono.just(handleRequest((ReturnHeldXmlRequest) _request));
			} else if(_request instanceof ReturnHeldDatenWithEreignisseRequest) {
				return Mono.just(handleRequest((ReturnHeldDatenWithEreignisseRequest) _request));
			} else if(_request instanceof ReturnHeldPdfRequest) {
				return Mono.just(handleRequest((ReturnHeldPdfRequest)_request));
			} else if(_request instanceof UpdateXmlRequest) {
				return Mono.just(handleRequest((UpdateXmlRequest) _request));
			}

			else if(_request instanceof GetAllHeldenRequest) {
				return Mono.just(handleRequest((GetAllHeldenRequest)_request));
			} else {
				System.err.println("Uncaught request " + _request.getClass());
			}
			return null;
		};
		Mockito.doAnswer(answer).when(heldenApi).doRequest(Mockito.any());
		userHeldenService = new UserHeldenService(userRepository, heldenApi, heldRepositoryService, versionService, versionRepositoryService);
		userService = new UserService(userRepository, gruppeRepository, userHeldenService, heldenApi);
		heldenService = new HeldenService(heldRepositoryService, heldenApi, userRepository, versionRepositoryService, securityUtils, lagerortRepository);
		userService.createUsers(Arrays.asList(new UserData(TEST_USER_NAME, TEST_TOKEN, null, null, TEST_GRUPPE, null)));
		testHeld = heldRepositoryService.findHeld(TEST_HELD_ID);

	}

	private InputStream handleRequest(ReturnHeldPdfRequest request) {
		return new NearlyEmptyInputStream();
	}

	private InputStream handleRequest(ConvertingRequest request) throws JAXBException {
		if(request.getFormat() == HeldenApi.Format.datenxml) {
			return handleDatenRequest();
		} else {
			return new NearlyEmptyInputStream();
		}
	}

	protected InputStream handleDatenRequest() throws JAXBException{
		Ereignis ereignis = new Ereignis();
		ereignis.setAp(heldenContext.getLastEreignisAp());
		ereignis.setKommentar(heldenContext.getLastEreignis());
		ereignis.setAktion("Abenteuer");
		Daten daten = daten()
				.addEreignis(ereignis)
				.apGesamt(heldenContext.getGesamtAp())
				.build();
		InputStream stream = JaxbHelper.marshall(daten);
		return stream;
	}

	private InputStream handleRequest(ReturnHeldDatenWithEreignisseRequest request) throws JAXBException {
		return handleDatenRequest();
	}

	protected InputStream handleRequest(GetAllHeldenRequest request) throws JAXBException {
		return JaxbHelper.marshall(heldenliste()
		.held(heldenContext.getHeldid(), heldenContext.getName(), heldenContext.getHeldid(), heldenContext.getStand())
		.build());
	}

	private InputStream handleRequest(ReturnHeldXmlRequest request) throws TransformerException, UnsupportedEncodingException {
		return new ReusableByteArrayStream(heldxml()
				.stand(heldenContext.getStand())
				.key(0L)
				.build());
	}

	private InputStream handleRequest(UpdateXmlRequest request) {
		return new ReusableByteArrayStream(new byte[0]);
	}
}
