package de.ichmann.applicant_importer.exporter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts a given ZIP code into the associated county ID (dt. Landkreisnummer). To use this converter you have to get the Singleton
 * instance and call the method convertZipCode() on it.
 * 
 * @author Christian Wichmann
 */
public class Zip2CountyConverter {

	private static final Logger logger = LoggerFactory.getLogger(Zip2CountyConverter.class);

	// create singleton instance
	private static Zip2CountyConverter sInstance = null;

	private static final String COUNTY_ID = "Landkreisschlüssel";
	private static final String ZIP_CODE = "Postleitzahl";
	private static final char FIELD_DELIMITER = ';';

	private static final Map<Integer, Integer> zip2idMapping = new HashMap<>();

	/**
	 * Private constructor to prevent multiple instances.
	 */
	private Zip2CountyConverter() {
		super();

		// read conversion data from file
		readDataFromFile();
	}

	private void readDataFromFile() {
		File csvData = null;
		CSVParser csvFileParser = null;
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader().withDelimiter(FIELD_DELIMITER);

		try {

			// open CSV file with mapping data
			csvData = new File(getClass().getResource("/data/Landkreisnummern.csv").toURI());

			// initialize CSVParser object
			csvFileParser = CSVParser.parse(csvData, Charset.forName("UTF-8"), csvFileFormat);

			// read the CSV file records starting from the second record to skip the header
			logger.info("Reading mapping data from CSV file...");
			for (CSVRecord csvRecord : csvFileParser) {
				zip2idMapping.put(Integer.parseInt(csvRecord.get(ZIP_CODE)), Integer.parseInt(csvRecord.get(COUNTY_ID)));
			}
			logger.info("Read mapping data from CSV file.");
		} catch (IOException e) {
			logger.error("Could not open from CSV file with mapping data!");
		} catch (URISyntaxException e) {
			logger.error("Could not find from CSV file with mapping data!");
		} finally {
			try {
				csvFileParser.close();
			} catch (IOException e) {
				logger.error("Could not close CSV file with mapping data!");
			}
		}
	}

	/**
	 * Gets the unique instance of this converter.
	 */
	public static synchronized Zip2CountyConverter getInstance() {
		if (sInstance == null) {
			sInstance = new Zip2CountyConverter();
		}
		return sInstance;
	}

	/**
	 * This is just a dummy method that can be called by the client. Replace this method by another one which you really need.
	 */
	public String convertZipCode(int zipCode) {
		if (zip2idMapping.containsKey(zipCode)) {
			return zip2idMapping.get(zipCode).toString();
		} else {
			return "";
		}
	}
}
