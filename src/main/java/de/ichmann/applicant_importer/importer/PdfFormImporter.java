/**
 *
 */
package de.ichmann.applicant_importer.importer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ichmann.applicant_importer.exporter.NationalityConverter;
import de.ichmann.applicant_importer.model.Applicant;
import de.ichmann.applicant_importer.model.Applicant.ApplicantBuilder;
import de.ichmann.applicant_importer.model.DataField;
import de.ichmann.applicant_importer.model.Degree;
import de.ichmann.applicant_importer.model.Religion;
import de.ichmann.applicant_importer.model.School;

/**
 * Imports and evaluates data from PDF forms and creates the Applicant objects accordingly. Form
 * fields inside the PDF file are defined by their fully qualified name. Only one parameter has to
 * be given: the directory in which to search for PDF files.
 * <p>
 * All string fields are read and stored in a new Applicant instance for each PDF file with form
 * fields. Data fields that have been declared in this importer but are not present in the PDF file
 * (because e.g. older PDF file version) will not set to a default value. If the value is later used
 * by calling of Applicant.getValue() a <code>null</code> is returned!
 * <p>
 * Besides the string fields some special fields are evaluated sperately: boolean fields
 * (Umschueler, Geschlecht), fields containing a duration (DauerAusbildung), and enumerated values
 * (SchulbesuchBisher, Schulabschluss).
 * <p>
 * After using the importer the disposeImporter() method has to be called to explicitly shutdown all
 * thread used to import the data.
 *
 * @author Christian Wichmann
 */
public final class PdfFormImporter {

    private static final Logger logger = LoggerFactory.getLogger(PdfFormImporter.class);

    private final List<Applicant> listOfStudents = new ArrayList<Applicant>();
    private final List<String> listOfInvalidPdfFiles = new ArrayList<String>();

    /**
     * Contains for every data field in the PDF file the associated name of that field.
     */
    private final Map<String, DataField> dataFieldNames = new HashMap<>();

    private final ExecutorService threadPool;
    private final ActionListener importListener;

    private final AtomicInteger numberOfPdfFiles = new AtomicInteger(0);
    private final AtomicInteger currentPdfFiles = new AtomicInteger(0);

    /**
     * Provides an action event fired when a file was imported.
     *
     * @author Christian Wichmann
     */
    public class PdfFormImporterEvent extends ActionEvent {

        private static final long serialVersionUID = 1759872763624710874L;
        private final int numberOfPdfFiles;
        private final int currentPdfFile;

        /**
         *
         * @param source
         *            source of the event
         * @param numberOfPdfFiles
         *            number of PDF files to be imported
         * @param currentPdfFile
         *            number of the currently importer PDF file
         */
        public PdfFormImporterEvent(final Object source, final int numberOfPdfFiles,
                final int currentPdfFile) {
            super(source, ActionEvent.ACTION_PERFORMED, "");
            this.numberOfPdfFiles = numberOfPdfFiles;
            this.currentPdfFile = currentPdfFile;
        }

        /**
         * Returns the number of PDF files to be imported from a given directory.
         *
         * @return number of PDF files
         */
        public final int getNumberOfPdfFiles() {
            return numberOfPdfFiles;
        }

        /**
         * Returns the number of the currently imported PDF file.
         *
         * @return number of the currently imported PDF file
         */
        public final int getCurrentPdfFile() {
            return currentPdfFile;
        }
    }

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
     * Btn - Formular speichern unter - Formular speichern unter - null
     * Btn - Senden - Senden - null
     * Ch - Konfession - Konfession - 3
     * Ch - Studiengang - Studiengang - -1
     * Ch - DauerAusbildung - DauerAusbildung - 3,5
     * Btn - Umschueler - Umschueler - UmschuelerNein
     * Btn - Geschlecht - Geschlecht - m
     * Tx - StraßeNr - StraßeNr - Uthof 3
     * Btn - SchulbesuchBisher - SchulbesuchBisher - RS
     * Btn - Schulabschluss - Schulabschluss - EI
     * Tx - SchulabschlussSonstigerErlaeuterung - SchulabschlussSonstigerErlaeuterung - null
     */

