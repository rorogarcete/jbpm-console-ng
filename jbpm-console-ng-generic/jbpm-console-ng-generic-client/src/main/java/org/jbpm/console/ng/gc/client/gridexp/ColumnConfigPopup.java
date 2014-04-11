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
                        // TODO 'materialize' the new setup in the gridcolumnssetup, i.e. replace the original setups with the new ones
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
        applyColumnChange(id.getValue(), 0);
    }

    @UiHandler("col1")
    void col1SelectionChanged(final ClickEvent event) {
        applyColumnChange(col1.getValue(), 1);
    }

    @UiHandler("col2")
    void col2SelectionChanged(final ClickEvent event) {
        applyColumnChange(col2.getValue(), 2);
    }

    @UiHandler("col3")
    void col3SelectionChanged(final ClickEvent event) {
        applyColumnChange(col3.getValue(), 3);
    }

    @UiHandler("col4")
    void col4SelectionChanged(final ClickEvent event) {
        applyColumnChange(col4.getValue(), 4);
    }

    private void applyColumnChange(boolean insert, int selectedColumnIndex) {
        if (!insert) {
            int removeIndex = gridColumnsSetup.notifyColumnToBeRemoved( selectedColumnIndex );
            dataGrid.removeColumn( removeIndex );
        } else {
            int addIndex = gridColumnsSetup.notifyColumnToBeAdded( selectedColumnIndex );
            dataGrid.insertColumn(addIndex,
                    gridColumnsSetup.getColumn(selectedColumnIndex),
                    gridColumnsSetup.getColumnHeader(selectedColumnIndex),
                    gridColumnsSetup.getColumnFooter(selectedColumnIndex));
            dataGrid.setColumnWidth( addIndex, gridColumnsSetup.getColumnWidth( selectedColumnIndex ) );
        }
        dataGrid.redraw();
    }
}
