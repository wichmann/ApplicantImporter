package de.ichmann.applicant_importer.importer;

import static org.junit.Assert.assertEquals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ichmann.applicant_importer.importer.PdfFormImporter.PdfFormImporterEvent;
import de.ichmann.applicant_importer.model.Applicant;
import de.ichmann.applicant_importer.model.DataField;
import de.ichmann.applicant_importer.model.Religion;

public class PdfFormImporterTest {

    private static PdfFormImporter importer;

    // create lock to wait with tests for the import to be complete
    private static final CountDownLatch wait_on_import = new CountDownLatch(1);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // get path to /src/test/resources
        final URL url = ClassLoader.getSystemResource("data/");
        final Path folder = Paths.get(url.toURI());
        importer = new PdfFormImporter(folder, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final PdfFormImporterEvent event = (PdfFormImporterEvent) e;
                final int numberOfPdfFiles = event.getNumberOfPdfFiles();
                final int currentPdfFiles = event.getCurrentPdfFile();
                if (numberOfPdfFiles == currentPdfFiles) {
                    wait_on_import.countDown();
                }
            }
        });
        wait_on_import.await();
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testGetListOfStudents() {
        final List<Applicant> listOfStudents = importer.getListOfStudents();
        assertEquals(3, listOfStudents.size());

        for (final Applicant a : listOfStudents) {
            if ("Müller".equals(a.getValue(DataField.LAST_NAME))) {
                assertEquals("Maria", a.getValue(DataField.FIRST_NAME));
                assertEquals("20.01.1991", a.getValue(DataField.BIRTHDAY));
                assertEquals("Osnabrück", a.getValue(DataField.BIRTHPLACE));
                assertEquals("Beispielweg 56", a.getValue(DataField.ADDRESS));
                assertEquals("49080", a.getValue(DataField.ZIP_CODE));
                assertEquals("Osnabrück", a.getValue(DataField.CITY));
                assertEquals("0541/232323", a.getValue(DataField.PHONE));
                assertEquals("maria@mueller.com", a.getValue(DataField.EMAIL));
                assertEquals(Religion.KATHOLISCH, DataField.RELIGION.getFrom(a));
                assertEquals("1.8.2015", a.getValue(DataField.START_OF_TRAINING));
                assertEquals(36, DataField.DURATION_OF_TRAINING.getFrom(a));
                assertEquals("Augenoptikerin", a.getValue(DataField.VOCATION));
                assertEquals("Augenoptik Große", a.getValue(DataField.COMPANY_NAME));
                assertEquals("Frau Feige", a.getValue(DataField.COMPANY_CONTACT_PERSON));
                assertEquals("Heinz und Klara", a.getValue(DataField.NAME_OF_LEGAL_GUARDIAN));
                assertEquals("Kleiner Weg 42", a.getValue(DataField.COMPANY_ADDRESS));
                assertEquals("49080", a.getValue(DataField.COMPANY_ZIP_CODE));
                assertEquals("Osnabrück", a.getValue(DataField.COMPANY_CITY));
            }
        }
    }

    @Test
    public final void testGetListOfInvalidPdfFiles() {
        final List<String> listOfInvalidPdfFiles = importer.getListOfInvalidPdfFiles();
        final int numberOfInvalidFiles = listOfInvalidPdfFiles.size();
        assertEquals(3, numberOfInvalidFiles);
    }

    @Test
    public final void testGetNumberOfPdfFiles() {
        final int numberOfFiles = importer.getNumberOfPdfFiles();
        assertEquals(6, numberOfFiles);
    }

    @Test
    public final void testGetCurrentPdfFiles() {

    }
}
