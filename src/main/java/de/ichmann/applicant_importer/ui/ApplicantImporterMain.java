package de.ichmann.applicant_importer.ui;

import java.awt.Color;
import java.awt.Component;
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
import de.ichmann.applicant_importer.importer.PdfFormImporter;
import de.ichmann.applicant_importer.model.Applicant;

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

    private PdfFormImporter importer = null;

    private List<Applicant> listOfApplicants;

    public ApplicantImporterMain() {

        setLookAndFeel();

        initialize();

        addListener();

        addKeyBindings();
    }

    private void initialize() {

        setTitle("BewerberImporter");
        setName("ApplicantImporter");
        setIconImage(new ImageIcon(getClass().getResource("/icons/icon.png")).getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        applicantInformationTable = new JTable() {
            private static final long serialVersionUID = -5076459486279170507L;

            /**
             * Overrides the default method from JTable and adds only the coloring of rows that
             * contain invalid information. This is easier than creating a new TableCellRenderer to
             * handles this!
             * 
             * Source: https://tips4java.wordpress.com/2010/01/24/table-row-rendering/
             */
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
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
                        row = applicantInformationTable.convertRowIndexToModel(row);
                        ApplicantInformationTableModel model = (ApplicantInformationTableModel) (applicantInformationTable
                                .getModel());
                        Applicant s = model.getApplicantForRow(row);
                        if (!s.checkPlausibility()) {
                            c.setBackground(ALARM_COLOR);
                        }
                    }
                }

                return c;
            }
        };
        applicantInformationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applicantInformationTable.setAutoCreateRowSorter(true);
        // applicantInformationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // TableColumnAdjuster tca = new TableColumnAdjuster(applicantInformationTable);
        // tca.adjustColumns();
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(applicantInformationTable);
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
    }

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
            public void stateChanged(ChangeEvent e) {
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
            public void actionPerformed(ActionEvent e) {
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

    protected void showHelpDialog() {

    }

    protected void showAboutDialog() {
        // TODO Make mail link clickable. (see
        // http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel)
        StringBuilder builder = new StringBuilder();
        builder.append("<html><font face=\"Candara\">");
        builder.append("<font size=+2>BewerberImport</font>");
        builder.append("<font size=+0><br>");
        builder.append("<br>Autor: Christian Wichmann &lt;wichmann@bbs-os-brinkstr.de&gt;");
        builder.append("<br>Version: 0.1");
        builder.append("<br>Datum: 21.03.2015");
        builder.append("</font></html>");
        JOptionPane.showMessageDialog(ApplicantImporterMain.this, builder.toString(), "Über...",
                JOptionPane.INFORMATION_MESSAGE);
    }

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

    protected String buildInfoMessage(final Applicant applicant) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<strong>Bewerber: ");
        builder.append(applicant.toString());
        builder.append("</strong>");
        builder.append("<br><br>");
        builder.append("Dateiname: ");
        builder.append(applicant.getFileName());
        builder.append("<br><br>");
        builder.append(Tools.wrapTextToWidth(applicant.buildCommentFromApplicant(), 400));
        builder.append("</html>");
        return builder.toString();
    }

    private void addKeyBindings() {
        InputMap inputMap = applicantInformationTable.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = applicantInformationTable.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {
            private static final long serialVersionUID = -2329856122068030091L;

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

    private void setLookAndFeel() {
        // set look and feel to new standard (since Java SE 6 Update 10)
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            logger.warn("Could not set look and feel.");
        } catch (InstantiationException e) {
            logger.warn("Could not set look and feel.");
        } catch (IllegalAccessException e) {
            logger.warn("Could not set look and feel.");
        } catch (UnsupportedLookAndFeelException e1) {
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
            StringBuilder builder = new StringBuilder();
            builder.append("<html>");
            builder.append("Aus dem Verzeichnis ");
            builder.append("<strong>" + chooser.getSelectedFile().getName() + "</strong>");
            builder.append(" wurden " + listOfApplicants.size());
            builder.append(" Bewerber importiert.<br><br>");
            builder.append("Folgende Dateien konnten nicht eingelesen werden:<br><br>");
            for (String s : importer.getListOfInvalidPdfFiles()) {
                builder.append(s + "<br>");
            }
            builder.append("</html>");
            JOptionPane.showMessageDialog(this, builder.toString(), "Import erfolgreich",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void importFromDirectory(final File selectedFile) {
        importer = new PdfFormImporter(selectedFile.toPath());
        listOfApplicants = importer.getListOfStudents();
        applicantInformationTable.setModel(new ApplicantInformationTableModel(listOfApplicants));
    }

    /**
     * Shows a file selection dialog to chose a output file. Exports all applicants data to this
     * file as CSV format and shows a message box informing the user about it.
     */
    private void doExportToFile() {
        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Datei für exportierte Daten auswählen...");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        final int returnValue = chooser.showSaveDialog(ApplicantImporterMain.this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            final BbsPlanungExporter exporter = new BbsPlanungExporter(chooser.getSelectedFile()
                    .toPath(), listOfApplicants, exportInvalidApplicantsMenuItem.isSelected());
            final String s = String.format(
                    "<html>%d Bewerber in die Datei <strong>%s</strong> exportiert.</html>",
                    exporter.getNumberExportedApplicants(), chooser.getSelectedFile().getName());
            JOptionPane.showMessageDialog(this, s, "Export erfolgreich",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
