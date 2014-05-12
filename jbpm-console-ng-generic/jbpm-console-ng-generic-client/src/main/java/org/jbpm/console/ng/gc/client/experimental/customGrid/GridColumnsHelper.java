package org.jbpm.console.ng.gc.client.experimental.customGrid;

import com.google.gwt.user.cellview.client.Column;
import com.github.gwtbootstrap.client.ui.DataGrid;

import java.util.HashMap;
import java.util.Map;

public class GridColumnsHelper {

	// Temporal 'storage' for grid configurations
	private static Map<String, GridColumnsConfig> gridColumnsConfigs = new HashMap<String, GridColumnsConfig>( 10 );

	private DataGrid dataGrid;

	private GridColumnsConfig gridColumnsConfig;

	private ColumnIndexMap indexMap;

	public GridColumnsHelper( String gridId, DataGrid dataGrid ) {
		this.dataGrid = dataGrid;
		this.gridColumnsConfig = gridColumnsConfigs.get( gridId );
		if ( gridColumnsConfig == null )
			gridColumnsConfigs.put( gridId, gridColumnsConfig = initializeGridColumnsConfig( gridId, dataGrid ) );

		indexMap = new ColumnIndexMap( dataGrid.getColumnCount() );
	}

	public void saveGridColumnsConfig() {
		if ( gridColumnsConfig != null ) gridColumnsConfigs.put( gridColumnsConfig.getGridId(), gridColumnsConfig );
		// TODO persist, attach to user preferences, ...
	}

	// Apply to all columns
	public void applyGridColumnsConfig() {
		if ( gridColumnsConfig == null ) throw new RuntimeException( "Grid customization widget is not correctly configured!" );
		// Empty the table to redraw it completely
		for ( int i = dataGrid.getColumnCount() - 1; i >= 0; i-- ) {
			dataGrid.removeColumn( i );
		}
		dataGrid.flush();
		for ( Map.Entry<Integer, ColumnSettings> entry : gridColumnsConfig.getColumnSettingsBySelectorIndex() ) {
			ColumnSettings settings = entry.getValue();
			if ( entry.getValue().isVisible() ) dataGrid.addColumn( settings.getCachedColumn(), settings.getCachedColumnHeader(), settings.getCachedColumnFooter() );
		}
		dataGrid.redraw();
	}

	// Apply to one single column
	public void applyGridColumnConfig( int selectorIndex, boolean insertColumn ) {
		ColumnSettings columnSettings = gridColumnsConfig.getColumnSettings( selectorIndex );
		columnSettings.setVisible( insertColumn );

		if ( !insertColumn ) {
			int removeIndex = indexMap.columnDropped( selectorIndex );
			dataGrid.removeColumn( removeIndex );
		} else {
			indexMap.columnAdded( selectorIndex );
			int addIndex = indexMap.getGridIndexForSelectedColumn( selectorIndex );
			dataGrid.insertColumn(  addIndex,
					columnSettings.getCachedColumn(),
					columnSettings.getCachedColumnHeader(),
					columnSettings.getCachedColumnFooter() );
			dataGrid.setColumnWidth( addIndex, columnSettings.getColumnWidth() );
		}
		// TODO leave data grid redrawing up to the caller?
		dataGrid.redraw();
	}

	public void columnShiftedRight( int selectorIndex ) {
		// Double check, but shouldn't really occur
		if (indexMap.getActiveGridColumnsCount() > 1) {
			int nextValidSelectorIndex = indexMap.getNextValidSelectorIndex( selectorIndex );
			swapColumns( selectorIndex, nextValidSelectorIndex );
		}
	}

	public void columnShiftedLeft( int selectorIndex ) {
		// Double check, but shouldn't really occur
		if (indexMap.getActiveGridColumnsCount() > 1) {
			int previousValidSelectorIndex = indexMap.getPreviousValidSelectorIndex( selectorIndex );
			swapColumns( previousValidSelectorIndex, selectorIndex );
		}
	}

	public GridColumnsConfig getGridColumnsConfig() {
		return gridColumnsConfig;
	}

