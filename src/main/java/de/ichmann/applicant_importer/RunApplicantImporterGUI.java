package de.ichmann.applicant_importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ichmann.applicant_importer.ui.ApplicantImporterMain;

/**
 * Main running class for the ApplicantImporter.
 *
 * @author Christian Wichmann
 */
public final class RunApplicantImporterGUI {

    private static final Logger logger = LoggerFactory.getLogger(RunApplicantImporterGUI.class);

    /**
     * Height of the main window.
     */
    private static final int WINDOW_HEIGHT = 768;

    /**
     * Width of the main window.
     */
    private static final int WINDOW_WIDTH = 1024;

    /**
     * Private constructor of utility class.
     */
    private RunApplicantImporterGUI() {
    }

    /**
     * Starts the Applicant Importer.
     * 
     * @param args
     *            command line arguments
     */
    public static void main(final String[] args) {
        logger.info("Starting ApplicantImporter...");
        try {
            ApplicantImporterMain w = new ApplicantImporterMain();
            w.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            w.setVisible(true);
        } catch (Exception e) {
            logger.error("Uncaught exception in ApplicantImporter: " + e.getMessage());
        }
    }
}
