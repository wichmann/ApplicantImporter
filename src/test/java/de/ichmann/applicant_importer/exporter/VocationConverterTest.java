package de.ichmann.applicant_importer.exporter;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VocationConverterTest {

    private VocationConverter vc;

    @Before
    public void setUp() throws Exception {
        vc = VocationConverter.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testGuessVocation() {
        assertEquals(vc.guessVocation("Elektroniker für Energie- und Gebäude"),
                "Elektroniker(in) - Energie- und Gebäudetechnik -");
        assertEquals(vc.guessVocation("Elektroniker Energie- und Gebäude"),
                "Elektroniker(in) - Energie- und Gebäudetechnik -");
        assertEquals(vc.guessVocation("Informationselektronikerin"), "Informationselektroniker(in)");
        assertEquals(vc.guessVocation("Chemiekant"), "Chemikant(in)");
        assertEquals(vc.guessVocation("Metallbauer Nutzfahrzeuge"),
                "Metallbauer(in) - Nutzfahrzeugbau -");
    }

    @Test
    public final void testConvertVocation() {
        assertEquals(vc.convertVocation("Elektroniker Energie- und Gebäude"), "EEG");
        assertEquals(vc.convertVocation("Elektroniker Energie- und Gebäude"), "EEG");
        assertEquals(vc.convertVocation("Informationselektronikerin"), "EIN");
        assertEquals(vc.convertVocation("Chemiekant"), "CCK");
        assertEquals(vc.convertVocation("Metallbauer Nutzfahrzeuge"), "MM5");
    }

}
