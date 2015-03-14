package studentImporter;

public enum School {

	FOERDERSCHULE(0),

	HAUPTSCHULE(0),

	REALSCHULE(0),

	OBERSCHULE(0),

	GESAMTSCHULE(0),

	GYMNASIUM(0),

	FACHOBERSCHULE(0),

	BERUFSSCHULE(0),

	BERUFSFACHSCHULE(0),

	SONSTIGES(0);

	private final int id;

	private School(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
}
