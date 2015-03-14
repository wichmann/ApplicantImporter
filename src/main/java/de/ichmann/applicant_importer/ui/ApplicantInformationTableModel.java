package de.ichmann.applicant_importer.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.ichmann.applicant_importer.model.Applicant;
import de.ichmann.applicant_importer.model.DataField;

public class ApplicantInformationTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2207776647966374826L;

	private List<Applicant> listOfApplicants;

	private Map<Integer, DataField> columns;

	public ApplicantInformationTableModel(List<Applicant> listOfApplicants) {

		this.listOfApplicants = listOfApplicants;
		columns = new HashMap<>();
		columns.put(0, DataField.LAST_NAME);
		columns.put(1, DataField.FIRST_NAME);
		columns.put(2, DataField.BIRTHDAY);
		columns.put(3, DataField.VOCATION);
		columns.put(4, DataField.SPECIALIZATION);
		columns.put(5, DataField.COMPANY_NAME);
		columns.put(6, DataField.RETRAINING);
	}

	/**
	 * Returns the Applicant object for a given row of the table containing all data of that applicant.
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
		return columns.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		assert columnIndex < columns.size() : "Given column does not exists!";
		Applicant chosenApplicant = listOfApplicants.get(rowIndex);
		return chosenApplicant.getValue(columns.get(columnIndex));
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columns.get(columnIndex).getTypeOfDataField();
	}

	@Override
	public String getColumnName(int col) {
		return columns.get(col).getDescription();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void removeRow(int row) {
		listOfApplicants.remove(row);
		fireTableDataChanged();
	}
}
