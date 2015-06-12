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
package org.jbpm.console.ng.mobile.ht.client.tasklist;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskQueryService;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;
import org.jbpm.console.ng.mobile.ht.client.utils.TaskStatus;

import com.google.gwt.core.client.GWT;

/**
 *
 * @author livthomas
 * @author salaboy
 */
@Dependent
public class TaskListPresenter {

    public interface TaskListView extends MGWTUberView<TaskListPresenter> {

        void render(List<TaskSummary> tasks);

    }

    @Inject
    private TaskListView view;

    @Inject
    private Caller<TaskQueryService> taskQueryService;

    @Inject
    private User identity;
    
    private QueryFilter currentFilter;
    
    private List<String> statuses;

    public TaskListPresenter() {

    }

    public TaskListView getView() {
        return view;
    }

    public void refresh() {
        statuses = new ArrayList<String>();
        for (TaskStatus status : TaskStatus.values()) {
            statuses.add(status.toString());
        }
        
        if(currentFilter == null) {
    		currentFilter = new PortableQueryFilter(0, 100, false, "", "t.name", true);
    	}
        
        if(currentFilter.getParams() != null) {
    		currentFilter.getParams().put("statuses", statuses);
    		currentFilter.getParams().put("userId", identity.getIdentifier());
    	}
        
        taskQueryService.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.render(tasks);
            }
        }).getAll(currentFilter);
    }

}
