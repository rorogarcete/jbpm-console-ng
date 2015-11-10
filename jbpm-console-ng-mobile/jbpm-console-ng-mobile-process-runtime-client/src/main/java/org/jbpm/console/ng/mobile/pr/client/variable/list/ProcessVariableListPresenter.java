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
package org.jbpm.console.ng.mobile.pr.client.variable.list;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;

import com.google.gwt.core.client.GWT;

/**
 *
 * @author rorogarcete
 */
public class ProcessVariableListPresenter {
    
    public interface ProcessVariableListView extends MGWTUberView<ProcessVariableListPresenter> {
        
        void render(Map<String, String> processVariables);
        
        void displayNotification(String title, String message);
        
    }

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;
    
    @Inject
    private Caller<KieSessionEntryPoint> sessionServices;
    
    @Inject
    private ProcessVariableListView view;

    public ProcessVariableListView getView() {
        return view;
    }
    
    public void getProcessVariables(final String deploymentId, final String processId) {
        dataServices.call(new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback(Map<String, String> processVariables) {
                view.render(processVariables);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                GWT.log("Error process variables list Process : " + message.toString());
                GWT.log("Error process variables list Process Throwable : " + throwable.toString());
                return true;
            }
        }).getRequiredInputData(deploymentId, processId);
    }
    
    public void startProcess(final String deploymentId, final String processId,
    		Map<String, Object> params) {
        sessionServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long instanceId) {
                view.displayNotification("Success", "New process instance with id = " + instanceId + " was started!");
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                GWT.log("Error start process: " + message.toString());
                GWT.log("Error start process Throwable: " + throwable.toString());
                return true;
            }
        }).startProcess(deploymentId, processId, params);
    }

}