package org.jbpm.console.ng.gc.client.experimental.customGrid;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class GridColumnsConfig {

	// Save some kind of grid identifier for future storage of specific grid column configurations.
	private String gridId;

	// Keep this key-ordered, for the purpose of applying the grid configuration as a whole, e.g. when returning to a previously customized table
	private Map<Integer, ColumnSettings> initialSettingsMap;
	private Map<Integer, ColumnSettings> columnSettingsMap = new TreeMap<Integer, ColumnSettings>();

	public GridColumnsConfig( String gridId, Map<Integer, ColumnSettings> settingsMap ) {
		this.gridId = gridId;
		this.columnSettingsMap = settingsMap;
		initialSettingsMap = cloneSettingsMap( columnSettingsMap );
	}

	// Get the ColumnSettings associated to the specified selectorIndex, if any.
	public ColumnSettings getColumnSettings( Integer selectorIndex ) {
		return columnSettingsMap.get( selectorIndex );
	}

	public void swapColumnSettings( Integer selectorIndex1, Integer selectorIndex2 ) {
		ColumnSettings columnSettings1 = columnSettingsMap.get( selectorIndex1 );
		columnSettingsMap.put( selectorIndex1, columnSettingsMap.put( selectorIndex2, columnSettings1 ) );
	}

	// Returns a Map<Integer,ColumnSettings>
	public Map<Integer,ColumnSettings> getColumnSettingsBySelectorIndex() {
		return Collections.unmodifiableMap( columnSettingsMap );
	}

	public Map<Integer,ColumnSettings> getInitialColumnSettingsBySelectorIndex() {
		return Collections.unmodifiableMap( initialSettingsMap );
	}

	public String getGridId() {
		return gridId;
	}

	// Reset the table configuration from the initial settings
	public void reset() {
		columnSettingsMap = cloneSettingsMap( initialSettingsMap );
	}

	// Clone a settings map
	private Map<Integer, ColumnSettings> cloneSettingsMap(Map<Integer, ColumnSettings> source) {
		Map<Integer, ColumnSettings> clone  =  new TreeMap<Integer, ColumnSettings>();
		for ( Map.Entry<Integer, ColumnSettings> entry : source.entrySet() ) {
			ColumnSettings origin = entry.getValue();
			ColumnSettings copy = new ColumnSettings();
			copy.setVisible( origin.isVisible() );
			copy.setColumnLabel( origin.getColumnLabel() );
			copy.setColumnWidth( origin.getColumnWidth() );
			copy.setCachedColumn( origin.getCachedColumn() );
			copy.setCachedColumnHeader( origin.getCachedColumnHeader() );
			copy.setCachedColumnFooter( origin.getCachedColumnFooter() );
			clone.put( entry.getKey(), copy );
		}
		return clone;
	}
}
