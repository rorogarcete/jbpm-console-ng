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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.ImageButton;
import com.googlecode.mgwt.ui.client.widget.list.celllist.BasicCell;
import com.googlecode.mgwt.ui.client.widget.list.celllist.CellList;
import com.googlecode.mgwt.ui.client.widget.list.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.list.celllist.CellSelectedHandler;
import com.googlecode.mgwt.ui.client.widget.panel.pull.PullArrowHeader;
import com.googlecode.mgwt.ui.client.widget.panel.pull.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.panel.pull.PullPanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;

/**
 *
 * @author livthomas
 * @author salaboy
 */
@Dependent
public class TaskListViewGwtImpl extends AbstractView implements TaskListPresenter.TaskListView {

    private final ImageButton newTaskButton;

    private PullPanel pullPanel;
    private PullArrowHeader pullArrowHeader;

    private final CellList<TaskSummary> cellList;

    private List<TaskSummary> tasksList;

    @Inject
    private MGWTPlaceManager placeManager;

    private TaskListPresenter presenter;

    public TaskListViewGwtImpl() {
        title.setTitle("Task List");

        newTaskButton = new ImageButton();
        newTaskButton.setText("New task");
        //headerPanel.setRightWidget(newTaskButton);
        headerPanel.add(newTaskButton);

        pullPanel = new PullPanel();
        pullArrowHeader = new PullArrowHeader();
        pullPanel.setHeader(pullArrowHeader);
        rootFlexPanel.add(pullPanel);

        cellList = new CellList<TaskSummary>(new BasicCell<TaskSummary>() {
            @Override
            public String getDisplayString(TaskSummary model) {
                return model.getId() + " : " + model.getName();
            }
        });
        pullPanel.add(cellList);
    }

    @Override
    public void init(final TaskListPresenter presenter) {
        this.presenter = presenter;
        
        newTaskButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("New Task", Animations.SLIDE);
            }
        });

        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Home", Animations.SLIDE_REVERSE);
            }
        });

        pullArrowHeader.setHTML("pull down");

        PullArrowStandardHandler headerHandler = new PullArrowStandardHandler(pullArrowHeader, pullPanel);

        headerHandler.setErrorText("Error");
        headerHandler.setLoadingText("Loading");
        headerHandler.setNormalText("pull down");
        headerHandler.setPulledText("release to load");
        headerHandler.setPullActionHandler(new PullArrowStandardHandler.PullActionHandler() {
            @Override
            public void onPullAction(final AsyncCallback<Void> callback) {
                new Timer() {
                    @Override
                    public void run() {
                        presenter.refresh();
                    }
                }.schedule(1000);

            }
        });
        pullPanel.setHeaderPullHandler(headerHandler);

        cellList.addCellSelectedHandler(new CellSelectedHandler() {
            @Override
            public void onCellSelected(CellSelectedEvent event) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("taskId", tasksList.get(event.getIndex()).getId());
                GWT.log("TaskId: "+ params.get("taskId"));
                GWT.log("Evento: "+ event.getIndex());
                placeManager.goTo("Task Details", Animations.SLIDE, params);
            }
        });

        presenter.refresh();
    }

    @Override
    public void render(List<TaskSummary> tasks) {
        tasksList = tasks;
        cellList.render(tasks);
        pullPanel.refresh();
    }

    @Override
    public void refresh() {
        presenter.refresh();
    }

    @Override
    public void setParameters(Map<String, Object> params) {

    }

}
