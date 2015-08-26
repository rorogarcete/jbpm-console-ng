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
package org.jbpm.console.ng.mobile.ht.client.taskinputmappings;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;

import com.google.gwt.core.client.GWT;

/**
 * @author rorogarcete
 */
public class TaskInputMappingListPresenter {
    
    public interface TaskInputMappingListView extends MGWTUberView<TaskInputMappingListPresenter> {
        
        void render(Map<String, String> inputMappings);
        
        void displayNotification(String title, String message);
        
    }

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;
    
    @Inject
    private Caller<TaskLifeCycleService> taskLifeCycleService;
    
    @Inject
    private User identity;
    
    @Inject
    private TaskInputMappingListView view;

    public TaskInputMappingListView getView() {
        return view;
    }
    
    public void getInputMappings(String deploymentId, String processId, String taskName) {
        dataServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> inputMappings) {
                view.render(inputMappings);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                GWT.log("Error render Inputs Mapping Task : "+ message.toString());
                GWT.log("Error render Inputs Mapping Task Throwable: "+ throwable.toString());
                return true;
            }
        }).getTaskInputMappings(deploymentId, processId, taskName);
    }
    
    public void completeTask(final long taskId, Map<String, Object> params) {
        taskLifeCycleService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Success", "Task with id = " + taskId + " was completed!");
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

}