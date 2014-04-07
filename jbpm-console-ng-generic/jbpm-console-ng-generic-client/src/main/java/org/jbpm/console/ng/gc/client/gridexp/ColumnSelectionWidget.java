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
    private GridColumnsSetup gridColumnsSetup;
    private boolean initialized = false;

    public ColumnSelectionWidget() {
        initWidget(uiBinder.createAndBindUi(this));
        id.setValue(Boolean.TRUE);
        col1.setValue(Boolean.TRUE);
        col2.setValue(Boolean.TRUE);
        col3.setValue(Boolean.TRUE);
        col4.setValue(Boolean.TRUE);
    }

    public void setDataGrid( DataGrid dataGrid ) {
        if (dataGrid == null) return;
        this.dataGrid = dataGrid;
        gridColumnsSetup = new GridColumnsSetup(dataGrid);
        initialized = true;
    }

    // TODO for now hardcoded 'real' (as in not the cached ones from when we initialized the widget) column indexes

    @UiHandler("id")
    void colIdSelectionChanged(final ClickEvent event) {
        if (!id.getValue()) {
            dataGrid.removeColumn(0);
        } else {
            dataGrid.insertColumn(0, gridColumnsSetup.getColumn(0), gridColumnsSetup.getColumnHeader(0));
            dataGrid.setColumnWidth(0, gridColumnsSetup.getColumnWidth(0));
        }
        dataGrid.redraw();
    }

    @UiHandler("col1")
    void col1SelectionChanged(final ClickEvent event) {
        if (!col1.getValue()) {
            dataGrid.removeColumn(1);
        } else {
            dataGrid.insertColumn(1, gridColumnsSetup.getColumn(1), gridColumnsSetup.getColumnHeader(1));
            dataGrid.setColumnWidth(1, gridColumnsSetup.getColumnWidth(1));
        }
        dataGrid.redraw();
    }

    @UiHandler("col2")
    void col2SelectionChanged(final ClickEvent event) {
        if (!col2.getValue()) {
            dataGrid.removeColumn(2);
        } else {
            dataGrid.insertColumn(2, gridColumnsSetup.getColumn(2), gridColumnsSetup.getColumnHeader(2));
            dataGrid.setColumnWidth(2, gridColumnsSetup.getColumnWidth(2));
        }
        dataGrid.redraw();
    }

    @UiHandler("col3")
    void col3SelectionChanged(final ClickEvent event) {
        if (!col3.getValue()) {
            dataGrid.removeColumn(3);
        } else {
            dataGrid.insertColumn(3, gridColumnsSetup.getColumn(3), gridColumnsSetup.getColumnHeader(3));
            dataGrid.setColumnWidth(3, gridColumnsSetup.getColumnWidth(3));
        }
        dataGrid.redraw();
    }

    @UiHandler("col4")
    void col4SelectionChanged(final ClickEvent event) {
        if (!col4.getValue()) {
            dataGrid.removeColumn(4);
        } else {
            dataGrid.insertColumn(4, gridColumnsSetup.getColumn(4), gridColumnsSetup.getColumnHeader(4));
            dataGrid.setColumnWidth(4, gridColumnsSetup.getColumnWidth(4));
        }
        dataGrid.redraw();
    }
}
