package de.failender.dsaonline.service;

import de.failender.dsaonline.restservice.HeldenTest;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AbenteuerServiceTest extends HeldenTest {

	@Autowired
	private AbenteuerService abenteuerService;

	@FlywayTest
	@Test
	public void testAbenteuerVisibility() {
		//TODO REIMPLEMENT

	}
}
