package de.ichmann.applicant_importer.model;

/**
 * Defines the possible values for religion in BBS-Planung.
 *
 * @author Christian Wichmann
 */
public enum Religion {

    OHNE_ANGABE(0),

    EVANGELISCH(1),

    KATHOLISCH(3),

    ALEVITISCH(5),

    ISLAMISCH(6),

    SONSTIGE(7),

    KEINE(8);

    private final int value;

    private Religion(int value) {
        this.value = value;
    }

    /**
     * Returns the assigned value for this religion.
     *
     * @return assigned value for religion
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the corresponding instance of this enumeration based on a given ID. The ID is the
     * value used by BBS-Planung to identify religions.
     *
     * @param x
     *            id of Religion
     * @return instance of Religion
     */
    public static Religion fromInteger(final int x) {
        for (Religion r : Religion.values()) {
            if (r.getValue() == x) {
                return r;
            }
        }
        return null;
    }
}
