package org.jbpm.console.ng.gc.client.gridexp;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.github.gwtbootstrap.client.ui.DataGrid;

import java.util.HashMap;

public class GridColumnsSetup {//TODO rename to GridColumnHelper

    private HashMap<Integer, ColumnSetup> columnSetupCollection = new HashMap<Integer, ColumnSetup>(10);

    private ColumnIndexHelper helper;

    public GridColumnsSetup( DataGrid dataGrid ) {
        for (int i = 0; i < dataGrid.getColumnCount(); i++) {
            Column<?, ?> column = dataGrid.getColumn(i);
            columnSetupCollection.put(
                            i,
                            new ColumnSetup( column,
                                             dataGrid.getHeader(i),
                                             dataGrid.getFooter(i),
                                             dataGrid.getColumnWidth(column) )
                           );
            helper = new ColumnIndexHelper( columnSetupCollection.size() );
        }
    }

    /**
     * Notifies the helper that a column is about to be removed
     * @param selectedColumnIndex The selector's index
     * @return The column that is about to be removed's true index within the data-grid
     */
    public int notifyColumnToBeRemoved( int selectedColumnIndex ) {
        return helper.indexDropped( selectedColumnIndex );
    }

    /**
     * Notifies the helper that a column is about to be added
     * @param selectedColumnIndex The selector's index
     * @return The index of the column before which the column that is about to be inserted should be placed.
     */
    public int notifyColumnToBeAdded( int selectedColumnIndex ) {
        return helper.indexAdded( selectedColumnIndex );
    }

    public String getColumnWidth( int cacheIndex ) {
        ColumnSetup columnSetup = columnSetupCollection.get( cacheIndex );
        return columnSetup != null ? columnSetup.getColumnWidth() : "";
    }

    public Header<?> getColumnHeader( int cacheIndex ) {
        ColumnSetup columnSetup = columnSetupCollection.get( cacheIndex );
        return columnSetup != null ? columnSetup.getColumnHeader() : null;
    }

    public Header<?> getColumnFooter( int cacheIndex ) {
        ColumnSetup columnSetup = columnSetupCollection.get( cacheIndex );
        return columnSetup != null ? columnSetup.getColumnFooter() : null;
    }

    public Column<?, ?> getColumn( int cacheIndex ) {
        ColumnSetup columnSetup = columnSetupCollection.get( cacheIndex );
        return columnSetup != null ? columnSetup.getColumn() : null;
    }

    //TODO implement some kind of adequate test for this
    private class ColumnIndexHelper {
        private int[] selectorIndexes;
        private int[] gridIndexes;
        private ColumnIndexHelper( int maxIndex ) {
            selectorIndexes = new int[maxIndex];
            gridIndexes = new int[maxIndex];
            for (int i = 0; i < maxIndex; i++) {
                selectorIndexes[i] = i;
                gridIndexes[i] = i;
            }
        }
        private int indexDropped( int selectedColumnIndex ) {
            int current = gridIndexes[selectedColumnIndex];
            int counter = current;
            if ( current == -1 ) throw new RuntimeException( "Internal error: index to be dropped (" + selectedColumnIndex + ") was not set (" + current + "). Something went wrong." );
            for ( int i = selectedColumnIndex; i < selectorIndexes.length; i++ ) {
                if ( i == selectedColumnIndex ) gridIndexes[i] = -1;
                else if (gridIndexes[i] != -1) {
                    gridIndexes[i] = counter++;
                }
            }
            return current;
        }
        // Returns the CURRENT data-grid index BEFORE which the new column is to be inserted
        private int indexAdded( int selectedColumnIndex ) {
            int current = gridIndexes[selectedColumnIndex];
            if (current != -1) throw new RuntimeException("Internal error: index to be added (" + selectedColumnIndex + ") was internally still set (" + current + "). Something went wrong.");
            int nextValidIndex = 0;
            for (int i = 0; i < selectedColumnIndex; i++) {
                if (gridIndexes[i] != -1) nextValidIndex = gridIndexes[i] + 1;
            }
            for (int i = selectedColumnIndex; i < selectorIndexes.length; i++) {
                if ( i == selectedColumnIndex ) gridIndexes[i] = nextValidIndex;
                else if (gridIndexes[i] != -1) gridIndexes[i] = gridIndexes[i] + 1;
            }
            return nextValidIndex;
        }
    }
}