    /**
     * Initialize an instance of PDF form importer.
     *
     * @param directory
     *            directory from which to import the PDF files containing the forms
     * @param importListener
     *            listener for changes at the import
     */
    public PdfFormImporter(final Path directory, final ActionListener importListener) {
        this.importListener = importListener;

        fillDataFieldNamesDictionary();

        final int poolSize = 1;
        threadPool = Executors.newFixedThreadPool(poolSize);
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                parseFiles(directory);
            }
        });
    }

    /**
     * Explicitly dispose the importer to exit VM correctly. The importer uses a executor service
     * from the concurrency library that produces non-daemon threads. These thread prevent the VM
     * from shutting down correctly if they are not explicitly closes.
     *
     * Alternatively the newFixedThreadPool() method of the executor service can take a
     * ThreadFactory to produce daemon threads. (See
     * http://stackoverflow.com/questions/7158587/daemon
     * -threads-scheduled-on-an-executorservice-explain-why-this-is-bad-form)
     */
    public void disposeImporter() {
        logger.info("Closing thread for importer...");
        threadPool.shutdown();
    }

    /**
     * Fires a new event signaling a change in the importer.
     *
     * @param e
     *            importer event
     */
    private synchronized void fireImportEvent(final PdfFormImporterEvent e) {
        if (importListener != null) {
            importListener.actionPerformed(e);
        }
    }

    /**
     * Finds and parses all PDF files in a given directory (not the subdirectories!).
     *
     * @param directory
     *            directory from which to parse PDF files
     */
    private synchronized void parseFiles(final Path directory) {
        // count number of PDF files in directory
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.pdf");
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (final Path path : directoryStream) {
                if (matcher.matches(path.getFileName())) {
                    numberOfPdfFiles.incrementAndGet();
                }
            }
        } catch (final IOException e) {
            logger.warn("Could not read directory listing!");
        }
        // find all PDF files and parse them
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (final Path path : directoryStream) {
                if (matcher.matches(path.getFileName())) {
                    logger.info("Found PDF file: " + path);
                    // parse every PDF file in given directory and add them to list
                    final Applicant a = parsePDFFile(path);
                    if (a != null) {
                        listOfStudents.add(a);
                    } else {
                        final Path invalidPdfFile = path.getFileName();
                        if (invalidPdfFile != null) {
                            listOfInvalidPdfFiles.add(invalidPdfFile.toString());
                        }
                    }
                    currentPdfFiles.incrementAndGet();
                    fireImportEvent(new PdfFormImporterEvent(this, numberOfPdfFiles.get(),
                            currentPdfFiles.get()));
                }
            }
        } catch (final IOException ex) {
            logger.warn("Could not open PDF file!");
        }
        // log all not imported files
        for (final String string : listOfInvalidPdfFiles) {
            logger.info("Could not import following file: " + string);
        }
    }

    /**
     * Fills a map with all form field names and the corresponding data fields from the enumeration.
     */
    private void fillDataFieldNamesDictionary() {
        dataFieldNames.put("Vorname", DataField.FIRST_NAME);
        dataFieldNames.put("Name", DataField.LAST_NAME);
        dataFieldNames.put("Ausbildungsberuf", DataField.VOCATION);
        dataFieldNames.put("Fachrichtung", DataField.SPECIALIZATION);
        dataFieldNames.put("Ausbildungsbeginn", DataField.START_OF_TRAINING);
        dataFieldNames.put("StraßeNr", DataField.ADDRESS);
        dataFieldNames.put("Tel", DataField.PHONE);
        dataFieldNames.put("Fax", DataField.FAX);
        dataFieldNames.put("PLZ", DataField.ZIP_CODE);
        dataFieldNames.put("Ort", DataField.CITY);
        dataFieldNames.put("EMail", DataField.EMAIL);
        dataFieldNames.put("Staatsangehörigkeit", DataField.NATIONALITY);
        /*
         * Because of changes in the names of PDF form fields there are two different ways of
         * reading birthday and birthplace.
         */
        dataFieldNames.put("Geburtsdatum", DataField.BIRTHDAY);
        dataFieldNames.put("geb am", DataField.BIRTHDAY);
        dataFieldNames.put("Geburtsort", DataField.BIRTHPLACE);
        dataFieldNames.put("in", DataField.BIRTHPLACE);
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
        dataFieldNames.put("SchulabschlussSonstigerErlaeuterung",
                DataField.DEGREE_ADDITIONAL_INFORMATION);
        dataFieldNames.put("ErlaeuterungBFS", DataField.SCHOOL_SPECIALIZATION);
        dataFieldNames.put("SonstigesSchulabschluss", DataField.SCHOOL_OTHER_TYPE);

        /*
         * The following fields do not contain text strings or have to be evaluated otherwise:
         *
         * DauerAusbildung: Has to be parsed as decimal number and multiplicated by 12 to get month.
         *
         * Staatsangehörigkeit: A integer number representing the nationality.
         *
         * ErlaeuterungBFS - SonstigesSchulabschluss: Stored as additional info in School Enum.
         *
         * SchulabschlussSonstigerErlaeuterung: Stored as additional info in Degree Enum.
         *
         * Formular drucken - Senden: Ignored because control buttons.
         *
         * Umschueler (UmschuelerNein, UmschuelerJa), Geschlecht (m, w): Binary choices that have to be
         * evaluated.
         *
         * SchulbesuchBisher (RS), Schulabschluss(EI)
         */
    }

    /**
     * Parses a single PDF file defined by a given Path. If the PDF file contains no form fields and
     * has no useable data, null is returned to the caller!
     *
     * @param path
     *            path describing the PDF file to be parsed
     * @return applicants data or null, if file did not contain any form fields
     */
    private synchronized Applicant parsePDFFile(final Path path) {
        PDDocument pdfDocument = null;
        Applicant student = null;

        try {
            pdfDocument = PDDocument.load(path.toFile());
            if (pdfDocument != null) {
                final PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
                final PDAcroForm acroForm = docCatalog.getAcroForm();
                if (acroForm != null) {
                    @SuppressWarnings("unchecked")
                    final List<PDField> formFields = acroForm.getFields();

                    final ApplicantBuilder builder = new ApplicantBuilder();
                    final Path pdfFileName = path.getFileName();
                    if (pdfFileName != null) {
                        builder.setFileName(pdfFileName.toString());
                    } else {
                        builder.setFileName("");
                    }
                    for (final PDField pdField : formFields) {
                        // ignore useless fields in PDF file
                        if ("Formular drucken".equals(pdField.getValue())
                                || "Senden".equals(pdField.getValue())) {
                            continue;
                        }
                        // get religion enumeration
                        extractReligion(pdField, builder);
                        // get all plain string elements and store them in builder
                        extractStringValues(pdField, builder);
                        // get duration of training and nationality
                        extractNumericValues(pdField, builder);
                        // get gender and whether applicant is in a retraining
                        extractBooleanValues(pdField, builder);
                        // get attended school
                        extractAttendedSchool(pdField, builder);
                        // get last degree
                        extractLastDegree(pdField, builder);

                        logger.debug(pdField.getFieldType() + " - "
                                + pdField.getFullyQualifiedName() + " - "
                                + pdField.getPartialName() + " - " + pdField.getValue());
                    }
                    student = builder.build();
                    logger.info("Added student registration: " + student);
                }
            }
        } catch (final IOException e) {
            logger.warn("Could not open PDF file.");
        } finally {
            try {
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
            } catch (final IOException e) {
                logger.warn("Could not close PDF file.");
            }
        }
        return student;
    }

    /**
     * Extracts the religion from a given PDF form field and stores the data inside a provided
     * ApplicantBuilder instance.
     *
     * @param pdField
     *            PDF form field data
     * @param builder
     *            builder object for Applicant data
     * @throws IOException
     *             if reading of PDF form data failed
     */
    private void extractReligion(final PDField pdField, final ApplicantBuilder builder)
            throws IOException {
        if ("Konfession".equals(pdField.getFullyQualifiedName())) {
            if (pdField.getValue() != null && !("".equals(pdField.getValue()))) {
                if ("-1".equals(pdField.getValue())) {
                    // no religion chosen in the form
                    builder.setValue(DataField.RELIGION, Religion.OHNE_ANGABE);
                } else {
                    try {
                        final Religion religion = Religion.fromInteger(Integer.valueOf(pdField
                                .getValue()));
                        builder.setValue(DataField.RELIGION, religion);
                    } catch (final NumberFormatException e) {
                        /*
                         * Sometimes the value of a combo box field is not the integer value stored
                         * inside the PDF file. Instead of the integer the value of the field is the
                         * string representation of that field. E.g. "katholisch" instead of the
                         * integer value 3.
                         *
                         * The valueOf method of the enum Religion allows the
                         * conversion from string to enum constant. The string has to be trimmed and
                         * upper case to allow comparation with the enum constants. If the string
                         * does not contain a valid enum value, a default value is set.
                         */
                        Religion r;
                        try {
                            r = Religion.valueOf(pdField.getValue().trim().toUpperCase());
                        } catch (final IllegalArgumentException e2) {
                            r = Religion.OHNE_ANGABE;
                        }
                        builder.setValue(DataField.RELIGION, r);
                    }
                }
            } else {
                builder.setValue(DataField.RELIGION, Religion.OHNE_ANGABE);
            }
        }
    }

    /**
     * Extracts all string values from a given PDF form field and stores the data inside a provided
     * ApplicantBuilder instance.
     *
     * @param pdField
     *            PDF form field data
     * @param builder
     *            builder object for Applicant data
     * @throws IOException
     *             if reading of PDF form data failed
     */
    private void extractStringValues(final PDField pdField, final ApplicantBuilder builder)
            throws IOException {
        if (dataFieldNames.containsKey(pdField.getFullyQualifiedName())) {
            final DataField df = dataFieldNames.get(pdField.getFullyQualifiedName());
            if (pdField.getValue() != null) {
                // TODO Should all string be trimmed before they are stored?!
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
    }

    /**
     * Extracts the duration of the training from a given PDF form field, casts it as integer number
     * and calculates the duration in months. The result is stored inside a provided
     * ApplicantBuilder instance.
     *
     * @param pdField
     *            PDF form field data
     * @param builder
     *            builder object for Applicant data
     * @throws IOException
     *             if reading of PDF form data failed
     */
    private void extractNumericValues(final PDField pdField, final ApplicantBuilder builder)
            throws IOException {
        if ("DauerAusbildung".equals(pdField.getFullyQualifiedName())) {
            if (pdField.getValue() != null && !"-1".equals(pdField.getValue())) {
                final int monthsInYear = 12;
                final Double d = Double.valueOf(pdField.getValue().replace(",", "."))
                        * monthsInYear;
                final Integer i = d.intValue();
                builder.setValue(DataField.DURATION_OF_TRAINING, i);
            } else {
                builder.setValue(DataField.DURATION_OF_TRAINING, 0);
            }
        }
        if ("Staatsangehörigkeit".equals(pdField.getFullyQualifiedName())) {
            if (pdField.getValue() != null) {
                try {
                    final Integer i = Integer.valueOf(pdField.getValue());
                    builder.setValue(DataField.NATIONALITY, i);
                } catch (final NumberFormatException e) {
                    final Integer i = NationalityConverter.getInstance().convertNationality(
                            pdField.getValue());
                    builder.setValue(DataField.NATIONALITY, i);
                }
            } else {
                builder.setValue(DataField.NATIONALITY, new Integer(0));
            }
        }
    }

    /**
     * Extracts all boolean values from a given PDF form field, evaluates them and stores the data
     * inside a provided ApplicantBuilder instance.
     *
     * @param pdField
     *            PDF form field data
     * @param builder
     *            builder object for Applicant data
     * @throws IOException
     *             if reading of PDF form data failed
     */
    private void extractBooleanValues(final PDField pdField, final ApplicantBuilder builder)
            throws IOException {
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
                // TODO Check whether to add a non-chosen gender type or to assign a best guess.
                logger.warn("Invalid gender!");
            }
        }
    }

    /**
     * Extracts information about the last accieved degree from a given PDF form field and stores
     * the data inside a provided ApplicantBuilder instance.
     *
     * @param pdField
     *            PDF form field data
     * @param builder
     *            builder object for Applicant data
     * @throws IOException
     *             if reading of PDF form data failed
     */
    private void extractLastDegree(final PDField pdField, final ApplicantBuilder builder)
            throws IOException {
        if ("Schulabschluss".equals(pdField.getFullyQualifiedName())) {
            Degree degree = Degree.SONSTIGER_ABSCHLUSS;
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
                    degree = Degree.SONSTIGER_ABSCHLUSS;
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
     * Extracts information about the attended school from a given PDF form field and stores the
     * data inside a provided ApplicantBuilder instance.
     *
     * @param pdField
     *            PDF form field data
     * @param builder
     *            builder object for Applicant data
     * @throws IOException
     *             if reading of PDF form data failed
     */
    private void extractAttendedSchool(final PDField pdField, final ApplicantBuilder builder)
            throws IOException {
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
                    schoolType = School.GYMNASIUM_OBERSTUFE;
                    break;
                case "B1": // TODO Check which Berufsfachschule should be default.
                    schoolType = School.BERUFSFACHSCHULE_ZWEIJAEHRIG_RS;
                    break;
                case "BS":
                    schoolType = School.BERUFSSCHULE;
                    break;
                case "IG":
                    schoolType = School.GESAMTSCHULE;
                    break;
                case "Oberschule":
                    schoolType = School.OBERSCHULE;
                    break;
                case "FO":
                    schoolType = School.FACHOBERSCHULE;
                    break;
                case "SA":
                    schoolType = School.FOERDERSCHULE;
                    break;
                case "XS":
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

    /**
     * Returns a list of all applicants data.
     *
     * @return list of all applicants data
     */
    public List<Applicant> getListOfStudents() {
        return listOfStudents;
    }

    /**
     * Returns a list with the file names of all invalid PDF files.
     *
     * @return list with the file names of all invalid PDF files
     */
    public List<String> getListOfInvalidPdfFiles() {
        return listOfInvalidPdfFiles;
    }

    /**
     * Gets the number of PDF files in the given directory.
     *
     * @return number of PDF files
     */
    public synchronized int getNumberOfPdfFiles() {
        return numberOfPdfFiles.get();
    }

    /**
     * Gets the currently imported PDF file.
     *
     * @return currently imported PDF file
     */
    public synchronized int getCurrentPdfFiles() {
        return currentPdfFiles.get();
    }
}
