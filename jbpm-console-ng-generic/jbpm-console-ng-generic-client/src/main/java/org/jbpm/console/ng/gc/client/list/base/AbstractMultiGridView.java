/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.gc.client.list.base;

import java.util.ArrayList;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ga.model.GenericSummary;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractMultiGridView<T extends GenericSummary, V extends AbstractListPresenter>
        extends Composite implements RequiresResize {

    public static String FILTER_TABLE_SETTINGS = "tableSettings";

    interface Binder extends UiBinder<Widget, AbstractMultiGridView> {
    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    public User identity;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    @UiField
    org.gwtbootstrap3.client.ui.Column column;

    protected V presenter;

    protected FilterPagedTable<T> filterPagedTable;

    protected ExtendedPagedTable<T> currentListGrid;

    protected RowStyles<T> selectedStyles = new RowStyles<T>() {

        @Override
        public String getStyleNames( T row,
                                     int rowIndex ) {
            if ( rowIndex == selectedRow ) {
                return "selected";
            }
            return null;
        }
    };

    protected NoSelectionModel<T> selectionModel;

    protected T selectedItem;

    protected int selectedRow = -1;

    protected Column actionsColumn;

    protected DefaultSelectionEventManager<T> noActionColumnManager;

    public GridGlobalPreferences currentGlobalPreferences;
    public Button createTabButton;

    public AbstractMultiGridView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void init( final V presenter,
                      final GridGlobalPreferences preferences,
                      final Button createNewGridButton ) {
        this.presenter = presenter;
        this.currentGlobalPreferences = preferences;
        this.createTabButton = createNewGridButton;

        filterPagedTable = new FilterPagedTable<T>();
        column.add( filterPagedTable.makeWidget() );

        filterPagedTable.setPreferencesService( preferencesService );
        preferencesService.call( new RemoteCallback<MultiGridPreferencesStore>() {

            @Override
            public void callback( MultiGridPreferencesStore multiGridPreferencesStore ) {
                if ( multiGridPreferencesStore == null ) {
                    multiGridPreferencesStore = new MultiGridPreferencesStore( preferences.getKey() );
                }
                String selectedGridId = multiGridPreferencesStore.getSelectedGrid();
                filterPagedTable.setMultiGridPreferencesStore( multiGridPreferencesStore );
                presenter.onGridPreferencesStoreLoaded();
                ArrayList<String> existingGrids = multiGridPreferencesStore.getGridsId();

                if ( existingGrids != null && existingGrids.size() > 0 ) {
                    String key;
                    for ( int i = 0; i < existingGrids.size(); i++ ) {
                        key = existingGrids.get( i );
                        final ExtendedPagedTable<T> extendedPagedTable = createGridInstance( preferences, key );
                        currentListGrid = extendedPagedTable;
                        presenter.addDataDisplay( extendedPagedTable );
                        extendedPagedTable.setDataProvider( presenter.dataProvider );
                        final String filterKey = key;
                        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter( filterKey );
                            }
                        } );
                        if ( currentListGrid != null && key.equals( selectedGridId ) ) {
                            currentListGrid = extendedPagedTable;
                        }
                    }

                    filterPagedTable.addAddTableButton( createNewGridButton );
                    if ( selectedGridId != null ) {
                        multiGridPreferencesStore.setSelectedGrid( selectedGridId );
                        preferencesService.call().saveUserPreferences( multiGridPreferencesStore );
                        filterPagedTable.setSelectedTab();
                    }

                } else {
                    initDefaultFilters( preferences, createNewGridButton );
                }
                initSelectionModel();
            }

        } ).loadUserPreferences( preferences.getKey(), UserPreferencesType.MULTIGRIDPREFERENCES );
        //presenter.setFilterPagedTable( filterPagedTable );
        // presenter.autoRefreshSeconds =  getMultiGridPreferencesStore().getRefreshInterval();
    }

    @Override
    public void onResize() {

    }

    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }
  /*
   * By default all the tables will have a refresh button
   */

    public void initGenericToolBar( ExtendedPagedTable<T> extendedPagedTable ) {
    }

    public String getValidKeyForAdditionalListGrid( String baseName ) {
        return filterPagedTable.getValidKeyForAdditionalListGrid( baseName );
    }

    public ExtendedPagedTable<T> createGridInstance( final GridGlobalPreferences preferences,
                                                     final String key ) {
        final ExtendedPagedTable<T> newListGrid = new ExtendedPagedTable<T>( 10, preferences );
        newListGrid.setShowLastPagerButton( true );
        newListGrid.setShowFastFordwardPagerButton( true );
        preferencesService.call( new RemoteCallback<GridPreferencesStore>() {

            @Override
            public void callback( GridPreferencesStore preferencesStore ) {
                newListGrid.setPreferencesService( preferencesService );
                if ( preferencesStore == null ) {
                    newListGrid.setGridPreferencesStore( new GridPreferencesStore( preferences ) );
                } else {
                    newListGrid.setGridPreferencesStore( preferencesStore );
                }
                initColumns( newListGrid );
                initGenericToolBar( newListGrid );
                newListGrid.loadPageSizePreferences();
            }
        } ).loadUserPreferences( key, UserPreferencesType.GRIDPREFERENCES );
        initExtraButtons( newListGrid );

        return newListGrid;
    }

    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    public ExtendedPagedTable<T> getListGrid() {
        return currentListGrid;
    }

    /*
     * For each specific implementation define the
     *  DataGrid columns and how they must be initialized
     */
    public abstract void initColumns( ExtendedPagedTable<T> extendedPagedTable );

    public abstract void initSelectionModel();

    public MultiGridPreferencesStore getMultiGridPreferencesStore() {
        if ( filterPagedTable != null ) {
            return filterPagedTable.getMultiGridPreferencesStore();
        }
        return null;
    }

    public void initExtraButtons( ExtendedPagedTable<T> extendedPagedTable ) {
    }

    public void initDefaultFilters( GridGlobalPreferences preferences,
                                    Button createTabButton ) {
    }

    public void applyFilterOnPresenter( String key ) {
    }

}
