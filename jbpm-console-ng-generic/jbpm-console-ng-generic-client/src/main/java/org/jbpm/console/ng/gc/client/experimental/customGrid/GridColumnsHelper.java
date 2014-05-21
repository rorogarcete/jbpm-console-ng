package org.jbpm.console.ng.gc.client.experimental.customGrid;

import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;

import java.util.*;

public class GridColumnsHelper {

	// TODO this won't do for clustered environments
	// Temporal 'storage' for grid configurations
	private static Map<String, GridColumnsConfig> gridColumnsConfigs = new HashMap<String, GridColumnsConfig>( 10 );

	private AbstractCellTable grid;

	private GridColumnsConfig gridColumnsConfig;

	private ColumnIndexMap indexMap;

	// Selector indexes of excluded columns
	private TreeSet<Integer> excludedColumns;

	public GridColumnsHelper( String gridId, AbstractCellTable grid ) {
		if ( grid != null ) {
			this.grid = grid;
			this.gridColumnsConfig = gridColumnsConfigs.get( gridId );
			if ( gridColumnsConfig == null )
				gridColumnsConfigs.put( gridId, gridColumnsConfig = initializeGridColumnsConfig( gridId, grid ) );

			indexMap = new ColumnIndexMap( grid.getColumnCount() );
			excludedColumns = new TreeSet<Integer>();
		}
	}

	/**
	 * Indicate columns that shouldn't be taken into account when building the column selector widget.
	 * @param gridColumnIndex The index of the column within the grid.
	 */
	public void excludeFromSelection( Integer gridColumnIndex ) {
		excludedColumns.add( indexMap.getSelectorIndexForGridColumn( gridColumnIndex ) );
	}

	public Set<Map.Entry<Integer,ColumnSettings>> getFilteredColumnSettingsBySelectorIndex() {
		Map<Integer,ColumnSettings> settingsMap = new TreeMap<Integer, ColumnSettings>( gridColumnsConfig.getColumnSettingsBySelectorIndex() );
		for ( Iterator<Integer> it = settingsMap.keySet().iterator(); it.hasNext(); ) {
			if (excludedColumns.contains( it.next() ) ) it.remove();
		}
		return settingsMap.entrySet();
	}

	public void saveGridColumnsConfig() {
		if ( gridColumnsConfig != null ) gridColumnsConfigs.put( gridColumnsConfig.getGridId(), gridColumnsConfig );
		// TODO persist, attach to user preferences, ...
	}

	// Apply to all columns
	public void applyGridColumnsConfig() {
		if ( gridColumnsConfig == null ) throw new RuntimeException( "Grid customization widget is not correctly configured!" );

		// Empty the table to redraw it completely (need to do maintain indexMap updated in the process, otherwise
		// when disabling columns, switching perspectives, then returning to the table and re-enabling a previously
		// disabled column, RuntimeExceptions might be thrown by the ColumnIndexMap.columnAdded/columnRemoved methods
		Map<Integer, ColumnSettings> columnSettings = gridColumnsConfig.getColumnSettingsBySelectorIndex();
		for ( Map.Entry<Integer, ColumnSettings> entry : columnSettings.entrySet() ) {
			int removeIndex = indexMap.columnDropped( entry.getKey() );
			grid.removeColumn( removeIndex );
		}
		grid.flush();
		// Now add the visible columns
		for ( Map.Entry<Integer, ColumnSettings> entry : columnSettings.entrySet() ) {
			int selectorIndex = entry.getKey();
			ColumnSettings settings = entry.getValue();
			if ( settings.isVisible() ) {
				indexMap.columnAdded( selectorIndex );
				int addIndex = indexMap.getGridIndexForSelectedColumn( selectorIndex );
				grid.insertColumn(  addIndex,
									settings.getCachedColumn(),
									settings.getCachedColumnHeader(),
									settings.getCachedColumnFooter() );
				grid.setColumnWidth( addIndex, settings.getColumnWidth() );
			}
		}
	}

	public void resetGrid() {

		indexMap.init();
		gridColumnsConfig.reset();

		for ( int i = grid.getColumnCount() - 1; i >= 0; i-- ) {
			grid.removeColumn( i );
		}
		grid.flush();
		// Now add the visible columns
		Map<Integer, ColumnSettings> columnSettings = gridColumnsConfig.getInitialColumnSettingsBySelectorIndex();
		for ( Map.Entry<Integer, ColumnSettings> entry : columnSettings.entrySet() ) {
			int selectorIndex = entry.getKey();
			ColumnSettings settings = entry.getValue();
			if ( settings.isVisible() ) {
				// Use selector index, is ok because we've reset the index map
				grid.insertColumn(  selectorIndex,
									settings.getCachedColumn(),
									settings.getCachedColumnHeader(),
									settings.getCachedColumnFooter() );
				grid.setColumnWidth( selectorIndex, settings.getColumnWidth() );
			}
		}
	}

