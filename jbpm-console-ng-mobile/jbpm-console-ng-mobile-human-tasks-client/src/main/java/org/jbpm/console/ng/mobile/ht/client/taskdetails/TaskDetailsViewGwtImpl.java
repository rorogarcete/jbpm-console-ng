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
package org.jbpm.console.ng.mobile.ht.client.taskdetails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.form.Form;
import com.googlecode.mgwt.ui.client.widget.form.FormEntry;
import com.googlecode.mgwt.ui.client.widget.input.MDateBox;
import com.googlecode.mgwt.ui.client.widget.input.MTextArea;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
import com.googlecode.mgwt.ui.client.widget.input.listbox.MListBox;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;
import com.googlecode.mgwt.ui.client.widget.panel.scroll.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.tabbar.ContactsTabBarButton;
import com.googlecode.mgwt.ui.client.widget.tabbar.HistoryTabBarButton;
import com.googlecode.mgwt.ui.client.widget.tabbar.MostViewedTabBarButton;
import com.googlecode.mgwt.ui.client.widget.tabbar.TabBarButton;
import com.googlecode.mgwt.ui.client.widget.tabbar.TabPanel;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;
import org.jbpm.console.ng.mobile.ht.client.utils.TaskStatus;

/**
 * @author livthomas
 * @author rorogarcete
 */
@Dependent
public class TaskDetailsViewGwtImpl extends AbstractView implements TaskDetailsPresenter.TaskDetailsView {

    private final Button saveButton;
    private final Button releaseButton;
    private final Button claimButton;
    private final Button startButton;
    private final Button completeButton;
    private long taskId = 0;
    private final MTextArea descriptionTextArea = new MTextArea();
    private final MTextBox statusTextBox = new MTextBox();
    private final MDateBox dueOnDateBox = new MDateBox();
    private final MListBox priorityListBox = new MListBox();
    private final MTextBox userTextBox = new MTextBox();
    private final MTextBox processInstanceIdTextBox = new MTextBox();
    private final MTextBox processDefinitionIdTextBox = new MTextBox();
    private final Button processInstanceDetailsButton = new Button("Process Instance Details");
    private final Button updateButton;

    private final Label potentialOwnersLabel = new Label();
    private final MTextBox delegateTextBox = new MTextBox();
    private final Button delegateButton;

    private TaskDetailsPresenter presenter;
    
    private Long instanceId;
    private String definitionId; 
    private String deploymentId;
    private String taskName;

    @Inject
    private MGWTPlaceManager placeManager;

    public TaskDetailsViewGwtImpl() {
        title.setText("Task Details");

        TabPanel tabPanel = new TabPanel();
        rootFlexPanel.add(tabPanel);

        // Work tab
        ScrollPanel workScrollPanel = new ScrollPanel();
        FlexPanel workFlexPanel = new FlexPanel();

        saveButton = new Button("Save");
        saveButton.setImportant(true);
        workFlexPanel.add(saveButton);

        releaseButton = new Button("Release");
        releaseButton.setImportant(true);
        workFlexPanel.add(releaseButton);

        claimButton = new Button("Claim");
        claimButton.setImportant(true);
        workFlexPanel.add(claimButton);

        startButton = new Button("Start");
        startButton.setConfirm(true);
        workFlexPanel.add(startButton);

        completeButton = new Button("Complete");
        completeButton.setConfirm(true);
        workFlexPanel.add(completeButton);

        TabBarButton workTabButton = new HistoryTabBarButton();
        workTabButton.setText("Work");

        workScrollPanel.setWidget(workFlexPanel);
        workScrollPanel.setScrollingEnabledX(false);
        workScrollPanel.setUsePos(MGWT.getOsDetection().isAndroid());

        tabPanel.add(workTabButton, workScrollPanel);

        // Details tab
        ScrollPanel detailsScrollPanel = new ScrollPanel();
        FlowPanel detailsFlowPanel = new FlowPanel();

        for (String priority : priorities) {
            priorityListBox.addItem(priority);
        }
        statusTextBox.setReadOnly(true);
        userTextBox.setReadOnly(true);

        Form detailsForm = new Form();
        detailsForm.setRound(true);
        detailsForm.add(new FormEntry("Description", descriptionTextArea));
        detailsForm.add(new FormEntry("Status", statusTextBox));
        detailsForm.add(new FormEntry("Due On", dueOnDateBox));
        detailsForm.add(new FormEntry("Priority", priorityListBox));
        detailsForm.add(new FormEntry("User", userTextBox));
        detailsFlowPanel.add(detailsForm);

        processInstanceIdTextBox.setReadOnly(true);
        processDefinitionIdTextBox.setReadOnly(true);
        processInstanceDetailsButton.setSmall(true);

        Form processContextForm = new Form();
        processContextForm.setRound(true);
        processContextForm.add(new FormEntry("Process Instance Id", processInstanceIdTextBox));
        processContextForm.add(new FormEntry("Process Definition Id", processDefinitionIdTextBox));
        processContextForm.add(new FormEntry("Process Instance Details", processInstanceDetailsButton));
        detailsFlowPanel.add(processContextForm);

        updateButton = new Button("Update");
        updateButton.setImportant(true);
        detailsFlowPanel.add(updateButton);

        TabBarButton detailsTabButton = new MostViewedTabBarButton();
        detailsTabButton.setText("Details");

        detailsScrollPanel.setWidget(detailsFlowPanel);
        detailsScrollPanel.setScrollingEnabledX(false);
        detailsScrollPanel.setUsePos(MGWT.getOsDetection().isAndroid());

        tabPanel.add(detailsTabButton, detailsScrollPanel);

        // Assignments tab
        ScrollPanel assignmentsScrollPanel = new ScrollPanel();
        FlowPanel assignmentsFlowPanel = new FlowPanel();

        Form assignmentsForm = new Form();
        assignmentsForm.setRound(true);
        assignmentsForm.add(new FormEntry("Potential Owners", potentialOwnersLabel));
        assignmentsForm.add(new FormEntry("User or Group", delegateTextBox));
        assignmentsFlowPanel.add(assignmentsForm);

        delegateButton = new Button("Delegate");
        delegateButton.setConfirm(true);
        assignmentsFlowPanel.add(delegateButton);

        TabBarButton assignmentsTabButton = new ContactsTabBarButton();
        assignmentsTabButton.setText("Assignments");

        assignmentsScrollPanel.setWidget(assignmentsFlowPanel);
        assignmentsScrollPanel.setScrollingEnabledX(false);
        assignmentsScrollPanel.setUsePos(MGWT.getOsDetection().isAndroid());

        tabPanel.add(assignmentsTabButton, assignmentsScrollPanel);

        tabPanel.setSelectedChild(1);
    }

