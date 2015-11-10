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
package org.jbpm.console.ng.pr.client.editors.definition.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.console.ng.pr.model.events.ProcessDefSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.kie.workbench.common.widgets.client.workbench.configuration.ContextualView;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
public class ProcessDefinitionListViewImpl extends AbstractListView<ProcessSummary, ProcessDefinitionListPresenter>
        implements ProcessDefinitionListPresenter.ProcessDefinitionListView {

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private Event<ProcessDefSelectionEvent> processDefSelected;

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

    @Inject
    private ContextualView contextualView;

    private String placeIdentifier;

    @Override
    public void init( final ProcessDefinitionListPresenter presenter ) {

        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add( constants.Name() );
        bannedColumns.add( constants.Actions() );
        List<String> initColumns = new ArrayList<String>();
        initColumns.add( constants.Name() );
        initColumns.add( constants.Version() );
        initColumns.add( constants.Project() );
        initColumns.add( constants.Actions() );
        super.init( presenter, new GridGlobalPreferences( "ProcessDefinitionsGrid", initColumns, bannedColumns ) );

        selectionModel = new NoSelectionModel<ProcessSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {

                boolean close = false;
                if ( selectedRow == -1 ) {
                    listGrid.setRowStyles( selectedStyles );
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                } else if ( listGrid.getKeyboardSelectedRow() != selectedRow ) {

                    listGrid.setRowStyles( selectedStyles );
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                } else {
                    close = true;
                }

                selectedItem = selectionModel.getLastSelectedObject();

                PlaceStatus instanceDetailsStatus = placeManager.getStatus( new DefaultPlaceRequest( "Process Instance Details Multi" ) );

                if ( instanceDetailsStatus == PlaceStatus.OPEN ) {
                    placeManager.closePlace( "Process Instance Details Multi" );
                }
//                placeIdentifier = "Basic Process Details Multi";
//                if ( contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES ).equals( ContextualView.ADVANCED_MODE ) ) {
                    placeIdentifier = "Advanced Process Details Multi";
//                }
                PlaceStatus status = placeManager.getStatus( new DefaultPlaceRequest( placeIdentifier ) );

                if ( status == PlaceStatus.CLOSE ) {
                    placeManager.goTo( placeIdentifier );
                    processDefSelected.fire( new ProcessDefSelectionEvent( selectedItem.getProcessDefId(), selectedItem.getDeploymentId() ) );
                } else if ( status == PlaceStatus.OPEN && !close ) {
                    processDefSelected.fire( new ProcessDefSelectionEvent( selectedItem.getProcessDefId(), selectedItem.getDeploymentId() ) );
                } else if ( status == PlaceStatus.OPEN && close ) {
                    placeManager.closePlace( placeIdentifier );
                }

            }
        } );

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager( new DefaultSelectionEventManager.EventTranslator<ProcessSummary>() {

                    @Override
                    public boolean clearCurrentSelection( CellPreviewEvent<ProcessSummary> event ) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent( CellPreviewEvent<ProcessSummary> event ) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if ( BrowserEvents.CLICK.equals( nativeEvent.getType() ) ) {
                            // Ignore if the event didn't occur in the correct column.
                            if ( listGrid.getColumnIndex( actionsColumn ) == event.getColumn() ) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }
                        }
                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                } );

        listGrid.setSelectionModel( selectionModel, noActionColumnManager );
        listGrid.setEmptyTableCaption( constants.No_Process_Definitions_Found() );
        listGrid.setRowStyles( selectedStyles );

    }

    @Override
    public void initColumns() {
        Column processNameColumn = initProcessNameColumn();
        Column versionColumn = initVersionColumn();
        Column projectColumn = initProjectColumn();
        actionsColumn = initActionsColumn();

        List<ColumnMeta<ProcessSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessSummary>>();
        columnMetas.add( new ColumnMeta<ProcessSummary>( processNameColumn, constants.Name() ) );
        columnMetas.add( new ColumnMeta<ProcessSummary>( versionColumn, constants.Version() ) );
        columnMetas.add( new ColumnMeta<ProcessSummary>( projectColumn, constants.Project() ) );
        columnMetas.add( new ColumnMeta<ProcessSummary>( actionsColumn, constants.Actions() ) );

        listGrid.addColumns( columnMetas );
    }

    private Column initProcessNameColumn() {
        // Process Name String.
        Column<ProcessSummary, String> processNameColumn = new Column<ProcessSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessSummary object ) {
                return object.getProcessDefName();
            }
        };
        processNameColumn.setSortable( true );
        processNameColumn.setDataStoreName( "ProcessName" );
        return processNameColumn;
    }

    private Column initVersionColumn() {
        Column<ProcessSummary, String> versionColumn = new Column<ProcessSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessSummary object ) {
                return object.getVersion();
            }
        };
        versionColumn.setSortable( true );
        versionColumn.setDataStoreName( "ProcessVersion" );
        return versionColumn;
    }

    private Column initProjectColumn() {
        Column<ProcessSummary, String> projectColumn = new Column<ProcessSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessSummary object ) {
                return object.getDeploymentId();
            }
        };
        projectColumn.setSortable( true );
        projectColumn.setDataStoreName( "Project" );
        return projectColumn;
    }

    private Column initActionsColumn() {
        // actions (icons)
        List<HasCell<ProcessSummary, ?>> cells = new LinkedList<HasCell<ProcessSummary, ?>>();

        cells.add( new StartActionHasCell( "Start process", new Delegate<ProcessSummary>() {
            @Override
            public void execute( ProcessSummary process ) {
                presenter.openGenericForm( process.getProcessDefId(), process.getDeploymentId(), process.getProcessDefName() );
            }
        } ) );

        CompositeCell<ProcessSummary> cell = new CompositeCell<ProcessSummary>( cells );
        Column<ProcessSummary, ProcessSummary> actionsColumn = new Column<ProcessSummary, ProcessSummary>( cell ) {
            @Override
            public ProcessSummary getValue( ProcessSummary object ) {
                return object;
            }
        };
        return actionsColumn;
    }

    public void refreshNewProcessInstance( @Observes NewProcessInstanceEvent newProcessInstance ) {
//        placeIdentifier = "Basic Process Details Multi";
//        if ( contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES ).equals( ContextualView.ADVANCED_MODE ) ) {
            placeIdentifier = "Advanced Process Details Multi";
//        }
        PlaceStatus definitionDetailsStatus = placeManager.getStatus( new DefaultPlaceRequest( placeIdentifier ) );
        if ( definitionDetailsStatus == PlaceStatus.OPEN ) {
            placeManager.closePlace( placeIdentifier );
        }
        placeManager.goTo( "Process Instance Details Multi" );
        processInstanceSelected.fire( new ProcessInstanceSelectionEvent( newProcessInstance.getDeploymentId(),
                                                                         newProcessInstance.getNewProcessInstanceId(),
                                                                         newProcessInstance.getNewProcessDefId(), newProcessInstance.getProcessDefName(), newProcessInstance.getNewProcessInstanceStatus() ) );

    }

    /*
     * Custom Action Columns for the Process Definition List
     */
    private class StartActionHasCell implements HasCell<ProcessSummary, ProcessSummary> {

        private ActionCell<ProcessSummary> cell;

        public StartActionHasCell( String text,
                                   Delegate<ProcessSummary> delegate ) {
            cell = new ActionCell<ProcessSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    ProcessSummary value,
                                    SafeHtmlBuilder sb ) {

                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant( new Button( constants.Start() ) {{
                        setSize( ButtonSize.EXTRA_SMALL );
                    }}.getElement().toString() );
                    sb.append( mysb.toSafeHtml() );
                }
            };
        }

        @Override
        public Cell<ProcessSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessSummary, ProcessSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessSummary getValue( ProcessSummary object ) {
            return object;
        }
    }

}
