package de.ichmann.applicant_importer.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ichmann.applicant_importer.exporter.BbsPlanungExporter;
import de.ichmann.applicant_importer.exporter.BbsPlanungExporter.ExportError;
import de.ichmann.applicant_importer.importer.PdfFormImporter;
import de.ichmann.applicant_importer.model.Applicant;

/**
 * Shows main window of Applicant Importer.
 *
 * @author Christian Wichmann
 */
public final class ApplicantImporterMain extends JFrame {

    private static final long serialVersionUID = -7501718764578733562L;

    private static final Logger logger = LoggerFactory.getLogger(ApplicantImporterMain.class);

    private static final Color ALARM_COLOR = new Color(255, 155, 155);

    private JButton importDirectoryButton = null;
    private JButton exportCsvButton = null;
    private JButton clearTableButton = null;
    private JTable applicantInformationTable = null;
    private JCheckBoxMenuItem exportInvalidApplicantsMenuItem = null;
    private JCheckBoxMenuItem highlightInvalidApplicantsMenuItem = null;
    private ProgressStatusBar statusBar = null;

    private List<Applicant> listOfApplicants = new ArrayList<Applicant>();

    /**
     * Instantiate a instance of the main window.
     */
    public ApplicantImporterMain() {

        setLookAndFeel();

        initialize();

        addListener();

        addKeyBindings();
    }

    /**
     * Provides a table to display applicants data. The table rows are colered alternatingly and
     * invalid rows can be highlighted.
     *
     * @author Christian Wichmann
     */
    private final class ApplicantInformationTable extends JTable {
        private static final long serialVersionUID = -5076459486279170507L;

        /**
         * Overrides the default method from JTable and adds only the coloring of rows that contain
         * invalid information. This is easier than creating a new TableCellRenderer to handles
         * this!
         *
         * Source: https://tips4java.wordpress.com/2010/01/24/table-row-rendering/
         */
        @Override
        public Component prepareRenderer(final TableCellRenderer renderer, final int row,
                final int column) {
            final Component c = super.prepareRenderer(renderer, row, column);
            if (!isRowSelected(row)) {
                // color row in alternating colors
                c.setBackground(row % 2 == 0 ? getBackground() : Color.LIGHT_GRAY);
                // color row depending on the underlining data and if it is plausible
                if (highlightInvalidApplicantsMenuItem.isSelected()) {
                    /*
                     * Convert the row index from the view to the corresponding row index of the model.
                     * Before when coloring the rows alternating this was not necessary because the rows
                     * should be colored as viewed and showed. But to get the correct applicant of a row to
                     * check for its plausibility the conversion is necessary!
                     */
                    final int rowInModel = applicantInformationTable.convertRowIndexToModel(row);
                    final ApplicantInformationTableModel model = (ApplicantInformationTableModel) (applicantInformationTable
                            .getModel());
                    final Applicant s = model.getApplicantForRow(rowInModel);
                    if (!s.checkPlausibility()) {
                        c.setBackground(ALARM_COLOR);
                    }
                }
            }

            return c;
        }
    }

    /**
     * Provides a status bar containing a progress bar.
     *
     * @author Christian Wichmann
     */
    public final class ProgressStatusBar extends JProgressBar {
        private static final long serialVersionUID = 508013547776082253L;

        /**
         * Initializes a new StatusBar instance.
         */
        public ProgressStatusBar() {
            super();

            final int width = 100;
            final int height = 16;

            setPreferredSize(new Dimension(width, height));
            setStringPainted(true);
            setString("");
            setValue(0);
        }

        /**
         * Sets minimum and maximum value for the progress bar. This method has to be called before
         * the progress bar can be incremented by using the fillProgressBar() method.
         *
         * @param minimum
         *            minimum value for the progress bar
         * @param maximum
         *            maximum value for the progress bar
         */
        public void prepareProgressBar(final int minimum, final int maximum) {
            setMinimum(minimum);
            setMaximum(maximum);
        }

        /**
         * Sets the value of the progress bar to a given value.
         *
         * @param progress
         *            value the progress bar should be set to
         */
        public void fillProgressBar(final int progress) {
            setString(String.format("%d von %d Dateien importiert...", progress, getMaximum()));
            setValue(progress);
        }
    }

