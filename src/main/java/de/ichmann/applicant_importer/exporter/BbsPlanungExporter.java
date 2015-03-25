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
import de.ichmann.applicant_importer.model.DateHelper;
import de.ichmann.applicant_importer.model.Degree;
import de.ichmann.applicant_importer.model.Religion;
import de.ichmann.applicant_importer.model.School;

/**
 * Exports to the file format read by BBS-Planung Bewerber-Import. Each instance can only be used
 * once to export one set of applicants data to one file.
 *
 * @author Christian Wichmann
 */
public class BbsPlanungExporter {

    private static final Logger logger = LoggerFactory.getLogger(BbsPlanungExporter.class);

    // set delimiter for end-of-line and between fields
    private static final String NEW_LINE_SEPARATOR = "\r\n";
    private static final char FIELD_DELIMITER = ';';

    // set all header for fields in CSV file
    private static final Object[] FILE_HEADER = {"SNR", "KL_NAME", "LFD", "STATUS", "NR_SCHÜLER",
            "NNAME", "VNAME", "GEBDAT", "GEBORT", "STR", "PLZ", "ORT", "TEL", "FAX", "LDK",
            "LDK_Z", "LANDKREIS", "EMAIL", "GESCHLECHT", "KONF", "KONF_TEXT", "STAAT", "FAMSTAND",
            "SFO", "TAKURZ", "KLST", "ORG", "DAUER", "TAKLSTORG", "SFOTEXT", "TALANG", "ORG_N",
            "A", "BG", "BG_SFO", "BG_BFELD", "BG_FREI", "BG_KLST", "BG_ORG", "BG_DAUER",
            "P_FAKTOR", "KO", "EINTR_DAT", "AUSB_BEGDAT", "A_DAUER", "A_ENDEDAT", "ANRECH_BGJ",
            "WIEDERHOL", "ABSCHLUSS", "HERKUNFT", "HER_ZUSATZ", "FH_Z", "SCHULPFLICHT", "N_DE",
            "HER_B", "BL_SOLL", "LM_M", "LM_Z", "LM_DAT", "UM", "A_AMT", "A_BEZIRK", "BETRAG",
            "BETRAG_G", "BAFOEG", "E_ANREDE", "E_NNAME", "E_VNAME", "E_STR", "E_PLZ", "E_ORT",
            "E_TEL", "E_FAX", "E_LDK", "E_EMAIL", "E_ANREDE2", "E_NNAME2", "E_VNAME2", "E_STR2",
            "E_PLZ2", "E_ORT2", "E_TEL2", "E_FAX2", "E_LDK2", "E_EMAIL2", "BETRIEB_NR",
            "BETRIEB_NR2", "BETRIEB_NR3", "BETRIEB_NR4", "BEMERK", "KENNUNG1", "KENNUNG2",
            "KENNUNG3", "KENNUNG4", "KENNUNG5", "KENNUNG6", "DATUM1", "DATUM2", "LML1", "BEW_W",
            "BEW_E", "PRIO1", "PRIO1_SNR", "PRIO1_KOR", "PRIO1_RANG", "PRIO1_ZU", "PRIO2",
            "PRIO2_SNR", "PRIO2_KOR", "PRIO2_RANG", "PRIO2_ZU", "PRIO3", "PRIO3_SNR", "PRIO3_KOR",
            "PRIO3_RANG", "PRIO3_ZU", "PRIO4", "PRIO4_SNR", "PRIO4_KOR", "PRIO4_RANG", "PRIO4_ZU",
            "PRIO5", "PRIO5_SNR", "PRIO5_KOR", "PRIO5_RANG", "PRIO5_ZU", "VN1", "VN2", "VN3",
            "VN4", "VN5", "VN6", "VN7", "VN8", "VN9", "VN10", "VN11", "VN12", "VN_S", "VN_S1",
            "VN_S2", "VN_S3", "VN_S4", "VN_S5", "ZUSAGE", "ZUSAGE_BG", "ZUSAGE_SNR", "AS", "SNR1",
            "SNR2", "ZU", "MARKE", "FEHLER", "IDENT", "TEL_HANDY"};

