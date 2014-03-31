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

package org.jbpm.console.ng.gc.client.gridexp;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "GridExp")
public class GridExpListPresenter {

    public interface GridExpListView extends UberView<GridExpListPresenter> {

        void displayNotification( String text );

        String getCurrentFilter();

        void setCurrentFilter( String filter );

        DataGrid<ProcessSummary> getDataGrid();

        void showBusyIndicator( String message );

        void hideBusyIndicator();
    }

    private Menus menus;

    @Inject
    private GridExpListView view;

    private DataService dataServices;

    private ListDataProvider<ProcessSummary> dataProvider = new ListDataProvider<ProcessSummary>();

    private List<ProcessSummary> currentProcesses;

    @WorkbenchPartTitle
    public String getTitle() {
        return "ProcDefs";
    }

    @WorkbenchPartView
    public UberView<GridExpListPresenter> getView() {
        return view;
    }

    public GridExpListPresenter() {
        makeMenuBar();
    }
    
    public void refreshProcessList() {
        currentProcesses = dataServices.getProcesses();

        filterProcessList( view.getCurrentFilter() );
    }

    public void filterProcessList( String filter ) {
        if ( filter.equals( "" ) ) {
            if ( currentProcesses != null ) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll( new ArrayList<ProcessSummary>( currentProcesses ) );
                dataProvider.refresh();

            }
        } else {
            if ( currentProcesses != null ) {
                List<ProcessSummary> processes = new ArrayList<ProcessSummary>( currentProcesses );
                List<ProcessSummary> filteredProcesses = new ArrayList<ProcessSummary>();
                for ( ProcessSummary ps : processes ) {
                    if ( ps.getName().toLowerCase().contains( filter.toLowerCase() ) ) {
                        filteredProcesses.add( ps );
                    }
                }
                dataProvider.getList().clear();
                dataProvider.getList().addAll( filteredProcesses );
                dataProvider.refresh();
            }
        }

    }

    public void addDataDisplay( HasData<ProcessSummary> display ) {
        dataProvider.addDataDisplay( display );
    }

    public ListDataProvider<ProcessSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnOpen
    public void onOpen() {
        refreshProcessList();
    }
    
    @OnFocus
    public void onFocus() {
        refreshProcessList();
    }

//    public void onSearch( @Observes final ProcessDefinitionsSearchEvent searchFilter ) {
//        view.setCurrentFilter( searchFilter.getFilter() );
//        dataServices.call( new RemoteCallback<List<ProcessSummary>>() {
//            @Override
//            public void callback( List<ProcessSummary> processes ) {
//                currentProcesses = processes;
//                filterProcessList( view.getCurrentFilter() );
//            }
//        } ).getProcesses();
//    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( "Refresh" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        refreshProcessList();
                        view.setCurrentFilter( "" );
                        view.displayNotification( "Refreshed" );
                    }
                } )
                .endMenu().
                        build();

    }
    

     
}
