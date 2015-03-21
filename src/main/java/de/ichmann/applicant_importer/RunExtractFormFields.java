package de.ichmann.applicant_importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

/**
 * Reads a PDF file and extracts all form fields including their values.
 * 
 * @author Christian Wichmann
 */
public final class RunExtractFormFields {

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
            pdfDocument = PDDocument
                    .load(new File(
                            "/home/christian/Desktop/BBSAnmeldung/AnmeldungBerufsschuleVerbessertMitDaten6.pdf"));
            PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();

            @SuppressWarnings("unchecked")
            List<PDField> formFields = acroForm.getFields();

            for (PDField pdField : formFields) {
                System.out
                        .println("" + pdField.getFullyQualifiedName() + ": " + pdField.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
