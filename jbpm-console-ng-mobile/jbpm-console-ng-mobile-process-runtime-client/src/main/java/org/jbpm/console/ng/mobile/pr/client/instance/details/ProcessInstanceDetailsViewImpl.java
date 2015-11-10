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
package org.jbpm.console.ng.mobile.pr.client.instance.details;

import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.form.Form;
import com.googlecode.mgwt.ui.client.widget.form.FormEntry;
import com.googlecode.mgwt.ui.client.widget.input.MTextArea;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
import com.googlecode.mgwt.ui.client.widget.panel.scroll.ScrollPanel;

import java.util.Map;

import javax.inject.Inject;

import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;

/**
 * @author livthomas
 * @author rorogarcete
 */
public class ProcessInstanceDetailsViewImpl extends AbstractView implements
        ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView {

    @Inject
    
    private MGWTPlaceManager placeManager;

    private ProcessInstanceDetailsPresenter presenter;

    private Long instanceId;
    private String definitionId;
    private String deploymentId;

    private final MTextBox instanceIdText = new MTextBox();
    private final MTextBox definitionIdText = new MTextBox();
    private final MTextBox definitionNameText = new MTextBox();
    private final MTextBox definitionVersionText = new MTextBox();
    private final MTextBox deploymentText = new MTextBox();
    private final MTextBox instanceStateText = new MTextBox();
    private final MTextArea currentActivitiesText = new MTextArea();
    private final MTextArea instanceLogText = new MTextArea();

    private final Button abortButton;

    public ProcessInstanceDetailsViewImpl() {
        title.setText("Instance Details");
      
        //Details
        ScrollPanel scrollPanel = new ScrollPanel();
        FlowPanel flowPanel = new FlowPanel();

        instanceIdText.setReadOnly(true);
        definitionIdText.setReadOnly(true);
        definitionNameText.setReadOnly(true);
        definitionVersionText.setReadOnly(true);
        deploymentText.setReadOnly(true);
        instanceStateText.setReadOnly(true);
        currentActivitiesText.setReadOnly(true);
        instanceLogText.setReadOnly(true);

        Form form = new Form();
        form.setRound(true);
        form.add(new FormEntry("Instance ID", instanceIdText));
        form.add(new FormEntry("Definition ID", definitionIdText));
        form.add(new FormEntry("Definition Name", definitionNameText));
        form.add(new FormEntry("Definition Version", definitionVersionText));
        form.add(new FormEntry("Deployment", deploymentText));
        form.add(new FormEntry("Instance State", instanceStateText));
        form.add(new FormEntry("Current Activities", currentActivitiesText));
        form.add(new FormEntry("Instance Log", instanceLogText));
        flowPanel.add(form);

        abortButton = new Button("Abort");
        abortButton.setImportant(true);
        flowPanel.add(abortButton);
        scrollPanel.setWidget(flowPanel);
        scrollPanel.setScrollingEnabledX(false);
        scrollPanel.setUsePos(MGWT.getOsDetection().isAndroid());
        
        rootFlexPanel.add(scrollPanel);
    }

    @Override
    public void init(final ProcessInstanceDetailsPresenter presenter) {
        this.presenter = presenter;

        headerBackButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Process Instances List", Animations.SLIDE_REVERSE);
            }
        });

        abortButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                presenter.abortProcessInstance(instanceId); 
            }
        });
    }

    @Override
    public void refresh() {
        presenter.refresh(instanceId, definitionId, deploymentId);
    }

    @Override
    public void goToInstancesList() {
        placeManager.goTo("Process Instances List", Animations.SLIDE_REVERSE);
    }

    @Override
    public void setParameters(Map<String, Object> params) {
    	if (params == null || !params.containsKey("deploymentId")) {
    		deploymentId = null;
        } else {
        	deploymentId = (String) params.get("deploymentId");
        }
    	
        instanceId = (Long) params.get("instanceId");
        definitionId = (String) params.get("definitionId");
    }

    @Override
    public Button getAbortButton() {
        return abortButton;
    }

	@Override
	public void setInstanceIdText(String text) {
		instanceIdText.setText(text);
	}

	@Override
	public void setDefinitionIdText(String text) {
		 definitionIdText.setText(text);
	}

	@Override
	public void setDefinitionNameText(String text) {
		definitionNameText.setText(text);
	}

	@Override
	public void setDefinitionVersionText(String text) {
		 definitionVersionText.setText(text);
	}

	@Override
	public void setDeploymentText(String text) {
		deploymentText.setText(text);
	}

	@Override
	public void setInstanceStateText(String text) {
		instanceStateText.setText(text);
	}

	@Override
	public void setCurrentActivitiesText(String text) {
		currentActivitiesText.setText(text);
	}

	@Override
	public void setInstanceLogText(String text) {
		instanceLogText.setText(text);
	}

}