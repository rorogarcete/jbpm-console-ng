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
package org.jbpm.console.ng.mobile.pr.client.definition.list;

import java.util.List;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;

import com.google.gwt.core.client.GWT;

/**
 *
 * @author livthomas
 * @author rorogarcete
 */
public class ProcessDefinitionsListPresenter {

    public interface ProcessDefinitionsListView extends MGWTUberView<ProcessDefinitionsListPresenter> {

        void render(List<ProcessSummary> definitions);
    }

    @Inject
    private Caller<ProcessDefinitionService> processDefinitionService;

    @Inject
    private ProcessDefinitionsListView view;
    
    @Inject
    private User identity;
    
    private QueryFilter currentFilter;

    public ProcessDefinitionsListView getView() {
        return view;
    }

    public void refresh() {   	
    	if (currentFilter == null) {
			currentFilter = new PortableQueryFilter(0, 100, false, "", "", true);
		}
   
    	processDefinitionService.call(new RemoteCallback<List<ProcessSummary>>() {
           @Override
            public void callback(List<ProcessSummary> definitions) {
               view.render(definitions);
           }
    	}, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                GWT.log("Error definition list :" + message.toString());
                GWT.log("Error definition list Throwable :" + throwable.toString());
                return true;
            }
       }).getAll(currentFilter);
    }

}