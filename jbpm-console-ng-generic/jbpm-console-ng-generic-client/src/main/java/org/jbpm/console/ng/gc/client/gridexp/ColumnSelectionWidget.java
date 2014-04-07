package org.jbpm.console.ng.gc.client.gridexp;

import com.google.gwt.user.cellview.client.Column;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;

public class ColumnSelectionWidget extends Composite {

    interface ColumnSelectionWidgetUIBinder
            extends UiBinder<Widget, ColumnSelectionWidget> {
    };

    private static ColumnSelectionWidgetUIBinder uiBinder = GWT.create(ColumnSelectionWidgetUIBinder.class);

    @UiField
    CheckBox id;

    @UiField
    CheckBox col1;

    @UiField
    CheckBox col2;

    @UiField
    CheckBox col3;

    @UiField
    CheckBox col4;

    private DataGrid dataGrid;

    private HashMap<Integer, Column> columns = new HashMap<Integer, Column>(5);

    public ColumnSelectionWidget() {
        initWidget(uiBinder.createAndBindUi(this));
        id.setValue(Boolean.TRUE);
        col1.setValue(Boolean.TRUE);
        col2.setValue(Boolean.TRUE);
        col3.setValue(Boolean.TRUE);
        col4.setValue(Boolean.TRUE);
    }

    public void setDataGrid( DataGrid dataGrid ) {
        this.dataGrid = dataGrid;
    }

    @UiHandler("id")
    void colIdSelectionChanged(final ClickEvent event) {
        if (dataGrid == null) return;
        if (!id.getValue()) {
            columns.put(0, dataGrid.getColumn(0));
            dataGrid.removeColumn(0);
        } else {
            dataGrid.insertColumn(0, columns.get(0));
        }
        dataGrid.redraw();
    }

    @UiHandler("col1")
    void col1SelectionChanged(final ClickEvent event) {
        if (dataGrid == null) return;
        if (!col1.getValue()) {
            columns.put(1, dataGrid.getColumn(1));
            dataGrid.removeColumn(1);
        } else {
            dataGrid.insertColumn(1, columns.get(1));
        }
        dataGrid.redraw();
    }

    @UiHandler("col2")
    void col2SelectionChanged(final ClickEvent event) {
        if (dataGrid == null) return;
        if (!col2.getValue()) {
            columns.put(2, dataGrid.getColumn(2));
            dataGrid.removeColumn(2);
        } else {
            dataGrid.insertColumn(2, columns.get(2));
        }
        dataGrid.redraw();
    }

    @UiHandler("col3")
    void col3SelectionChanged(final ClickEvent event) {
        if (dataGrid == null) return;
        if (!col3.getValue()) {
            columns.put(3, dataGrid.getColumn(3));
            dataGrid.removeColumn(3);
        } else {
            dataGrid.insertColumn(3, columns.get(3));
        }
        dataGrid.redraw();
    }

    @UiHandler("col4")
    void col4SelectionChanged(final ClickEvent event) {
        if (dataGrid == null) return;
        if (!col4.getValue()) {
            columns.put(4, dataGrid.getColumn(4));
            dataGrid.removeColumn(4);
        } else {
            dataGrid.insertColumn(4, columns.get(4));
        }
        dataGrid.redraw();
    }
}
