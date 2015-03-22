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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts a given ZIP code into the associated county ID (dt. Landkreisnummer) used in
 * BBS-Planung. To use this converter you have to get the Singleton instance and call the method
 * convertZipCode() on it.
 * <p>
 * The county IDs are defined by the German statistical office.
 *
 * @see <a
 *      href="http://www.destatis.de/DE/ZahlenFakten/LaenderRegionen/Regionales/Gemeindeverzeichnis/Administrativ/AdministrativeUebersicht.html">destatis
 *      Statistisches Bundesamt</a>
 * 
 * @author Christian Wichmann
 */
public final class Zip2CountyConverter {

    private static final Logger logger = LoggerFactory.getLogger(Zip2CountyConverter.class);

    /**
     * Singleton instance.
     */
    private static Zip2CountyConverter sInstance = null;

    private static final String COUNTY_ID = "Landkreisschlüssel";
    private static final String ZIP_CODE = "Postleitzahl";
    private static final char FIELD_DELIMITER = ';';

    private static final Map<Integer, Integer> ZIP_2_ID_MAPPING = new HashMap<>();

    /**
     * Private constructor to prevent multiple instances.
     */
    private Zip2CountyConverter() {
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
            InputStream in = Zip2CountyConverter.class
                    .getResourceAsStream("/data/Landkreisnummern.csv");
            InputStreamReader isr = new InputStreamReader(in, Charset.forName("UTF-8"));

            // initialize CSVParser object
            csvFileParser = new CSVParser(isr, csvFileFormat);

            // read the CSV file records starting from the second record to skip the header
            logger.info("Reading mapping data from CSV file...");
            for (CSVRecord csvRecord : csvFileParser) {
                ZIP_2_ID_MAPPING.put(Integer.parseInt(csvRecord.get(ZIP_CODE)),
                        Integer.parseInt(csvRecord.get(COUNTY_ID)));
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
    public static synchronized Zip2CountyConverter getInstance() {
        if (sInstance == null) {
            sInstance = new Zip2CountyConverter();
        }
        return sInstance;
    }

    /**
     * Converts a given zip code into a string containing the ID of the corresponding county.
     *
     * @param zipCode
     *            zip code for which to find the county id
     * @return string containing the county id for the given zip code
     */
    public String convertZipCode(final int zipCode) {
        if (ZIP_2_ID_MAPPING.containsKey(zipCode)) {
            return ZIP_2_ID_MAPPING.get(zipCode).toString();
        } else {
            return "";
        }
    }
}
