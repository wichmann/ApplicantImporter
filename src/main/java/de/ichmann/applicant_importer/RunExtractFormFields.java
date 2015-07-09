package de.ichmann.applicant_importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a PDF file and extracts all form fields including their values.
 *
 * @author Christian Wichmann
 */
public final class RunExtractFormFields {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunExtractFormFields.class);

    /**
     * Private constructor of utility class.
     */
    private RunExtractFormFields() {
    }

    /**
     * Starts to parse and print all form fields in a given PDF file.
     *
     * @param args
     *            command line arguments
     */
    public static void main(final String[] args) {
        PDDocument pdfDocument = null;

        try {
            final String pdfFile = "";
            pdfDocument = PDDocument.load(new File(pdfFile));
            final PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
            final PDAcroForm acroForm = docCatalog.getAcroForm();

            @SuppressWarnings("unchecked")
            final List<PDField> formFields = acroForm.getFields();

            for (final PDField pdField : formFields) {
                System.out
                        .println("" + pdField.getFullyQualifiedName() + ": " + pdField.getValue());
            }
        } catch (final FileNotFoundException e) {
            LOGGER.error("Could find given PDF file.");
        } catch (final IOException e) {
            LOGGER.error("Could not open given PDF file.");
        }
    }
}
