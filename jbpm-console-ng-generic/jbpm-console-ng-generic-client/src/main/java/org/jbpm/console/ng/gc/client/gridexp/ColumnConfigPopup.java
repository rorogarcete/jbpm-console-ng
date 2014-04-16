package org.jbpm.console.ng.gc.client.gridexp;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.common.popups.footers.ModalFooterOKButton;

import java.util.Map;

public class ColumnConfigPopup extends Modal {

    interface ColumnConfigPopupUIBinder
            extends UiBinder<Widget, ColumnConfigPopup> {
    };

    private static ColumnConfigPopupUIBinder uiBinder = GWT.create(ColumnConfigPopupUIBinder.class);

    @UiField
    VerticalPanel columnPopupMainPanel;

    private DataGrid dataGrid;
    private GridColumnsHelper gridColumnsHelper;

    public ColumnConfigPopup() {

        setTitle( "Configure grid columns" );
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe(true);

        add(uiBinder.createAndBindUi(this));

        add( new ModalFooterOKButton(
                new Command() {
                    @Override
                    public void execute() {
                        // TODO 'materialize' the new setup in the gridcolumnshelper, i.e. replace the original setups with the new ones
                        hide();
                    }
                }
        ) );
    }

    public void init( DataGrid dataGrid ) {
        // Initialize the popup when the widget's icon is actually clicked
        this.dataGrid = dataGrid;
        gridColumnsHelper = new GridColumnsHelper(dataGrid);
        columnPopupMainPanel.clear();
        for ( final Map.Entry<Integer, ColumnSettings> entry : gridColumnsHelper.getColumnSettings().entrySet()) {
            ColumnSettings columnSettings = entry.getValue();

            final CheckBox checkBox = new com.google.gwt.user.client.ui.CheckBox();
            checkBox.setValue( columnSettings.isVisible() );
            checkBox.addClickHandler(new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    applyColumnChange(checkBox.getValue(), entry.getKey());
                }
            });
            columnPopupMainPanel.add( new ColumnConfigRowWidget( checkBox, columnSettings.getColumnLabel()) );
        }
    }

    private void applyColumnChange(boolean insert, int selectedColumnIndex) {
        if (!insert) {
            int removeIndex = gridColumnsHelper.notifyColumnToBeRemoved( selectedColumnIndex );
            dataGrid.removeColumn( removeIndex );
        } else {
            int addIndex = gridColumnsHelper.notifyColumnToBeAdded( selectedColumnIndex );
            dataGrid.insertColumn(addIndex,
                    gridColumnsHelper.getColumn(selectedColumnIndex),
                    gridColumnsHelper.getColumnHeader(selectedColumnIndex),
                    gridColumnsHelper.getColumnFooter(selectedColumnIndex));
            dataGrid.setColumnWidth( addIndex, gridColumnsHelper.getColumnWidth( selectedColumnIndex ) );
        }
        dataGrid.redraw();
    }
}
