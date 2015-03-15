package de.ichmann.applicant_importer.exporter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Zip2CountyConverterTest {

	Zip2CountyConverter converter = null;

	@Before
	public void setUp() throws Exception {
		converter = Zip2CountyConverter.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testConvertZipCode() {
		assertEquals(converter.convertZipCode(49074), "404");
		assertEquals(converter.convertZipCode(38023), "101");
		assertEquals(converter.convertZipCode(38300), "158");
		assertEquals(converter.convertZipCode(49034), "404");
		assertEquals(converter.convertZipCode(27781), "458");
	}

}
