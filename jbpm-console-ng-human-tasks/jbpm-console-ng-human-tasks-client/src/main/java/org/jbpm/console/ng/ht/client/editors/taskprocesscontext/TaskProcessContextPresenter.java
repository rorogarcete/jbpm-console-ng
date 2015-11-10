/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.client.editors.taskprocesscontext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskQueryService;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.uberfire.client.mvp.PlaceManager;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

@Dependent
public class TaskProcessContextPresenter {

    public interface TaskProcessContextView extends UberView<TaskProcessContextPresenter> {

        void displayNotification(String text);

        void setProcessInstanceId(String none);

        void setProcessId(String none);

        void enablePIDetailsButton(boolean enable);
    }

    private PlaceManager placeManager;

    private TaskProcessContextView view;

    private Event<ProcessInstancesWithDetailsRequestEvent> processInstanceSelected;

    private Caller<TaskQueryService> taskQueryService;

    private Caller<DataServiceEntryPoint> dataServices;

    private long currentTaskId = 0;
    private long currentProcessInstanceId = -1L;

    @Inject
    public TaskProcessContextPresenter(
            TaskProcessContextView view,
            PlaceManager placeManager,
            Caller<TaskQueryService> taskQueryService,
            Caller<DataServiceEntryPoint> dataServices,
            Event<ProcessInstancesWithDetailsRequestEvent> processInstanceSelected
    ) {
        this.view = view;
        this.taskQueryService = taskQueryService;
        this.dataServices = dataServices;
        this.placeManager = placeManager;
        this.processInstanceSelected = processInstanceSelected;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public IsWidget getView() {
        return view;
    }

    public void goToProcessInstanceDetails() {
        dataServices.call(new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback(ProcessInstanceSummary summary) {
                placeManager.goTo("Process Instances");
                processInstanceSelected.fire(new ProcessInstancesWithDetailsRequestEvent(
                        summary.getDeploymentId(),
                        summary.getProcessInstanceId(),
                        summary.getProcessId(),
                        summary.getProcessName(),
                        summary.getState())
                );
            }
        },
                new DefaultErrorCallback()
        ).getProcessInstanceById(currentProcessInstanceId);
    }

    public void refreshProcessContextOfTask() {
        taskQueryService.call(new RemoteCallback<TaskSummary>() {
            @Override
            public void callback(TaskSummary details) {
                if (details == null || details.getProcessInstanceId() == -1) {
                    view.setProcessInstanceId("None");
                    view.setProcessId("None");
                    view.enablePIDetailsButton(false);
                    return;
                }

                currentProcessInstanceId = details.getProcessInstanceId();
                view.setProcessInstanceId(String.valueOf(currentProcessInstanceId));
                view.setProcessId(details.getProcessId());
                view.enablePIDetailsButton(true);
            }
        },
                new DefaultErrorCallback()
        ).getItem(new TaskKey(currentTaskId));
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        this.currentTaskId = event.getTaskId();
        refreshProcessContextOfTask();
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (currentTaskId == event.getTaskId()) {
            refreshProcessContextOfTask();
        }
    }
}
