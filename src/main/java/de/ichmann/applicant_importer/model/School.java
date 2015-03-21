package de.ichmann.applicant_importer.model;

/**
 * Defines all school types and their id used in BBS-Planung to identify them.
 * 
 * @author Christian Wichmann
 */
public enum School {

    /**
     * 
     */
    BERUFSEINSTIEGSKLASSE("BE"),

    /**
     * 
     */
    BERUFSSCHULE("BS"),

    /**
     * 
     */
    KOOPERATIVES_BERUFSGRUNDBILDUNGSJAHR("BK"),

    /**
     * 
     */
    SCHULISCHES_BERUFSGRUNDBILDUNGSJAHR("BG"),

    /**
     * 
     */
    BERUFSOBERSCHULE("BO"),

    /**
     * 
     */
    BERUFSVORBEREITUNGSJAHR("BV"),

    /**
     * 
     */
    BERUFSVORBEREITUNGSJAHR_FIER_AUSLAENDER("BR"),

    /**
     * 
     */
    BERUFSFACHSCHULE_EINJAEHRIG("B1"),

    /**
     * 
     */
    BERUFSFACHSCHULE_EINJAEHRIG_RS("B2"),

    /**
     * 
     */
    BERUFSFACHSCHULE_EINEINHALBJAEHRIG("B4"),

    /**
     * 
     */
    BERUFSFACHSCHULE_ZWEIJAEHRIG("B7"),

    /**
     * 
     */
    BERUFSFACHSCHULE_ZWEIJAEHRIG_RS("B8"),

    /**
     * 
     */
    FACHSCHULE_EINJAEHRIG("F1"),

    /**
     * 
     */
    FACHSCHULE_ZWEIJAEHRIG("F2"),

    /**
     * 
     */
    FACHSCHULE_SEEFAHRT("F4"),

    /**
     * 
     */
    FACHOBERSCHULE("FO"),

    /**
     * 
     */
    FACHGYMNASIUM("FG"),

    /**
     * 
     */
    FACHHOCHSCHULE("FA"),

    /**
     * 
     */
    FREIE_WALDORFSCHULE("FW"),

    /**
     * 
     */
    GYMNASIUM_BIS_KLASSE_9("G1"),

    /**
     * 
     */
    GYMNASIUM_BIS_KLASSE_10("G2"),

    /**
     * 
     */
    GYMNASIUM_OBERSTUFE("GY"),

    /**
     * 
     */
    HOCHSCHULE("HO"),

    /**
     * 
     */
    HAUPTSCHULE("HS"),

    /**
     * 
     */
    GESAMTSCHULE("IG"),

    /**
     * 
     */
    REALSCHULE("RS"),

    /**
     * 
     */
    FOERDERSCHULE("SA"),

    /**
     * 
     */
    FOERDERSCHULE_SCHWERPUNKT_LERNEN("SL"),

    /**
     * 
     */
    SCHULE_IN_NEUEN_BUNDESLAENDERN("XD"),

    /**
     * 
     */
    OBERSCHULE("OS"), // TODO Check if identifier is correct!

    /**
     * 
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
