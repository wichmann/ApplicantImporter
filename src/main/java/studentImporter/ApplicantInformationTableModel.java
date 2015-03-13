package studentImporter;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ApplicantInformationTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2207776647966374826L;

	private List<Applicant> listOfStudents;

	public ApplicantInformationTableModel(List<Applicant> listOfStudents) {

		this.listOfStudents = listOfStudents;// Applicant is
	}

	/**
	 * Returns the Student object for a given row of the table containing all
	 * data of that student.
	 * 
	 * @param rowIndex
	 *            index of row to get Student data for
	 * @return Student object with students data
	 */
	public Applicant getStudentForRow(int rowIndex) {
		return listOfStudents.get(rowIndex);
	}

	@Override
	public int getRowCount() {
		return listOfStudents.size();
	}

	@Override
	public int getColumnCount() {
		return 8;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Applicant chosenStudent = listOfStudents.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return chosenStudent.getSurname();
		case 1:
			return chosenStudent.getFirstName();
		case 2:
			return chosenStudent.getVocation();
		case 3:
			return chosenStudent.getSpecialization();
		case 4:
			return chosenStudent.getBirthday();
		case 5:
			return chosenStudent.getBirthplace();
		case 6:
			return chosenStudent.getEmail();
		case 7:
			// JCheckBox retrainingCheckBox = new JCheckBox("",
			// chosenStudent.isRetraining());
			// return retrainingCheckBox;
			return chosenStudent.isRetraining();
		default:
			return "";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 7) {
			return Boolean.class;
		} else {
			return Object.class;
		}
	}

	@Override
	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Name";
		case 1:
			return "Vorname";
		case 2:
			return "Ausbildungsberufe";
		case 3:
			return "Vertiefungsrichtung";
		case 4:
			return "Geburtstag";
		case 5:
			return "Geburtsort";
		case 6:
			return "E-Mail";
		case 7:
			return "Umschüler";
		default:
			return "";
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
