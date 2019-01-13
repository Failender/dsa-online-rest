package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.service.VersionRepositoryService;
import de.failender.dsaonline.rest.dto.Differences;
import de.failender.dsaonline.restservice.helper.DatenBuilder;
import de.failender.dsaonline.restservice.helper.JaxbHelper;
import de.failender.dsaonline.service.HeldenService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.UUID;

import static de.failender.dsaonline.restservice.helper.DatenBuilder.daten;

public class HeldenServiceTest extends HeldenTest {

	private int i = 0;

	private HeldenService heldenService;

	@Before
	public void doBefore() {
		i = 0;
		VersionEntity ve = new VersionEntity();
		ve.setCacheId(UUID.randomUUID());
		VersionRepositoryService versionRepositoryService = Mockito.mock(VersionRepositoryService.class);
		Mockito.when(versionRepositoryService.findVersion(Mockito.any(HeldEntity.class), Mockito.any(Integer.class)))
				.thenReturn(ve);
		heldenService = new HeldenService(heldRepositoryService, heldenApi, userRepository, versionRepositoryService, securityUtils, lagerortRepository);
	}

	@Test
	@FlywayTest
	public void testComparison() {

		Differences differences = heldenService.calculateDifferences(TEST_HELD_ID, 1,2);
		Assertions.assertThat(differences.getHeldname().equals(TEST_HELD_NAME));
		Assertions.assertThat(differences.getTalente().isEmpty()).isEqualTo(true);
		Assertions.assertThat(differences.getVorteile().isEmpty()).isEqualTo(true);
		Assertions.assertThat(differences.getZauber().isEmpty()).isEqualTo(true);
		Assertions.assertThat(differences.getEigenschaften().isEmpty()).isEqualTo(false);
		Assertions.assertThat(differences.getEigenschaften().get(0).getName()).isEqualTo("Mut");
//		Assertions.assertThat(differences.getEigenschaften().get(0).getTooltip().contains("12345")).isEqualTo(true);
	}

	@Override
	protected InputStream handleDatenRequest() throws JAXBException {
		Ereignis e = new Ereignis();
		e.setAktion("");

		DatenBuilder builder =daten()
				.addEreignis(e)
				.apGesamt(heldenContext.getGesamtAp());

		builder.mut(10 + i);
		if(i == 1) {
			Ereignis ereignis = new Ereignis();
			ereignis.setAlterzustand("10");
			ereignis.setAlterzustand("11");
			ereignis.setObject("Mut");
			ereignis.setBemerkung("");
			ereignis.setAp(12345);
			ereignis.setAktion("");
			builder.addEreignis(ereignis);
		}
		i++;


		Daten daten =  builder.build();
		return JaxbHelper.marshall(daten);

	}
}
