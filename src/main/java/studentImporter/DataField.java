package studentImporter;

/**
 * Describes all data fields that can be stored for an Applicant. Not all data
 * fields are required, some can be left empty. All required fields must not be
 * empty for a valid Applicant.
 * <p>
 * Every Enum element stores the type of the associated value in the Applicant
 * class. Some utility methods are provided to get the type for a specific data
 * field and to cast its values.
 * 
 * @author Christian Wichmann
 */
public enum DataField {

	/**
	 * First name of the applicant.
	 */
	FIRST_NAME(String.class, true),

	/**
	 * Last name of the applicant.
	 */
	LAST_NAME(String.class, true),

	/**
	 * Ausbildungsberuf.
	 */
	VOCATION(String.class, true),

	/**
	 * Fachrichtung.
	 */
	SPECIALIZATION(String.class, false),

	ADDRESS(String.class, true),

	/**
	 * Umschüler.
	 */
	RETRAINING(Boolean.class, true),

	/**
	 * Ausbildungsbeginn. Stored as String object containing a valid date in the
	 * format "dd.mm.yyyy".
	 */
	START_OF_TRAINING(String.class, true),

	/**
	 * Duration of vocational training (Ausbildungsdauer) in months.
	 */
	DURATION_OF_TRAINING(Double.class, true),

	BIRTHDAY(String.class, true),

	BIRTHPLACE(String.class, true),

	/**
	 * Religion (dt. Konfession) of the applicant defined by a number between 0
	 * and 6???.
	 * <p>
	 * <table>
	 * <tr>
	 * <td>0</td>
	 * <td>ohne Angabe</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>evangelisch</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>römisch-katholisch</td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td>alevitisch</td>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td>islamisch</td>
	 * </tr>
	 * <tr>
	 * <td>5</td>
	 * <td>sonstige</td>
	 * </tr>
	 * <tr>
	 * <td>6</td>
	 * <td>keine</td>
	 * </tr>
	 * </table>
	 */
	RELIGION(Integer.class, true),

	ZIP_CODE(Integer.class, true),

	CITY(String.class, true),

	PHONE(String.class, true),

	FAX(String.class, true),

	EMAIL(String.class, true),

	NAME_OF_LEGAL_GUARDIAN(String.class, true),

	ADDRESS_OF_LEGAL_GUARDIAN(String.class, true),

	PHONE_OF_LEGAL_GUARDIAN(String.class, true),

	/**
	 * Gender of the applicant. Currently either "m" for men or "w" for women.
	 */
	GENDER(Character.class, true),

	SCHOOL_ATTENDANCE_BEGIN(String.class, false),

	SCHOOL_ATTENDANCE_END(String.class, false),

	SCHOOL_ATTENDANCE_YEARS(Integer.class, false),

	SCHOOL_ATTENDANCE_TYPE(School.class, true),

	ACHIEVED_DEGREE(Degree.class, true),

	COMPANY_NAME(String.class, true),

	COMPANY_ADDRESS(String.class, true),

	COMPANY_ZIP_CODE(String.class, true),

	COMPANY_CITY(String.class, true),

	COMPANY_TELEPHONE(String.class, true),

	COMPANY_FAX(String.class, true),

	COMPANY_CONTACT_PERSON(String.class, true),

	COMPANY_CONTACT_MAIL(String.class, true),

	NOTES(String.class, false),

	SCHOOL(School.class, true),

	DEGREE(Degree.class, true);

	private final Class<?> dataFieldType;
	private boolean isRequired;

	DataField(Class<?> classType, boolean isRequired) {
		this.dataFieldType = classType;
		this.isRequired = isRequired;
	}

	/**
	 * Gets the type for a specific data field. The associated type is stored
	 * inside the Enum to allow correct casts of the return value of Applicant's
	 * getValue() method.
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
	 * Returns whether this data field is required for all valid applicants. All
	 * required fields MUST not be empty!
	 * 
	 * @return true, if data field is required
	 */
	public boolean isRequired() {
		return isRequired;
	}

	/**
	 * Gets the value from given Applicant for this Enum element. The returned
	 * element is cast to the type that the compiler decides by type inference
	 * from the left-hand value at the calling code.
	 * 
	 * @param applicant
	 *            from which correctly cast value should be returned
	 * @return value from given applicant, correctly cast
	 */
	@SuppressWarnings("unchecked")
	public <T> T getFrom(Applicant applicant) {
		return (T) applicant.getValue(this);
	}
}