	// Apply to one single column
	public void applyGridColumnConfig( int selectorIndex, boolean insertColumn ) {
		ColumnSettings columnSettings = gridColumnsConfig.getColumnSettings( selectorIndex );
		columnSettings.setVisible( insertColumn );

		if ( !insertColumn ) {
			int removeIndex = indexMap.columnDropped( selectorIndex );
			grid.removeColumn( removeIndex );
		} else {
			indexMap.columnAdded( selectorIndex );
			int addIndex = indexMap.getGridIndexForSelectedColumn( selectorIndex );
			grid.insertColumn(  addIndex,
					columnSettings.getCachedColumn(),
					columnSettings.getCachedColumnHeader(),
					columnSettings.getCachedColumnFooter() );
			grid.setColumnWidth( addIndex, columnSettings.getColumnWidth() );
		}
	}

	public void columnShiftedRight( int selectorIndex ) {
		// Double check, but shouldn't really occur
		if (indexMap.getActiveGridColumnsCount() > 1) {
			// To avoid over-complicating things, 1/ get the current grid indexes for the anchored columns
			TreeMap<Integer, Integer> stashedIndexes = stashAnchoredIndexes();

			// 2/ remove the anchored columns
			removeAnchored( stashedIndexes.keySet() );

			// 3/ perform the swap operation
			int nextValidSelectorIndex = indexMap.getNextValidSelectorIndex( selectorIndex );
			swapColumns( selectorIndex, nextValidSelectorIndex );

			// 4/ Finally, restore the anchored columns
			restoreAnchored( stashedIndexes );
		}
	}

	public void columnShiftedLeft( int selectorIndex ) {
		// Double check, but shouldn't really occur
		if (indexMap.getActiveGridColumnsCount() > 1) {
			// To avoid over-complicating things, 1/ get the current grid indexes for the anchored columns
			TreeMap<Integer, Integer> stashedIndexes = stashAnchoredIndexes();

			// 2/ remove the anchored columns
			removeAnchored( stashedIndexes.keySet() );

			// 3/ perform the swap operation
			int previousValidSelectorIndex = indexMap.getPreviousValidSelectorIndex( selectorIndex );
			swapColumns( previousValidSelectorIndex, selectorIndex );

			// 4/ Finally, restore the anchored columns
			restoreAnchored( stashedIndexes );
		}
	}

//	public GridColumnsConfig getGridColumnsConfig() {
//		return gridColumnsConfig;
//	}

	private void removeAnchored( Set<Integer> selectorIndexes ) {
		for ( Iterator<Integer> it = selectorIndexes.iterator(); it.hasNext(); ) {
			int formerGridIndex = indexMap.columnDropped( it.next() );
			grid.removeColumn( formerGridIndex );
		}
	}

	private void restoreAnchored( TreeMap<Integer, Integer> stashedIndexes ) {
		for ( Map.Entry<Integer, Integer> entry : stashedIndexes.entrySet() ) {
			ColumnSettings columnSettings = gridColumnsConfig.getColumnSettings( entry.getKey() );
			indexMap.columnAdded( entry.getKey() );
			grid.insertColumn(  entry.getValue(),
								columnSettings.getCachedColumn(),
								columnSettings.getCachedColumnHeader(),
								columnSettings.getCachedColumnFooter() );
			grid.setColumnWidth( entry.getValue(), columnSettings.getColumnWidth() );
		}
	}

	private TreeMap<Integer, Integer> stashAnchoredIndexes() {
		// key: selectorIndex, value: former grid index
		TreeMap<Integer, Integer> stashedIndexes = new TreeMap<Integer, Integer>();
		for ( Iterator<Integer> it = excludedColumns.iterator(); it.hasNext(); ) {
			int selectedIndex = it.next();
			stashedIndexes.put( selectedIndex, indexMap.getGridIndexForSelectedColumn( selectedIndex ) );
		}
		return stashedIndexes;
	}

//	private boolean columnIsExcluded( int selectorIndex ) {
//		return excludedColumns.contains( selectorIndex );
//	}

