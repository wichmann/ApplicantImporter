package de.ichmann.applicant_importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ichmann.applicant_importer.ui.ApplicantImporterMain;

public class RunApplicantImporterGUI {

	private static final Logger logger = LoggerFactory.getLogger(RunApplicantImporterGUI.class);

	private static final int WINDOW_HEIGHT = 768;
	private static final int WINDOW_WIDTH = 1024;

	public static void main(String[] args) {
		logger.info("Starting ApplicantImporter...");
		ApplicantImporterMain w = new ApplicantImporterMain();
		w.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		w.setVisible(true);
	}
}
