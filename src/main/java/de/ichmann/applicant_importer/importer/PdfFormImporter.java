/**
 * 
 */
package de.ichmann.applicant_importer.importer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ichmann.applicant_importer.model.Applicant;
import de.ichmann.applicant_importer.model.DataField;
import de.ichmann.applicant_importer.model.Degree;
import de.ichmann.applicant_importer.model.School;
import de.ichmann.applicant_importer.model.Applicant.ApplicantBuilder;

/**
 * Imports and evaluates data from PDF forms and creates the Applicant objects
 * accordingly. Form fields inside the PDF file are defined by their fully
 * qualified name.
 * 
 * @author Christian Wichmann
 */
public final class PdfFormImporter {

	private final static Logger logger = LoggerFactory.getLogger(PdfFormImporter.class);

	private final List<Applicant> listOfStudents = new ArrayList<Applicant>();

	/**
	 * Contains for every data field in the PDF file the associated name of that
	 * field.
	 */
	private final Map<String, DataField> dataFieldNames = new HashMap<>();

	/*
	 * Current field names (2015-03-13):
	 * 
	 * Tx - Name - Name - Wichmann
	 * Tx - Vorname - Vorname - Christian
	 * Tx - Ausbildungsberuf - Ausbildungsberuf - Energieelektroniker
	 * Tx - Fachrichtung - Fachrichtung - Anlagentechnik
	 * Tx - Ausbildungsbeginn - Ausbildungsbeginn - 1.8.1997
	 * Tx - Tel - Tel - 05461/1320
	 * Tx - PLZ - PLZ - 49565
	 * Tx - Ort - Ort - Bramsche
	 * Tx - EMail - EMail - mail@gmx.de
	 * Tx - Namen der Erziehungsberechtigten - Namen der Erziehungsberechtigten - Willi
	 * Tx - Bemerkungen - Bemerkungen - Bemerkung
	 * Tx - EMailBetrieb - EMailBetrieb - willers@bahn.de
	 * Tx - BeginnSchulbesuch - BeginnSchulbesuch - 15.7.1987
	 * Tx - EndeSchulbesuch - EndeSchulbesuch - 1.6.1997
	 * Tx - JahreSchulbesuch - JahreSchulbesuch - 10
	 * Tx - ErlaeuterungBFS - ErlaeuterungBFS - null
	 * Tx - SonstigesSchulabschluss - SonstigesSchulabschluss - null
	 * Tx - OrtBetrieb - OrtBetrieb - Osnabrück
	 * Tx - Geburtsdatum - Geburtsdatum - 18.06.1981
	 * Tx - Geburtsort - Geburtsort - Ankum
	 * Tx - StraßeNrBetrieb - StraßeNrBetrieb - Schinkelstraße
	 * Tx - TelefonBetrieb - TelefonBetrieb - 054122222
	 * Tx - FaxBetrieb - FaxBetrieb - 0541333333
	 * Tx - NameAnsprechpartnerBetrieb - NameAnsprechpartnerBetrieb - Herr Willers
	 * Tx - PLZBetrieb - PLZBetrieb - 49080
	 * Tx - NameBetrieb - NameBetrieb - Deutsche Bahn  AG
	 * Tx - TelEltern - TelEltern - 054611320
	 * Tx - AnschriftEltern - AnschriftEltern - Uthof 3
	 * Btn - Formular drucken - Formular drucken - null
	 * Btn - Senden - Senden - null
	 * Ch - Konfession - Konfession - 3
	 * Ch - DauerAusbildung - DauerAusbildung - 3,5
	 * Btn - Umschueler - Umschueler - UmschuelerNein
	 * Btn - Geschlecht - Geschlecht - m
	 * Tx - StraßeNr - StraßeNr - Uthof 3
	 * Btn - SchulbesuchBisher - SchulbesuchBisher - RS
	 * Btn - Schulabschluss - Schulabschluss - EI
	 * Tx - SchulabschlussSonstigerErlaeuterung - SchulabschlussSonstigerErlaeuterung - null
	 */


