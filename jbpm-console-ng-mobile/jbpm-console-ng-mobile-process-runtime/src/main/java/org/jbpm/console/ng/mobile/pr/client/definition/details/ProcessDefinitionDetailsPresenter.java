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

        void setDefinitionIdText(String text);

        void setDefinitionNameText(String text);

        void setDeploymentText(String text);

        void setHumanTasksText(String text);

        void setUsersAndGroupsText(String text);

        void setSubprocessesText(String text);

        void setProcessVariablesText(String text);

        void setServicesText(String text);

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
            	view.setDefinitionIdText(String.valueOf(process.getId()));
                view.setDefinitionNameText(process.getName());
                view.setDeploymentText(process.getDeploymentId());
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

        // Human Tasks
        dataServices.call(new RemoteCallback<List<TaskDefSummary>>() {
            @Override
            public void callback(List<TaskDefSummary> tasks) {
                if (tasks.isEmpty()) {
                    view.setHumanTasksText("No User Tasks defined in this process");
                } else {
                    StringBuilder humanTasksText = new StringBuilder();
                    for (TaskDefSummary task : tasks) {
                        humanTasksText.append(task.getName());
                        humanTasksText.append('\n');
                    }
                    view.setHumanTasksText(humanTasksText.toString());
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

        // Users and Groups
        dataServices.call(new RemoteCallback<Map<String, Collection<String>>>() {
            @Override
            public void callback(Map<String, Collection<String>> entities) {
                if (entities.keySet().isEmpty()) {
                    view.setUsersAndGroupsText("No user or group used in this process");
                } else {
                	StringBuilder usersAndGroupsText = new StringBuilder();
                    for (String key : entities.keySet()) {    
                    	Collection<String> entityNames = entities.get(key);
                        if (entityNames != null) {
							for (String entity : entityNames) {
								usersAndGroupsText.append(entity);
								usersAndGroupsText.append( " " + entity + " " );
							}	
						}
            
                        usersAndGroupsText.append(usersAndGroupsText);
                        usersAndGroupsText.append(" - ");
                        usersAndGroupsText.append(key);
                        usersAndGroupsText.append('\n');
                    }
                    view.setUsersAndGroupsText(usersAndGroupsText.toString());
       
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

        // Subprocesses
        dataServices.call(new RemoteCallback<Collection<String>>() {
            @Override
            public void callback(Collection<String> subprocesses) {
                if (subprocesses.isEmpty()) {
                    view.setSubprocessesText("No subproceses required by this process");
                } else {
                    StringBuilder subprocessesText = new StringBuilder();
                    for (String key : subprocesses) {
                        subprocessesText.append(key);
                        subprocessesText.append('\n');
                    }
                    view.setSubprocessesText(subprocessesText.toString());
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

        // Process Variables
        dataServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> inputs) {
                if (inputs.keySet().isEmpty()) {
                    view.setProcessVariablesText("No process variables defined for this process");
                } else {
                    StringBuilder processVariablesText = new StringBuilder();
                    for (String key : inputs.keySet()) {
                        processVariablesText.append(key);
                        processVariablesText.append(" - ");
                        processVariablesText.append(inputs.get(key));
                        processVariablesText.append('\n');
                    }
                    view.setProcessVariablesText(processVariablesText.toString());
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

        // Services
        dataServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> services) {
                if (services.keySet().isEmpty()) {
                    view.setServicesText("No services required for this process");
                } else {
                    StringBuilder servicesText = new StringBuilder();
                    for (String key : services.keySet()) {
                        servicesText.append(key);
                        servicesText.append(" - ");
                        servicesText.append(services.get(key));
                        servicesText.append('\n');
                    }
                    view.setServicesText(servicesText.toString());
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