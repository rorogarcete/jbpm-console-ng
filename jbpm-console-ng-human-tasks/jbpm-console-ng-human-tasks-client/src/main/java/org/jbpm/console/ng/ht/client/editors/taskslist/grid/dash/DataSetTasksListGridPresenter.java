/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.RefreshSelectorMenuBuilder;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.jbpm.console.ng.ht.client.editors.quicknewtask.QuickNewTaskPopup;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.dashbuilder.dataset.filter.FilterFactory.*;

@Dependent
@WorkbenchScreen(identifier = "DataSet Tasks List")
public class DataSetTasksListGridPresenter extends AbstractScreenListPresenter<TaskSummary> {

    public interface DataSetTaskListView extends ListView<TaskSummary, DataSetTasksListGridPresenter> {

        int getRefreshValue();

        void restoreTabs();

        void saveRefreshValue( int newValue );

        void applyFilterOnPresenter( String key );
    }

    @Inject
    private DataSetTaskListView view;

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private Caller<TaskLifeCycleService> taskOperationsService;

    @Inject
    DataSetQueryHelper dataSetQueryHelper;

    @Inject
    private QuickNewTaskPopup quickNewTaskPopup;

    @Inject
    private ErrorPopupPresenter errorPopup;

    private RefreshSelectorMenuBuilder refreshSelectorMenuBuilder = new RefreshSelectorMenuBuilder( this );

    public Button menuRefreshButton = new Button();
    public Button menuResetTabsButton = new Button();

    private final List<MenuItem> items = new ArrayList<MenuItem>();

