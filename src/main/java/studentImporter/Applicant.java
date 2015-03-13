package studentImporter;

import java.util.Date;
import java.util.Random;

public class Applicant {

	private final String firstName;
	private final String surname;
	private final String vocation; // Ausbildungsberuf
	private final String specialization; // Fachrichtung
	private final boolean retraining; // Umschüler
	private final String startOfTraining; // Ausbildungsbeginn
	private final double DurationOfTraining; // Ausbildungsdauer
	private final Date birthday;
	private final String birthplace;
	private final String religion; // Konfession
	private final String city; // PLZ und Stadt
	private final String email;
	private final String nameOfLegalGuardian;
	private final String addressOfLegalGuardian;
	private final String phoneOfLegalGuardian;

	public static class StudentBuilder {
		private String firstName = "";
		private String surname = "";
		private String vocation = ""; // Ausbildungsberuf
		private String specialization = ""; // Fachrichtung
		private boolean retraining = false; // Umschüler
		private String startOfTraining; // Ausbildungsbeginn
		private double DurationOfTraining = 0.0; // Ausbildungsdauer
		private Date birthday;
		private String birthplace = "";
		private String religion = ""; // Konfession
		private String city = ""; // PLZ und Stadt
		private String email = "";
		private String nameOfLegalGuardian = "";
		private String addressOfLegalGuardian = "";
		private String phoneOfLegalGuardian = "";

		public StudentBuilder() {
		}

		public StudentBuilder firstName(String val) {
			firstName = val;
			return this;
		}

		public StudentBuilder surname(String val) {
			surname = val;
			return this;
		}

		public StudentBuilder vocation(String val) {
			vocation = val;
			return this;
		}

		public StudentBuilder specialization(String val) {
			specialization = val;
			return this;
		}

		public StudentBuilder retraining(boolean val) {
			retraining = val;
			return this;
		}

		public StudentBuilder startOfTraining(String val) {
			startOfTraining = val;
			return this;
		}

		public StudentBuilder DurationOfTraining(double val) {
			DurationOfTraining = val;
			return this;
		}

		public StudentBuilder birthday(Date val) {
			birthday = val;
			return this;
		}

		public StudentBuilder birthplace(String val) {
			birthplace = val;
			return this;
		}

		public StudentBuilder religion(String val) {
			religion = val;
			return this;
		}

		public StudentBuilder city(String val) {
			city = val;
			return this;
		}

		public StudentBuilder email(String val) {
			email = val;
			return this;
		}

		public StudentBuilder nameOfLegalGuardian(String val) {
			nameOfLegalGuardian = val;
			return this;
		}

		public StudentBuilder addressOfLegalGuardian(String val) {
			addressOfLegalGuardian = val;
			return this;
		}

		public StudentBuilder phoneOfLegalGuardian(String val) {
			phoneOfLegalGuardian = val;
			return this;
		}

		public Applicant build() {
			return new Applicant(this);
		}
	}

	private Applicant(Applicant.StudentBuilder builder) {
		firstName = builder.firstName;
		surname = builder.surname;
		vocation = builder.vocation;
		specialization = builder.specialization;
		retraining = builder.retraining;
		startOfTraining = builder.startOfTraining;
		DurationOfTraining = builder.DurationOfTraining;
		birthday = builder.birthday;
		birthplace = builder.birthplace;
		religion = builder.religion;
		city = builder.city;
		email = builder.email;
		nameOfLegalGuardian = builder.nameOfLegalGuardian;
		addressOfLegalGuardian = builder.addressOfLegalGuardian;
		phoneOfLegalGuardian = builder.phoneOfLegalGuardian;
	}

	@Override
	public String toString() {
		return "<" + firstName + " " + surname + ">";
	}

	public final String getFirstName() {
		return firstName;
	}

	public final String getSurname() {
		return surname;
	}

	public final String getVocation() {
		return vocation;
	}

	public final String getSpecialization() {
		return specialization;
	}

	public final boolean isRetraining() {
		return retraining;
	}

	public final String getStartOfTraining() {
		return startOfTraining;
	}

	public final double getDurationOfTraining() {
		return DurationOfTraining;
	}

	public final Date getBirthday() {
		return birthday;
	}

	public final String getBirthplace() {
		return birthplace;
	}

	public final String getReligion() {
		return religion;
	}

	public final String getCity() {
		return city;
	}

	public final String getEmail() {
		return email;
	}

	public final String getNameOfLegalGuardian() {
		return nameOfLegalGuardian;
	}

	public final String getAddressOfLegalGuardian() {
		return addressOfLegalGuardian;
	}

	public final String getPhoneOfLegalGuardian() {
		return phoneOfLegalGuardian;
	}

	/**
	 * Checks all data for plausibility. It checks whether all necessary data is
	 * present and if all data has the expected format.
	 * 
	 * @return true, only if all data is OK
	 */
	public boolean checkPlausibility() {
		Random r = new Random();
		return r.nextBoolean();
	}

	public String getStreet() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getZipCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSex() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReligionText() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDegree() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLastSchool() {
		// TODO Auto-generated method stub
		return null;
	}
}
