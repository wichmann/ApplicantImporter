package de.ichmann.applicant_importer.ui;

import java.awt.FontMetrics;
import java.text.BreakIterator;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Provides utility methods for wrapping text.
 * 
 * @author Christian Wichmann
 */
public final class Tools {

    /**
     * Private constructor of utility class.
     */
    private Tools() {
    }

    /**
     * Wraps text for labels by calculating the size and breaking the text with html elements
     * according to the available size of its parent. (see
     * http://fauzilhaqqi.net/2010/01/java-tutorial-wrap-text-into-jlabel/)
     * 
     * @param width
     *            width to which the text should be wrapped
     * @param text
     *            text to wrap
     * @return string containing the wrapped text (done by HTML tags)
     */
    public static String wrapTextToWidth(final String[] text, final int width) {

        // measure the length of font in pixel
        JLabel label = new JLabel();
        FontMetrics fm = label.getFontMetrics(label.getFont());
        // to find the word separation
        BreakIterator boundary = BreakIterator.getWordInstance();
        // main string to be added
        StringBuffer m = new StringBuffer("<html>");
        // loop each index of array
        for (String str : text) {
            boundary.setText(str);
            // save each line
            StringBuffer line = new StringBuffer();
            // save each paragraph
            StringBuffer par = new StringBuffer();
            int start = boundary.first();
            // wrap loop
            for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary
                    .next()) {
                String word = str.substring(start, end);
                line.append(word);
                // compare width with font metrics
                int trialWidth = SwingUtilities.computeStringWidth(fm, line.toString());
                // if bigger, add new line
                if (trialWidth > width) {
                    line = new StringBuffer(word);
                    par.append("<br />");
                }
                // add new word to paragraphs
                par.append(word);
            }
            // add new line each paragraph
            par.append("<br />");
            // add paragraph into main string
            m.append(par);
        }
        // closed tag
        m.append("</html>");
        return m.toString();
    }

    /**
     * Wrap a String into a label.
     * 
     * @param width
     *            width to which the text should be wrapped
     * @param text
     *            text to wrap
     * @return string containing the wrapped text (done by HTML tags)
     */
    public static String wrapTextToWidth(final String text, final int width) {

        String[] newText = new String[] {text};
        return wrapTextToWidth(newText, width);
    }
}
