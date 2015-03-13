package studentImporter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BbsPlanungExporter {

	private final static Logger logger = LoggerFactory
			.getLogger(BbsPlanungExporter.class);

	// set delimiter for end-of-line and between fields
	private static final String NEW_LINE_SEPARATOR = "\r\n";
	private static final char FIELD_DELIMITER = ';';
	
	// set all header for fields in CSV file
	private static final Object[] FILE_HEADER = { "SNR", "KL_NAME", "LFD",
			"STATUS", "NR_SCHÜLER", "NNAME", "VNAME", "GEBDAT", "GEBORT",
			"STR", "PLZ", "ORT", "TEL", "FAX", "LDK", "LDK_Z", "LANDKREIS",
			"EMAIL", "GESCHLECHT", "KONF", "KONF_TEXT", "STAAT", "FAMSTAND",
			"SFO", "TAKURZ", "KLST", "ORG", "DAUER", "TAKLSTORG", "SFOTEXT",
			"TALANG", "ORG_N", "A", "BG", "BG_SFO", "BG_BFELD", "BG_FREI",
			"BG_KLST", "BG_ORG", "BG_DAUER", "P_FAKTOR", "KO", "EINTR_DAT",
			"AUSB_BEGDAT", "A_DAUER", "A_ENDEDAT", "ANRECH_BGJ", "WIEDERHOL",
			"ABSCHLUSS", "HERKUNFT", "HER_ZUSATZ", "FH_Z", "SCHULPFLICHT",
			"N_DE", "HER_B", "BL_SOLL", "LM_M", "LM_Z", "LM_DAT", "UM",
			"A_AMT", "A_BEZIRK", "BETRAG", "BETRAG_G", "BAFOEG", "E_ANREDE",
			"E_NNAME", "E_VNAME", "E_STR", "E_PLZ", "E_ORT", "E_TEL", "E_FAX",
			"E_LDK", "E_EMAIL", "E_ANREDE2", "E_NNAME2", "E_VNAME2", "E_STR2",
			"E_PLZ2", "E_ORT2", "E_TEL2", "E_FAX2", "E_LDK2", "E_EMAIL2",
			"BETRIEB_NR", "BETRIEB_NR2", "BETRIEB_NR3", "BETRIEB_NR4",
			"BEMERK", "KENNUNG1", "KENNUNG2", "KENNUNG3", "KENNUNG4",
			"KENNUNG5", "KENNUNG6", "DATUM1", "DATUM2", "LML1", "BEW_W",
			"BEW_E", "PRIO1", "PRIO1_SNR", "PRIO1_KOR", "PRIO1_RANG",
			"PRIO1_ZU", "PRIO2", "PRIO2_SNR", "PRIO2_KOR", "PRIO2_RANG",
			"PRIO2_ZU", "PRIO3", "PRIO3_SNR", "PRIO3_KOR", "PRIO3_RANG",
			"PRIO3_ZU", "PRIO4", "PRIO4_SNR", "PRIO4_KOR", "PRIO4_RANG",
			"PRIO4_ZU", "PRIO5", "PRIO5_SNR", "PRIO5_KOR", "PRIO5_RANG",
			"PRIO5_ZU", "VN1", "VN2", "VN3", "VN4", "VN5", "VN6", "VN7", "VN8",
			"VN9", "VN10", "VN11", "VN12", "VN_S", "VN_S1", "VN_S2", "VN_S3",
			"VN_S4", "VN_S5", "ZUSAGE", "ZUSAGE_BG", "ZUSAGE_SNR", "AS",
			"SNR1", "SNR2", "ZU", "MARKE", "FEHLER", "IDENT", "TEL_HANDY" };

	public BbsPlanungExporter(Path file, List<Applicant> listOfStudents) {

		FileWriter fileWriter = null;

		CSVPrinter csvFilePrinter = null;

		// Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT
				.withRecordSeparator(NEW_LINE_SEPARATOR).withDelimiter(FIELD_DELIMITER);

		try {
			// initialize FileWriter object
			fileWriter = new FileWriter(file.toFile());

			// initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

			// create CSV file header
			csvFilePrinter.printRecord(FILE_HEADER);

			// write a new student object list to the CSV file
			int index = 1;
			for (Applicant student : listOfStudents) {
				List<String> studentDataRecord = new ArrayList<>();
				studentDataRecord.add("72679"); // Schulnummer
				studentDataRecord.add(""); // Klassenname
				studentDataRecord.add(String.valueOf(index)); // Lfd
				studentDataRecord.add(""); // Status
				studentDataRecord.add(String.valueOf(index)); // Schülernummer
				index++;
				studentDataRecord.add(student.getSurname()); // Nachname
				studentDataRecord.add(student.getFirstName()); // Vorname
				studentDataRecord.add(String.valueOf(student.getBirthday())); // Geburtstag
				studentDataRecord.add(student.getStreet()); // Strasse,
															// Hausnummer
				studentDataRecord.add(student.getZipCode()); // PLZ
				studentDataRecord.add(student.getCity()); // Ort
				studentDataRecord.add(student.getBirthplace()); // Telefon
				studentDataRecord.add(student.getBirthplace()); // Fax
				studentDataRecord.add("404"); // LDK
				studentDataRecord.add(""); // LDK_Z
				studentDataRecord.add("404"); // Landkreis
				// TODO get Landkreisnummer from zip code?
				studentDataRecord.add(student.getEmail()); // E-Mail-Adresse
				studentDataRecord.add(student.getSex()); // Geschlecht
				studentDataRecord.add(student.getReligion()); // Konfession
				studentDataRecord.add(student.getReligionText()); // Konfession-Text
				studentDataRecord.add("000"); // Staatszugehörigkeit
				studentDataRecord.add(""); // Familienstand
				studentDataRecord.add(""); // SFO
				studentDataRecord.add(""); // TAKURZ
				studentDataRecord.add(""); // KLST
				studentDataRecord.add(""); // ORG
				studentDataRecord.add(""); // DAUER
				studentDataRecord.add(""); // TAKLSTORG
				studentDataRecord.add(""); // SFOTEXT
				studentDataRecord.add(""); // TALANG
				studentDataRecord.add(""); // ORG_N
				studentDataRecord.add(""); // A
				studentDataRecord.add(""); // BG
				studentDataRecord.add(""); // BG_SFO
				studentDataRecord.add(""); // BG_BFELD
				studentDataRecord.add(""); // BG_FREI
				studentDataRecord.add(""); // BG_KLST
				studentDataRecord.add(""); // BG_ORG
				studentDataRecord.add(""); // BG_DAUER
				studentDataRecord.add(""); // P_FAKTOR
				studentDataRecord.add(""); // KO
				studentDataRecord.add(student.getStartOfTraining()); // EINTR_DAT
				studentDataRecord.add(student.getStartOfTraining()); // AUSB_BEGDAT
				studentDataRecord.add(String.valueOf(student
						.getDurationOfTraining() * 12)); // A_DAUER
				studentDataRecord.add(""); // A_ENDEDAT
				// TODO calculate end date for vocational training
				studentDataRecord.add(""); // ANRECH_BGJ
				studentDataRecord.add(""); // WIEDERHOL
				studentDataRecord.add(student.getDegree()); // ABSCHLUSS
				studentDataRecord.add(student.getLastSchool()); // HERKUNFT
				studentDataRecord.add(""); // HER_ZUSATZ
				studentDataRecord.add(""); // FH_Z
				studentDataRecord.add(""); // SCHULPFLICHT
				studentDataRecord.add(""); // N_DE
				studentDataRecord.add(""); // HER_B
				studentDataRecord.add(""); // BL_SOLL
				studentDataRecord.add(""); // LM_M
				studentDataRecord.add(""); // LM_Z
				studentDataRecord.add(""); // LM_DAT
				studentDataRecord.add(student.isRetraining() ? "J" : "N"); // UM
				studentDataRecord.add(""); // A_AMT
				studentDataRecord.add(""); // A_BEZIRK
				studentDataRecord.add(""); // BETRAG
				studentDataRecord.add(""); // BETRAG_G
				studentDataRecord.add(""); // BAFOEG
				studentDataRecord.add(""); // E_ANREDE
				studentDataRecord.add(""); // E_NNAME
				studentDataRecord.add(""); // E_VNAME
				studentDataRecord.add(""); // E_STR
				studentDataRecord.add(""); // E_PLZ
				studentDataRecord.add(""); // E_ORT
				studentDataRecord.add(""); // E_TEL
				studentDataRecord.add(""); // E_FAX
				studentDataRecord.add(""); // E_LDK
				studentDataRecord.add(""); // E_EMAIL
				studentDataRecord.add(""); // E_ANREDE2
				studentDataRecord.add(""); // E_NNAME2
				studentDataRecord.add(""); // E_VNAME2
				studentDataRecord.add(""); // E_STR2
				studentDataRecord.add(""); // E_PLZ2
				studentDataRecord.add(""); // E_ORT2
				studentDataRecord.add(""); // E_TEL2
				studentDataRecord.add(""); // E_FAX2
				studentDataRecord.add(""); // E_LDK2
				studentDataRecord.add(""); // E_EMAIL2
				studentDataRecord.add(""); // BETRIEB_NR
				studentDataRecord.add(""); // BETRIEB_NR2
				studentDataRecord.add(""); // BETRIEB_NR3
				studentDataRecord.add(""); // BETRIEB_NR4
				studentDataRecord.add(""); // BEMERK
				studentDataRecord.add(""); // KENNUNG1
				studentDataRecord.add(""); // KENNUNG2
				studentDataRecord.add(""); // KENNUNG3
				studentDataRecord.add(""); // KENNUNG4
				studentDataRecord.add(""); // KENNUNG5
				studentDataRecord.add(""); // KENNUNG6
				studentDataRecord.add(""); // DATUM1
				studentDataRecord.add(""); // DATUM2
				studentDataRecord.add(""); // LML1
				studentDataRecord.add(""); // BEW_W
				studentDataRecord.add(""); // BEW_E
				studentDataRecord.add(""); // PRIO1
				studentDataRecord.add(""); // PRIO1_SNR
				studentDataRecord.add(""); // PRIO1_KOR
				studentDataRecord.add(""); // PRIO1_RANG
				studentDataRecord.add(""); // PRIO1_ZU
				studentDataRecord.add(""); // PRIO2
				studentDataRecord.add(""); // PRIO2_SNR
				studentDataRecord.add(""); // PRIO2_KOR
				studentDataRecord.add(""); // PRIO2_RANG
				studentDataRecord.add(""); // PRIO2_ZU
				studentDataRecord.add(""); // PRIO3
				studentDataRecord.add(""); // PRIO3_SNR
				studentDataRecord.add(""); // PRIO3_KOR
				studentDataRecord.add(""); // PRIO3_RANG
				studentDataRecord.add(""); // PRIO3_ZU
				studentDataRecord.add(""); // PRIO4
				studentDataRecord.add(""); // PRIO4_SNR
				studentDataRecord.add(""); // PRIO4_KOR
				studentDataRecord.add(""); // PRIO4_RANG
				studentDataRecord.add(""); // PRIO4_ZU
				studentDataRecord.add(""); // PRIO5
				studentDataRecord.add(""); // PRIO5_SNR
				studentDataRecord.add(""); // PRIO5_KOR
				studentDataRecord.add(""); // PRIO5_RANG
				studentDataRecord.add(""); // PRIO5_ZU
				studentDataRecord.add(""); // VN1
				studentDataRecord.add(""); // VN2
				studentDataRecord.add(""); // VN3
				studentDataRecord.add(""); // VN4
				studentDataRecord.add(""); // VN5
				studentDataRecord.add(""); // VN6
				studentDataRecord.add(""); // VN7
				studentDataRecord.add(""); // VN8
				studentDataRecord.add(""); // VN9
				studentDataRecord.add(""); // VN10
				studentDataRecord.add(""); // VN11
				studentDataRecord.add(""); // VN12
				studentDataRecord.add(""); // VN_S
				studentDataRecord.add(""); // VN_S1
				studentDataRecord.add(""); // VN_S2
				studentDataRecord.add(""); // VN_S3
				studentDataRecord.add(""); // VN_S4
				studentDataRecord.add(""); // VN_S5
				studentDataRecord.add(""); // ZUSAGE
				studentDataRecord.add(""); // ZUSAGE_BG
				studentDataRecord.add(""); // ZUSAGE_SNR
				studentDataRecord.add(""); // AS
				studentDataRecord.add(""); // SNR1
				studentDataRecord.add(""); // SNR2
				studentDataRecord.add(""); // ZU
				studentDataRecord.add(""); // MARKE
				studentDataRecord.add(""); // FEHLER
				studentDataRecord.add(""); // IDENT
				studentDataRecord.add(""); // TEL_HANDY
				csvFilePrinter.printRecord(studentDataRecord);
			}

			logger.info("CSV file was created successfully.");

		} catch (Exception e) {
			logger.warn("Could not write to CSV file.");

		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				logger.error("Error while flushing/closing fileWriter/csvPrinter !!!");
			}
		}
	}
}
