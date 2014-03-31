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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "GridExpListViewImpl.html")
public class GridExpListViewImpl extends Composite
        implements GridExpListPresenter.GridExpListView,
        RequiresResize {

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private GridExpListPresenter presenter;

    private String currentFilter = "";

    @Inject
    @DataField
    public DataGrid<ProcessSummary> processdefListGrid;

    @Inject
    @DataField
    public LayoutPanel listContainer;

    @DataField
    public SimplePager pager;

    private Set<ProcessSummary> selectedProcessDef;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private ListHandler<ProcessSummary> sortHandler;

    public GridExpListViewImpl() {
        pager = new SimplePager(SimplePager.TextLocation.LEFT, false, true);
    }

    @Override
    public String getCurrentFilter() {
        return currentFilter;
    }

    @Override
    public void setCurrentFilter(String currentFilter) {
        this.currentFilter = currentFilter;
    }

    @Override
    public void init(final GridExpListPresenter presenter) {
        this.presenter = presenter;

        listContainer.add(processdefListGrid);
        pager.setDisplay(processdefListGrid);
        pager.setPageSize(10);

        // Set the message to display when the table is empty.
        Label emptyTable = new Label("None");
        emptyTable.setStyleName("");
        processdefListGrid.setEmptyTableWidget(emptyTable);

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ListHandler<ProcessSummary>(presenter.getDataProvider().getList());
        processdefListGrid.addColumnSortHandler(sortHandler);

        // Add a selection model so we can select cells.
        final MultiSelectionModel<ProcessSummary> selectionModel = new MultiSelectionModel<ProcessSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedProcessDef = selectionModel.getSelectedSet();
                for (ProcessSummary pd : selectedProcessDef) {
                }
            }
        });

        processdefListGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<ProcessSummary>createCheckboxManager());

        initTableColumns(selectionModel);

        presenter.addDataDisplay(processdefListGrid);

    }

    private void initTableColumns(final SelectionModel<ProcessSummary> selectionModel) {

        processdefListGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<ProcessSummary>() {

            @Override
            public void onCellPreview(final CellPreviewEvent<ProcessSummary> event) {
                ProcessSummary process = null;
                if (BrowserEvents.CLICK.equalsIgnoreCase(event.getNativeEvent().getType())) {
                    int column = event.getColumn();
                    int columnCount = processdefListGrid.getColumnCount();
                    if (column != columnCount - 1) {
                        PlaceStatus instanceDetailsStatus = placeManager.getStatus(new DefaultPlaceRequest("Process Instance Details"));
                        if(instanceDetailsStatus == PlaceStatus.OPEN){
                            placeManager.closePlace("Process Instance Details");
                        }
                        process = event.getValue();
                        placeManager.goTo("Process Definition Details");
                    }
                }
            }
        });

        // Process Name String.
        Column<ProcessSummary, String> processNameColumn = new Column<ProcessSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessSummary object) {
                return object.getName();
            }
        };
        processNameColumn.setSortable(true);
        sortHandler.setComparator(processNameColumn, new Comparator<ProcessSummary>() {
            @Override
            public int compare(ProcessSummary o1,
                    ProcessSummary o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
        processdefListGrid.addColumn(processNameColumn, new SafeHtml() {
            @Override
            public String asString() {
                return "Name";
            }
        });

        // Version Type
        Column<ProcessSummary, String> versionColumn = new Column<ProcessSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessSummary object) {
                return object.getVersion();
            }
        };
        versionColumn.setSortable(true);
        sortHandler.setComparator(versionColumn, new Comparator<ProcessSummary>() {
            @Override
            public int compare(ProcessSummary o1,
                    ProcessSummary o2) {
                Integer version1;
                Integer version2;
                try{
                    version1 =  Integer.valueOf(o1.getVersion());
                    version2 = Integer.valueOf(o2.getVersion());
                    return version1.compareTo(version2);
                }catch(NumberFormatException nfe){
                    return o1.getVersion().compareTo(o2.getVersion());
                }
            }
        });
        processdefListGrid
                .addColumn(versionColumn, new SafeHtml() {
                    @Override
                    public String asString() {
                        return "Version";
                    }
                });
        processdefListGrid.setColumnWidth(versionColumn, "90px");

        // actions (icons)
        List<HasCell<ProcessSummary, ?>> cells = new LinkedList<HasCell<ProcessSummary, ?>>();

        cells.add(new StartActionHasCell("Start process", new Delegate<ProcessSummary>() {
            @Override
            public void execute(ProcessSummary process) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display Popup");
                placeRequestImpl.addParameter("processId", process.getId());
                placeRequestImpl.addParameter("domainId", process.getDeploymentId());
                placeRequestImpl.addParameter("processName", process.getName() );
                placeManager.goTo(placeRequestImpl);
            }
        }));

        cells.add(new DetailsActionHasCell("Details", new Delegate<ProcessSummary>() {
            @Override
            public void execute(ProcessSummary process) {

                PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Process Definition Details"));
                String nameSelected = DataGridUtils.getProcessNameRowSelected(processdefListGrid);
                String versionSelected = DataGridUtils.getProcessVersionRowSelected(processdefListGrid);
                PlaceStatus instanceDetailsStatus = placeManager.getStatus(new DefaultPlaceRequest("Process Instance Details"));
                if(instanceDetailsStatus == PlaceStatus.OPEN){
                    placeManager.closePlace("Process Instance Details");
                }
                if (status == PlaceStatus.CLOSE || !(process.getName().equals(nameSelected)
                        && process.getVersion().equals(versionSelected))) {
                    placeManager.goTo("Process Definition Details");
                } else if (status == PlaceStatus.OPEN || (process.getName().equals(nameSelected)
                        && process.getVersion().equals(versionSelected))) {
                    placeManager.closePlace(new DefaultPlaceRequest("Process Definition Details"));
                }

            }
        }));

        CompositeCell<ProcessSummary> cell = new CompositeCell<ProcessSummary>(cells);
        Column<ProcessSummary, ProcessSummary> actionsColumn = new Column<ProcessSummary, ProcessSummary>(cell) {
            @Override
            public ProcessSummary getValue(ProcessSummary object) {
                return object;
            }
        };
        processdefListGrid.addColumn(actionsColumn, new SafeHtml() {
            @Override
            public String asString() {
                return "Actions";
            }
        });
        processdefListGrid.setColumnWidth(actionsColumn, "70px");
    }

    @Override
    public void onResize() {
        if ((getParent().getOffsetHeight() - 120) > 0) {
            listContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
        }
    }

