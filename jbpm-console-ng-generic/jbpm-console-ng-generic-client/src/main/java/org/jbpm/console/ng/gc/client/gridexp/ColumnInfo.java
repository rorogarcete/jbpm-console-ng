package org.jbpm.console.ng.gc.client.gridexp;

import com.google.gwt.user.cellview.client.Header;

public class ColumnInfo {

    private String columnWidth;

    private Header<?> columnHeader;

    private Header<?> columnFooter;

    public ColumnInfo( String columnWidth, Header<?> columnHeader, Header<?> columnFooter ) {
        this.columnWidth = columnWidth;
        this.columnHeader = columnHeader;
        this.columnFooter = columnFooter;
    }

    public Header<?> getColumnFooter() {
        return columnFooter;
    }

    public Header<?> getColumnHeader() {
        return columnHeader;
    }

    public String getColumnWidth() {
        return columnWidth;
    }
}
