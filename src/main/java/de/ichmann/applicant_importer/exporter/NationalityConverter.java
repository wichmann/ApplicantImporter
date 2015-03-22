package de.ichmann.applicant_importer.exporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts a given string with a description of a nationality into a best guess of its ID. Because
 * the nationality can be written in different ways, the result is not guaranteed to be correct!
 *
 * @author Christian Wichmann
 */
public final class NationalityConverter {

    private static final Logger logger = LoggerFactory.getLogger(NationalityConverter.class);

    /**
     * Singleton instance.
     */
    private static NationalityConverter sInstance = null;

    private static final String NATIONALITY = "Staat";
    private static final String ID = "ID";
    private static final char FIELD_DELIMITER = ';';

    private static final Map<String, Integer> NATIONALITY_2_ID_MAPPING = new HashMap<>();

    /**
     * Private constructor to prevent multiple instances.
     */
    private NationalityConverter() {
        super();

        // read conversion data from file
        readDataFromFile();
    }

    /**
     * Reads mapping data from file. The data is only loaded once at the first call to
     * getInstance().
     */
    private void readDataFromFile() {
        CSVParser csvFileParser = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader().withDelimiter(FIELD_DELIMITER);
        try {
            // open CSV file with mapping data
            InputStream in = NationalityConverter.class
                    .getResourceAsStream("/data/Staatennummern.csv");
            InputStreamReader isr = new InputStreamReader(in, Charset.forName("UTF-8"));

            // initialize CSVParser object
            csvFileParser = new CSVParser(isr, csvFileFormat);

            // read the CSV file records starting from the second record to skip the header
            logger.info("Reading mapping data from CSV file...");
            for (CSVRecord csvRecord : csvFileParser) {
                NATIONALITY_2_ID_MAPPING.put(csvRecord.get(NATIONALITY),
                        Integer.parseInt(csvRecord.get(ID)));
            }
            logger.info("Read mapping data from CSV file.");
        } catch (IOException e) {
            logger.error("Could not open from CSV file with mapping data!");
        } finally {
            try {
                if (csvFileParser != null) {
                    csvFileParser.close();
                }
            } catch (IOException e) {
                logger.error("Could not close CSV file with mapping data!");
            }
        }
    }

    /**
     * Gets the unique instance of this converter.
     *
     * @return singleton instance of this converter
     */
    public static synchronized NationalityConverter getInstance() {
        if (sInstance == null) {
            sInstance = new NationalityConverter();
        }
        return sInstance;
    }

    /**
     * Guesses the best match for a given string within a list of nationalities. The best guess is
     * found by using the Jaro Winkler distance between string.
     *
     * @param nationality
     *            string describing a nationality
     * @return string containing the best matching nationality
     * @throws IllegalArgumentException
     *             if parameter string is {@code null}
     */
    public String guessNationality(final String nationality) {
        if (nationality == null) {
            throw new IllegalArgumentException("Parameter nationality must not be null");
        }
        // handle default case "Deutschland"
        String trimmedNationality = nationality.trim();
        if ("".equals(trimmedNationality)) {
            logger.debug("Falling back on default nationality \"Deutschland\".");
            return "Deutschland";
        }
        double bestValue = 0.0;
        String bestGuess = "";

        for (String s : NATIONALITY_2_ID_MAPPING.keySet()) {
            double currentValue = StringUtils.getJaroWinklerDistance(s, trimmedNationality);
            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestGuess = s;
            }
        }
        logger.debug(String.format("Found best guess for %s -> %s", trimmedNationality, bestGuess));

        return bestGuess;
    }

    /**
     * Converts a string with a nationality into the corresponding ID. The best guess for the given
     * nationality string is found by using the Jaro Winkler distance between string.
     *
     * @param nationality
     *            string describing a nationality
     * @return ID of the given nationality
     * @throws IllegalArgumentException
     *             if parameter string is {@code null}
     */
    public int convertNationality(final String nationality) {
        if (nationality == null) {
            throw new IllegalArgumentException("Parameter nationality must not be null");
        }
        return NATIONALITY_2_ID_MAPPING.get(guessNationality(nationality));
    }
}
