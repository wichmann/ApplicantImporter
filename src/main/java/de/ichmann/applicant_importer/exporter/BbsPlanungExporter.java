package de.ichmann.applicant_importer.exporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ichmann.applicant_importer.model.Applicant;
import de.ichmann.applicant_importer.model.DataField;
import de.ichmann.applicant_importer.model.Degree;
import de.ichmann.applicant_importer.model.School;

public class BbsPlanungExporter {

	private final static Logger logger = LoggerFactory.getLogger(BbsPlanungExporter.class);

	// set delimiter for end-of-line and between fields
	private static final String NEW_LINE_SEPARATOR = "\r\n";
	private static final char FIELD_DELIMITER = ';';

	// set all header for fields in CSV file
	private static final Object[] FILE_HEADER = { "SNR", "KL_NAME", "LFD", "STATUS", "NR_SCHÜLER", "NNAME", "VNAME", "GEBDAT", "GEBORT",
			"STR", "PLZ", "ORT", "TEL", "FAX", "LDK", "LDK_Z", "LANDKREIS", "EMAIL", "GESCHLECHT", "KONF", "KONF_TEXT", "STAAT",
			"FAMSTAND", "SFO", "TAKURZ", "KLST", "ORG", "DAUER", "TAKLSTORG", "SFOTEXT", "TALANG", "ORG_N", "A", "BG", "BG_SFO",
			"BG_BFELD", "BG_FREI", "BG_KLST", "BG_ORG", "BG_DAUER", "P_FAKTOR", "KO", "EINTR_DAT", "AUSB_BEGDAT", "A_DAUER", "A_ENDEDAT",
			"ANRECH_BGJ", "WIEDERHOL", "ABSCHLUSS", "HERKUNFT", "HER_ZUSATZ", "FH_Z", "SCHULPFLICHT", "N_DE", "HER_B", "BL_SOLL", "LM_M",
			"LM_Z", "LM_DAT", "UM", "A_AMT", "A_BEZIRK", "BETRAG", "BETRAG_G", "BAFOEG", "E_ANREDE", "E_NNAME", "E_VNAME", "E_STR",
			"E_PLZ", "E_ORT", "E_TEL", "E_FAX", "E_LDK", "E_EMAIL", "E_ANREDE2", "E_NNAME2", "E_VNAME2", "E_STR2", "E_PLZ2", "E_ORT2",
			"E_TEL2", "E_FAX2", "E_LDK2", "E_EMAIL2", "BETRIEB_NR", "BETRIEB_NR2", "BETRIEB_NR3", "BETRIEB_NR4", "BEMERK", "KENNUNG1",
			"KENNUNG2", "KENNUNG3", "KENNUNG4", "KENNUNG5", "KENNUNG6", "DATUM1", "DATUM2", "LML1", "BEW_W", "BEW_E", "PRIO1", "PRIO1_SNR",
			"PRIO1_KOR", "PRIO1_RANG", "PRIO1_ZU", "PRIO2", "PRIO2_SNR", "PRIO2_KOR", "PRIO2_RANG", "PRIO2_ZU", "PRIO3", "PRIO3_SNR",
			"PRIO3_KOR", "PRIO3_RANG", "PRIO3_ZU", "PRIO4", "PRIO4_SNR", "PRIO4_KOR", "PRIO4_RANG", "PRIO4_ZU", "PRIO5", "PRIO5_SNR",
			"PRIO5_KOR", "PRIO5_RANG", "PRIO5_ZU", "VN1", "VN2", "VN3", "VN4", "VN5", "VN6", "VN7", "VN8", "VN9", "VN10", "VN11", "VN12",
			"VN_S", "VN_S1", "VN_S2", "VN_S3", "VN_S4", "VN_S5", "ZUSAGE", "ZUSAGE_BG", "ZUSAGE_SNR", "AS", "SNR1", "SNR2", "ZU", "MARKE",
			"FEHLER", "IDENT", "TEL_HANDY" };

	private int numberExportedApplicants = 0;

