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
package org.jbpm.console.ng.mobile.pr.client.definition.details;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasText;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ht.model.TaskDefSummary;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;

/**
 *
 * @author livthomas
 */
public class ProcessDefinitionDetailsPresenter {

    public interface ProcessDefinitionDetailsView extends MGWTUberView<ProcessDefinitionDetailsPresenter> {

        HasText getDefinitionIdText();

        HasText getDefinitionNameText();

        HasText getDeploymentText();

        HasText getHumanTasksText();

        HasText getUsersAndGroupsText();

        HasText getSubprocessesText();

        HasText getProcessVariablesText();

        HasText getServicesText();

        void displayNotification(String title, String message);

    }

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;
    
    @Inject
    private Caller<ProcessDefinitionService> processDefService;

    @Inject
    private Caller<KieSessionEntryPoint> sessionServices;

    @Inject
    private ProcessDefinitionDetailsView view;

    public ProcessDefinitionDetailsView getView() {
        return view;
    }

    public void refresh(String deploymentId, String processId) {
    	processDefService.call(new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback(ProcessSummary process) {
                view.getDefinitionIdText().setText(String.valueOf(process.getId()));
                view.getDefinitionNameText().setText(process.getName());
                view.getDeploymentText().setText(process.getDeploymentId());
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error refresh: "+ message.toString());
                GWT.log("Error refresh Throwable: "+ throwable.toString());
                return true;
            }
        }).getItem(new ProcessDefinitionKey(deploymentId, processId));
        //getProcessById(deploymentId, processId); //.getItem(key); //getProcessById(deploymentId, processId);

        // Human Tasks
        dataServices.call(new RemoteCallback<List<TaskDefSummary>>() {
            @Override
            public void callback(List<TaskDefSummary> tasks) {
                if (tasks.isEmpty()) {
                    view.getHumanTasksText().setText("No User Tasks defined in this process");
                } else {
                    StringBuilder humanTasksText = new StringBuilder();
                    for (TaskDefSummary task : tasks) {
                        humanTasksText.append(task.getName());
                        humanTasksText.append('\n');
                    }
                    view.getHumanTasksText().setText(humanTasksText.toString());
                }
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error Human Task refresh: "+ message.toString());
                GWT.log("Error Human Task refresh Throwable: "+ throwable.toString());
                return true;
            }
        }).getAllTasksDef(deploymentId, processId);
        //.getAllTasksDef(processId);

        // Users and Groups
        dataServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> entities) {
                if (entities.keySet().isEmpty()) {
                    view.getUsersAndGroupsText().setText("No user or group used in this process");
                } else {
                    StringBuilder usersAndGroupsText = new StringBuilder();
                    for (String key : entities.keySet()) {
                        usersAndGroupsText.append(entities.get(key));
                        usersAndGroupsText.append(" - ");
                        usersAndGroupsText.append(key);
                        usersAndGroupsText.append('\n');
                    }
                    view.getUsersAndGroupsText().setText(usersAndGroupsText.toString());
                }
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error Users and Groups refresh: "+ message.toString());
                GWT.log("Error Users and Groups refresh Throwable: "+ throwable.toString());
                return true;
            }
        }).getAssociatedEntities(deploymentId, processId);
        //.getAssociatedEntities(processId);

        // Subprocesses
        dataServices.call(new RemoteCallback<Collection<String>>() {
            @Override
            public void callback(Collection<String> subprocesses) {
                if (subprocesses.isEmpty()) {
                    view.getSubprocessesText().setText("No subproceses required by this process");
                } else {
                    StringBuilder subprocessesText = new StringBuilder();
                    for (String key : subprocesses) {
                        subprocessesText.append(key);
                        subprocessesText.append('\n');
                    }
                    view.getSubprocessesText().setText(subprocessesText.toString());
                }
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error subprocess refresh: "+ message.toString());
                GWT.log("Error subprocess refresh Throwable: "+ throwable.toString());
                return true;
            }
        }).getReusableSubProcesses(deploymentId, processId);
        //.getReusableSubProcesses(processId);

        // Process Variables
        dataServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> inputs) {
                if (inputs.keySet().isEmpty()) {
                    view.getProcessVariablesText().setText("No process variables defined for this process");
                } else {
                    StringBuilder processVariablesText = new StringBuilder();
                    for (String key : inputs.keySet()) {
                        processVariablesText.append(key);
                        processVariablesText.append(" - ");
                        processVariablesText.append(inputs.get(key));
                        processVariablesText.append('\n');
                    }
                    view.getProcessVariablesText().setText(processVariablesText.toString());
                }
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error process variable refresh: "+ message.toString());
                GWT.log("Error process variable refresh Throwable: "+ throwable.toString());
                return true;
            }
        }).getRequiredInputData(deploymentId, processId);
        //.getRequiredInputData(processId);

        // Services
        dataServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> services) {
                if (services.keySet().isEmpty()) {
                    view.getServicesText().setText("No services required for this process");
                } else {
                    StringBuilder servicesText = new StringBuilder();
                    for (String key : services.keySet()) {
                        servicesText.append(key);
                        servicesText.append(" - ");
                        servicesText.append(services.get(key));
                        servicesText.append('\n');
                    }
                    view.getServicesText().setText(servicesText.toString());
                }
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error services refresh: "+ message.toString());
                GWT.log("Error services refresh Throwable: "+ throwable.toString());
                return true;
            }
        }).getServiceTasks(deploymentId, processId);
        //.getServiceTasks(processId);
    }

    public void startProcess(final String deploymentId, final String processId) {
        sessionServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long instanceId) {
                view.displayNotification("Success", "New process instance with id = " + instanceId + " was started!");
                refresh(deploymentId, processId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error start process: "+ message.toString());
                GWT.log("Error start process Throwable: "+ throwable.toString());
                return true;
            }
        }).startProcess(deploymentId, processId);
    }

}