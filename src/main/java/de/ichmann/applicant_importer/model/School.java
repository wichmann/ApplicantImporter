package de.ichmann.applicant_importer.model;

/**
 * Defines all school types and their id used in BBS-Planung to identify them.
 *
 * @author Christian Wichmann
 */
public enum School {

    /**
     * Berufseinstiegsklasse.
     */
    BERUFSEINSTIEGSKLASSE("BE"),

    /**
     * Berufsschule mit Teilzeit oder Blockunterricht.
     */
    BERUFSSCHULE("BS"),

    /**
     * Kooperatives Berufsgrundbildungsjahr.
     */
    KOOPERATIVES_BERUFSGRUNDBILDUNGSJAHR("BK"),

    /**
     * Schulisches Berufsgrundbildungsjahr.
     */
    SCHULISCHES_BERUFSGRUNDBILDUNGSJAHR("BG"),

    /**
     * Berufsoberschule.
     */
    BERUFSOBERSCHULE("BO"),

    /**
     * Berufsvorbereitungsjahr - Regelform.
     */
    BERUFSVORBEREITUNGSJAHR("BV"),

    /**
     * Berufsvorbereitungsjahr für Aussiedler und Ausländer.
     */
    BERUFSVORBEREITUNGSJAHR_FIER_AUSLAENDER("BR"),

    /**
     * Einjährige BFS.
     */
    BERUFSFACHSCHULE_EINJAEHRIG("B1"),

    /**
     * Einjährige BFS, SEK I - Abschluss - Realschulabschluss.
     */
    BERUFSFACHSCHULE_EINJAEHRIG_RS("B2"),

    /**
     * Einjährige oder eineinhalbj. BFS, berufl. Abschluss.
     */
    BERUFSFACHSCHULE_EINEINHALBJAEHRIG("B4"),

    /**
     * Zweijährige BFS, die zu einem beruflichen Abschluss führt.
     */
    BERUFSFACHSCHULE_ZWEIJAEHRIG("B7"),

    /**
     * Zweijährige BFS, die zu einem schulischen Abschluss führt.
     */
    BERUFSFACHSCHULE_ZWEIJAEHRIG_RS("B8"),

    /**
     * Einjährige oder eineinhalbjährige Fachschule.
     */
    FACHSCHULE_EINJAEHRIG("F1"),

    /**
     * Zweijährige Fachschule.
     */
    FACHSCHULE_ZWEIJAEHRIG("F2"),

    /**
     * Fachschule Seefahrt.
     */
    FACHSCHULE_SEEFAHRT("F4"),

    /**
     * Fachoberschule.
     */
    FACHOBERSCHULE("FO"),

    /**
     * Berufliches Gymnasium.
     */
    FACHGYMNASIUM("FG"),

    /**
     * Fachhochschule.
     */
    FACHHOCHSCHULE("FA"),

    /**
     * Freie Waldorfschule.
     */
    FREIE_WALDORFSCHULE("FW"),

    /**
     * Gymnasium Sekundarstufe I (bis einschließlich Klasse 9).
     */
    GYMNASIUM_BIS_KLASSE_9("G1"),

    /**
     * Gymnasium Sekundarstufe I (Klasse 10).
     */
    GYMNASIUM_BIS_KLASSE_10("G2"),

    /**
     * Gymnasiale Oberstufe.
     */
    GYMNASIUM_OBERSTUFE("GY"),

    /**
     * Hochschule.
     */
    HOCHSCHULE("HO"),

    /**
     * Hauptschule/ Hauptschulzweig.
     */
    HAUPTSCHULE("HS"),

    /**
     * Integrierte Gesamtschule.
     */
    GESAMTSCHULE("IG"),

    /**
     * Realschule/-zweig/Sekundarstufe I des Gymnasiums.
     */
    REALSCHULE("RS"),

    /**
     * Förderschule, mit Ausnahme Schwerpunkt Lernen.
     */
    FOERDERSCHULE("SA"),

    /**
     * Förderschule Schwerpunkt Lernen.
     */
    FOERDERSCHULE_SCHWERPUNKT_LERNEN("SL"),

    /**
     * Schule in den neuen Bundesländern.
     */
    SCHULE_IN_NEUEN_BUNDESLAENDERN("XD"),

    /**
     *
     */
    OBERSCHULE("OS"), // TODO Check if identifier is correct!

    /**
     * Sonstige Schule.
     */
    SONSTIGES("XS");

    private final String id;

    /**
     * Instantiate a new enumeration value for a school type.
     *
     * @param id
     *            id identifying the last school type
     */
    private School(final String id) {
        this.id = id;
    }

    /**
     * Returns the ID identifying the school type in the BBS-Planung software.
     *
     * @return id of school type
     */
    public String getId() {
        return id;
    }
}
