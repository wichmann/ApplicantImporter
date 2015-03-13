/**
 * 
 */
package studentImporter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import studentImporter.Applicant.StudentBuilder;

/**
 * Imports and evaluates data from PDF forms and creates the Applicant objects
 * accordingly. Form fields inside the PDF file are defined by their fully
 * qualified name.
 * 
 * @author Christian Wichmann
 */
public final class PdfFormImporter {

	private final static Logger logger = LoggerFactory
			.getLogger(PdfFormImporter.class);

	private final List<Applicant> listOfStudents = new ArrayList<Applicant>();

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
	
	public PdfFormImporter(Path directory) {

		// find all PDF files and parse them
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher(
				"glob:*.pdf");
		try (DirectoryStream<Path> directoryStream = Files
				.newDirectoryStream(directory)) {
			for (Path path : directoryStream) {
				if (matcher.matches(path.getFileName())) {
					logger.info("Found PDF file: " + path);
					// parse every PDF file in given directory
					listOfStudents.add(parsePDFFile(path));
				}
			}
		} catch (IOException ex) {
			logger.warn("Could not open PDF file!");
		}
	}

	private Applicant parsePDFFile(Path path) {

		PDDocument pdfDocument = null;
		Applicant student = null;

		try {
			pdfDocument = PDDocument.load(path.toFile());

			PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			@SuppressWarnings("unchecked")
			List<PDField> formFields = Collections.checkedList(
					acroForm.getFields(), PDField.class);

			StudentBuilder builder = new StudentBuilder();
			for (PDField pdField : formFields) {
				if (pdField.getValue() == null) {
					continue;
				}
				switch (pdField.getFullyQualifiedName()) {
				case "Formular drucken":
				case "Senden":
					// skip fields that are control buttons inside the PDF
					// document
					break;
				case "Name":
					builder.surname(pdField.getValue());
					break;
				case "Vorname":
					builder.firstName(pdField.getValue());
					break;
				case "Ausbildungsberuf":
					builder.vocation(pdField.getValue());
					break;
				case "Fachrichtung":
					builder.specialization(pdField.getValue());
					break;
				case "ja": // case "nein" is not explicitly tested
					if ("On".equals(pdField.getValue())) {
						builder.retraining(true);
					}
					break;
				case "Ausbildungsbeginn":
					builder.startOfTraining(pdField.getValue());
					break;
				case "Ausbildungsdauer":
					builder.DurationOfTraining(Double.parseDouble(pdField
							.getValue().replace(",", ".")));
					break;
				}

				logger.debug(pdField.getFieldType() + " - "
						+ pdField.getFullyQualifiedName() + " - "
						+ pdField.getPartialName() + " - " + pdField.getValue());
			}
			student = builder.build();
			logger.info("Added student registration: " + student);

			pdfDocument.close();

		} catch (IOException e) {
			logger.warn("Could not open PDF file.");
		}

		return student;
	}

	public final List<Applicant> getListOfStudents() {
		return listOfStudents;
	}

	public static void main(String[] args) {
		new PdfFormImporter(Paths.get("/home/christian/Desktop/"));
	}
}
