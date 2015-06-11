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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.form.FormEntry;
import com.googlecode.mgwt.ui.client.widget.input.MTextArea;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
import com.googlecode.mgwt.ui.client.widget.list.widgetlist.WidgetList;
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
        FlowPanel flowPanel = new FlowPanel();

        definitionIdText.setReadOnly(true);
        definitionNameText.setReadOnly(true);
        deploymentText.setReadOnly(true);
        humanTasksText.setReadOnly(true);
        usersAndGroupsText.setReadOnly(true);
        subprocessesText.setReadOnly(true);
        processVariablesText.setReadOnly(true);
        servicesText.setReadOnly(true);

        WidgetList widgetList = new WidgetList();
        widgetList.setRound(true);
        widgetList.add(new FormEntry("Definition Id", definitionIdText));
        widgetList.add(new FormEntry("Definition Name", definitionNameText));
        widgetList.add(new FormEntry("Deployment", deploymentText));
        widgetList.add(new FormEntry("Human Tasks", humanTasksText));
        widgetList.add(new FormEntry("Users and Groups", usersAndGroupsText));
        widgetList.add(new FormEntry("Subprocesses", subprocessesText));
        widgetList.add(new FormEntry("Process Variables", processVariablesText));
        widgetList.add(new FormEntry("Services", servicesText));
        flowPanel.add(widgetList);

        newInstanceButton = new Button("New Instance");
        newInstanceButton.setConfirm(true);
        flowPanel.add(newInstanceButton);

        instancesListButton = new Button("View Process Instances");
        flowPanel.add(instancesListButton);

        scrollPanel.setWidget(flowPanel);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getDefinitionNameText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getDeploymentText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getHumanTasksText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getUsersAndGroupsText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getSubprocessesText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getProcessVariablesText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getServicesText() {
		// TODO Auto-generated method stub
		return null;
	}

//    @Override
//    public MTextBox getDefinitionIdText() {
//        return definitionIdText;
//    }
//
//    @Override
//    public MTextBox getDefinitionNameText() {
//        return definitionNameText;
//    }
//
//    @Override
//    public MTextBox getDeploymentText() {
//        return deploymentText;
//    }
//
//    @Override
//    public MTextArea getHumanTasksText() {
//        return humanTasksText;
//    }
//
//    @Override
//    public MTextArea getUsersAndGroupsText() {
//        return usersAndGroupsText;
//    }
//
//    @Override
//    public MTextArea getSubprocessesText() {
//        return subprocessesText;
//    }
//
//    @Override
//    public MTextArea getProcessVariablesText() {
//        return processVariablesText;
//    }
//
//    @Override
//    public MTextArea getServicesText() {
//        return servicesText;
//    }

}
