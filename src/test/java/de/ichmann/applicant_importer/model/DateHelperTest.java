package de.ichmann.applicant_importer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.ichmann.applicant_importer.model.Applicant.ApplicantBuilder;

/**
 * Test the date helper class that provides methods to calculate the end date of a training and
 * whether a applicant is already older than 18.
 *
 * @author Christian Wichmann
 */
public final class DateHelperTest {

    private Applicant a1;
    private Applicant a2;
    private Applicant a3;
    private Applicant a4;

    private DateHelper dh;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    @Before
    public void setUp() throws Exception {
        dh = DateHelper.getInstance();

        // create calendar to calculate dates to be tested
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        final String oldDate = dateFormatter.format(calendar.getTime());

        final ApplicantBuilder ab = new ApplicantBuilder();
        ab.setValue(DataField.START_OF_TRAINING, "01.09.1999");
        ab.setValue(DataField.DURATION_OF_TRAINING, 36);
        ab.setValue(DataField.BIRTHDAY, oldDate);
        a1 = ab.build();

        ab.setValue(DataField.START_OF_TRAINING, "1.8.2014");
        ab.setValue(DataField.DURATION_OF_TRAINING, 30);
        ab.setValue(DataField.BIRTHDAY, oldDate);
        a2 = ab.build();

        ab.setValue(DataField.START_OF_TRAINING, "1-1-2012");
        ab.setValue(DataField.DURATION_OF_TRAINING, 24);
        ab.setValue(DataField.BIRTHDAY, oldDate);
        a3 = ab.build();

        calendar.add(Calendar.DAY_OF_MONTH, 2);
        final String newerDate = dateFormatter.format(calendar.getTime());

        ab.setValue(DataField.START_OF_TRAINING, "01.9.2012");
        ab.setValue(DataField.DURATION_OF_TRAINING, 42);
        ab.setValue(DataField.BIRTHDAY, newerDate);
        a4 = ab.build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsOlderThan18() {
        assertTrue(dh.isOlderThan18(a1));
        assertTrue(dh.isOlderThan18(a2));
        assertTrue(dh.isOlderThan18(a3));
        assertFalse(dh.isOlderThan18(a4));
    }

    @Test
    public void testGetEndDateOfTraining() {
        assertEquals(dh.getEndDateOfTraining(a1), "31.08.2002");
        assertEquals(dh.getEndDateOfTraining(a2), "31.01.2017");
        assertEquals(dh.getEndDateOfTraining(a3), "");
        assertEquals(dh.getEndDateOfTraining(a4), "29.02.2016");
    }
}
