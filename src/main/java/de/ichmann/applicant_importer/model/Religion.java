package de.ichmann.applicant_importer.model;

public enum Religion {

	OHNE_ANGABE(0),

	EVANGELISCH(1),

	KATHOLISCH(3),

	ALEVITISCH(5),

	ISLAMISCH(6),

	SONSTIGE(7),

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
