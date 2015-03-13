package studentImporter;

public enum School {

	FOERDERSCHULE,

	HAUPTSCHULE,

	REALSCHULE,

	OBERSCHULE,

	GESAMTSCHULE,

	GYMNASIUM,

	FACHOBERSCHULE,

	BERUFSSCHULE,

	BERUFSFACHSCHULE,

	SONSTIGES;

	private String information = "";

	public void admitAdditionalInformation(String information) {
		if (this.equals(BERUFSFACHSCHULE) || this.equals(SONSTIGES)) {
			this.information = information;
		} else {
			assert false : "Additional information is only available on certain types of this Enum.";
		}
	}

	public String getAdditionalInformation() {
		if (this.equals(BERUFSFACHSCHULE) || this.equals(SONSTIGES)) {
			return information;
		} else {
			assert false : "Additional information is only available on certain types of this Enum.";
			return "";
		}
	}
}
