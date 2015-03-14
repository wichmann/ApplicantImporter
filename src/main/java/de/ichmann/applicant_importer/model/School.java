package de.ichmann.applicant_importer.model;

public enum School {

	FOERDERSCHULE(""),

	HAUPTSCHULE(""),

	REALSCHULE(""),

	OBERSCHULE(""),

	GESAMTSCHULE("IG"),

	GYMNASIUM(""),

	FACHOBERSCHULE(""),

	BERUFSSCHULE(""),

	BERUFSFACHSCHULE(""),

	SONSTIGES("");

	private final String id;

	private School(String id) {
		this.id = id;
	}

	/**
	 * Returns the ID identifying the school type in the BBS-Planung software.
	 * 
	 * @return id of school type
	 */
	public String getId() {
		return id;
	}
}
