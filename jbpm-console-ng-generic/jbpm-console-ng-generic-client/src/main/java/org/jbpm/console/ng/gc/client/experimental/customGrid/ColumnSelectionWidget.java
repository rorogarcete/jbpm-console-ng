package org.jbpm.console.ng.gc.client.experimental.customGrid;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;

import java.util.Map;

public class ColumnSelectionWidget extends Composite {

	Panel panel;
	private AbstractCellTable grid;

	FlexTable selectorGrid;
	private GridColumnsHelper gridColumnsHelper;

	public ColumnSelectionWidget() {
		panel = new VerticalPanel();
		selectorGrid = new FlexTable();
		selectorGrid.setCellPadding( 4 );
		panel.add( selectorGrid );
		initWidget( panel );

	}

	public void setGrid( String gridId, AbstractCellTable grid ) {
		this.grid = grid;
		gridColumnsHelper = new GridColumnsHelper( gridId, grid );
	}

	// Apply any previously applied column configuration to the data grid (an explicit call to this method is necessary whenever the data grid is being redrawn)
	// Made this into a separate call, so that it can be executed when clicking the typical table refresh button (for example)
	public void applyGridColumnsConfig() {
		check();
		gridColumnsHelper.applyGridColumnsConfig();
		grid.redraw();
		setSelectorContent();
	}

	public void anchorColumn( Integer gridIndex ) {
		check();
		gridColumnsHelper.excludeFromSelection( gridIndex );
	}

	private void check() {
		if (grid == null) throw new RuntimeException( "Table column widget is not initialized" );
	}

	private void setSelectorContent() {
		setSelectorGridContent();
		setSelectorButtons();
	}

	private void setSelectorGridContent() {
		selectorGrid.clear( true );
		int row = 0;
		for ( final Map.Entry<Integer, ColumnSettings> entry : gridColumnsHelper.getFilteredColumnSettingsBySelectorIndex() ) {
			final int selectedIndex = entry.getKey();
			final ColumnSettings columnSettings = entry.getValue();

			Label label = new Label( columnSettings.getColumnLabel() );

			final CheckBox checkBox = new com.google.gwt.user.client.ui.CheckBox();

			final Icon shiftRightIcon = new Icon( IconType.ARROW_DOWN );
			shiftRightIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
			shiftRightIcon.sinkEvents( Event.ONCLICK );
			shiftRightIcon.setVisible( columnSettings.isVisible() );

			final Icon shiftLeftIcon = new Icon( IconType.ARROW_UP );
			shiftLeftIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
			shiftLeftIcon.sinkEvents( Event.ONCLICK );
			shiftLeftIcon.setVisible( columnSettings.isVisible() );

			checkBox.setValue( columnSettings.isVisible() );
			checkBox.addClickHandler( new ClickHandler() {
				@Override
				public void onClick( ClickEvent event ) {
					boolean isVisible = checkBox.getValue();
					shiftLeftIcon.setVisible( isVisible );
					shiftRightIcon.setVisible( isVisible );
					gridColumnsHelper.changeColumnVisibility( selectedIndex, isVisible );
					grid.redraw();
				}
			} );

			shiftRightIcon.addHandler( new ClickHandler() {
				@Override
				public void onClick( ClickEvent event ) {
					// This call updates the data grid and also reset the internal indexes in the getGridColumnsConfig
					gridColumnsHelper.columnShiftedRight( selectedIndex );
					grid.redraw();
					// Refresh the selector popup's content after moving columns
					setSelectorGridContent();
				}
			}, ClickEvent.getType() );

			shiftLeftIcon.addHandler( new ClickHandler() {
				@Override
				public void onClick( ClickEvent event ) {
					// This call updates the data grid and also reset the internal indexes in the getGridColumnsConfig
					gridColumnsHelper.columnShiftedLeft( selectedIndex );
					grid.redraw();
					// Refresh the selector popup's content after moving columns
					setSelectorGridContent();
				}
			}, ClickEvent.getType() );

			selectorGrid.setWidget( row, 0, checkBox );
			selectorGrid.setHTML( row, 1, "&nbsp;&nbsp;" );
			selectorGrid.setWidget( row, 2, label );
			selectorGrid.setHTML( row, 3, "&nbsp;&nbsp;" );
			selectorGrid.setWidget( row, 4, shiftRightIcon );
			selectorGrid.setHTML( row, 5, "&nbsp;&nbsp;" );
			selectorGrid.setWidget( row, 6, shiftLeftIcon );
			row++;
		}
	}

	private void setSelectorButtons() {
		Button resetButton = new Button( "Reset table", new ClickHandler() {
			@Override
			public void onClick( ClickEvent event ) {
				gridColumnsHelper.resetGrid();
				grid.redraw();
				setSelectorGridContent();
			}
		});
		panel.add( resetButton );
	}
}
