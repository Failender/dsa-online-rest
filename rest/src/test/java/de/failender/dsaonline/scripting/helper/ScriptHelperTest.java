package de.failender.dsaonline.scripting.helper;

import de.failender.dsaonline.restservice.DsaOnlineTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ScriptHelperTest extends DsaOnlineTest {

	@Autowired
	private EventHelper eventHelper;

	@Test
	public void testScriptHelper() {
		Assertions.assertThat(eventHelper.getMethods().size()).isEqualTo(2);
		Assertions.assertThat(eventHelper.getMethods().get(0).getName()).isEqualTo("getAllApEreignisFromHeld");
		Assertions.assertThat(eventHelper.getMethods().get(0).getParameter().size()).isEqualTo(1);
		Assertions.assertThat(eventHelper.getMethods().get(0).getParameter().get(0).getName()).isEqualTo("daten");
		Assertions.assertThat(eventHelper.getMethods().get(1).getName()).isEqualTo("getLastApEreignisFromHeld");


	}
}
