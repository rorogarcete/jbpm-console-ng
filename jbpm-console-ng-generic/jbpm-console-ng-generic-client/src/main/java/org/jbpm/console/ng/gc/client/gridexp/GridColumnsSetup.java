package org.jbpm.console.ng.gc.client.gridexp;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.github.gwtbootstrap.client.ui.DataGrid;

import java.util.HashMap;

public class GridColumnsSetup {

    private HashMap<Integer, Column<?, ?>> columns = new HashMap<Integer, Column<?, ?>>(5);
    private HashMap<Integer, ColumnInfo> columnInfos = new HashMap<Integer, ColumnInfo>(5);

    public GridColumnsSetup(DataGrid dataGrid) {
        for (int i = 0; i < dataGrid.getColumnCount(); i++) {
            Column<?, ?> column = dataGrid.getColumn(i);
            columns.put(i, column);
            columnInfos.put(i, new ColumnInfo( dataGrid.getColumnWidth(column), dataGrid.getHeader(i), dataGrid.getFooter(i) ) );
        }
    }

    public String getColumnWidth(int cacheIndex) {
        ColumnInfo columnInfo = columnInfos.get(cacheIndex);
        String width = columnInfo != null ? columnInfo.getColumnWidth() : "";
        return width;
    }

    public Header<?> getColumnHeader(int cacheIndex) {
        Header<?> header = null;
        ColumnInfo columnInfo = columnInfos.get(cacheIndex);
        if (columnInfo != null) header = columnInfo.getColumnHeader();
        return header;
    }

    public Header<?> getColumnFooter(int cacheIndex) {
        Header<?> footer = null;
        ColumnInfo columnInfo = columnInfos.get(cacheIndex);
        if (columnInfo != null) footer = columnInfo.getColumnFooter();
        return footer;
    }

    public Column<?, ?> getColumn(int cacheIndex) {
        return columns.get(cacheIndex);
    }
}
