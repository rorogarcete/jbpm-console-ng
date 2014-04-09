package org.jbpm.console.ng.gc.client.gridexp;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.common.popups.footers.ModalFooterOKButton;

public class ColumnConfigPopup extends Modal {

    interface ColumnConfigPopupUIBinder
            extends UiBinder<Widget, ColumnConfigPopup> {
    };

    private static ColumnConfigPopupUIBinder uiBinder = GWT.create(ColumnConfigPopupUIBinder.class);

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

    public ColumnConfigPopup() {

        setTitle( "Configure grid columns" );
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );

        add( uiBinder.createAndBindUi( this ) );

        add( new ModalFooterOKButton(
                new Command() {
                    @Override
                    public void execute() {
                        hide();
                    }
                }
        ) );

        id.setValue(Boolean.TRUE);
        col1.setValue(Boolean.TRUE);
        col2.setValue(Boolean.TRUE);
        col3.setValue(Boolean.TRUE);
        col4.setValue(Boolean.TRUE);
    }

    public void init( DataGrid dataGrid ) {
        // Initialize the popup when the widget's icon is actually clicked
        if (this.dataGrid == null) {
            this.dataGrid = dataGrid;
            gridColumnsSetup = new GridColumnsSetup(dataGrid);
        }
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
