package de.ichmann.applicant_importer.model;


/**
 * Describes all data fields that can be stored for an Applicant. Not all data fields are required,
 * some can be left empty. All required fields must not be empty for a valid Applicant.
 * <p>
 * Every Enum element stores the type of the associated value in the Applicant class. Some utility
 * methods are provided to get the type for a specific data field and to cast its values.
 *
 * @author Christian Wichmann
 */
public enum DataField {

    /**
     * First name of the applicant.
     */
    FIRST_NAME(String.class, "Vorname", true),

    /**
     * Last name of the applicant.
     */
    LAST_NAME(String.class, "Nachname", true),

    /**
     * Ausbildungsberuf.
     */
    VOCATION(String.class, "Ausbildungsberuf", true),

    /**
     * Fachrichtung.
     */
    SPECIALIZATION(String.class, "Vertiefungsrichtung", false),

    /**
     * Address of the applicant containing the street name and house number.
     */
    ADDRESS(String.class, "Adresse", true),

    /**
     * Umschüler.
     */
    RETRAINING(Boolean.class, "Umschüler", true),

    /**
     * Ausbildungsbeginn. Stored as String object containing a valid date in the format
     * "dd.mm.yyyy".
     */
    START_OF_TRAINING(String.class, "Ausbildungsbeginn", true),

    /**
     * Duration of vocational training (Ausbildungsdauer) in months.
     */
    DURATION_OF_TRAINING(Double.class, "Ausbildungsdauer", true),

    /**
     *
     */
    BIRTHDAY(String.class, "Geburtstag", true),

    /**
     *
     */
    BIRTHPLACE(String.class, "Geburtsort", true),

    /**
     * Religion (Konfession) of the applicant defined by a member of the Religion enumeration.
     */
    RELIGION(Religion.class, "Konfession", true),

    /**
     *
     */
    ZIP_CODE(Integer.class, "PLZ", true),

    /**
     *
     */
    CITIZENSHIP(String.class, "Staatsangehörigkeit", true),

    /**
     *
     */
    CITY(String.class, "Ort", true),

    /**
     *
     */
    PHONE(String.class, "Telefon", true),

    /**
     *
     */
    FAX(String.class, "Fax", false),

    /**
     * Email address of the applicant.
     */
    EMAIL(String.class, "E-Mail", true),

    /**
     * Nationality of the Applicant. It is stored in text form in German. For exporting it to
     * BBS-Planung the helper class NationalityConverter can be used.
     */
    NATIONALITY(String.class, "Staatsangehörigkeit", true),

    /**
     *
     */
    NAME_OF_LEGAL_GUARDIAN(String.class, "Name der Erziehungsberechtigten", true),

    /**
     *
     */
    ADDRESS_OF_LEGAL_GUARDIAN(String.class, "Adresse der Erziehungsberechtigten", true),

    /**
     *
     */
    PHONE_OF_LEGAL_GUARDIAN(String.class, "Telefon der Erziehungsberechtigten", true),

    /**
     * Gender of the applicant. Currently either "m" for men or "w" for women.
     */
    GENDER(Character.class, "Geschlecht", true),

    /**
     *
     */
    SCHOOL_ATTENDANCE_BEGIN(String.class, "Beginn des Schulbesuch", false),

    /**
     *
     */
    SCHOOL_ATTENDANCE_END(String.class, "Ende des Schulbesuch", false),

    /**
     *
     */
    SCHOOL_ATTENDANCE_YEARS(Integer.class, "Jahre des Schulbesuch", false),

    /**
     *
     */
    SCHOOL_ATTENDANCE_TYPE(School.class, "Schulart", true),

    /**
     *
     */
    ACHIEVED_DEGREE(Degree.class, "Erreichter Abschluss", true),

    /**
     *
     */
    COMPANY_NAME(String.class, "Name des Betrieb", true),

    /**
     *
     */
    COMPANY_ADDRESS(String.class, "Adresse des Betrieb", true),

    /**
     *
     */
    COMPANY_ZIP_CODE(String.class, "PLZ des Betrieb", true),

    /**
     *
     */
    COMPANY_CITY(String.class, "Ort des Betrieb", true),

    /**
     *
     */
    COMPANY_TELEPHONE(String.class, "Telefon des Betrieb", true),

    /**
     *
     */
    COMPANY_FAX(String.class, "Fax des Betrieb", false),

    /**
     *
     */
    COMPANY_CONTACT_PERSON(String.class, "Ansprechpartner des Betrieb", true),

    /**
     *
     */
    COMPANY_CONTACT_MAIL(String.class, "Kontakt des Betrieb", true),

    /**
     *
     */
    NOTES(String.class, "Bemerkungen", false),

    /**
     * Last visited school type for the applicant.
     */
    SCHOOL(School.class, "Schulart", true),

    /**
     * Type of school that has been attended last before applying. Should only be used if the value
     * of SCHOOL is SONSTIGES!
     */
    SCHOOL_OTHER_TYPE(String.class, "Sonstige Schule", false),

    /**
     * Specialization of given school type that has been attended before. Currently only used for
     * Berufsfachschulen.
     */
    SCHOOL_SPECIALIZATION(String.class, "Fachrichtung", false),

    /**
     *
     */
    DEGREE(Degree.class, "Erreichter Abschluss", true),

    /**
     *
     */
    DEGREE_ADDITIONAL_INFORMATION(String.class, "Sonstiges Abschluss", false);

    private final Class<?> dataFieldType;
    private boolean isRequired;
    private String description;

    /**
     * Instantiates a new enumeration value of a data field.
     *
     * @param classType
     *            type of the value that this data field contains
     * @param description
     *            descriptive text of the data field (in German)
     * @param isRequired
     *            whether this data field is required
     */
    private DataField(final Class<?> classType, final String description, final boolean isRequired) {
        this.dataFieldType = classType;
        this.description = description;
        this.isRequired = isRequired;
    }

    /**
     * Gets the type for a specific data field. The associated type is stored inside the Enum to
     * allow correct casts of the return value of Applicant's getValue() method.
     * <p>
     * <b>Example:</b>
     * <p>
     * <code>DataField.BIRTHDAY.getTypeOfDataField().cast(object);</code>.
     *
     * @return type of
     */
    public Class<?> getTypeOfDataField() {
        return dataFieldType;
    }

    /**
     * Returns whether this data field is required for all valid applicants. All required fields
     * MUST not be empty!
     *
     * @return true, if data field is required
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * Gets the value from given Applicant for this Enum element. The returned element is cast to
     * the type that the compiler decides by type inference from the left-hand value at the calling
     * code.
     *
     * @param applicant
     *            from which correctly cast value should be returned
     * @param <T>
     *            type to which to cast the value of the data field
     * @return value from given applicant, correctly cast
     */
    @SuppressWarnings("unchecked")
    public <T> T getFrom(final Applicant applicant) {
        return (T) applicant.getValue(this);
    }

    /**
     * Returns a describing label for this data field. The returned string contains the description
     * in German.
     *
     * @return describing label for this data field
     */
    public String getDescription() {
        return description;
    }
}
