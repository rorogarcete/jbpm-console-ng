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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import java.util.ArrayList;

import org.dashbuilder.dataset.client.DataSetClientServiceError;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.util.TaskUtils;
import org.jbpm.console.ng.gc.client.util.TaskUtils.TaskType;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.console.ng.ht.service.TaskQueryService;
import org.uberfire.client.annotations.WorkbenchScreen;

import java.util.Date;
import java.util.List;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import static org.jbpm.console.ng.ht.util.TaskRoleDefinition.TASK_ROLE_ADMINISTRATOR;
import static org.jbpm.console.ng.ht.util.TaskRoleDefinition.TASK_ROLE_POTENTIALOWNER;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;

import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
@WorkbenchScreen(identifier = "DataSet Tasks List")
public class DataSetTasksListGridPresenter extends AbstractScreenListPresenter<TaskSummary> {

    public static String FILTER_STATUSES_PARAM_NAME = "statuses";
    public static String FILTER_CURRENT_ROLE_PARAM_NAME = "filter";

    public interface DataSetTaskListView extends ListView<TaskSummary, DataSetTasksListGridPresenter> {

    }

    @Inject
    private DataSetTaskListView view;

    private Constants constants = GWT.create(Constants.class);

    @Inject
    private Caller<TaskQueryService> taskQueryService;

    @Inject
    private Caller<TaskLifeCycleService> taskOperationsService;

    private String currentRole;

    private List<String> currentStatuses;

    private TaskType currentStatusFilter = TaskUtils.TaskType.ACTIVE;

