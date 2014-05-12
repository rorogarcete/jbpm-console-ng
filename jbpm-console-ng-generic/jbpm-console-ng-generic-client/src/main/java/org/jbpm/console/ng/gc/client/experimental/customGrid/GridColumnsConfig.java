package org.jbpm.console.ng.gc.client.experimental.customGrid;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class GridColumnsConfig {

	// Save some kind of grid identifier for future storage of specific grid column configurations.
	private String gridId;

	// Keep this key-ordered, for the purpose of applying the grid configuration as a whole, e.g. when returning to a previously customized table
	private TreeMap<Integer, ColumnSettings> columnSettingsMap = new TreeMap<Integer, ColumnSettings>();

	public GridColumnsConfig( String gridId ) {
		this.gridId = gridId;
	}

	// Get the ColumnSettings associated to the specified selectorIndex, if any.
	public ColumnSettings getColumnSettings( Integer selectorIndex ) {
		return columnSettingsMap.get( selectorIndex );
	}

	// Put the ColumnSettings to the specified selectorIndex and returns the previously associated value, if any.
	public void putColumnSettings( Integer selectorIndex, ColumnSettings columnSettings ) {
		columnSettingsMap.put( selectorIndex, columnSettings );
	}

	public void swapColumnSettings( Integer selectorIndex1, Integer selectorIndex2 ) {
		ColumnSettings columnSettings1 = columnSettingsMap.get( selectorIndex1 );
		columnSettingsMap.put( selectorIndex1, columnSettingsMap.put( selectorIndex2, columnSettings1 ) );
	}

	// Returns a Set of Map.Entry<Integer,ColumnSettings> objects
	public Set<Map.Entry<Integer,ColumnSettings>> getColumnSettingsBySelectorIndex() {
		return columnSettingsMap.entrySet();
	}

	public String getGridId() {
		return gridId;
	}
}
