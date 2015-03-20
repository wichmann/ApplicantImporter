package de.ichmann.applicant_importer.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains some helper methods for handling date calculations and checks.
 * 
 * @author Christian Wichmann
 */
public class DateHelper {

	private static final Logger logger = LoggerFactory.getLogger(DateHelper.class);

	private static DateHelper sInstance = null;

	/**
	 * Private constructor to prevent multiple instances.
	 */
	private DateHelper() {
		super();
	}

	/**
	 * Gets the unique instance of this converter.
	 */
	public static synchronized DateHelper getInstance() {
		if (sInstance == null) {
			sInstance = new DateHelper();
		}
		return sInstance;
	}

	/**
	 * Returns whether an applicant is older than 18 years. If the given date string can not be correctly parsed a false value is returned
	 * to the called.
	 * 
	 * @return whether an applicant is older than 18 years
	 */
	public boolean isOlderThan18(final Applicant applicant) {
		String birthdayString = DataField.BIRTHDAY.getFrom(applicant);
		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = DateFormat.getDateInstance();
		boolean isOlderThan18 = false;
		try {
			Date birthday, birthdayPlus18Years, today;
			birthday = dateFormat.parse(birthdayString);
			// add 18 years to original birthday
			cal.setTime(birthday);
			cal.add(Calendar.YEAR, 18);
			birthdayPlus18Years = cal.getTime();
			today = new Date();
			// check whether the birthday plus 18 years has happened before today
			if (birthdayPlus18Years.before(today)) {
				isOlderThan18 = true;
			} else {
				isOlderThan18 = false;
			}
		} catch (ParseException e) {
			logger.warn("Could not parse birthday from applicant " + applicant.toString() + ".");
		}
		return isOlderThan18;
	}

	public String getEndDateOfTraining(final Applicant applicant) {
		String startDateString = DataField.START_OF_TRAINING.getFrom(applicant);
		String endDateString = "";
		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = DateFormat.getDateInstance();
		Date startDate;
		try {
			startDate = dateFormat.parse(startDateString);
			cal.setTime(startDate);
			Integer months = DataField.DURATION_OF_TRAINING.getFrom(applicant);
			cal.add(Calendar.MONTH, months);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			endDateString = dateFormat.format(cal.getTime());
		} catch (ParseException e) {
			logger.warn("Could not parse start date from applicant " + applicant.toString() + ".");
		}
		return endDateString;
	}
}
