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
package org.jbpm.console.ng.mobile.ht.client.newtask;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;

/**
 *
 * @author livthomas
 * @author salaboy
 */
@Dependent
public class NewTaskPresenter {

    public interface NewTaskView extends MGWTUberView<NewTaskPresenter> {

        void goBackToTaskList();

    }

    @Inject
    private NewTaskView view;
    
    @Inject
    private User identity;

    @Inject
    private Caller<TaskOperationsService> taskServices;

    public NewTaskPresenter() {
    }

    public NewTaskView getView() {
        return view;
    }

    public void addTask(final List<String> users, List<String> groups, final String taskName, int priority,
            boolean isAssignToMe, long dueDate, long dueDateTime) {

        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                view.goBackToTaskList();
            }
        }).addQuickTask(taskName, priority, new Date(dueDate+dueDateTime), users, groups, 
        		identity.getIdentifier(), isAssignToMe, isAssignToMe, taskName, "", -1L);
    }

}