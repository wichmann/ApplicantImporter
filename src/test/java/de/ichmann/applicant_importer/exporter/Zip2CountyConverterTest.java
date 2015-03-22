package de.ichmann.applicant_importer.exporter;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the conversion utility class for conversion between a zip code and the corresponding county
 * id for use in BBS-Planung.
 * 
 * @author Christian Wichmann
 */
public final class Zip2CountyConverterTest {

    private Zip2CountyConverter converter = null;

    @Before
    public void setUp() throws Exception {
        converter = Zip2CountyConverter.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConvertZipCode() {
        assertEquals(converter.convertZipCode(49074), "404");
        assertEquals(converter.convertZipCode(38100), "101");
        assertEquals(converter.convertZipCode(38300), "158");
        assertEquals(converter.convertZipCode(49074), "404");
        assertEquals(converter.convertZipCode(27243), "458");
        assertEquals(converter.convertZipCode(49565), "459");
        assertEquals(converter.convertZipCode(12345), "511");
        assertEquals(converter.convertZipCode(0), "");
    }
}