    private int numberExportedApplicants = 0;

    /**
     * Instantiates a new exporter object.
     *
     * @param file
     *            file to which export the applicants data
     * @param listOfApplicants
     *            list containing all applicants
     * @param exportInvalidApplicants
     *            whether to export applicants with invalid data fields
     */
    public BbsPlanungExporter(final Path file, final List<Applicant> listOfApplicants,
            final boolean exportInvalidApplicants) {

        OutputStreamWriter osw = null;
        CSVPrinter csvFilePrinter = null;

        // create the CSVFormat object with correct record separator and delimiter
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR)
                .withDelimiter(FIELD_DELIMITER);

        try {
            // open file to write to (in Latin encoding because BBS-Planung runs under MS Windows)
            FileOutputStream fos = new FileOutputStream(file.toFile());
            osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-15").newEncoder());

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

                    filloutApplicantData(applicant, applicantDataRecord);

                    filloutSchoolData(applicant, applicantDataRecord);

                    filloutGuardian(applicant, applicantDataRecord);

                    filloutCompany(applicant, applicantDataRecord);

                    filloutMiscellaneous(applicant, applicantDataRecord);

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
     * Fill out the applicants data like her name, address and so on.
     *
     * @param applicant
     *            applicants data to be exported
     * @param applicantDataRecord
     *            list of all data to be exported in the correct order
     */
    private void filloutApplicantData(final Applicant applicant,
            final List<String> applicantDataRecord) {
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

        // parse zip code and corresponding county ID
        String countyID;
        try {
            String zipCode = DataField.ZIP_CODE.getFrom(applicant);
            if (zipCode != null) {
                Integer zipCodeAsNumber = Integer.valueOf(zipCode.trim());
                countyID = Zip2CountyConverter.getInstance().convertZipCode(zipCodeAsNumber);
            } else {
                countyID = "";
            }
        } catch (NumberFormatException e) {
            logger.warn("Could not parse zip code of applicant " + applicant + " while exporting!");
        } finally {
            countyID = "";
        }
        applicantDataRecord.add(countyID); // LDK
        applicantDataRecord.add(""); // LDK_Z
        applicantDataRecord.add(countyID); // Landkreis
        applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.EMAIL))); // E-Mail-Adresse

        String gender = "m".equals(applicant.getValue(DataField.GENDER)) ? "1" : "2";
        applicantDataRecord.add(gender); // Geschlecht
        Religion r = DataField.RELIGION.getFrom(applicant);
        if (r != null) {
            applicantDataRecord.add(String.valueOf(r.getValue())); // Konfession
        } else {
            applicantDataRecord.add(String.valueOf(Religion.OHNE_ANGABE.getValue())); // Konfession
        }
        applicantDataRecord.add(""); // Konfession-Text
        Integer i = DataField.NATIONALITY.getFrom(applicant);
        if (i != null) {
            applicantDataRecord.add(String.format("%03d", i)); // Staatszugehörigkeit
        } else {
            applicantDataRecord.add("000"); // Staatszugehörigkeit
        }
        applicantDataRecord.add(""); // Familienstand
    }

    /**
     * Fill out the school data like the last visited school and the highest achieved degree.
     *
     * @param applicant
     *            applicants data to be exported
     * @param applicantDataRecord
     *            list of all data to be exported in the correct order
     */
    private void filloutSchoolData(final Applicant applicant, final List<String> applicantDataRecord) {
        String vocationName = applicant.getValue(DataField.VOCATION) + " "
                + applicant.getValue(DataField.SPECIALIZATION);
        String vocationID = VocationConverter.getInstance().convertVocation(vocationName);
        applicantDataRecord.add("BS"); // SFO
        if (vocationID == null || "".equals(vocationID)) {
            applicantDataRecord.add(""); // TAKURZ
        } else {
            applicantDataRecord.add(vocationID); // TAKURZ
        }
        applicantDataRecord.add("1"); // KLST
        applicantDataRecord.add("A"); // ORG
        applicantDataRecord.add("0"); // DAUER
        applicantDataRecord.add(""); // TAKLSTORG
        applicantDataRecord.add(""); // SFOTEXT
        applicantDataRecord.add(""); // TALANG
        applicantDataRecord.add(""); // ORG_N
        applicantDataRecord.add(""); // A
        applicantDataRecord.add(""); // BG
        applicantDataRecord.add("BS"); // BG_SFO: Berufsschule
        if (vocationID == null || "".equals(vocationID)) {
            applicantDataRecord.add(""); // BG_BFELD: Elektro (E)
            applicantDataRecord.add(""); // BG_FREI: Fachinformatiker
        } else {
            applicantDataRecord.add(vocationID.substring(0, 1)); // BG_BFELD
            applicantDataRecord.add(vocationID.substring(1, 3)); // BG_FREI
        }
        applicantDataRecord.add("1"); // BG_KLST: Klassenstufe
        applicantDataRecord.add("A"); // BG_ORG: Organisationsart übliche Klasse/Teilzeit ohne
                                      // Blockunterricht
        applicantDataRecord.add("0"); // BG_DAUER: Gesamtes Schuljahr
        applicantDataRecord.add(""); // P_FAKTOR
        applicantDataRecord.add(""); // KO
        String sot = DataField.START_OF_TRAINING.getFrom(applicant);
        Integer dot = DataField.DURATION_OF_TRAINING.getFrom(applicant);
        String eot = DateHelper.getInstance().getEndDateOfTraining(applicant);
        applicantDataRecord.add(""); // EINTR_DAT: Will be added later by the secretaries.
        applicantDataRecord.add(sot); // AUSB_BEGDAT
        if (dot != null) {
            applicantDataRecord.add(dot.toString()); // A_DAUER
        } else {
            applicantDataRecord.add(""); // A_DAUER
        }
        applicantDataRecord.add(eot); // A_ENDEDAT
        applicantDataRecord.add(""); // ANRECH_BGJ
        applicantDataRecord.add(""); // WIEDERHOL
        Degree d = DataField.DEGREE.getFrom(applicant);
        if (d != null) {
            applicantDataRecord.add(String.valueOf(d.getId())); // ABSCHLUSS
        } else {
            applicantDataRecord.add(String.valueOf(Degree.SONSTIGER_ABSCHLUSS.getId())); // ABSCHLUSS
        }
        School s = DataField.SCHOOL.getFrom(applicant);
        if (s != null) {
            applicantDataRecord.add(String.valueOf(s.getId())); // HERKUNFT
        } else {
            applicantDataRecord.add(String.valueOf(School.SONSTIGES.getId())); // HERKUNFT
        }
        applicantDataRecord.add(""); // HER_ZUSATZ
        applicantDataRecord.add(""); // FH_Z
        applicantDataRecord.add(""); // SCHULPFLICHT
        applicantDataRecord.add(""); // N_DE
        applicantDataRecord.add(""); // HER_B
        applicantDataRecord.add(""); // BL_SOLL
        applicantDataRecord.add(""); // LM_M
        applicantDataRecord.add(""); // LM_Z
        applicantDataRecord.add(""); // LM_DAT
        Boolean r = DataField.RETRAINING.getFrom(applicant);
        r = (r == null) ? false : r;
        applicantDataRecord.add(r ? "J" : "N"); // UM
        applicantDataRecord.add(""); // A_AMT
        applicantDataRecord.add(""); // A_BEZIRK
        applicantDataRecord.add(""); // BETRAG
        applicantDataRecord.add(""); // BETRAG_G
        applicantDataRecord.add(""); // BAFOEG
    }

    /**
     * Fill out the data of the legal guardians of the applicant.
     *
     * @param applicant
     *            applicants data to be exported
     * @param applicantDataRecord
     *            list of all data to be exported in the correct order
     */
    private void filloutGuardian(final Applicant applicant, final List<String> applicantDataRecord) {
        applicantDataRecord.add(""); // E_ANREDE
        String nlg = DataField.NAME_OF_LEGAL_GUARDIAN.getFrom(applicant);
        applicantDataRecord.add(nlg); // E_NNAME
        applicantDataRecord.add(""); // E_VNAME
        DateHelper dh = DateHelper.getInstance();
        if (dh.isOlderThan18(applicant)) {
            String alg = DataField.ADDRESS_OF_LEGAL_GUARDIAN.getFrom(applicant);
            String plg = DataField.PHONE_OF_LEGAL_GUARDIAN.getFrom(applicant);
            applicantDataRecord.add(alg); // E_STR
            applicantDataRecord.add(""); // E_PLZ
            applicantDataRecord.add(""); // E_ORT
            applicantDataRecord.add(plg); // E_TEL
            applicantDataRecord.add(""); // E_FAX
        } else {
            // insert applicants data for guardians when applicant is not yet of age (older than 18)
            applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.ADDRESS))); // E_STR
            applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.ZIP_CODE))); // E_PLZ
            applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.CITY))); // E_ORT
            applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.PHONE))); // E_TEL
            applicantDataRecord.add(""); // E_FAX
        }
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
    }

    /**
     * Fill out the data of the company at which the vocational training of the applicant takes
     * place.
     *
     * @param applicant
     *            applicants data to be exported
     * @param applicantDataRecord
     *            list of all data to be exported in the correct order
     */
    private void filloutCompany(final Applicant applicant, final List<String> applicantDataRecord) {
        // TODO Check which company information should be in which field!
        applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.COMPANY_NAME))); // BETRIEB_NR
        applicantDataRecord
                .add(String.valueOf(applicant.getValue(DataField.COMPANY_CONTACT_PERSON))); // BETRIEB_NR2
        applicantDataRecord.add(String.valueOf(applicant.getValue(DataField.COMPANY_ADDRESS))); // BETRIEB_NR3
        String sc = String.valueOf(applicant.getValue(DataField.COMPANY_ZIP_CODE)) + " "
                + String.valueOf(applicant.getValue(DataField.COMPANY_CITY));
        applicantDataRecord.add(sc); // BETRIEB_NR4
    }

    /**
     * Fill out all other data fields, e.g. the comment block.
     *
     * @param applicant
     *            applicants data to be exported
     * @param applicantDataRecord
     *            list of all data to be exported in the correct order
     */
    private void filloutMiscellaneous(final Applicant applicant,
            final List<String> applicantDataRecord) {
        applicantDataRecord.add(buildComment(applicant)); // BEMERK
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
    }

    /**
     * Builds a comment string for a given applicant. It contains the vocation, company and invalid
     * data fields.
     *
     * @param applicant
     *            applicant for which to build a comment string
     * @return string containing the comment
     */
    private String buildComment(final Applicant applicant) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Beruf: ");
        builder.append(String.valueOf(applicant.getValue(DataField.VOCATION)));
        builder.append(" ");
        builder.append(String.valueOf(applicant.getValue(DataField.SPECIALIZATION)));
        builder.append("; ");
        builder.append("Betrieb: ");
        builder.append(String.valueOf(applicant.getValue(DataField.COMPANY_NAME)));
        builder.append(", ");
        builder.append(String.valueOf(applicant.getValue(DataField.COMPANY_ADDRESS)));
        builder.append(" ");
        builder.append(String.valueOf(applicant.getValue(DataField.COMPANY_ZIP_CODE)));
        builder.append(" ");
        builder.append(String.valueOf(applicant.getValue(DataField.COMPANY_CITY)));
        builder.append("; ");
        builder.append("Ansprechpartner: ");
        builder.append(String.valueOf(applicant.getValue(DataField.COMPANY_CONTACT_PERSON)));
        builder.append(", ");
        builder.append(String.valueOf(applicant.getValue(DataField.COMPANY_CONTACT_MAIL)));
        builder.append("; ");
        builder.append("Datei: ");
        builder.append(applicant.getFileName());
        logger.info("Builded comment for applicant: " + builder.toString());
        return builder.toString();
    }

    /**
     * Returns the number of actually exported applicants. All applicants that have invalid data are
     * only counted if the parameter <code>exportInvalidApplicants</code> is set.
     *
     * @return number of actually exported applicants
     */
    public final int getNumberExportedApplicants() {
        return numberExportedApplicants;
    }
}