    @Override
    public void init(final TaskDetailsPresenter presenter) {
        this.presenter = presenter;

        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Tasks List", Animations.SLIDE_REVERSE);
            }
        });

        saveButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                presenter.saveTask(taskId);
            }
        });

        releaseButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                presenter.releaseTask(taskId);
            }
        });

        claimButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                presenter.claimTask(taskId);
            }
        });

        startButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                presenter.startTask(taskId);
            }
        });

        completeButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
            	completeTaks();
            }
        });

        updateButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                try {
                    presenter.updateTask(taskId, "", descriptionTextArea.getText(), new MDateBox.DateParser().parse(
                            dueOnDateBox.getText()), priorityListBox.getSelectedIndex());
                } catch (ParseException ex) {
                    displayNotification("Wrong date format", "Enter the date in the correct format!");
                }
            }
        });

        delegateButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                presenter.delegateTask(taskId, delegateTextBox.getText());
            }
        });
    }
    
    public void completeTaks(){
    	if (instanceId != -1 && deploymentId != null) {
    		Map<String, Object> params = new HashMap<String, Object>();
            params.put("processId", definitionId);
            params.put("deploymentId", deploymentId);
            params.put("taskName", taskName);
            params.put("taskId", taskId);
            placeManager.goTo("Task Input Mapping List", Animations.SLIDE_REVERSE, params);
		}else {
			presenter.completeTask(taskId);
		}
    }

    @Override
    public void refreshTask(TaskSummary task, boolean owned) {
        TaskStatus status = TaskStatus.valueOf(task.getStatus());

        switch (status) {
            case Ready:
                saveButton.setVisible(false);
                releaseButton.setVisible(false);
                claimButton.setVisible(true);
                startButton.setVisible(false);
                completeButton.setVisible(false);
                break;
            case Reserved:
                saveButton.setVisible(false);
                releaseButton.setVisible(true);
                claimButton.setVisible(false);
                startButton.setVisible(true);
                completeButton.setVisible(false);
                break;
            case InProgress:
                saveButton.setVisible(true);
                releaseButton.setVisible(true);
                claimButton.setVisible(false);
                startButton.setVisible(false);
                completeButton.setVisible(true);
                break;
            default:
                saveButton.setVisible(false);
                releaseButton.setVisible(false);
                claimButton.setVisible(false);
                startButton.setVisible(false);
                completeButton.setVisible(false);
        }

        taskName = task.getName();
        descriptionTextArea.setText(task.getDescription());
        statusTextBox.setText(task.getStatus());
        priorityListBox.setSelectedIndex(task.getPriority());
        userTextBox.setText(task.getActualOwner());

        if (status.equals(TaskStatus.Completed)) {
            descriptionTextArea.setReadOnly(true);
            dueOnDateBox.setReadOnly(true);
            priorityListBox.setEnabled(false);
            updateButton.setVisible(false);
        } else {
            descriptionTextArea.setReadOnly(false);
            dueOnDateBox.setReadOnly(false);
            priorityListBox.setEnabled(true);
            updateButton.setVisible(true);
        }

        instanceId = task.getProcessInstanceId();
        definitionId = task.getProcessId();
        deploymentId = task.getDeploymentId();
        if (instanceId != -1 && definitionId != null) {
            processInstanceIdTextBox.setText(Long.toString(instanceId));
            processDefinitionIdTextBox.setText(definitionId);
        } else {
            processInstanceIdTextBox.setText("None");
            processDefinitionIdTextBox.setText("None");
        }

        processInstanceDetailsButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                if (instanceId != -1 && definitionId != null) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("instanceId", instanceId);
                    params.put("definitionId", definitionId);
                    placeManager.goTo("Process Instance Details", Animations.SLIDE, params);
                }
            }
        });

        if (owned && !status.equals(TaskStatus.Completed)) {
            delegateTextBox.setReadOnly(false);
            delegateButton.setVisible(true);
        } else {
            delegateTextBox.setReadOnly(true);
            delegateButton.setVisible(false);
        }
    }

    @Override
    public void setPotentialOwnersText(String text) {
        potentialOwnersLabel.setText(text);
    }

    @Override
    public void setDelegateTextBox(String text) {
        delegateTextBox.setText(text);
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    @Override
    public void refresh() {
        presenter.refresh(taskId);
    }

    @Override
    public void setParameters(Map<String, Object> params) {
        taskId = (Long) params.get("taskId");
        GWT.log("TaskId setParamters : " + params.get("taskId"));
    }

}
