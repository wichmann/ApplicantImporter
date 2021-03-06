package de.ichmann.applicant_importer.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Class to manage the widths of columns in a table.
 *
 * Various properties control how the width of the column is calculated. Another property controls
 * whether column width calculation should be dynamic. Finally, various Actions will be added to the
 * table to allow the user to customize the functionality.
 *
 * This class was designed to be used with tables that use an auto resize mode of AUTO_RESIZE_OFF.
 * With all other modes you are constrained as the width of the columns must fit inside the table.
 * So if you increase one column, one or more of the other columns must decrease. Because of this
 * the resize mode of RESIZE_ALL_COLUMNS will work the best.
 *
 * Source: https://tips4java.wordpress.com/2008/11/10/table-column-adjuster/
 */
public final class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {
    private static final int DEFAULT_SPACING = 6;
    private JTable table;
    private int spacing;
    private boolean isColumnHeaderIncluded;
    private boolean isColumnDataIncluded;
    private boolean isOnlyAdjustLarger;
    private boolean isDynamicAdjustment;
    private Map<TableColumn, Integer> columnSizes = new HashMap<TableColumn, Integer>();

    /**
     * Specify the table and use default spacing.
     *
     * @param table
     *            table to be adjusted
     */
    public TableColumnAdjuster(final JTable table) {
        this(table, DEFAULT_SPACING);
    }

    /**
     * Specify the table and spacing.
     *
     * @param table
     *            table to be adjusted
     * @param spacing
     *            spacing between columns
     */
    public TableColumnAdjuster(final JTable table, final int spacing) {
        this.table = table;
        this.spacing = spacing;
        setColumnHeaderIncluded(true);
        setColumnDataIncluded(true);
        setOnlyAdjustLarger(false);
        setDynamicAdjustment(false);
        installActions();
    }

