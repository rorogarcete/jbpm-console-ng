package org.jbpm.console.ng.gc.client.experimental.customGrid;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;

// TODO adapt this for non-string headers
public class ColumnSettings {

//	private int selectorIndex;
//	private int previousValidSelectorIndex;
//	private int nextValidSelectorIndex;
//	private int gridIndex;

	private boolean isVisible;
	private String columnLabel;
	private String columnWidth;

	private Column<?, ?> cachedColumn;
	private Header<?> cachedColumnHeader;
	private Header<?> cachedColumnFooter;

	public ColumnSettings(){}

//	public int getSelectorIndex() {
//		return selectorIndex;
//	}
//
//	public void setSelectorIndex( int selectorIndex ) {
//		this.selectorIndex = selectorIndex;
//	}
//
//	public int getNextValidSelectorIndex() {
//		return nextValidSelectorIndex;
//	}
//
//	public void setNextValidSelectorIndex( int nextValidSelectorIndex ) {
//		this.nextValidSelectorIndex = nextValidSelectorIndex;
//	}
//
//	public int getPreviousValidSelectorIndex() {
//		return previousValidSelectorIndex;
//	}
//
//	public void setPreviousValidSelectorIndex( int previousValidSelectorIndex ) {
//		this.previousValidSelectorIndex = previousValidSelectorIndex;
//	}
//
//	public int getGridIndex() {
//		return gridIndex;
//	}
//
//	public void setGridIndex( int gridIndex ) {
//		this.gridIndex = gridIndex;
//	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible( boolean visible ) {
		isVisible = visible;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel( String columnLabel ) {
		this.columnLabel = columnLabel;
	}

	public String getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth( String columnWidth ) {
		this.columnWidth = columnWidth;
	}

	public Column<?, ?> getCachedColumn() {
		return cachedColumn;
	}

	public void setCachedColumn( Column<?, ?> cachedColumn ) {
		this.cachedColumn = cachedColumn;
	}

	public Header<?> getCachedColumnFooter() {
		return cachedColumnFooter;
	}

	public void setCachedColumnFooter( Header<?> cachedColumnFooter ) {
		this.cachedColumnFooter = cachedColumnFooter;
	}

	public Header<?> getCachedColumnHeader() {
		return cachedColumnHeader;
	}

	public void setCachedColumnHeader( Header<?> cachedColumnHeader ) {
		this.cachedColumnHeader = cachedColumnHeader;
	}
}
