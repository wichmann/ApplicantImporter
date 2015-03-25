package de.ichmann.applicant_importer.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all data field information for a given applicant that has been imported. This class is
 * immutable and new objects can only be created by use of the contained ApplicantBuilder.
 *
 * @author Christian Wichmann
 */
public final class Applicant {

    private static final Logger logger = LoggerFactory.getLogger(Applicant.class);

    private final Map<DataField, Object> applicantData = new HashMap<>();
    private String filename;

    /**
     * Collects all data field information and builds Applicant object with this data.
     *
     * @author Christian Wichmann
     */
    public static class ApplicantBuilder {

        private final Map<DataField, Object> applicantData = new HashMap<>();
        private String filename;

        /**
         * Instantiates a new ApplicantBuilder.
         */
        public ApplicantBuilder() {
        }

        /**
         * Sets a value in this ApplicantBuilder instance.
         *
         * @param dataField
         *            data field to be set
         * @param data
         *            data object for given data field
         * @return this builder itself
         * @throws IllegalArgumentException
         *             if parameter string is {@code null}
         */
        public final ApplicantBuilder setValue(final DataField dataField, final Object data) {
            if (dataField == null || data == null) {
                logger.error("" + dataField);
                throw new IllegalArgumentException("Parameters dataField and data must not be null");
            }
            applicantData.put(dataField, data);
            return this;
        }

        /**
         * Sets the name of the PDF file from which the data of this applicant has been read.
         *
         * @param filename
         *            name of the PDF file for this applicant
         * @return this builder itself
         * @throws IllegalArgumentException
         *             if parameter string is {@code null}
         */
        public final ApplicantBuilder setFileName(final String filename) {
            if (filename == null) {
                throw new IllegalArgumentException("Parameter filename must not be null");
            }
            this.filename = filename;
            return this;
        }

        /**
         * Create a new Applicant with the data stored in this ApplicantBuilder object.
         *
         * @return new Applicant object
         */
        public final Applicant build() {
            return new Applicant(this);
        }
    }

    /**
     * Creates a new applicant object from a given builder.
     *
     * @param builder
     *            builder containing the applicants data
     */
    private Applicant(final ApplicantBuilder builder) {
        assert builder != null : "Builder instance should not be null!";

        this.applicantData.putAll(builder.applicantData);
        this.filename = builder.filename;
    }

    @Override
    public String toString() {
        return applicantData.get(DataField.FIRST_NAME) + " "
                + applicantData.get(DataField.LAST_NAME);
    }

    /**
     * Gets the value of a specific data field for this applicant. This method returns never a
     * {@code null} but always an "empty" value of type string or integer or whatever type is
     * necessary depending on the specified data field.
     * <p>
     * If a data field value has been imported can be checked by the method wasImported().
     *
     * @param dataField
     *            data field to get value for
     * @return value for data field
     * @throws IllegalArgumentException
     *             if parameter string is {@code null}
     */
    public Object getValue(final DataField dataField) {
        if (dataField == null) {
            throw new IllegalArgumentException("Parameter dataField must not be null");
        }
        Object o = applicantData.get(dataField);
        if (o == null) {
            if (dataField.getTypeOfDataField() == Boolean.class) {
                o = new Boolean(false);
            } else if (dataField.getTypeOfDataField() == Integer.class) {
                o = new Integer(0);
            } else if (dataField.getTypeOfDataField() == Religion.class) {
                o = Religion.OHNE_ANGABE;
            } else if (dataField.getTypeOfDataField() == Degree.class) {
                o = Degree.SONSTIGER_ABSCHLUSS;
            } else if (dataField.getTypeOfDataField() == School.class) {
                o = School.SONSTIGES;
            } else if (dataField.getTypeOfDataField() == String.class) {
                o = new String();
            } else if (dataField.getTypeOfDataField() == Character.class) {
                o = new Character('m');
            } else {
                logger.error("Could not create value for \"empty\" data field: " + dataField);
                assert false : "Could not create value for \"empty\" data field: " + dataField;
            }
        }
        return o;
    }

    /**
     * Checks whether a value was set for a given data field. If no value was imported a false is
     * returned.
     *
     * @param dataField
     *            data field to check if it was imported
     * @return true, if a value was imported and stored for a given data field
     */
    public boolean wasImported(final DataField dataField) {
        Object o = applicantData.get(dataField);
        return !(o == null);
    }

    /**
     * Gets the name of the PDF file from which the data of this applicant has been read.
     *
     * @return name of the PDF file for this applicant
     */
    public String getFileName() {
        return filename;
    }

    /**
     * Checks all data for plausibility. It checks whether all necessary data is present and if all
     * data has the expected format.
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
                logger.warn("Required value " + dataField + " in applicant " + toString()
                        + " is missing!");
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a set containing all data fields that have either a invalid value or are required and
     * not given for this applicant. The decision is based only on the currently stored values.
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
     * Constructs and returns a string containing a help message mentioning those data fields of an
     * applicant that contain invalid information.
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