    public DataSetTasksListGridPresenter() {

        final DataSetLookup lookup = DataSetFactory.newDataSetLookupBuilder()
                .dataset("jbpmHumanTasks")
                //            .filter(ClusterMetricsGenerator.COLUMN_TIMESTAMP, timeFrame("-1second"))
                //            .group(ClusterMetricsGenerator.COLUMN_SERVER)
                //            .column(ClusterMetricsGenerator.COLUMN_SERVER)
                .column("taskId")
                .column("name")
                .column("actualOwner")
                .column("createdOn")
                .column("status")
                .column("description")
                
                .buildLookup();

        dataProvider = new AsyncDataProvider<TaskSummary>() {

            @Override
            protected void onRangeChanged(HasData<TaskSummary> display) {
                view.showBusyIndicator(constants.Loading());
                final Range visibleRange = display.getVisibleRange();
                ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                if (currentFilter == null) {
                    currentFilter = new PortableQueryFilter(visibleRange.getStart(),
                            visibleRange.getLength(),
                            false, "",
                            (columnSortList.size() > 0) ? columnSortList.get(0)
                                    .getColumn().getDataStoreName() : "",
                            (columnSortList.size() > 0) ? columnSortList.get(0)
                                    .isAscending() : true);

                }
                // If we are refreshing after a search action, we need to go back to offset 0
                if (currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
                        || currentFilter.getParams().get("textSearch") == null || currentFilter.getParams().get("textSearch").equals("")) {
                    currentFilter.setOffset(visibleRange.getStart());
                    currentFilter.setCount(visibleRange.getLength());
                    currentFilter.setFilterParams("");
                } else {
                    currentFilter.setFilterParams("(LOWER(t.name) like '" + currentFilter.getParams().get("textSearch")
                            + "' or LOWER(t.description) like '" + currentFilter.getParams().get("textSearch") + "') ");
                    currentFilter.setOffset(0);
                    currentFilter.setCount(view.getListGrid().getPageSize());
                }

                if (currentStatusFilter == null) {
                    currentFilter.getParams().put("statuses", TaskUtils.getStatusByType(currentStatusFilter));
                } else {
                    currentFilter.getParams().put("statuses", currentStatuses);
                }
                currentFilter.getParams().put("filter", currentStatusFilter.toString());
                currentFilter.getParams().put("userId", identity.getIdentifier());
                currentFilter.getParams().put("taskRole", currentRole);
                currentFilter.setOrderBy((columnSortList.size() > 0) ? columnSortList.get(0)
                        .getColumn().getDataStoreName() : "");
                currentFilter.setIsAscending((columnSortList.size() > 0) ? columnSortList.get(0)
                        .isAscending() : true);

                try {
                    lookup.setNumberOfRows(view.getListGrid().getPageSize());
                    lookup.setRowOffset(visibleRange.getStart());
                    lookup.setNumberOfRows(visibleRange.getLength());
                    DataSetClientServices.get().lookupDataSet(lookup, new DataSetReadyCallback() {
                        @Override
                        public void callback(DataSet dataSet) {
                            if (dataSet != null && dataSet.getRowCount() > 0) {
                                List<TaskSummary> myTasksFromDataSet = new ArrayList<TaskSummary>();

                                for (int i = 0;i <  dataSet.getRowCount(); i ++) {
                                    myTasksFromDataSet.add( new TaskSummary(
                                                    (Long)dataSet.getColumnByIndex(0).getValues().get(i),
                                                    (String) dataSet.getColumnByIndex(1).getValues().get(i),
                                                    (String) dataSet.getColumnByIndex(5).getValues().get(i),
                                                    (String) dataSet.getColumnByIndex(4).getValues().get(i),
                                    0,(String) dataSet.getColumnByIndex(2).getValues().get(i),
                                    "",(Date) dataSet.getColumnByIndex(3).getValues().get(i),null,null,"",-1,-1,"",-1)
                                    );
                                }

                                view.hideBusyIndicator();
                                dataProvider.updateRowCount(dataSet.getRowCount(),
                                        true); // true ??
                                dataProvider.updateRowData(0,///dataSet.getStartRowIndex() ???
                                        myTasksFromDataSet);
                            }
                        }
                        @Override
                        public void notFound() {
                            GWT.log("DataSet with UUID [  jbpmHumanTasks ] not found.");
                        }

                        @Override
                        public boolean onError(DataSetClientServiceError error) {
                            GWT.log("DataSet with UUID [  jbpmHumanTasks ] error: ", error.getThrowable());
                            return false;
                        }
                    });

                } catch (Exception e) {
                    GWT.log("Error looking up dataset with UUID [ jbpmHumanTasks ]");
                }

//                taskQueryService.call(new RemoteCallback<PageResponse<TaskSummary>>() {
//                    @Override
//                    public void callback(PageResponse<TaskSummary> response) {
//                        view.hideBusyIndicator();
//                        dataProvider.updateRowCount(response.getTotalRowSize(),
//                                response.isTotalRowSizeExact());
//                        dataProvider.updateRowData(response.getStartRowIndex(),
//                                response.getPageRowList());
//                    }
//                }, new ErrorCallback<Message>() {
//                    @Override
//                    public boolean error(Message message, Throwable throwable) {
//                        view.hideBusyIndicator();
//                        view.displayNotification("Error: Getting Tasks: " + throwable.toString());
//                        GWT.log(message.toString());
//                        return true;
//                    }
//                }).getData(currentFilter);
            }
        };
    }

    public void filterGrid(String currentRole, List<String> currentStatuses) {
        this.currentRole = currentRole;
        this.currentStatuses = currentStatuses;
        refreshGrid();

    }

    public void refreshActiveTasks() {
        currentRole = TASK_ROLE_POTENTIALOWNER;
        currentStatusFilter = TaskUtils.TaskType.ACTIVE;
        refreshGrid();
    }

    public void refreshPersonalTasks() {
        currentRole = TASK_ROLE_POTENTIALOWNER;
        currentStatusFilter = TaskUtils.TaskType.PERSONAL;
        refreshGrid();
    }

    public void refreshGroupTasks() {
        currentRole = TASK_ROLE_POTENTIALOWNER;
        currentStatusFilter = TaskUtils.TaskType.GROUP;
        refreshGrid();
    }

    public void refreshAllTasks() {
        currentRole = TASK_ROLE_POTENTIALOWNER;
        currentStatusFilter = TaskUtils.TaskType.ALL;
        refreshGrid();
    }

    public void refreshAdminTasks() {
        currentRole = TASK_ROLE_ADMINISTRATOR;
        currentStatusFilter = TaskUtils.TaskType.ADMIN;
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

    public void releaseTask(final Long taskId, final String userId) {
        taskOperationsService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task Released");
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).release(taskId, userId);
    }

    public void claimTask(final Long taskId, final String userId, final String deploymentId) {
        taskOperationsService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task Claimed");
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }).claim(taskId, userId, deploymentId);
    }
}
