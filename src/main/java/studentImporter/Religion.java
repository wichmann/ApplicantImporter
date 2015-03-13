package studentImporter;

public enum Religion {

	OHNE_ANGABE(0),

	EVANGELISCH(1),

	KATHOLISCH(2),

	ALEVITISCH(3), // TODO check if number is correct!

	ISLAMISCH(4),

	SONSTIGE(5),

	KEINE(6);

	private final int value;

	private Religion(int value) {
		this.value = value;
	}

	/**
	 * Returns the assigned value for this religion.
	 * 
	 * @return assigned value for religion
	 */
	public int getValue() {
		return value;
	}
}
