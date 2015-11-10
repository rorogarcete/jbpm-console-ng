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

import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.form.Form;
import com.googlecode.mgwt.ui.client.widget.form.FormEntry;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
import com.googlecode.mgwt.ui.client.widget.input.checkbox.MCheckBox;
import com.googlecode.mgwt.ui.client.widget.input.listbox.MListBox;
import com.googlecode.mgwt.ui.client.widget.panel.scroll.ScrollPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;

/**
 *
 * @author livthomas
 * @author salaboy
 * @author rorogarcete
 */
@Dependent
public class NewTaskViewGwtImpl extends AbstractView implements NewTaskPresenter.NewTaskView {

    private final MTextBox taskNameTextBox = new MTextBox();
    private final MCheckBox assignToMeCheckBox = new MCheckBox();
    private final MListBox priorityListBox = new MListBox();
    private final MTextBox userTextBox = new MTextBox();
    private final Button addTaskButton;

    private NewTaskPresenter presenter;

    @Inject
    private MGWTPlaceManager placeManager;

    @Inject
    protected User identity;

    public NewTaskViewGwtImpl() {
    	
        title.setText("New Task");

        ScrollPanel scrollPanel = new ScrollPanel();
        rootFlexPanel.add(scrollPanel);

        FlowPanel newTaskPanel = new FlowPanel();

        for (String priority : priorities) {
            priorityListBox.addItem(priority);
        }

        Form newTaskForm = new Form();
        newTaskForm.setRound(true); 
        newTaskForm.add(new FormEntry("Task Name", taskNameTextBox));
        newTaskForm.add(new FormEntry("Auto Assign To Me", assignToMeCheckBox));
        newTaskForm.add(new FormEntry("Priority", priorityListBox));
        newTaskForm.add(new FormEntry("User", userTextBox));
        newTaskPanel.add(newTaskForm);

        addTaskButton = new Button("Add");
        addTaskButton.setConfirm(true);
        newTaskPanel.add(addTaskButton);
        scrollPanel.add(newTaskPanel);
    }

    @Override
    public void init(final NewTaskPresenter presenter) {
        this.presenter = presenter;
        assignToMeCheckBox.setValue(false);
        userTextBox.setText(identity.getIdentifier());

        addTaskButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                    List<String> users = new ArrayList<String>();
                    users.add(userTextBox.getText());
                    List<String> groups = new ArrayList<String>();
                    String taskName = taskNameTextBox.getText();
                    int priority = priorityListBox.getSelectedIndex();
                    boolean assignToMe = assignToMeCheckBox.getValue();
                    long time = 0;

                    presenter.addTask(users, groups, taskName, priority, assignToMe, time, time);

                    taskNameTextBox.setText("");
                    priorityListBox.setSelectedIndex(0);
                    assignToMeCheckBox.setValue(false);

                    placeManager.goTo("Tasks List", Animations.SLIDE);
            }
        });

        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                taskNameTextBox.setText("");
                placeManager.goTo("Tasks List", Animations.SLIDE_REVERSE);
            }
        });
    }

    @Override
    public void goBackToTaskList() {
        placeManager.goTo("Tasks List", Animations.SLIDE_REVERSE);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void setParameters(Map<String, Object> params) {

    }

}