	private void swapColumns( int selectorIndex1, int selectorIndex2 ) {
		// TODO columns are being swapped, not added or dropped, is it necessary to mantain consistency with the indexMap?
		// TODO (i.e. call colmnAdded/columnRemoved) since at the end of the operation it'll be consistent again anyway ?
//		if ( selectorIndex2 < selectorIndex1 ) swapColumns( selectorIndex2, selectorIndex1 );
		int originalGridIndexForSelectorIndex1 = indexMap.getGridIndexForSelectedColumn( selectorIndex1 );
		int originalGridIndexForSelectorIndex2 = indexMap.getGridIndexForSelectedColumn( selectorIndex2 );

		boolean isRollOverSwap = Math.abs( (originalGridIndexForSelectorIndex1 - originalGridIndexForSelectorIndex2) ) > 1;

		ColumnSettings columnSettings1 = gridColumnsConfig.getColumnSettings( selectorIndex1 );

		// Adapt and redraw the data grid (don't change the order of the following calls)
		grid.removeColumn( originalGridIndexForSelectorIndex1 );
		if (isRollOverSwap) {
			grid.removeColumn( originalGridIndexForSelectorIndex2 );
		}
		grid.insertColumn(  originalGridIndexForSelectorIndex2,
				columnSettings1.getCachedColumn(),
				columnSettings1.getCachedColumnHeader(),
				columnSettings1.getCachedColumnFooter() );
		grid.setColumnWidth( originalGridIndexForSelectorIndex2, columnSettings1.getColumnWidth() );

		if (isRollOverSwap) {
			ColumnSettings columnSettings2 = gridColumnsConfig.getColumnSettings( selectorIndex2 );
			grid.insertColumn(  originalGridIndexForSelectorIndex1,
					columnSettings2.getCachedColumn(),
					columnSettings2.getCachedColumnHeader(),
					columnSettings2.getCachedColumnFooter() );
			grid.setColumnWidth( originalGridIndexForSelectorIndex1, columnSettings2.getColumnWidth() );
		}

		// Switch columnsettings in gridColumnsConfig
		gridColumnsConfig.swapColumnSettings( selectorIndex1, selectorIndex2 );
	}

	private GridColumnsConfig initializeGridColumnsConfig( String gridId, AbstractCellTable grid ) {

		Map<Integer, ColumnSettings> settingsMap = new TreeMap<Integer, ColumnSettings>();
		int maxIndex = grid.getColumnCount() - 1;
		for ( int i = 0; i <= maxIndex ; i++ ) {
			ColumnSettings settings = new ColumnSettings();

			settings.setVisible( true );
			settings.setColumnLabel( ( String ) grid.getHeader( i ).getValue() );

			Column<?, ?> column = grid.getColumn( i );
			settings.setCachedColumn( column );
			settings.setColumnWidth( grid.getColumnWidth( column ) );

			settings.setCachedColumnHeader( grid.getHeader( i ) );
			settings.setCachedColumnFooter( grid.getFooter( i ) );

			settingsMap.put( i, settings );
		}
		return new GridColumnsConfig( gridId, settingsMap );
	}

	//TODO implement some kind of adequate test for this
	private class ColumnIndexMap {
		private int maxIndex;
		private int[] selectorIndexes;
		private int[] gridIndexes;
		// Selector indexes by grid index
		private Map<Integer, Integer> gridSelectorMap;

		private ColumnIndexMap( int maxIndex ) {
			this.maxIndex = maxIndex;
			gridSelectorMap = new HashMap<Integer, Integer>( maxIndex );
			init();
		}

		/**
		 * This method will look from the index passed in onwards to find the next valid column-switching candidate.
		 * If none is found, it will roll around to the beginning and continue looking until reaching the index passed.
		 */
		//todo las columnas 'excluidas' no deben ser tenidas en cuenta tampoco a la hora de calcular next o previous index
		//todo pero tampoco no se les puede poner a -1, porque siguen siendo columnas validas a nivel de tabla
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

		private int getSelectorIndexForGridColumn( int gridIndex ) {
			Integer index = gridSelectorMap.get( gridIndex );
			if ( index == null ) throw new RuntimeException( "Internal error: selector index corresponding to grid index: " + gridIndex + " not set" );
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
			calculateGridSelectorMap();
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
			calculateGridSelectorMap();
			return former;
		}

		private void calculateGridSelectorMap() {
			gridSelectorMap.clear();
			for ( int i = 0; i < selectorIndexes.length; i++ ) {
				int gridIndex = gridIndexes[i];
				if (gridIndex != -1) gridSelectorMap.put( gridIndex, i );
			}
		}

		private void init() {
			selectorIndexes = new int[maxIndex];
			gridIndexes = new int[maxIndex];
			for ( int i = 0; i < maxIndex; i++ ) {
				selectorIndexes[i] = i;
				gridIndexes[i] = i;
			}
			calculateGridSelectorMap();
		}
	}
}
