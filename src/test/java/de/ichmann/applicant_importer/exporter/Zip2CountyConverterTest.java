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
		assertEquals(converter.convertZipCode(38100), "101");
		assertEquals(converter.convertZipCode(38300), "158");
		assertEquals(converter.convertZipCode(49074), "404");
		assertEquals(converter.convertZipCode(27243), "458");
		assertEquals(converter.convertZipCode(49565), "459");
		assertEquals(converter.convertZipCode(0), "");
		assertEquals(converter.convertZipCode(12345), "");
	}

}
