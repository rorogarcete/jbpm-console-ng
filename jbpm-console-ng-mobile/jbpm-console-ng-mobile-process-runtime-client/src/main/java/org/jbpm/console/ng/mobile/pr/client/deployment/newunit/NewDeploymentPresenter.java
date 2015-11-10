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
package org.jbpm.console.ng.mobile.pr.client.deployment.newunit;

import com.google.gwt.core.shared.GWT;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;

/**
 *
 * @author rorogarcete
 */
public class NewDeploymentPresenter {

    public interface NewDeploymentView extends MGWTUberView<NewDeploymentPresenter> {

        void goBackToDeploymentList();
        
        void displayNotification(String title, String message);

    }

    @Inject
    private NewDeploymentView view;
    
    @Inject
    private Caller<DeploymentManagerEntryPoint> deploymentManager;

    public NewDeploymentPresenter() {
    }

    public NewDeploymentView getView() {
        return view;
    }

    public void deployUnit(final String group, final String artifact, final String version) {
        deploymentManager.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification(" Deployment " ," Kjar Deployed " + group + ":" + artifact + ":" + version );
                view.goBackToDeploymentList();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                GWT.log("Error deploy :" + message.toString());
                GWT.log("Error deploy throwable :" + throwable.toString());
                return true;
            }
        }).deploy(new KModuleDeploymentUnitSummary( group + ":" + artifact + ":" + version, group, artifact, version, null, null, null, null ));
    }

}