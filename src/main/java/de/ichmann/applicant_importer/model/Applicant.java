package de.ichmann.applicant_importer.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all data field information for a given applicant that has been imported. This class is immutable and new objects can only be
 * created by use of the contained ApplicantBuilder.
 * 
 * @author Christian Wichmann
 */
public class Applicant {

	private final static Logger logger = LoggerFactory.getLogger(Applicant.class);

	private final Map<DataField, Object> applicantData = new HashMap<>();

	/**
	 * Collects all data field information and builds Applicant object with this data.
	 * 
	 * @author Christian Wichmann
	 */
	public static class ApplicantBuilder {

		private final Map<DataField, Object> applicantData = new HashMap<>();

		public ApplicantBuilder() {
		}

		public ApplicantBuilder setValue(DataField dataField, Object data) {
			applicantData.put(dataField, data);
			return this;
		}

		public Applicant build() {
			return new Applicant(this);
		}
	}

	private Applicant(ApplicantBuilder builder) {
		this.applicantData.putAll(builder.applicantData);
	}

	@Override
	public String toString() {
		return applicantData.get(DataField.FIRST_NAME) + " " + applicantData.get(DataField.LAST_NAME);
	}

	public final Object getValue(DataField dataField) {
		return applicantData.get(dataField);
	}

	/**
	 * Checks all data for plausibility. It checks whether all necessary data is present and if all data has the expected format.
	 * 
	 * @return true, only if all data is OK
	 */
	public boolean checkPlausibility() {

		for (Object o : applicantData.values()) {
			if (o == null) {
				logger.warn("Null value in applicant " + toString());
				return false;
			}
		}

		for (Entry<DataField, Object> entry : applicantData.entrySet()) {
			DataField dataField = entry.getKey();
			Object value = entry.getValue();
			if (dataField.isRequired() && (value == null || "".equals(value))) {
				logger.warn("Required value " + dataField + " in applicant " + toString() + " is missing!");
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns a set containing all data fields that have either a invalid value or are required and not given for this applicant. The
	 * decision is based only on the currently stored values.
	 * 
	 * @return set containing all invalid data fields
	 */
	public EnumSet<DataField> getInvalidDataFields() {
		EnumSet<DataField> invalidFields = EnumSet.noneOf(DataField.class);
		for (Entry<DataField, Object> entry : applicantData.entrySet()) {
			DataField dataField = entry.getKey();
			Object value = entry.getValue();
			if (dataField.isRequired() && (value == null || "".equals(value))) {
				invalidFields.add(dataField);
			}
		}
		return invalidFields;
	}

	/**
	 * Constructs and returns a string containing a help message mentioning those data fields of an applicant that contain invalid
	 * information.
	 * 
	 * @return help message mentioning all invalid data fields
	 */
	public String buildCommentFromApplicant() {
		StringBuilder commentBuilder = new StringBuilder();
		commentBuilder.append("Bitte die folgenden Felder überprüfen: ");
		boolean firstTime = true;
		for (DataField dataField : getInvalidDataFields()) {
			if (firstTime) {
				firstTime = false;
			} else {
				commentBuilder.append(", ");
			}
			commentBuilder.append(dataField.getDescription());
		}
		if (firstTime) {
			commentBuilder.append("KEINE");
		}
		return commentBuilder.toString();
	}
}
