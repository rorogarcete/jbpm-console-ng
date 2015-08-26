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
package org.jbpm.console.ng.mobile.ht.client.taskdetails;

import com.google.gwt.core.client.GWT;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;

/**
 *
 * @author livthomas
 */
@Dependent
public class TaskDetailsPresenter {

    public interface TaskDetailsView extends MGWTUberView<TaskDetailsPresenter> {

        void refreshTask(TaskSummary task, boolean owned);

        void setPotentialOwnersText(String text);

        void setDelegateTextBox(String text);

        void displayNotification(String title, String message);

    }

    @Inject
    private TaskDetailsView view;

    @Inject
    private User identity;

    @Inject
    private Caller<TaskOperationsService> taskOperationService;
    
    @Inject
    private Caller<TaskLifeCycleService> taskLifeCycleService;
    
    //private Caller<TaskQueryService> taskQueryService;

    public TaskDetailsView getView() {
        return view;
    }

    public void refresh(final long taskId) {
    	taskOperationService.call(new RemoteCallback<TaskSummary>() {
            @Override
            public void callback(TaskSummary task) {
                view.refreshTask(task, task.getActualOwner().equals(identity.getIdentifier()));
                refreshPotentialOwners(taskId);
            }
    	}, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error refresh: "+ message.toString());
                GWT.log("Error refresh Throwable: "+ throwable.toString());
                return true;
            }
        }).getTaskDetails(taskId); //getItem(new TaskKey(taskId));
    }

    public void refreshPotentialOwners(final long taskId) {
        List<Long> taskIds = new ArrayList<Long>(1);
        taskIds.add(taskId);
        taskOperationService.call(new RemoteCallback<Map<Long, List<String>>>() {
            @Override
            public void callback(Map<Long, List<String>> ids) {
                if (ids.isEmpty()) {
                    view.setPotentialOwnersText("No potential owners");
                } else {
                    view.setPotentialOwnersText(ids.get(taskId).toString());
                }
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                //view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error refreshPotentialOwners: "+ message.toString());
                GWT.log("Error refreshPotentialOwners Throwable: "+ throwable.toString());
                return true;
            }
        }).getTaskDetails(taskId);
    }

    public void saveTask(final long taskId) {
        // TODO with forms
    }

    public void releaseTask(final long taskId) {
    	taskLifeCycleService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + taskId + " was released!");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error releaseTask: "+ message.toString());
                GWT.log("Error releaseTask Throwable: "+ throwable.toString());
                return true;
            }
        }).release(taskId, identity.getIdentifier());
    }

    public void claimTask(final long taskId) {
        taskLifeCycleService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + taskId + " was claimed!");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error claimTask: "+ message.toString());
                GWT.log("Error claimTask Throwable: "+ throwable.toString());
                return true;
            }
        }).claim(taskId, identity.getIdentifier(), "");
    }

    public void startTask(final long taskId) {
        taskLifeCycleService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + taskId + " was started!");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error startTask: "+ message.toString());
                GWT.log("Error startTask Throwable: "+ throwable.toString());
                return true;
            }
        }).start(taskId, identity.getIdentifier());
    }

    public void completeTask(final long taskId) {
        final Map<String, Object> params = new HashMap<String, Object>();
        taskLifeCycleService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + taskId + " was completed!");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error completeTask: "+ message.toString());
                GWT.log("Error completeTask Throwable: "+ throwable.toString());
                return true;
            }
        }).complete(taskId, identity.getIdentifier(), params);
    }

    public void updateTask(final long taskId, String name, String description, Date dueDate, int priority) {
        List<String> descriptions = new ArrayList<String>();
        descriptions.add(description);

        List<String> names = new ArrayList<String>();
        names.add(name);

        taskOperationService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                view.displayNotification("Success", "Task details has been updated for the task with id = "
                        + taskId);
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error updateTask: "+ message.toString());
                GWT.log("Error updateTask Throwable: "+ throwable.toString());
                return true;
            }
        }).updateTask(taskId, priority, descriptions, dueDate);
    }

    public void delegateTask(final long taskId, String entity) {
        taskLifeCycleService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task was succesfully delegated");
                view.setDelegateTextBox("");
                refresh(taskId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error delegateTask: "+ message.toString());
                GWT.log("Error delegateTask Throwable: "+ throwable.toString());
                return true;
            }
        }).delegate(taskId, identity.getIdentifier(), entity);
    }

}