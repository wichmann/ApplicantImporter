package de.ichmann.applicant_importer.model;

/**
 * Defines all possible values for the highest accieved degree of an applicant in BBS-Planung.
 *
 * @author Christian Wichmann
 */
public enum Degree {

    /**
     * Ohne erfolgreichen Besuch/Abschluss.
     */
    OHNE_ABSCHLUSS("OA"),

    /**
     * Hauptschulabschluss (einschl. qualifizierter).
     */
    HAUPTSCHULABSCHLUSS("HA"),

    /**
     * SEK I - Hauptschulabschl. / Hauptschulab. n. der 10. Klasse.
     */
    SEKUNDAR_I_HAUPTSCHULE("HK"),

    /**
     * Sekundarabschluss I - Realschulabschluss.
     */
    SEKUNDAR_I_REALSCHULE("SI"),

    /**
     * Erweiterter Sekundarabschluss I.
     */
    ERWEITERTER_SEKUNDAR_I("EI"),

    /**
     * Fachhochschulreife.
     */
    FACHHOCHSCHULREIFE("FH"),

    /**
     * Fachgebundene Hochschulreife.
     */
    FACHGEBUDENE_HOCHSCHULREIFE("GH"),

    /**
     * Schulischer Teil der Fachhochschulreife.
     */
    SCHULISCHER_TEIL_DER_FACHHOCHSCHULREIFE("FT"),

    /**
     * AH Allgemeine Hochschulreife.
     */
    ALLGEMEINE_HOCHSCHULEREIFE("AH"),

    /**
     * Abschluss der Förderschule Schwerpunkt Lernen.
     */
    ABSCHLUSS_DER_FOERDERSCHULE("AL"),

    /**
     * Sonst. ausländischer Schulabschluss.
     */
    SONSTIGER_AUSLAENDISCHER_ABSCHLUSS("XA"),

    /**
     * Sonst. Schulabschluss.
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
