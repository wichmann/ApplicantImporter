package studentImporter;

public enum Degree {

	OHNE_ABSCHLUSS("OA"),

	SEKUNDAR_I_HAUPTSCHULE("HA"),

	SEKUNDAR_I_REALSCHULE("SI"),

	ERWEITERTER_SEKUNDAR_I("EI"),

	FACHHOCHSCHULREIFE("FH"),

	ALLGEMEINE_HOCHSCHULEREIFE("AH"),

	SONSTIGES("");

	private final String id;

	private Degree(String id) {
		this.id = id;
	}

	/**
	 * Returns the ID identifying the achieved degree in the BBS-Planung software.
	 * 
	 * @return id of achieved degree
	 */
	public String getId() {
		return id;
	}
}
