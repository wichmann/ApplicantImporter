package studentImporter;

public enum Degree {

	OHNE_ABSCHLUSS(0),

	SEKUNDAR_I_HAUPTSCHULE(0),

	SEKUNDAR_I_REALSCHULE(0),

	ERWEITERTER_SEKUNDAR_I(0),

	FACHHOCHSCHULREIFE(0),

	ALLGEMEINE_HOCHSCHULEREIFE(0),

	SONSTIGES(0);

	private final int id;

	private Degree(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
}
