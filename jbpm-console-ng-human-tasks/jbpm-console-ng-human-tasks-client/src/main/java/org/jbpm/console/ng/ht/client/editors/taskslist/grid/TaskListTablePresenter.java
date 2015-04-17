/*
 * Copyright 2015 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.client.editors.taskslist.grid;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Task List (new)")
public class TaskListTablePresenter {

    @Inject
    protected TaskListTableDisplayer tableDisplayer;

    private Constants constants = GWT.create(Constants.class);

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        tableDisplayer.setTableRefreshEnabled(true);
        tableDisplayer.setTableSelectorEnabled(true);
        tableDisplayer.setTableCreationEnabled(true);
        tableDisplayer.draw();
    }

    @OnClose
    public void onClose() {
        tableDisplayer.close();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List() + " (new)";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return tableDisplayer;
    }
}