    public DataSetTasksListGridPresenter() {

        dataProvider = new AsyncDataProvider<TaskSummary>() {

            @Override
            protected void onRangeChanged( HasData<TaskSummary> display ) {
                view.showBusyIndicator( constants.Loading() );
                final Range visibleRange = view.getListGrid().getVisibleRange();
                getData( visibleRange );

            }
        };
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData( final Range visibleRange ) {
        try {
            FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
            if ( currentTableSettings != null ) {
                currentTableSettings.setTablePageSize( view.getListGrid().getPageSize() );
                ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                GWT.log( "-----taskList getData " + columnSortList.size() + "currentTableSettings table name " + currentTableSettings.getTableName() );
                if ( columnSortList != null && columnSortList.size() > 0 ) {
                    dataSetQueryHelper.setLastOrderedColumn( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 ).getColumn().getDataStoreName() : "" );
                    dataSetQueryHelper.setLastSortOrder( ( columnSortList.size() > 0 ) && columnSortList.get( 0 ).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING );
                } else {
                    dataSetQueryHelper.setLastOrderedColumn( DataSetTasksListGridViewImpl.COLUMN_CREATEDON );
                    dataSetQueryHelper.setLastSortOrder( SortOrder.ASCENDING );
                }
                dataSetQueryHelper.setDataSetHandler( currentTableSettings );
                if ( textSearchStr != null && textSearchStr.trim().length() > 0 ) {

                    DataSetFilter filter = new DataSetFilter();
                    List<ColumnFilter> filters = new ArrayList<ColumnFilter>();
                    filters.add( likeTo( DataSetTasksListGridViewImpl.COLUMN_NAME, textSearchStr.toLowerCase(), false ) );
                    filters.add( likeTo( DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION, textSearchStr.toLowerCase(), false ) );
                    filters.add( likeTo( DataSetTasksListGridViewImpl.COLUMN_PROCESSID, textSearchStr.toLowerCase(), false ) );
                    filter.addFilterColumn( OR( filters ) );

                    if ( currentTableSettings.getDataSetLookup().getFirstFilterOp() != null ) {
                        currentTableSettings.getDataSetLookup().getFirstFilterOp().addFilterColumn( OR( filters ) );
                    } else {
                        currentTableSettings.getDataSetLookup().addOperation( filter );
                    }
                    textSearchStr = "";
                }
                dataSetQueryHelper.lookupDataSet( visibleRange.getStart(), new DataSetReadyCallback() {
                    @Override
                    public void callback( DataSet dataSet ) {
                        if ( dataSet != null ) {
                            List<TaskSummary> myTasksFromDataSet = new ArrayList<TaskSummary>();

                            for ( int i = 0; i < dataSet.getRowCount(); i++ ) {
                                myTasksFromDataSet.add( new TaskSummary(
                                        dataSetQueryHelper.getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_TASKID, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_NAME, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_STATUS, i ),
                                        dataSetQueryHelper.getColumnIntValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PRIORITY, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_CREATEDBY, i ),
                                        dataSetQueryHelper.getColumnDateValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_CREATEDON, i ),
                                        dataSetQueryHelper.getColumnDateValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME, i ),
                                        dataSetQueryHelper.getColumnDateValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_DUEDATE, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PROCESSID, i ),
                                        dataSetQueryHelper.getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID, i ),
                                        dataSetQueryHelper.getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID, i ),
                                        dataSetQueryHelper.getColumnLongValue( dataSet, DataSetTasksListGridViewImpl.COLUMN_PARENTID, i ) ) );

                            }
                            PageResponse<TaskSummary> taskSummaryPageResponse = new PageResponse<TaskSummary>();
                            taskSummaryPageResponse.setPageRowList( myTasksFromDataSet );
                            taskSummaryPageResponse.setStartRowIndex( visibleRange.getStart() );
                            taskSummaryPageResponse.setTotalRowSize( dataSet.getRowCountNonTrimmed() );
                            taskSummaryPageResponse.setTotalRowSizeExact( true );
                            if ( visibleRange.getStart() + dataSet.getRowCount() == dataSet.getRowCountNonTrimmed() ) {
                                taskSummaryPageResponse.setLastPage( true );
                            } else {
                                taskSummaryPageResponse.setLastPage( false );
                            }
                            DataSetTasksListGridPresenter.this.updateDataOnCallback( taskSummaryPageResponse );
                        }
                        view.hideBusyIndicator();
                    }

                    @Override
                    public void notFound() {
                        view.hideBusyIndicator();
                        errorPopup.showMessage( "Not found DataSet with UUID [  jbpmHumanTasks ] " );
                        GWT.log( "DataSet with UUID [  jbpmHumanTasks ] not found." );
                    }

                    @Override
                    public boolean onError( final ClientRuntimeError error ) {
                        view.hideBusyIndicator();
                        error.getThrowable().printStackTrace();
                        errorPopup.showMessage( "DataSet with UUID [  jbpmHumanTasks ] error: " + error.getThrowable() );
                        GWT.log( "DataSet with UUID [  jbpmHumanTasks ] error: ", error.getThrowable() );
                        return false;
                    }
                } );
            } else {
                view.hideBusyIndicator();
            }
        } catch ( Exception e ) {
            GWT.log( "Error looking up dataset with UUID [ jbpmHumanTasks ]" );
        }

    }

    public void filterGrid( FilterSettings tableSettings ) {
        dataSetQueryHelper.setCurrentTableSettings( tableSettings );
        refreshGrid();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List();
    }

    @WorkbenchPartView
    public UberView<DataSetTasksListGridPresenter> getView() {
        return view;
    }

    public void releaseTask( final Long taskId,
                             final String userId ) {
        taskOperationsService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task Released" );
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                errorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).release( taskId, userId );
    }

    public void claimTask( final Long taskId,
                           final String userId,
                           final String deploymentId ) {
        taskOperationsService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task Claimed" );
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                errorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).claim( taskId, userId, deploymentId );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory

                .newTopLevelMenu( Constants.INSTANCE.New_Task() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        quickNewTaskPopup.show();
                    }
                } )
                .endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                menuRefreshButton.addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent clickEvent ) {
                                        refreshGrid();
                                    }
                                } );
                                return menuRefreshButton;
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTaskListGridPresenter#menuRefreshButton";
                            }

                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( refreshSelectorMenuBuilder ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                menuResetTabsButton.addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent clickEvent ) {
                                        view.restoreTabs();
                                    }
                                } );
                                return menuResetTabsButton;
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTaskListGridPresenter#menuResetTabsButton";
                            }

                        };
                    }
                } ).endMenu()
                .build();

    }

    @PostConstruct
    public void setupButtons() {
        menuRefreshButton.setIcon( IconType.REFRESH );
        menuRefreshButton.setSize( ButtonSize.SMALL );
        menuRefreshButton.setTitle( Constants.INSTANCE.Refresh() );

        menuResetTabsButton.setIcon( IconType.TH_LIST );
        menuResetTabsButton.setSize( ButtonSize.SMALL );
        menuResetTabsButton.setTitle( Constants.INSTANCE.RestoreDefaultFilters() );
    }

    @Override
    public void onGridPreferencesStoreLoaded() {
        refreshSelectorMenuBuilder.loadOptions( view.getRefreshValue() );
    }

    @Override
    protected void updateRefreshInterval( boolean enableAutoRefresh, int newInterval ) {
        super.updateRefreshInterval( enableAutoRefresh, newInterval );
        view.saveRefreshValue( newInterval );
    }

    @Override
    protected void onSearchEvent( @Observes SearchEvent searchEvent ) {
        textSearchStr = searchEvent.getFilter();
        if ( textSearchStr != null && textSearchStr.trim().length() > 0 ) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put( "textSearch", textSearchStr );
            dataSetQueryHelper.getCurrentTableSettings().getKey();

            view.applyFilterOnPresenter( dataSetQueryHelper.getCurrentTableSettings().getKey() );
        }
    }
}