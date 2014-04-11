package org.jbpm.console.ng.gc.client.gridexp;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;

public class ColumnSetup {

    private Column<?, ?> column;

    private Header<?> columnHeader;

    private Header<?> columnFooter;

    private String columnWidth;

    public ColumnSetup( Column<?, ?> column, Header<?> columnHeader, Header<?> columnFooter, String columnWidth ) {
        this.column = column;
        this.columnHeader = columnHeader;
        this.columnFooter = columnFooter;
        this.columnWidth = columnWidth;
    }

    public Column<?, ?> getColumn() {
        return column;
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
