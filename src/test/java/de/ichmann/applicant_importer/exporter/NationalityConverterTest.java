package de.ichmann.applicant_importer.exporter;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the converter to get an ID for a given string describing a nationality.
 * 
 * @author Christian Wichmann
 */
public class NationalityConverterTest {

    private NationalityConverter nc;

    @Before
    public void setUp() throws Exception {
        nc = NationalityConverter.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testGuessNationality() {
        assertEquals(nc.guessNationality(""), "Deutschland");
        assertEquals(nc.guessNationality(" "), "Deutschland");
        assertEquals(nc.guessNationality("        "), "Deutschland");
        assertEquals(nc.guessNationality("Deutsch"), "Deutschland");
        assertEquals(nc.guessNationality("deutsch"), "Deutschland");
        assertEquals(nc.guessNationality("französisch"), "Frankreich");
        assertEquals(nc.guessNationality("türkisch"), "Türkei");
        assertEquals(nc.guessNationality("Italian"), "Italien");
    }

    @Test
    public final void testConvertNationality() {
        assertEquals(nc.convertNationality(""), 0);
        assertEquals(nc.convertNationality(" "), 0);
        assertEquals(nc.convertNationality("        "), 0);
        assertEquals(nc.convertNationality("Deutsch"), 0);
        assertEquals(nc.convertNationality("deutsch"), 0);
        assertEquals(nc.convertNationality("französisch"), 129);
        assertEquals(nc.convertNationality("türkisch"), 163);
        assertEquals(nc.convertNationality("Italian"), 137);
    }
}
