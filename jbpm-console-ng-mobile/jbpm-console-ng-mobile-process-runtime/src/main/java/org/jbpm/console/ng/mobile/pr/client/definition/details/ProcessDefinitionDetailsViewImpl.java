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

import com.google.gwt.user.client.ui.HasText;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.form.Form;
import com.googlecode.mgwt.ui.client.widget.form.FormEntry;
import com.googlecode.mgwt.ui.client.widget.input.MTextArea;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;
import com.googlecode.mgwt.ui.client.widget.panel.scroll.ScrollPanel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;

/**
 *
 * @author livthomas
 */
public class ProcessDefinitionDetailsViewImpl extends AbstractView implements
        ProcessDefinitionDetailsPresenter.ProcessDefinitionDetailsView {

    private final MTextBox definitionIdText = new MTextBox();
    private final MTextBox definitionNameText = new MTextBox();
    private final MTextBox deploymentText = new MTextBox();
    private final MTextArea humanTasksText = new MTextArea();
    private final MTextArea usersAndGroupsText = new MTextArea();
    private final MTextArea subprocessesText = new MTextArea();
    private final MTextArea processVariablesText = new MTextArea();
    private final MTextArea servicesText = new MTextArea();

    private final Button newInstanceButton;
    private final Button instancesListButton;

    private String processId;
    private String deploymentId;

    @Inject
    private MGWTPlaceManager placeManager;

    private ProcessDefinitionDetailsPresenter presenter;

    public ProcessDefinitionDetailsViewImpl() {
        title.setTitle("Definition Details");

        ScrollPanel scrollPanel = new ScrollPanel();
        FlexPanel flexPanel = new FlexPanel();

        definitionIdText.setReadOnly(true);
        definitionNameText.setReadOnly(true);
        deploymentText.setReadOnly(true);
        humanTasksText.setReadOnly(true);
        usersAndGroupsText.setReadOnly(true);
        subprocessesText.setReadOnly(true);
        processVariablesText.setReadOnly(true);
        servicesText.setReadOnly(true);

        Form widgetList = new Form();
        widgetList.setRound(true);
        widgetList.add(new FormEntry("Definition Id", definitionIdText));
        widgetList.add(new FormEntry("Definition Name", definitionNameText));
        widgetList.add(new FormEntry("Deployment", deploymentText));
        widgetList.add(new FormEntry("Human Tasks", humanTasksText));
        widgetList.add(new FormEntry("Users and Groups", usersAndGroupsText));
        widgetList.add(new FormEntry("Subprocesses", subprocessesText));
        widgetList.add(new FormEntry("Process Variables", processVariablesText));
        widgetList.add(new FormEntry("Services", servicesText));
        flexPanel.add(widgetList);

        newInstanceButton = new Button("New Instance");
        newInstanceButton.setConfirm(true);
        flexPanel.add(newInstanceButton);

        instancesListButton = new Button("View Process Instances");
        flexPanel.add(instancesListButton);

        scrollPanel.setWidget(flexPanel);
        scrollPanel.setScrollingEnabledX(false);
        scrollPanel.setUsePos(MGWT.getOsDetection().isAndroid());
        rootFlexPanel.add(scrollPanel);
    }

    @Override
    public void init(final ProcessDefinitionDetailsPresenter presenter) {
        this.presenter = presenter;

        headerBackButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Process Definitions List", Animations.SLIDE_REVERSE);
            }
        });

        newInstanceButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                presenter.startProcess(deploymentId, processId);
            }
        });

        instancesListButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("definitionId", processId);
                placeManager.goTo("Process Instances List", Animations.SLIDE_REVERSE, params);
            }
        });
    }

    @Override
    public void refresh() {
        presenter.refresh(deploymentId, processId);
    }

    @Override
    public void setParameters(Map<String, Object> params) {
        processId = (String) params.get("processId");
        deploymentId = (String) params.get("deploymentId");
    }

	@Override
	public HasText getDefinitionIdText() {
		return (HasText) definitionIdText;
	}

	@Override
	public HasText getDefinitionNameText() {
		return (HasText) definitionNameText;
	}

	@Override
	public HasText getDeploymentText() {
		return (HasText) deploymentText;
	}

	@Override
	public HasText getHumanTasksText() {
		return (HasText) humanTasksText;
	}

	@Override
	public HasText getUsersAndGroupsText() {
		return (HasText) usersAndGroupsText;
	}

	@Override
	public HasText getSubprocessesText() {
		return (HasText) subprocessesText;
	}

	@Override
	public HasText getProcessVariablesText() {
		return (HasText) processVariablesText;
	}

	@Override
	public HasText getServicesText() {
		return (HasText) servicesText;
	}

}