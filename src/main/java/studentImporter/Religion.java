package studentImporter;

public enum Religion {

	KEINE(0),

	EVANGELISCH(1),

	KATHOLISCH(2);

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