    /**
     * Initialize all components of the user interface and add them to the layout manager.
     */
    private void initialize() {

        setTitle("BewerberImporter");
        setName("ApplicantImporter");
        setIconImage(new ImageIcon(getClass().getResource("/icons/icon.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setJMenuBar(buildMenuBar());

        final GridBagConstraints c = new GridBagConstraints();
        final int inset = 10;
        c.insets = new Insets(inset, inset, inset, inset);
        final GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        // create and add button to choose directory
        importDirectoryButton = new JButton("PDF-Dateien importieren...");
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        add(importDirectoryButton, c);

        // create and add table to show student data
        applicantInformationTable = new ApplicantInformationTable();
        applicantInformationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applicantInformationTable.setAutoCreateRowSorter(true);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        final JScrollPane scrollPane = new JScrollPane(applicantInformationTable);
        add(scrollPane, c);

        // create and add button to export data to CSV file
        exportCsvButton = new JButton("Bewerber-Daten exportieren...");
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.NONE;
        add(exportCsvButton, c);

        // create and add button to export data to CSV file
        clearTableButton = new JButton("Tabelle löschen");
        c.gridx = 1;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        add(clearTableButton, c);

        statusBar = new ProgressStatusBar();
        c.gridx = 0;
        c.gridy = 3;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(statusBar, c);
    }

    /**
     * Builds a menu bar including all menu items.
     *
     * @return menu bar
     */
    private JMenuBar buildMenuBar() {
        JMenuBar menuBar;
        JMenu fileMenu, helpMenu;
        JMenuItem quitMenuItem, helpMenuItem, aboutMenuItem;

        menuBar = new JMenuBar();
        fileMenu = new JMenu("Datei");
        fileMenu.setMnemonic(KeyEvent.VK_D);
        fileMenu.getAccessibleContext().setAccessibleDescription("Datei-Menü");
        menuBar.add(fileMenu);

        exportInvalidApplicantsMenuItem = new JCheckBoxMenuItem(
                "Unvollständige Bewerber exportieren");
        exportInvalidApplicantsMenuItem.setMnemonic(KeyEvent.VK_U);
        exportInvalidApplicantsMenuItem.setSelected(true);
        fileMenu.add(exportInvalidApplicantsMenuItem);
        highlightInvalidApplicantsMenuItem = new JCheckBoxMenuItem(
                "Unvollständige Bewerber markieren");
        highlightInvalidApplicantsMenuItem.setMnemonic(KeyEvent.VK_M);
        highlightInvalidApplicantsMenuItem.setSelected(false);
        highlightInvalidApplicantsMenuItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                // repaint applicant table if this option has been changed
                applicantInformationTable.repaint();
            }
        });
        fileMenu.add(highlightInvalidApplicantsMenuItem);
        fileMenu.addSeparator();
        quitMenuItem = new JMenuItem("Beenden", KeyEvent.VK_B);
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        quitMenuItem.getAccessibleContext().setAccessibleDescription("Beenden des Programms");
        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        });
        fileMenu.add(quitMenuItem);

        helpMenu = new JMenu("Hilfe");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.getAccessibleContext().setAccessibleDescription("Hilfe-Menü");
        menuBar.add(helpMenu);

        helpMenuItem = new JMenuItem("Hilfe...", KeyEvent.VK_H);
        helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        helpMenuItem.getAccessibleContext().setAccessibleDescription("Hilfe zum Programms");
        helpMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                showHelpDialog();
            }
        });
        helpMenu.add(helpMenuItem);

        aboutMenuItem = new JMenuItem("Über...", KeyEvent.VK_U);
        aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        aboutMenuItem.getAccessibleContext().setAccessibleDescription("Über das Programm");
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aboutMenuItem);

        return menuBar;
    }

    /**
     * Shows help information dialog.
     */
    protected void showHelpDialog() {

    }

    /**
     * Shows a about dialog with information about the author and this program.
     */
    protected void showAboutDialog() {
        // TODO Make mail link clickable. (see
        // http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel)
        final StringBuilder builder = new StringBuilder();
        builder.append("<html><font face=\"Candara\">");
        builder.append("<font size=+2>BewerberImport</font>");
        builder.append("<font size=+0><br>");
        builder.append("<br>Autor: Christian Wichmann &lt;wichmann@bbs-os-brinkstr.de&gt;");

        final String versionInformation = this.getClass().getPackage().getSpecificationVersion();
        builder.append("<br>Version: ");
        builder.append(versionInformation);

        final String buildDateInformation = this.getClass().getPackage().getImplementationVersion();
        builder.append("<br>Datum: ");
        builder.append(buildDateInformation);
        builder.append("</font></html>");
        JOptionPane.showMessageDialog(ApplicantImporterMain.this, builder.toString(), "Über...",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Adds action listeners for all buttons and the mouse listener for the applicants data table.
     */
    private void addListener() {
        importDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                doImportFromDirectory();
            }
        });

        exportCsvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                doExportToFile();
            }
        });

        clearTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                listOfApplicants.clear();
                applicantInformationTable.setModel(new ApplicantInformationTableModel(
                        listOfApplicants));
            }
        });

        applicantInformationTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent me) {
                final JTable table = (JTable) me.getSource();
                final Point p = me.getPoint();
                final int row = table.rowAtPoint(p);
                final ApplicantInformationTableModel model = (ApplicantInformationTableModel) (table
                        .getModel());
                final Applicant applicant = model.getApplicantForRow(row);
                if (me.getClickCount() == 2) {
                    JOptionPane.showMessageDialog(ApplicantImporterMain.this,
                            buildInfoMessage(applicant), "Bewerberdaten",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    /**
     * Builds a information message that is shown when double-clicking on an applicant in the table.
     *
     * @param applicant
     *            applicant for which to show information
     * @return string containing information message
     */
    protected String buildInfoMessage(final Applicant applicant) {
        final int textWidth = 400;
        final StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<strong>Bewerber: ");
        builder.append(applicant.toString());
        builder.append("</strong>");
        builder.append("<br><br>");
        builder.append("Dateiname: ");
        builder.append(applicant.getFileName());
        builder.append("<br><br>");
        builder.append(Tools.wrapTextToWidth(applicant.buildCommentFromApplicant(), textWidth));
        builder.append("</html>");
        return builder.toString();
    }

    /**
     * Adds key bindings for the main window.
     */
    private void addKeyBindings() {
        final InputMap inputMap = applicantInformationTable.getInputMap(JComponent.WHEN_FOCUSED);
        final ActionMap actionMap = applicantInformationTable.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            private static final long serialVersionUID = -2329856122068030091L;

            @Override
            public void actionPerformed(final ActionEvent evt) {
                int row = applicantInformationTable.getSelectedRow();
                int col = applicantInformationTable.getSelectedColumn();
                if (row >= 0 && col >= 0) {
                    row = applicantInformationTable.convertRowIndexToModel(row);
                    col = applicantInformationTable.convertColumnIndexToModel(col);
                    final ApplicantInformationTableModel model = (ApplicantInformationTableModel) (applicantInformationTable
                            .getModel());
                    model.removeRow(row);
                }
                applicantInformationTable.repaint();
            }
        });
    }

    /**
     * Sets Swing LAF to "Nimbus".
     */
    private void setLookAndFeel() {
        // set look and feel to new standard (since Java SE 6 Update 10)
        try {
            for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (final ClassNotFoundException e) {
            logger.warn("Could not set look and feel.");
        } catch (final InstantiationException e) {
            logger.warn("Could not set look and feel.");
        } catch (final IllegalAccessException e) {
            logger.warn("Could not set look and feel.");
        } catch (final UnsupportedLookAndFeelException e1) {
            logger.warn("Could not set look and feel.");
        }
    }

    /**
     * Shows a file selection dialog to chose a directory. Imports all PDF file in that given
     * directory and shows a message box informing the user about it.
     */
    private void doImportFromDirectory() {
        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Verzeichnis mit PDF-Dateien auswählen...");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        final int returnValue = chooser.showOpenDialog(ApplicantImporterMain.this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            importFromDirectory(chooser.getSelectedFile());
        }
    }

    /**
     * Show a dialog box to indicate to the user that the import has been finished.
     *
     * @param listOfInvalidPdfFiles
     *            list of all PDF files that could not be imported
     * @param selectedImportDirectory
     *            directory from which files were imported
     */
    private void showImportFinishedDialog(final List<String> listOfInvalidPdfFiles,
            final String selectedImportDirectory) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("Aus dem Verzeichnis ");
        builder.append("<strong>" + selectedImportDirectory + "</strong>");
        builder.append(" wurden " + listOfApplicants.size());
        builder.append(" Bewerber importiert.<br><br>");
        // add ignored files to message if there were any
        if (listOfInvalidPdfFiles.size() != 0) {
            builder.append("Folgende Dateien konnten nicht eingelesen werden:<br><br>");
            for (final String s : listOfInvalidPdfFiles) {
                builder.append(s + "<br>");
            }
        }
        builder.append("</html>");
        JOptionPane.showMessageDialog(this, builder.toString(), "Import erfolgreich",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Imports all applicants data from a given directory, stores them and updates the table to
     * display them.
     *
     * @param selectedFile
     *            directory to be imported
     */
    private void importFromDirectory(final File selectedFile) {
        new PdfFormImporter(selectedFile.toPath(), new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final PdfFormImporter importer = ((PdfFormImporter) e.getSource());
                final int numberOfPdfFiles = importer.getNumberOfPdfFiles();
                statusBar.prepareProgressBar(0, numberOfPdfFiles);
                final int currentPdfFiles = importer.getCurrentPdfFiles();
                statusBar.fillProgressBar(currentPdfFiles);
                if (numberOfPdfFiles == currentPdfFiles) {
                    // show dialog and fill table only when all files have been imported
                    listOfApplicants = importer.getListOfStudents();
                    applicantInformationTable.setModel(new ApplicantInformationTableModel(
                            listOfApplicants));
                    final List<String> listOfInvalidPdfFiles = importer.getListOfInvalidPdfFiles();
                    final String selectedImportDirectory = selectedFile.getName();
                    showImportFinishedDialog(listOfInvalidPdfFiles, selectedImportDirectory);
                    // explicitly dispose the importer (shuts down the executer service) to exit VM
                    // correctly
                    importer.disposeImporter();
                }
            }
        });
    }

    /**
     * Shows a file selection dialog to chose a output file. Exports all applicants data to this
     * file as CSV format and shows a message box informing the user about it.
     */
    private void doExportToFile() {
        final JFileChooser chooser = new JFileChooser() {
            private static final long serialVersionUID = -6194898076069880017L;

            @Override
            public void approveSelection() {
                final File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    final int result = JOptionPane.showConfirmDialog(this,
                            "Die Datei existiert bereits. Soll sie überschrieben werden?",
                            "Bereits existierende Datei überschreiben?", JOptionPane.YES_NO_OPTION);
                    switch (result) {
                    case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                    case JOptionPane.CANCEL_OPTION:
                        cancelSelection();
                        return;
                    default:
                        // handle JOptionPane.NO_OPTION and JOptionPane.CLOSED_OPTION
                        return;
                    }
                }
                super.approveSelection();
            }
        };
        final String exportFileDefaultName = "Bewerber_Aus_Nebenstelle.txt";
        chooser.setDialogTitle("Datei für exportierte Daten auswählen...");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setSelectedFile(new File(exportFileDefaultName));

        final int returnValue = chooser.showSaveDialog(ApplicantImporterMain.this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            final BbsPlanungExporter exporter = new BbsPlanungExporter(chooser.getSelectedFile()
                    .toPath(), listOfApplicants, exportInvalidApplicantsMenuItem.isSelected());
            final List<ExportError> listOfErrors = exporter.getListOfExportErrors();
            if (listOfErrors.isEmpty()) {
                final String s = String
                        .format("<html>%d Bewerber in die Datei <strong>%s</strong> exportiert.</html>",
                                exporter.getNumberExportedApplicants(), chooser.getSelectedFile()
                                        .getName());
                JOptionPane.showMessageDialog(this, s, "Export erfolgreich",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                final StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                sb.append(String
                        .format("%d Bewerber in die Datei <strong>%s</strong> exportiert.",
                                exporter.getNumberExportedApplicants(), chooser.getSelectedFile()
                                        .getName()));
                sb.append("<br><br>");
                sb.append("Die folgenden Bewerber wurden auf Grund eines Fehlers nicht korrekt exportiert:");
                for (final ExportError e : listOfErrors) {
                    sb.append("<br>");
                    sb.append(e.getApplicant().toString());
                }
                sb.append("</html>");
                JOptionPane.showMessageDialog(this, sb.toString(),
                        "Fehler beim Export aufgetreten", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