	private void swapColumns( int selectorIndex1, int selectorIndex2 ) {
		// TODO columns are being swapped, not added or dropped, is it necessary to mantain consistency with the indexMap?
		// TODO (i.e. call colmnAdded/columnRemoved) since at the end of the operation it'll be consistent again anyway ?
//		if ( selectorIndex2 < selectorIndex1 ) swapColumns( selectorIndex2, selectorIndex1 );
		int originalGridIndexForSelectorIndex1 = indexMap.getGridIndexForSelectedColumn( selectorIndex1 );
		int originalGridIndexForSelectorIndex2 = indexMap.getGridIndexForSelectedColumn( selectorIndex2 );

		boolean isRollOverSwap = Math.abs( (originalGridIndexForSelectorIndex1 - originalGridIndexForSelectorIndex2) ) > 1;

		ColumnSettings columnSettings1 = gridColumnsConfig.getColumnSettings( selectorIndex1 );

		// Adapt and redraw the data grid (don't change the order of the following calls)
		dataGrid.removeColumn( originalGridIndexForSelectorIndex1 );
		if (isRollOverSwap) {
			dataGrid.removeColumn( originalGridIndexForSelectorIndex2 );
		}
		dataGrid.insertColumn(  originalGridIndexForSelectorIndex2,
				columnSettings1.getCachedColumn(),
				columnSettings1.getCachedColumnHeader(),
				columnSettings1.getCachedColumnFooter() );
		dataGrid.setColumnWidth( originalGridIndexForSelectorIndex2, columnSettings1.getColumnWidth() );

		if (isRollOverSwap) {
			ColumnSettings columnSettings2 = gridColumnsConfig.getColumnSettings( selectorIndex2 );
			dataGrid.insertColumn(  originalGridIndexForSelectorIndex1,
					columnSettings2.getCachedColumn(),
					columnSettings2.getCachedColumnHeader(),
					columnSettings2.getCachedColumnFooter() );
			dataGrid.setColumnWidth( originalGridIndexForSelectorIndex1, columnSettings2.getColumnWidth() );
		}

		dataGrid.redraw();

		// Switch columnsettings in gridColumnsConfig
		gridColumnsConfig.swapColumnSettings( selectorIndex1, selectorIndex2 );
	}

	private GridColumnsConfig initializeGridColumnsConfig( String gridId, DataGrid dataGrid ) {

		GridColumnsConfig gridColumnsConfig = new GridColumnsConfig( gridId );
		int maxIndex = dataGrid.getColumnCount() - 1;
		for ( int i = 0; i <= maxIndex ; i++ ) {
			ColumnSettings settings = new ColumnSettings();

//			settings.setSelectorIndex( i );
//			int nextValidIndex = i < maxIndex ? i + 1 : 0;
//			settings.setNextValidSelectorIndex( nextValidIndex );
//			int previousValidIndex = i == 0 ? maxIndex : i - 1;
//			settings.setPreviousValidSelectorIndex( previousValidIndex );
//			settings.setGridIndex( i );

			settings.setVisible( true );
			settings.setColumnLabel( ( String ) dataGrid.getHeader( i ).getValue() );

			Column<?, ?> column = dataGrid.getColumn( i );
			settings.setCachedColumn( column );
			settings.setColumnWidth( dataGrid.getColumnWidth( column ) );

			settings.setCachedColumnHeader( dataGrid.getHeader( i ) );
			settings.setCachedColumnFooter( dataGrid.getFooter( i ) );

			gridColumnsConfig.putColumnSettings( i, settings );
		}
		return gridColumnsConfig;
	}

	//TODO implement some kind of adequate test for this
	private class ColumnIndexMap {
		private int[] selectorIndexes;
		private int[] gridIndexes;

		private ColumnIndexMap( int maxIndex ) {
			selectorIndexes = new int[maxIndex];
			gridIndexes = new int[maxIndex];
			for ( int i = 0; i < maxIndex; i++ ) {
				selectorIndexes[i] = i;
				gridIndexes[i] = i;
			}
		}

