/*
 * Copyright 2015 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.client.editors.taskslist.grid;

import java.util.Date;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.gc.client.displayer.BaseTableDisplayer;
import org.jbpm.console.ng.gc.client.displayer.TableSettings;
import org.jbpm.console.ng.gc.client.displayer.TableSettingsBuilder;
import org.jbpm.console.ng.ht.client.editors.quicknewtask.QuickNewTaskPopup;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;

/**
 * Task list table displayer
 */
@Dependent
public class TaskListTableDisplayer extends BaseTableDisplayer<TaskSummary> {

    public static final String COLUMN_ACTIVATIONTIME = "activationTime";
    public static final String COLUMN_ACTUALOWNER = "actualOwner";
    public static final String COLUMN_CREATEDBY = "createdBy";
    public static final String COLUMN_CREATEDON = "createdOn";
    public static final String COLUMN_DEPLOYMENTID = "deploymentId";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DUEDATE = "dueDate";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PARENTID = "parentId";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_PROCESSID = "processId";
    public static final String COLUMN_PROCESSINSTANCEID = "processInstanceId";
    public static final String COLUMN_PROCESSSESSIONID = "processSessionId";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TASKID = "taskId";
    public static final String COLUMN_WORKITEMID = "workItemId";

    @Inject
    protected User identity;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Event<TaskSelectionEvent> taskSelectedEvent;

    @Inject
    private QuickNewTaskPopup quickNewTaskPopup;

    private final Constants constants = GWT.create(Constants.class);

    @PostConstruct
    protected void init() {
        buildDefaultTables();
        loadUserTables();
    }

    protected TableSettings buildTablePrototype() {
        return (TableSettings) TableSettingsBuilder.init()
                .dataset("jbpmHumanTasks")
                .column(COLUMN_TASKID).format(constants.Id())
                .column(COLUMN_NAME).format(constants.Task())
                .column(COLUMN_ACTUALOWNER).format("Owner")
                .column(COLUMN_CREATEDON).format("Created on", "MMM dd E, yyyy")
                .column(COLUMN_STATUS).format(constants.Status())
                .column(COLUMN_DESCRIPTION).format(constants.Description())
                .filterOn(true, true, true)
                .tableWidth(1000)
                .tableOrderEnabled(true)
                .tableOrderDefault(COLUMN_CREATEDON, DESCENDING)
                .buildSettings();
    }

    protected void buildDefaultTables() {

        // All
        super.addTableSettings("All", false,
                (TableSettings) TableSettingsBuilder.init()
                        .dataset("jbpmHumanTasks")
                        .column(COLUMN_TASKID).format(constants.Id())
                        .column(COLUMN_NAME).format(constants.Task())
                        .column(COLUMN_ACTUALOWNER).format("Owner")
                        .column(COLUMN_CREATEDON).format("Created on", "MMM dd E, yyyy")
                        .column(COLUMN_STATUS).format(constants.Status())
                        .column(COLUMN_DESCRIPTION).format(constants.Description())
                        .filterOn(true, true, true)
                        .tableWidth(1000)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(COLUMN_CREATEDON, DESCENDING)
                        .buildSettings());

        // Active
        super.addTableSettings("Active", false,
                (TableSettings) TableSettingsBuilder.init()
                        .dataset("jbpmHumanTasks")
                        .filter(COLUMN_ACTUALOWNER, equalsTo(identity.getIdentifier()))
                        .column(COLUMN_TASKID).format(constants.Id())
                        .column(COLUMN_NAME).format(constants.Task())
                        .column(COLUMN_ACTUALOWNER).format("Owner")
                        .column(COLUMN_CREATEDON).format("Created on", "MMM dd E, yyyy")
                        .column(COLUMN_STATUS).format(constants.Status()).expression("value.toUpperCase()")
                        .column(COLUMN_DESCRIPTION).format(constants.Description())
                        .filterOn(true, true, true)
                        .tableWidth(1000)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(COLUMN_CREATEDON, DESCENDING)
                        .buildSettings());
    }

    protected void loadUserTables() {

        // TODO: Get the json definitions from the backend services
/*
        List<String> jsonList = ...
        for (String json : jsonList) {
            TableSettingsJSONMarshaller jsonMarshaller = new TableSettingsJSONMarshaller();
            TableSettings userDefinedTable = jsonMarshaller.fromJsonString(json);
            super.addTableSettings(userDefinedTable);
        }
*/
    }