	private void fillDataFieldNamesDictionary() {
		dataFieldNames.put("Vorname", DataField.FIRST_NAME);
		dataFieldNames.put("Name", DataField.LAST_NAME);
		dataFieldNames.put("Ausbildungsberuf", DataField.VOCATION);
		dataFieldNames.put("Fachrichtung", DataField.SPECIALIZATION);
		dataFieldNames.put("Ausbildungsbeginn", DataField.START_OF_TRAINING);
		dataFieldNames.put("Konfession", DataField.RELIGION);
		dataFieldNames.put("StraßeNr", DataField.ADDRESS);
		dataFieldNames.put("Tel", DataField.PHONE);
		dataFieldNames.put("Fax", DataField.FAX);
		dataFieldNames.put("PLZ", DataField.ZIP_CODE);
		dataFieldNames.put("Ort", DataField.CITY);
		dataFieldNames.put("EMail", DataField.EMAIL);
		dataFieldNames.put("Geburtsdatum", DataField.BIRTHDAY);
		dataFieldNames.put("Geburtsort", DataField.BIRTHPLACE);
		dataFieldNames.put("Namen der Erziehungsberechtigten", DataField.NAME_OF_LEGAL_GUARDIAN);
		dataFieldNames.put("TelEltern", DataField.PHONE_OF_LEGAL_GUARDIAN);
		dataFieldNames.put("AnschriftEltern", DataField.ADDRESS_OF_LEGAL_GUARDIAN);
		dataFieldNames.put("BeginnSchulbesuch", DataField.SCHOOL_ATTENDANCE_BEGIN);
		dataFieldNames.put("EndeSchulbesuch", DataField.SCHOOL_ATTENDANCE_END);
		dataFieldNames.put("JahreSchulbesuch", DataField.SCHOOL_ATTENDANCE_YEARS);
		dataFieldNames.put("StraßeNrBetrieb", DataField.COMPANY_ADDRESS);
		dataFieldNames.put("OrtBetrieb", DataField.COMPANY_CITY);
		dataFieldNames.put("EMailBetrieb", DataField.COMPANY_CONTACT_MAIL);
		dataFieldNames.put("TelefonBetrieb", DataField.COMPANY_TELEPHONE);
		dataFieldNames.put("FaxBetrieb", DataField.COMPANY_FAX);
		dataFieldNames.put("NameAnsprechpartnerBetrieb", DataField.COMPANY_CONTACT_PERSON);
		dataFieldNames.put("PLZBetrieb", DataField.COMPANY_ZIP_CODE);
		dataFieldNames.put("NameBetrieb", DataField.COMPANY_NAME);
		dataFieldNames.put("Bemerkungen", DataField.NOTES);
		dataFieldNames.put("SchulabschlussSonstigerErlaeuterung", DataField.DEGREE_ADDITIONAL_INFORMATION);
		dataFieldNames.put("ErlaeuterungBFS", DataField.SCHOOL_SPECIALIZATION);
		dataFieldNames.put("SonstigesSchulabschluss", DataField.SCHOOL_OTHER_TYPE);

		/*
		 * The following fields do not contain text strings or have to be
		 * evaluated otherwise:
		 * 
		 * DauerAusbildung: Has to be parsed as decimal number and multiplicated
		 * by 12 to get month.
		 * 
		 * ErlaeuterungBFS - SonstigesSchulabschluss: Stored as additional info
		 * in School Enum.
		 * 
		 * SchulabschlussSonstigerErlaeuterung: Stored as additional info in
		 * Degree Enum.
		 * 
		 * Formular drucken - Senden: Ignored because control buttons.
		 * 
		 * Umschueler (UmschuelerNein, UmschuelerJa), Geschlecht (m, w): Binary
		 * choices that have to be evaluated.
		 * 
		 * SchulbesuchBisher (RS), Schulabschluss(EI)
		 */
	}

