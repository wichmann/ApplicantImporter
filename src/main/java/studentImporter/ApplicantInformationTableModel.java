package studentImporter;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ApplicantInformationTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2207776647966374826L;

	private List<Applicant> listOfApplicants;

	public ApplicantInformationTableModel(List<Applicant> listOfApplicants) {

		this.listOfApplicants = listOfApplicants;
	}

	/**
	 * Returns the Applicant object for a given row of the table containing all
	 * data of that applicant.
	 * 
	 * @param rowIndex
	 *            index of row to get Applicant data for
	 * @return Applicant object with applicant data
	 */
	public Applicant getApplicantForRow(int rowIndex) {
		return listOfApplicants.get(rowIndex);
	}

	@Override
	public int getRowCount() {
		return listOfApplicants.size();
	}

	@Override
	public int getColumnCount() {
		return 8;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Applicant chosenApplicant = listOfApplicants.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return chosenApplicant.getValue(DataField.LAST_NAME);
		case 1:
			return chosenApplicant.getValue(DataField.FIRST_NAME);
		case 2:
			return chosenApplicant.getValue(DataField.VOCATION);
		case 3:
			return chosenApplicant.getValue(DataField.SPECIALIZATION);
		case 4:
			return chosenApplicant.getValue(DataField.BIRTHDAY);
		case 5:
			return chosenApplicant.getValue(DataField.BIRTHPLACE);
		case 6:
			return chosenApplicant.getValue(DataField.EMAIL);
		case 7:
			return chosenApplicant.getValue(DataField.RETRAINING);
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
