package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.ScriptEntity;
import de.failender.dsaonline.data.entity.ScriptVariable;
import de.failender.dsaonline.scripting.IntConstantSupplier;
import de.failender.dsaonline.scripting.LatestHeldenForGruppePublicSupplier;
import de.failender.dsaonline.scripting.ScriptService;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.util.JaxbUtil;
import de.failender.heldensoftware.xml.datenxml.Daten;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScriptTest extends DsaOnlineTest {

	@Autowired
	public LatestHeldenForGruppePublicSupplier latestHeldenForGruppePublicSupplier;

	@Autowired
	private ScriptService scriptService;

	private static final String FAKE_GRUPPE_ID = "1";
	private static final String FAKE_HELD_ID="36222";
	private static final String FAKE_HELD_RED="500";


	@Test
	public void test() throws IOException {
		HeldenService heldenService = Mockito.mock(HeldenService.class);
		List<Daten> fakeDaten = new ArrayList<>();
		fakeDaten.add(JaxbUtil.datenFromStream(getResource("helden/36222.xml")));
		fakeDaten.add(JaxbUtil.datenFromStream(getResource("helden/36236.xml")));

		Mockito.when(heldenService.findPublicByGruppeId(Integer.valueOf(FAKE_GRUPPE_ID))).thenReturn(fakeDaten);
		latestHeldenForGruppePublicSupplier.setHeldenService(heldenService);

		ScriptEntity scriptEntity = new ScriptEntity();
		scriptEntity.setBody(IOUtils.toString(getResource("scripts/average_ap_public.js"), "UTF-8"));
		List<ScriptVariable> scriptVariables = new ArrayList<>();

		ScriptVariable groupVariable = new ScriptVariable();
		groupVariable.setType(LatestHeldenForGruppePublicSupplier.TYPE);
		groupVariable.setValue(FAKE_GRUPPE_ID);
		groupVariable.setName("helden");
		scriptVariables.add(groupVariable);


		ScriptVariable torfAmountVariable = new ScriptVariable();
		torfAmountVariable.setType(IntConstantSupplier.TYPE);
		torfAmountVariable.setValue(FAKE_HELD_RED);
		torfAmountVariable.setName("torfMissingAp");
		scriptVariables.add(torfAmountVariable);

		scriptEntity.setScriptVariables(scriptVariables);
		double result = (double) scriptService.execute(scriptEntity);
		int intResult = (int) result;
		Assertions.assertThat(intResult).isEqualTo(3085);


	}
}
