package studentImporter;

public enum Degree {

	OHNE_ABSCHLUSS,

	SEKUNDAR_I_HAUPTSCHULE,

	SEKUNDAR_I_REALSCHULE,

	ERWEITERTER_SEKUNDAR_I,

	FACHHOCHSCHULREIFE,

	ALLGEMEINE_HOCHSCHULEREIFE,

	SONSTIGES;

	private String information = "";

	public void admitAdditionalInformation(String information) {
		if (this.equals(SONSTIGES)) {
			this.information = information;
		} else {
			assert false : "Additional information is only available on certain types of this Enum.";
		}
	}

	public String getAdditionalInformation() {
		if (this.equals(SONSTIGES)) {
			return information;
		} else {
			assert false : "Additional information is only available on certain types of this Enum.";
			return "";
		}
	}
}