    /**
     * Adjust the widths of all the columns in the table.
     */
    public void adjustColumns() {
        TableColumnModel tcm = table.getColumnModel();

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            adjustColumn(i);
        }
    }

    /**
     * Adjust the width of the specified column in the table.
     *
     * @param column
     *            column to be adjusted
     */
    public void adjustColumn(final int column) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if (!tableColumn.getResizable()) {
            return;
        }

        int columnHeaderWidth = getColumnHeaderWidth(column);
        int columnDataWidth = getColumnDataWidth(column);
        int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);

        updateTableColumn(column, preferredWidth);
    }

    /**
     * Calculated the width based on the column name.
     *
     * @param column
     *            column of the table
     * @return column header width
     */
    private int getColumnHeaderWidth(final int column) {
        if (!isColumnHeaderIncluded) {
            return 0;
        }

        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();

        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }

        Component c = renderer
                .getTableCellRendererComponent(table, value, false, false, -1, column);
        return c.getPreferredSize().width;
    }

    /**
     * Calculate the width based on the widest cell renderer for the given column.
     *
     * @param column
     *            column of the table
     * @return column data width
     */
    private int getColumnDataWidth(final int column) {
        if (!isColumnDataIncluded) {
            return 0;
        }

        int preferredWidth = 0;
        int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();

        for (int row = 0; row < table.getRowCount(); row++) {
            preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));

            // We've exceeded the maximum width, no need to check other rows

            if (preferredWidth >= maxWidth) {
                break;
            }
        }

        return preferredWidth;
    }

    /**
     * Get the preferred width for the specified cell.
     *
     * @param row
     *            row of the table
     * @param column
     *            column of the table
     * @return cell data width
     */
    private int getCellDataWidth(final int row, final int column) {
        // Invoke the renderer for the cell to calculate the preferred width

        TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
        Component c = table.prepareRenderer(cellRenderer, row, column);
        int width = c.getPreferredSize().width + table.getIntercellSpacing().width;

        return width;
    }

    /**
     * Update the TableColumn with the newly calculated width.
     *
     * @param width
     *            width of the table
     * @param column
     *            column of the table
     */
    private void updateTableColumn(final int column, final int width) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if (!tableColumn.getResizable()) {
            return;
        }

        int newWidth = width;
        newWidth += spacing;

        // Don't shrink the column width

        if (isOnlyAdjustLarger) {
            newWidth = Math.max(newWidth, tableColumn.getPreferredWidth());
        }

        columnSizes.put(tableColumn, new Integer(tableColumn.getWidth()));

        table.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(newWidth);
    }

    /**
     * Restore the widths of the columns in the table to its previous width.
     */
    public void restoreColumns() {
        TableColumnModel tcm = table.getColumnModel();

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            restoreColumn(i);
        }
    }

    /**
     * Restore the width of the specified column to its previous width.
     *
     * @param column
     *            column of the table
     */
    private void restoreColumn(final int column) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        Integer width = columnSizes.get(tableColumn);

        if (width != null) {
            table.getTableHeader().setResizingColumn(tableColumn);
            tableColumn.setWidth(width.intValue());
        }
    }

    /**
     * Indicates whether to include the header in the width calculation.
     *
     * @param isColumnHeaderIncluded
     *            whether to include column header
     */
    public void setColumnHeaderIncluded(final boolean isColumnHeaderIncluded) {
        this.isColumnHeaderIncluded = isColumnHeaderIncluded;
    }

    /**
     * Indicates whether to include the model data in the width calculation.
     *
     * @param isColumnDataIncluded
     *            whether to include column data
     */
    public void setColumnDataIncluded(final boolean isColumnDataIncluded) {
        this.isColumnDataIncluded = isColumnDataIncluded;
    }

    /**
     * Indicates whether columns can only be increased in size.
     *
     * @param isOnlyAdjustLarger
     *            whether to increase columns
     */
    public void setOnlyAdjustLarger(final boolean isOnlyAdjustLarger) {
        this.isOnlyAdjustLarger = isOnlyAdjustLarger;
    }

    /**
     * Indicate whether changes to the model should cause the width to be dynamically recalculated.
     *
     * @param isDynamicAdjustment
     *            whether to recalculate dynamically
     */
    public void setDynamicAdjustment(final boolean isDynamicAdjustment) {
        // May need to add or remove the TableModelListener when changed

        if (this.isDynamicAdjustment != isDynamicAdjustment) {
            if (isDynamicAdjustment) {
                table.addPropertyChangeListener(this);
                table.getModel().addTableModelListener(this);
            } else {
                table.removePropertyChangeListener(this);
                table.getModel().removeTableModelListener(this);
            }
        }

        this.isDynamicAdjustment = isDynamicAdjustment;
    }

    /**
     * Implement the PropertyChangeListener.
     */
    @Override
    public void propertyChange(final PropertyChangeEvent e) {
        // When the TableModel changes we need to update the listeners
        // and column widths

        if ("model".equals(e.getPropertyName())) {
            TableModel model = (TableModel) e.getOldValue();
            model.removeTableModelListener(this);

            model = (TableModel) e.getNewValue();
            model.addTableModelListener(this);
            adjustColumns();
        }
    }

    /**
     * Implement the TableModelListener.
     */
    @Override
    public void tableChanged(final TableModelEvent e) {
        if (!isColumnDataIncluded) {
            return;
        }

        // A cell has been updated
        if (e.getType() == TableModelEvent.UPDATE) {
            int column = table.convertColumnIndexToView(e.getColumn());

            // Only need to worry about an increase in width for this cell

            if (isOnlyAdjustLarger) {
                int row = e.getFirstRow();
                TableColumn tableColumn = table.getColumnModel().getColumn(column);

                if (tableColumn.getResizable()) {
                    int width = getCellDataWidth(row, column);
                    updateTableColumn(column, width);
                }
            } else {
                // Could be an increase of decrease so check all rows
                adjustColumn(column);
            }
        } else {
            // The update affected more than one column so adjust all columns
            adjustColumns();
        }
    }

    /**
     * Install Actions to give user control of certain functionality.
     */
    private void installActions() {
        installColumnAction(true, true, "adjustColumn", "control ADD");
        installColumnAction(false, true, "adjustColumns", "control shift ADD");
        installColumnAction(true, false, "restoreColumn", "control SUBTRACT");
        installColumnAction(false, false, "restoreColumns", "control shift SUBTRACT");

        installToggleAction(true, false, "toggleDynamic", "control MULTIPLY");
        installToggleAction(false, true, "toggleLarger", "control DIVIDE");
    }

    /**
     * Update the input and action maps with a new ColumnAction.
     */
    private void installColumnAction(final boolean isSelectedColumn, final boolean isAdjust,
            final String key, final String keyStroke) {
        Action action = new ColumnAction(isSelectedColumn, isAdjust);
        KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        table.getInputMap().put(ks, key);
        table.getActionMap().put(key, action);
    }

    /**
     * Update the input and action maps with new ToggleAction.
     */
    private void installToggleAction(final boolean isToggleDynamic, final boolean isToggleLarger,
            final String key, final String keyStroke) {
        Action action = new ToggleAction(isToggleDynamic, isToggleLarger);
        KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        table.getInputMap().put(ks, key);
        table.getActionMap().put(key, action);
    }

    /**
     * Action to adjust or restore the width of a single column or all columns.
     */
    class ColumnAction extends AbstractAction {
        private static final long serialVersionUID = 1934489249767212448L;

        private boolean isSelectedColumn;
        private boolean isAdjust;

        /**
         * Instantiates a new column action.
         *
         * @param isSelectedColumn
         *            ???
         * @param isAdjust
         *            ???
         */
        public ColumnAction(final boolean isSelectedColumn, final boolean isAdjust) {
            this.isSelectedColumn = isSelectedColumn;
            this.isAdjust = isAdjust;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            // Handle selected column(s) width change actions

            if (isSelectedColumn) {
                int[] columns = table.getSelectedColumns();

                for (int i = 0; i < columns.length; i++) {
                    if (isAdjust) {
                        adjustColumn(columns[i]);
                    } else {
                        restoreColumn(columns[i]);
                    }
                }
            } else {
                if (isAdjust) {
                    adjustColumns();
                } else {
                    restoreColumns();
                }
            }
        }
    }

    /**
     * Toggle properties of the TableColumnAdjuster so the user can customize the functionality to
     * their preferences.
     */
    class ToggleAction extends AbstractAction {
        private static final long serialVersionUID = 6714305016228352327L;

        private boolean isToggleDynamic;
        private boolean isToggleLarger;

        /**
         * Instantiates a new toggle action.
         *
         * @param isToggleDynamic
         *            whether the toggle should be dynamic
         * @param isToggleLarger
         *            whether to toggle larger
         */
        public ToggleAction(final boolean isToggleDynamic, final boolean isToggleLarger) {
            this.isToggleDynamic = isToggleDynamic;
            this.isToggleLarger = isToggleLarger;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (isToggleDynamic) {
                setDynamicAdjustment(!isDynamicAdjustment);
                return;
            }

            if (isToggleLarger) {
                setOnlyAdjustLarger(!isOnlyAdjustLarger);
                return;
            }
        }
    }
}
