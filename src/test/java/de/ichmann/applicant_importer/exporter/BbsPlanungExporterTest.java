package de.ichmann.applicant_importer.exporter;

import static org.junit.Assert.assertEquals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ichmann.applicant_importer.importer.PdfFormImporter;
import de.ichmann.applicant_importer.importer.PdfFormImporter.PdfFormImporterEvent;

public class BbsPlanungExporterTest {

    private static PdfFormImporter importer;
    private static BbsPlanungExporter exporter;

    // create lock to wait with tests for the import to be complete
    private static final CountDownLatch wait_on_import = new CountDownLatch(1);

    private static File tempOutputFile;

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

        tempOutputFile = File.createTempFile("test", ".txt");
        exporter = new BbsPlanungExporter(tempOutputFile.toPath(), importer.getListOfStudents(),
                true);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testBbsPlanugExporter() throws IOException, URISyntaxException {
        final URL url = ClassLoader.getSystemResource("export_of_test_data.txt");
        final File testDataFile = new File(url.toURI());
        final boolean areFilesEqual = FileUtils.contentEquals(tempOutputFile, testDataFile);
        assertEquals(true, areFilesEqual);
    }

    @Test
    public final void testGetNumberExportedApplicants() {
        final int numberExporterApplicants = exporter.getNumberExportedApplicants();
        assertEquals(3, numberExporterApplicants);
    }

    @Test
    public final void testGetListOfExportErrors() {
        final int numberOfExportErrors = exporter.getListOfExportErrors().size();
        assertEquals(0, numberOfExportErrors);
    }

}