		/**
		 * This method will look from the index passed in onwards to find the next valid column-switching candidate.
		 * If none is found, it will roll around to the beginning and continue looking until reaching the index passed.
		 */
		private int getNextValidSelectorIndex( int fromSelectedColumnIndex ) {
			int nextValid = fromSelectedColumnIndex + 1;
			// step upwards from position following selected index to end
			while ( nextValid < selectorIndexes.length ) {
				if ( gridIndexes[nextValid] != -1 ) return nextValid;
				nextValid++;
			}

			// if none found step upwards from start up to selected index
			nextValid = 0;
			while ( nextValid < fromSelectedColumnIndex ) {
				if ( gridIndexes[nextValid] != -1 ) return nextValid;
				nextValid++;
			}
			// If no valid next selector index is found, then this means that somebody was allowed to shift a column left or right
			// when he really shouldn't have been (e.g. when only 1 column is being shown)
			throw new RuntimeException( "No next valid selector index was found!" );
		}

		/**
		 * This method will look from the index passed in backwards to find the previous valid column-switching candidate.
		 * If none is found, it will roll around to the end and continue looking until reaching the index passed.
		 */
		private int getPreviousValidSelectorIndex( int fromSelectedColumnIndex ) {
			int previousValid = fromSelectedColumnIndex - 1;
			// step downwards from position previous to selected index to start
			while ( previousValid >= 0 ) {
				if ( gridIndexes[previousValid] != -1 ) return previousValid;
				previousValid--;
			}

			// if none found step upwards from start up to selected index
			previousValid = selectorIndexes.length - 1;
			while ( previousValid > fromSelectedColumnIndex ) {
				if ( gridIndexes[previousValid] != -1 ) return previousValid;
				previousValid--;
			}
			// If no valid next selector index is found, then this means that somebody was allowed to shift a column left or right
			// when he really shouldn't have been (e.g. when only 1 column is being shown)
			throw new RuntimeException( "No previous valid selector index was found!" );
		}

		private int getActiveGridColumnsCount() {
			int count = 0;
			for ( int i = 0; i < selectorIndexes.length; i++ ) {
				if ( gridIndexes[i] != -1 ) count++;
			}
			return count;
		}

		private int getGridIndexForSelectedColumn( int selectorIndex ) {
			int index = gridIndexes[selectorIndex];
			if ( index == -1 ) throw new RuntimeException( "Internal error: column index corresponding to selected index is unset" );
			return index;
		}

		// Adjust the gridIndexes for adding the specified column
		private void columnAdded( int selectorIndex ) {
			int current = gridIndexes[selectorIndex];
			if ( current != -1 )
				throw new RuntimeException( "Internal error: index to be added (" + selectorIndex + ") was internally still set (" + current + "). " +
						"Probably the widget's client forgot to call applyGridColumnsConfig()." );
			// Find the grid index that should be set to the column that is becoming visible, 0 if the selector index is 0.
			int nextValidGridIndexValue = 0;
			for ( int i = selectorIndex - 1; i >= 0 ; i-- ) {
				if (gridIndexes[i] != -1) {
					nextValidGridIndexValue = gridIndexes[i] + 1;
					break;
				}
			}
			// Assign new gridIndex to the 'new' column, and bump all the indexes of following visible columns' up one
			for ( int i = selectorIndex; i < selectorIndexes.length; i++ ) {
				if ( i == selectorIndex ) gridIndexes[i] = nextValidGridIndexValue;
				else if ( gridIndexes[i] != -1 ) gridIndexes[i] = gridIndexes[i] + 1;
			}
		}

		// Adjust the gridIndexes for removing the specified column, return the former grid index of the selected column
		private int columnDropped( int selectorIndex ) {
			int former = gridIndexes[selectorIndex];
			int counter = former;
			if ( former == -1 )
				throw new RuntimeException( "Internal error: index to be dropped (" + selectorIndex + ") was not set (" + former + "). " +
						"Probably the widget's client forgot to call applyGridColumnsConfig()." );
			// Assign gridIndex -1 to the to dropped column, and bump all the indexes of following visible columns' down one
			for ( int i = selectorIndex; i < selectorIndexes.length; i++ ) {
				if ( i == selectorIndex ) gridIndexes[i] = -1;
				else if ( gridIndexes[i] != -1 ) {
					gridIndexes[i] = counter++;
				}
			}
			return former;
		}
	}
}
