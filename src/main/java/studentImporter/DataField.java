package studentImporter;

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
	 * Ausbildungsdauer.
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

	EMAIL(String.class, true),

	NAME_OF_LEGAL_GUARDIAN(String.class, true),

	ADDRESS_OF_LEGAL_GUARDIAN(String.class, true),

	PHONE_OF_LEGAL_GUARDIAN(String.class, true),

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

	NOTES(String.class, false);

	private final Class<?> dataFieldType;
	private boolean isRequired;

	DataField(Class<?> classType, boolean isRequired) {
		this.dataFieldType = classType;
		this.isRequired = isRequired;
	}

	public Class<?> getTypeOfDataField() {
		return dataFieldType;
	}

	public boolean isRequired() {
		return isRequired;
	}
}