	public PdfFormImporter(Path directory) {

		fillDataFieldNamesDictionary();

		// find all PDF files and parse them
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.pdf");
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
			for (Path path : directoryStream) {
				if (matcher.matches(path.getFileName())) {
					logger.info("Found PDF file: " + path);
					// parse every PDF file in given directory
					Applicant a = parsePDFFile(path);
					if (a != null) {
						listOfStudents.add(a);
					}
				}
			}
		} catch (IOException ex) {
			logger.warn("Could not open PDF file!");
		}
	}

	/**
	 * Parses a single PDF file defined by a given Path. If the PDF file
	 * contains no form fields and has no useable data, null is returned to the
	 * caller!
	 * 
	 * @param path
	 *            path describing the PDF file to be parsed
	 * @return applicants data or null, if file did not contain any form fields
	 */
	private Applicant parsePDFFile(Path path) {

		PDDocument pdfDocument = null;
		Applicant student = null;

		try {
			pdfDocument = PDDocument.load(path.toFile());
			if (pdfDocument != null) {
				PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
				PDAcroForm acroForm = docCatalog.getAcroForm();
				if (acroForm != null) {
					@SuppressWarnings("unchecked")
					List<PDField> formFields = Collections.checkedList(acroForm.getFields(), PDField.class);

					ApplicantBuilder builder = new ApplicantBuilder();
					for (PDField pdField : formFields) {
						// ignore useless fields in PDF file
						if ("Formular drucken".equals(pdField.getValue()) || "Senden".equals(pdField.getValue())) {
							continue;
						}
						// get all plain string elements and store them in
						// builder
						if (dataFieldNames.containsKey(pdField.getFullyQualifiedName())) {
							DataField df = dataFieldNames.get(pdField.getFullyQualifiedName());
							if (pdField.getValue() != null) {
								// TODO When " þÿ" please do not use! (BOM)
								if ("þÿ".equals(pdField.getValue())) {
									builder.setValue(df, "");
								} else {
									builder.setValue(df, pdField.getValue());
								}
							} else {
								builder.setValue(df, "");
							}
						}
						// get duration of training
						if ("DauerAusbildung".equals(pdField.getFullyQualifiedName())) {
							if (pdField.getValue() != null) {
								Double d = Double.valueOf(pdField.getValue().replace(",", ".")) * 12;
								Integer i = d.intValue();
								builder.setValue(DataField.DURATION_OF_TRAINING, i);
							} else {
								builder.setValue(DataField.DURATION_OF_TRAINING, 0);
							}
						}
						// get gender and whether applicant is in a retraining
						if ("Umschueler".equals(pdField.getFullyQualifiedName())) {
							boolean retraining = false;
							if ("UmschuelerNein".equals(pdField.getValue())) {
								retraining = false;
							} else if ("UmschuelerJa".equals(pdField.getValue())) {
								retraining = true;
							}
							builder.setValue(DataField.RETRAINING, retraining);
						}
						if ("Geschlecht".equals(pdField.getFullyQualifiedName())) {
							if ("m".equals(pdField.getValue())) {
								builder.setValue(DataField.GENDER, pdField.getValue());
							} else if ("w".equals(pdField.getValue())) {
								builder.setValue(DataField.GENDER, pdField.getValue());
							} else {
								logger.warn("Invalid gender!");
								assert false : "Invalid gender chosen!";
							}
						}
						// get attended school
						extractAttendedSchool(pdField, builder);
						// get last degree
						extractLastDegree(pdField, builder);

						logger.debug(pdField.getFieldType() + " - " + pdField.getFullyQualifiedName() + " - " + pdField.getPartialName()
								+ " - " + pdField.getValue());
					}
					student = builder.build();
					logger.info("Added student registration: " + student);
				}
			}
		} catch (IOException e) {
			logger.warn("Could not open PDF file.");
		} finally {
			try {
				if (pdfDocument != null) {
					pdfDocument.close();
				}
			} catch (IOException e) {
				logger.warn("Could not close PDF file.");
			}
		}

		return student;
	}

	/**
	 * Extracts information about the last accieved degree from a given PDF form
	 * field and stores the data inside a provided ApplicantBuilder instance.
	 * 
	 * @param pdField
	 *            PDF form field data
	 * @param builder
	 *            builder object for Applicant data
	 * @throws IOException
	 *             if reading of PDF form data failed
	 */
	private void extractLastDegree(PDField pdField, ApplicantBuilder builder) throws IOException {
		if ("Schulabschluss".equals(pdField.getFullyQualifiedName())) { 
			Degree degree = Degree.SONSTIGES;
			if (pdField.getValue() != null) {
				switch (pdField.getValue()) {
				case "HA":
					degree = Degree.SEKUNDAR_I_HAUPTSCHULE;
					break;
				case "SI":
					degree = Degree.SEKUNDAR_I_REALSCHULE;
					break;
				case "EI":
					degree = Degree.ERWEITERTER_SEKUNDAR_I;
					break;
				case "FH":
					degree = Degree.FACHHOCHSCHULREIFE;
					break;
				case "AH":
					degree = Degree.ALLGEMEINE_HOCHSCHULEREIFE;
					break;
				case "OA":
					degree = Degree.OHNE_ABSCHLUSS;
					break;
				case "XS":
					degree = Degree.SONSTIGES;
					break;
				default:
					logger.warn("Invalid degree type: " + pdField.getValue());
					assert false : "No attended school chosen!";
				}
			}
			builder.setValue(DataField.DEGREE, degree);
		}
	}

	/**
	 * Extracts information about the attended school from a given PDF form
	 * field and stores the data inside a provided ApplicantBuilder instance.
	 * 
	 * @param pdField
	 *            PDF form field data
	 * @param builder
	 *            builder object for Applicant data
	 * @throws IOException
	 *             if reading of PDF form data failed
	 */
	private void extractAttendedSchool(PDField pdField, ApplicantBuilder builder) throws IOException {
		if ("SchulbesuchBisher".equals(pdField.getFullyQualifiedName())) {
			School schoolType = School.SONSTIGES;
			if (pdField.getValue() != null) {
				switch (pdField.getValue()) {
				case "RS":
					schoolType = School.REALSCHULE;
					break;
				case "HS":
					schoolType = School.HAUPTSCHULE;
					break;
				case "GY":
					schoolType = School.GYMNASIUM;
					break;
				case "B1": // TODO What is with B2 and B7...
					schoolType = School.BERUFSFACHSCHULE;
					break;
				case "BS":
					schoolType = School.BERUFSSCHULE;
					break;
				case "IG":
					schoolType = School.GESAMTSCHULE;
					break;
				case "OS???":
					schoolType = School.OBERSCHULE;
					break;
				case "FO":
					schoolType = School.FACHOBERSCHULE;
					break;
				case "SA":
					schoolType = School.FOERDERSCHULE;
					break;
				case "Sonstiges???":
					schoolType = School.SONSTIGES;
					break;
				default:
					logger.warn("Invalid school type: " + pdField.getValue());
					assert false : "No attended school chosen!";
				}
			}
			builder.setValue(DataField.SCHOOL, schoolType);
		}
	}

	public final List<Applicant> getListOfStudents() {
		return listOfStudents;
	}

	public static void main(String[] args) {
		new PdfFormImporter(Paths.get("/home/christian/Desktop/"));
	}
}
