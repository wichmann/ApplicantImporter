package de.ichmann.applicant_importer.model;

/**
 * Defines all possible values for the highest accieved degree of an applicant in BBS-Planung.
 *
 * @author Christian Wichmann
 */
public enum Degree {

    /**
     * 
     */
    OHNE_ABSCHLUSS("OA"),

    /**
     * 
     */
    HAUPTSCHULABSCHLUSS("HA"),

    /**
     * 
     */
    SEKUNDAR_I_HAUPTSCHULE("HK"),

    /**
     * 
     */
    SEKUNDAR_I_REALSCHULE("SI"),

    /**
     * 
     */
    ERWEITERTER_SEKUNDAR_I("EI"),

    /**
     * 
     */
    FACHHOCHSCHULREIFE("FH"),

    /**
     * 
     */
    FACHGEBUDENE_HOCHSCHULREIFE("GH"),

    /**
     * 
     */
    SCHULISCHER_TEIL_DER_FACHHOCHSCHULREIFE("FT"),

    /**
     * 
     */
    ALLGEMEINE_HOCHSCHULEREIFE("AH"),

    /**
     * 
     */
    ABSCHLUSS_DER_FOERDERSCHULE("AL"),

    /**
     * 
     */
    SONSTIGER_AUSLAENDISCHER_ABSCHLUSS("XA"),

    /**
     * 
     */
    SONSTIGER_ABSCHLUSS("XS");

    private final String id;

    /**
     * Instantiate a new enumeration value for type of degree.
     * 
     * @param id
     *            id identifying the type of degree
     */
    private Degree(final String id) {
        this.id = id;
    }

    /**
     * Returns the ID identifying the achieved degree in the BBS-Planung software.
     *
     * @return id of achieved degree
     */
    public String getId() {
        return id;
    }
}