	public BbsPlanungExporter(Path file, List<Applicant> listOfApplicants, boolean exportInvalidApplicants) {

		OutputStreamWriter osw = null;
		CSVPrinter csvFilePrinter = null;

		// create the CSVFormat object with correct record separator and delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR).withDelimiter(FIELD_DELIMITER);

		try {
			// open file to write to
			FileOutputStream fos = new FileOutputStream(file.toFile());
			osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-15").newEncoder());

			// initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(osw, csvFileFormat);

			// create CSV file header
			csvFilePrinter.printRecord(FILE_HEADER);

			// write a new student object list to the CSV file
			int index = 1;
			for (Applicant applicant : listOfApplicants) {
				if (exportInvalidApplicants || applicant.checkPlausibility()) {
					List<String> applicantDataRecord = new ArrayList<>();
					applicantDataRecord.add("72679"); // Schulnummer
					applicantDataRecord.add(""); // Klassenname
					applicantDataRecord.add(String.valueOf(index)); // Lfd
					applicantDataRecord.add(""); // Status
					applicantDataRecord.add(String.valueOf(index)); // Schülernummer
					index++;
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.LAST_NAME))); // Nachname
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.FIRST_NAME))); // Vorname
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.BIRTHDAY))); // Geburtstag
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.BIRTHPLACE))); // Geburtsort
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.ADDRESS))); // Strasse,
																									// Hausnummer
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.ZIP_CODE))); // PLZ
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.CITY))); // Ort
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.PHONE))); // Telefon
					applicantDataRecord.add(""); // Fax
					applicantDataRecord.add("404"); // LDK
					applicantDataRecord.add(""); // LDK_Z
					applicantDataRecord.add("404"); // Landkreis
					// TODO get Landkreisnummer from zip code?
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.EMAIL))); // E-Mail-Adresse
					String gender = "m".equals(applicant.getValue(DataField.GENDER)) ? "1" : "2";
					applicantDataRecord.add(gender); // Geschlecht
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.RELIGION))); // Konfession
					applicantDataRecord.add(""); // Konfession-Text
					applicantDataRecord.add("000"); // Staatszugehörigkeit
					applicantDataRecord.add(""); // Familienstand
					applicantDataRecord.add(""); // SFO
					applicantDataRecord.add(""); // TAKURZ
					applicantDataRecord.add(""); // KLST
					applicantDataRecord.add(""); // ORG
					applicantDataRecord.add(""); // DAUER
					applicantDataRecord.add(""); // TAKLSTORG
					applicantDataRecord.add(""); // SFOTEXT
					applicantDataRecord.add(""); // TALANG
					applicantDataRecord.add(""); // ORG_N
					applicantDataRecord.add(""); // A
					applicantDataRecord.add(""); // BG
					applicantDataRecord.add(""); // BG_SFO
					applicantDataRecord.add(""); // BG_BFELD
					applicantDataRecord.add(""); // BG_FREI
					applicantDataRecord.add(""); // BG_KLST
					applicantDataRecord.add(""); // BG_ORG
					applicantDataRecord.add(""); // BG_DAUER
					applicantDataRecord.add(""); // P_FAKTOR
					applicantDataRecord.add(""); // KO
					String sot = DataField.START_OF_TRAINING.getFrom(applicant);
					Integer dot = DataField.DURATION_OF_TRAINING.getFrom(applicant);
					applicantDataRecord.add(sot); // EINTR_DAT
					applicantDataRecord.add(sot); // AUSB_BEGDAT
					applicantDataRecord.add(dot.toString()); // A_DAUER
					applicantDataRecord.add(""); // A_ENDEDAT
					// TODO calculate end date for vocational training
					applicantDataRecord.add(""); // ANRECH_BGJ
					applicantDataRecord.add(""); // WIEDERHOL
					Degree d = DataField.DEGREE.getFrom(applicant);
					applicantDataRecord.add(String.valueOf(d.getId())); // ABSCHLUSS
					School s = DataField.SCHOOL.getFrom(applicant);
					applicantDataRecord.add(String.valueOf(s.getId())); // HERKUNFT
					applicantDataRecord.add(""); // HER_ZUSATZ
					applicantDataRecord.add(""); // FH_Z
					applicantDataRecord.add(""); // SCHULPFLICHT
					applicantDataRecord.add(""); // N_DE
					applicantDataRecord.add(""); // HER_B
					applicantDataRecord.add(""); // BL_SOLL
					applicantDataRecord.add(""); // LM_M
					applicantDataRecord.add(""); // LM_Z
					applicantDataRecord.add(""); // LM_DAT
					boolean r = DataField.RETRAINING.getFrom(applicant);
					applicantDataRecord.add(r ? "J" : "N"); // UM
					applicantDataRecord.add(""); // A_AMT
					applicantDataRecord.add(""); // A_BEZIRK
					applicantDataRecord.add(""); // BETRAG
					applicantDataRecord.add(""); // BETRAG_G
					applicantDataRecord.add(""); // BAFOEG
					applicantDataRecord.add(""); // E_ANREDE
					String nlg = DataField.NAME_OF_LEGAL_GUARDIAN.getFrom(applicant);
					String alg = DataField.ADDRESS_OF_LEGAL_GUARDIAN.getFrom(applicant);
					String plg = DataField.PHONE_OF_LEGAL_GUARDIAN.getFrom(applicant);
					applicantDataRecord.add(nlg); // E_NNAME
					applicantDataRecord.add(""); // E_VNAME
					applicantDataRecord.add(alg); // E_STR
					applicantDataRecord.add(""); // E_PLZ
					applicantDataRecord.add(""); // E_ORT
					applicantDataRecord.add(plg); // E_TEL
					applicantDataRecord.add(""); // E_FAX
					applicantDataRecord.add(""); // E_LDK
					applicantDataRecord.add(""); // E_EMAIL
					applicantDataRecord.add(""); // E_ANREDE2
					applicantDataRecord.add(""); // E_NNAME2
					applicantDataRecord.add(""); // E_VNAME2
					applicantDataRecord.add(""); // E_STR2
					applicantDataRecord.add(""); // E_PLZ2
					applicantDataRecord.add(""); // E_ORT2
					applicantDataRecord.add(""); // E_TEL2
					applicantDataRecord.add(""); // E_FAX2
					applicantDataRecord.add(""); // E_LDK2
					applicantDataRecord.add(""); // E_EMAIL2
					// TODO Check which company information should be in which field!
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.COMPANY_NAME))); // BETRIEB_NR
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.COMPANY_CONTACT_PERSON))); // BETRIEB_NR2
					applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.COMPANY_ADDRESS))); // BETRIEB_NR3
					String sc = String.valueOf(applicant.getValue(DataField.COMPANY_ZIP_CODE)) + " "
							+ String.valueOf(applicant.getValue(DataField.COMPANY_CITY));
					applicantDataRecord.add(sc); // BETRIEB_NR4
					applicantDataRecord.add(""); // BEMERK
					applicantDataRecord.add(""); // KENNUNG1
					applicantDataRecord.add(""); // KENNUNG2
					applicantDataRecord.add(""); // KENNUNG3
					applicantDataRecord.add(""); // KENNUNG4
					applicantDataRecord.add(""); // KENNUNG5
					applicantDataRecord.add(""); // KENNUNG6
					applicantDataRecord.add(""); // DATUM1
					applicantDataRecord.add(""); // DATUM2
					applicantDataRecord.add(""); // LML1
					applicantDataRecord.add(""); // BEW_W
					applicantDataRecord.add(""); // BEW_E
					applicantDataRecord.add(""); // PRIO1
					applicantDataRecord.add(""); // PRIO1_SNR
					applicantDataRecord.add(""); // PRIO1_KOR
					applicantDataRecord.add(""); // PRIO1_RANG
					applicantDataRecord.add(""); // PRIO1_ZU
					applicantDataRecord.add(""); // PRIO2
					applicantDataRecord.add(""); // PRIO2_SNR
					applicantDataRecord.add(""); // PRIO2_KOR
					applicantDataRecord.add(""); // PRIO2_RANG
					applicantDataRecord.add(""); // PRIO2_ZU
					applicantDataRecord.add(""); // PRIO3
					applicantDataRecord.add(""); // PRIO3_SNR
					applicantDataRecord.add(""); // PRIO3_KOR
					applicantDataRecord.add(""); // PRIO3_RANG
					applicantDataRecord.add(""); // PRIO3_ZU
					applicantDataRecord.add(""); // PRIO4
					applicantDataRecord.add(""); // PRIO4_SNR
					applicantDataRecord.add(""); // PRIO4_KOR
					applicantDataRecord.add(""); // PRIO4_RANG
					applicantDataRecord.add(""); // PRIO4_ZU
					applicantDataRecord.add(""); // PRIO5
					applicantDataRecord.add(""); // PRIO5_SNR
					applicantDataRecord.add(""); // PRIO5_KOR
					applicantDataRecord.add(""); // PRIO5_RANG
					applicantDataRecord.add(""); // PRIO5_ZU
					applicantDataRecord.add(""); // VN1
					applicantDataRecord.add(""); // VN2
					applicantDataRecord.add(""); // VN3
					applicantDataRecord.add(""); // VN4
					applicantDataRecord.add(""); // VN5
					applicantDataRecord.add(""); // VN6
					applicantDataRecord.add(""); // VN7
					applicantDataRecord.add(""); // VN8
					applicantDataRecord.add(""); // VN9
					applicantDataRecord.add(""); // VN10
					applicantDataRecord.add(""); // VN11
					applicantDataRecord.add(""); // VN12
					applicantDataRecord.add(""); // VN_S
					applicantDataRecord.add(""); // VN_S1
					applicantDataRecord.add(""); // VN_S2
					applicantDataRecord.add(""); // VN_S3
					applicantDataRecord.add(""); // VN_S4
					applicantDataRecord.add(""); // VN_S5
					applicantDataRecord.add(""); // ZUSAGE
					applicantDataRecord.add(""); // ZUSAGE_BG
					applicantDataRecord.add(""); // ZUSAGE_SNR
					applicantDataRecord.add(""); // AS
					applicantDataRecord.add(""); // SNR1
					applicantDataRecord.add(""); // SNR2
					applicantDataRecord.add(""); // ZU
					applicantDataRecord.add(""); // MARKE
					applicantDataRecord.add(""); // FEHLER
					applicantDataRecord.add(""); // IDENT
					applicantDataRecord.add(""); // TEL_HANDY
					csvFilePrinter.printRecord(applicantDataRecord);
				}
			}

			numberExportedApplicants = index - 1;
			logger.info(String.format("%d applicants sucessfully exported to CSV file.", index - 1));

		} catch (IOException e) {
			logger.warn("Could not write to CSV file.");

		} finally {
			try {
				osw.flush();
				osw.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				logger.error("Error while flushing/closing fileWriter/csvPrinter !!!");
			}
		}
	}

	/**
	 * Returns the number of actually exported applicants. All applicants that have invalid data are only counted if the parameter
	 * <code>exportInvalidApplicants</code> is set.
	 * 
	 * @return number of actually exported applicants
	 */
	public int getNumberExportedApplicants() {
		return numberExportedApplicants;
	}
}