    // TableDisplayer overrides

    @Override
    public DisplayerConstraints createDisplayerConstraints() {

        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupAllowed(false)
                .setMaxColumns(-1)
                .setMinColumns(1)
                .setExtraColumnsAllowed(true)
                .setColumnsTitle("Columns");

        return new DisplayerConstraints(lookupConstraints)
                .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.TABLE_GROUP);
    }

    @Override
    protected PagedTable<Integer> createTable() {
        PagedTable<Integer> pagedTable = super.createTable();
        Button newTaskButton = new Button();
        newTaskButton.setTitle(constants.New_Task());
        newTaskButton.setIcon( IconType.PLUS_SIGN );
        newTaskButton.setTitle( Constants.INSTANCE.New_Task() );
        newTaskButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                quickNewTaskPopup.show();
            }
        });
        pagedTable.getLeftToolbar().add(newTaskButton);
        return pagedTable;
    }

    @Override
    protected TaskSummary createItem(Map<String, Object> itemValues) {
        String missingColumn = missingColumn(COLUMN_TASKID, COLUMN_NAME, COLUMN_STATUS);
        if (missingColumn != null) {
            GWT.log("A mandatory data set column is missing: " + missingColumn);
        }

        Long id = Long.parseLong(itemValues.get(COLUMN_TASKID).toString());
        String parentId = (String) itemValues.get(COLUMN_PARENTID);
        String processId = (String) itemValues.get(COLUMN_PROCESSID);
        String processSessionId = (String) itemValues.get(COLUMN_PROCESSSESSIONID);
        String processInstanceId = (String) itemValues.get(COLUMN_PROCESSINSTANCEID);
        String deploymentId = (String) itemValues.get(COLUMN_DEPLOYMENTID);
        String name = (String) itemValues.get(COLUMN_NAME);
        String descr = (String) itemValues.get(COLUMN_DESCRIPTION);
        String status = (String) itemValues.get(COLUMN_STATUS);
        String owner = (String) itemValues.get(COLUMN_ACTUALOWNER);
        String createdBy = (String) itemValues.get(COLUMN_CREATEDBY);
        Date creation = (Date) itemValues.get(COLUMN_CREATEDON);
        Date activation = (Date) itemValues.get(COLUMN_ACTIVATIONTIME);
        Number priority = (Number) itemValues.get(COLUMN_PRIORITY);

        return new TaskSummary(id,
                name,
                descr,
                status,
                (priority != null ? priority.intValue() : 0),
                owner,
                createdBy,
                creation,
                activation,
                null,
                processId,
                (processSessionId != null ? Long.parseLong(processSessionId) : 0),
                (processInstanceId != null ? Long.parseLong(processInstanceId) : 0),
                deploymentId,
                (parentId != null ? Long.parseLong(parentId) : 0));
    }

    protected TaskSummary selectedTask = null;

    @Override
    protected void onItemSelected(TaskSummary selectedItem) {

        boolean close = false;
        if (selectedTask == null) {
            selectedTask = selectedItem;
            //listGrid.setRowStyles(selectedStyles);
            //listGrid.redraw();
        } else if (!selectedTask.getTaskId().equals(selectedItem.getTaskId())) {
            selectedTask = selectedItem;
            //listGrid.setRowStyles(selectedStyles);
            //listGrid.redraw();
        } else {
            close = true;
        }

        PlaceStatus status = placeManager.getStatus("Task Details Multi");
        boolean logOnly = false;
        if(selectedItem.getStatus().equals("Completed") && selectedItem.isLogOnly()){
            logOnly = true;
        }
        if (status == PlaceStatus.CLOSE) {
            placeManager.goTo("Task Details Multi");
            taskSelectedEvent.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName(), selectedItem.isForAdmin(), logOnly));
        } else if (status == PlaceStatus.OPEN && !close) {
            taskSelectedEvent.fire(new TaskSelectionEvent(selectedItem.getTaskId(), selectedItem.getTaskName(), selectedItem.isForAdmin(), logOnly));
        } else if (status == PlaceStatus.OPEN && close) {
            placeManager.closePlace("Task Details Multi");
        }
    }

    @Override
    public TableSettings createTableSettingsPrototype() {
        return buildTablePrototype();
    }

    @Override
    protected String getNewTableSettingsTitle() {
        return Constants.INSTANCE.New_TaskList();
    }
}
