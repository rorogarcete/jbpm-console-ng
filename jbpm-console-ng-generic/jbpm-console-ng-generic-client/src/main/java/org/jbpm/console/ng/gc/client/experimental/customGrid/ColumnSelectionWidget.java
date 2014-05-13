package org.jbpm.console.ng.gc.client.experimental.customGrid;

import com.github.gwtbootstrap.client.ui.Icon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.Map;

public class ColumnSelectionWidget extends Composite {

	interface ColumnSelectionWidgetUIBinder
			extends UiBinder<Widget, ColumnSelectionWidget> {
	}

	private static ColumnSelectionWidgetUIBinder uiBinder = GWT.create( ColumnSelectionWidgetUIBinder.class );

	@UiField
	Icon dynGridIcon;

	PopupPanel columnSelectorPopup;
	Panel popupContent;

	private AbstractCellTable grid;
	private GridColumnsHelper gridColumnsHelper;

	public ColumnSelectionWidget() {
		initWidget( uiBinder.createAndBindUi( this ) );

		dynGridIcon.getElement().getStyle().setPaddingLeft( 4, Style.Unit.PX );
		dynGridIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );

		dynGridIcon.sinkEvents( Event.ONCLICK );
		dynGridIcon.addHandler( new ClickHandler() {
			@Override
			public void onClick( ClickEvent event ) {
				showSelectorPopup();
			}
		}, ClickEvent.getType() );

		columnSelectorPopup = new PopupPanel( true );
		columnSelectorPopup.setTitle( "Configure columns" );
		columnSelectorPopup.addCloseHandler( new CloseHandler<PopupPanel>() {
			public void onClose( CloseEvent<PopupPanel> popupPanelCloseEvent ) {
				gridColumnsHelper.saveGridColumnsConfig();
				columnSelectorPopup.hide();
			}
		} );

		popupContent = new VerticalPanel();
		columnSelectorPopup.add( popupContent );
	}

	public void setGrid( String gridId, AbstractCellTable grid ) {
		this.grid = grid;
		gridColumnsHelper = new GridColumnsHelper( gridId, grid );

	}

	// Apply any previously applied column configuration to the data grid (an explicit call to this method is necessary whenever the data grid is being redrawn)
	// Made this into a separate call, so that it can be executed when clicking the typical table refresh button (for example)
	public void applyGridColumnsConfig() {
		gridColumnsHelper.applyGridColumnsConfig();
	}

	private void setPopupContent() {

		popupContent.clear();

		for ( final Map.Entry<Integer, ColumnSettings> entry : gridColumnsHelper.getGridColumnsConfig().getColumnSettingsBySelectorIndex() ) {
			final int selectedIndex = entry.getKey();
			final ColumnSettings columnSettings = entry.getValue();

			// TODO Replace the ColumnConfigRowWidgets with a DataGrid widget in the ColumnSelectionWidget, thus improving styling
			// options, which would also allow getting rid of the callbacks
			popupContent.add(
					new ColumnConfigRowWidget(
							columnSettings.isVisible(),
							new ColumnVisibilityChangedCallback() {
								@Override
								public void columnVisibilityChanged(Boolean isVisible) {
									gridColumnsHelper.applyGridColumnConfig( selectedIndex, isVisible );
								}
							},
							columnSettings.getColumnLabel(),
							new RightColumnShiftCallback() {
								@Override
								public void columnShiftedRight() {
									// This call updates the data grid and also reset the internal indexes in the getGridColumnsConfig
									gridColumnsHelper.columnShiftedRight( selectedIndex );
									// Refresh the selector popup's content after moving columns
									refreshSelectorPopup();
								}
							},
							new LeftColumnShiftCallback() {
								@Override
								public void columnShiftedLeft() {
									// This call updates the data grid and also reset the internal indexes in the getGridColumnsConfig
									gridColumnsHelper.columnShiftedLeft( selectedIndex );
									// Refresh the selector popup's content after moving columns
									refreshSelectorPopup();
								}
							}
					)
			);
		}
	}

	private void showSelectorPopup() {
		if ( grid == null ) {
			Window.alert( "Grid customization widget is not correctly configured!" );
			return;
		}

		setPopupContent();

		columnSelectorPopup.setPopupPosition( dynGridIcon.getAbsoluteLeft(),
				dynGridIcon.getAbsoluteTop() + dynGridIcon.getOffsetHeight() );
		columnSelectorPopup.show();
	}

	private void refreshSelectorPopup() {

		columnSelectorPopup.hide();
		showSelectorPopup();
	}
}
