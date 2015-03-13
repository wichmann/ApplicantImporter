package studentImporter;

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

import javax.swing.JButton;
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
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicantImporterMain extends JFrame {

	private static final long serialVersionUID = -7501718764578733562L;

	private static final Logger logger = LoggerFactory.getLogger(ApplicantImporterMain.class);

	private static final int WINDOW_HEIGHT = 768;
	private static final int WINDOW_WIDTH = 1024;
	private static final Color ALARM_COLOR = new Color(255, 155, 155);

	private JButton importDirectoryButton = null;
	private JButton exportCsvButton = null;
	private JButton clearTableButton = null;
	private JTable applicantInformationTable = null;

	private PdfFormImporter importer = null;
	private BbsPlanungExporter exporter = null;

	private List<Applicant> listOfApplicants;

	public ApplicantImporterMain() {

		setLookAndFeel();

		initialize();

		addListener();
	}

	private void initialize() {

		setJMenuBar(buildMenuBar());

		GridBagConstraints c = new GridBagConstraints();
		int inset = 10;
		c.insets = new Insets(inset, inset, inset, inset);
		GridBagLayout layout = new GridBagLayout();
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
			 * Overrides the default method from JTable and adds only the
			 * coloring of rows that contain invalid information. This is easier
			 * than creating a new TableCellRenderer to handles this!
			 * 
			 * Source:
			 * https://tips4java.wordpress.com/2010/01/24/table-row-rendering/
			 */
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if (!isRowSelected(row)) {
					// color row in alternating colors
					c.setBackground(row % 2 == 0 ? getBackground() : Color.LIGHT_GRAY);
					// color row depending on the underlining data and if it is
					// plausible
					Applicant s = ((ApplicantInformationTableModel) getModel()).getApplicantForRow(row);
					if (!s.checkPlausibility()) {
						c.setBackground(ALARM_COLOR);
					}
				}

				return c;
			}
		};
		applicantInformationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		JMenu fileMenu;
		JMenuItem quitMenuItem;

		menuBar = new JMenuBar();
		fileMenu = new JMenu("Datei");
		fileMenu.setMnemonic(KeyEvent.VK_D);
		fileMenu.getAccessibleContext().setAccessibleDescription("Datei-Menü");
		menuBar.add(fileMenu);

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

		return menuBar;
	}

	private void addListener() {
		importDirectoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Verzeichnis mit PDF-Dateien auswählen...");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				int returnValue = chooser.showOpenDialog(ApplicantImporterMain.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					importFromDirectory(chooser.getSelectedFile());
				}
			}
		});

		exportCsvButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Datei für exportierte Daten auswählen...");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				int returnValue = chooser.showSaveDialog(ApplicantImporterMain.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					exportToFile(chooser.getSelectedFile());
				}
			}
		});

		clearTableButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listOfApplicants.clear();
				applicantInformationTable.setModel(new ApplicantInformationTableModel(listOfApplicants));
			}
		});

		applicantInformationTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				JTable table = (JTable) me.getSource();
				Point p = me.getPoint();
				int row = table.rowAtPoint(p);
				ApplicantInformationTableModel model = (ApplicantInformationTableModel) (table.getModel());
				Applicant applicant = model.getApplicantForRow(row);
				if (me.getClickCount() == 2) {
					JOptionPane.showMessageDialog(ApplicantImporterMain.this, applicant, "Zeile ausgewählt",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	}

	private void importFromDirectory(File selectedFile) {
		importer = new PdfFormImporter(selectedFile.toPath());
		listOfApplicants = importer.getListOfStudents();
		applicantInformationTable.setModel(new ApplicantInformationTableModel(listOfApplicants));
	}

	private void exportToFile(File selectedFile) {
		exporter = new BbsPlanungExporter(selectedFile.toPath(), listOfApplicants);
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

	public static void main(String[] args) {
		ApplicantImporterMain w = new ApplicantImporterMain();
		w.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		w.setVisible(true);
	}
}
