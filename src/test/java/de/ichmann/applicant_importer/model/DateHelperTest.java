package de.ichmann.applicant_importer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.ichmann.applicant_importer.model.Applicant.ApplicantBuilder;

public class DateHelperTest {

	private Applicant a1;
	private Applicant a2;
	private Applicant a3;
	private Applicant a4;
	private DateHelper dh;

	@Before
	public void setUp() throws Exception {
		dh = DateHelper.getInstance();
		ApplicantBuilder ab = new ApplicantBuilder();
		ab.setValue(DataField.START_OF_TRAINING, "01.09.1999");
		ab.setValue(DataField.DURATION_OF_TRAINING, 36);
		ab.setValue(DataField.BIRTHDAY, "18.06.1981");
		a1 = ab.build();
		ab.setValue(DataField.START_OF_TRAINING, "1.8.2014");
		ab.setValue(DataField.DURATION_OF_TRAINING, 30);
		ab.setValue(DataField.BIRTHDAY, "4.11.1984");
		a2 = ab.build();
		ab.setValue(DataField.START_OF_TRAINING, "1-1-2012");
		ab.setValue(DataField.DURATION_OF_TRAINING, 24);
		ab.setValue(DataField.BIRTHDAY, "19.3.1997");
		a3 = ab.build();
		ab.setValue(DataField.START_OF_TRAINING, "01.9.2012");
		ab.setValue(DataField.DURATION_OF_TRAINING, 42);
		ab.setValue(DataField.BIRTHDAY, "1.7.1997");
		a4 = ab.build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testIsOlderThan18() {
		assertTrue(dh.isOlderThan18(a1));
		assertTrue(dh.isOlderThan18(a2));
		assertTrue(dh.isOlderThan18(a3));
		assertFalse(dh.isOlderThan18(a4));
	}

	@Test
	public final void testGetEndDateOfTraining() {
		assertEquals(dh.getEndDateOfTraining(a1), "31.08.2002");
		assertEquals(dh.getEndDateOfTraining(a2), "31.01.2017");
		assertEquals(dh.getEndDateOfTraining(a3), "");
		assertEquals(dh.getEndDateOfTraining(a4), "29.02.2016");
	}
}