//    public void changeRowSelected(@Observes ProcessDefStyleEvent processDefStyleEvent) {
//        if (processDefStyleEvent.getProcessDefName() != null) {
//            DataGridUtils.paintRowSelected(processdefListGrid,
//                    processDefStyleEvent.getProcessDefName(), processDefStyleEvent.getProcessDefVersion());
//        }
//    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public DataGrid<ProcessSummary> getDataGrid() {
        return processdefListGrid;
    }

    public ListHandler<ProcessSummary> getSortHandler() {
        return sortHandler;
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    private class StartActionHasCell implements HasCell<ProcessSummary, ProcessSummary> {

        private ActionCell<ProcessSummary> cell;

        public StartActionHasCell(String text,
                Delegate<ProcessSummary> delegate) {
            cell = new ActionCell<ProcessSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context,
                        ProcessSummary value,
                        SafeHtmlBuilder sb) {

                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='Start' style='margin-right:5px;'>");
                    mysb.append(new SafeHtml() {
                        @Override
                        public String asString() {
                            return "Start";
                        }
                    });
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
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
        public ProcessSummary getValue(ProcessSummary object) {
            return object;
        }
    }

    private class DetailsActionHasCell implements HasCell<ProcessSummary, ProcessSummary> {

        private ActionCell<ProcessSummary> cell;

        public DetailsActionHasCell(String text,
                Delegate<ProcessSummary> delegate) {
            cell = new ActionCell<ProcessSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context,
                        ProcessSummary value,
                        SafeHtmlBuilder sb) {

                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='Details' style='margin-right:5px;'>");
                    mysb.append(new SafeHtml() {
                        @Override
                        public String asString() {
                            return "Details";
                        }
                    });
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
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
        public ProcessSummary getValue(ProcessSummary object) {
            return object;
        }
    }
}
