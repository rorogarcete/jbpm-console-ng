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

import java.util.Map;

import javax.inject.Inject;

import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;

/**
 *
 * @author livthomas
 */
public class ProcessInstanceDetailsViewImpl extends AbstractView implements
        ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView {

    @Inject
    private MGWTPlaceManager placeManager;

    private ProcessInstanceDetailsPresenter presenter;

    private Long instanceId;
    private String definitionId;

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
        title.setTitle("Instance Details");

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

        WidgetList widgetList = new WidgetList();
        widgetList.setRound(true);
        widgetList.add(new FormEntry("Instance ID", instanceIdText));
        widgetList.add(new FormEntry("Definition ID", definitionIdText));
        widgetList.add(new FormEntry("Definition Name", definitionNameText));
        widgetList.add(new FormEntry("Definition Version", definitionVersionText));
        widgetList.add(new FormEntry("Deployment", deploymentText));
        widgetList.add(new FormEntry("Instance State", instanceStateText));
        widgetList.add(new FormEntry("Current Activities", currentActivitiesText));
        widgetList.add(new FormEntry("Instance Log", instanceLogText));
        flowPanel.add(widgetList);

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
        presenter.refresh(instanceId, definitionId);
    }

    @Override
    public void goToInstancesList() {
        placeManager.goTo("Process Instances List", Animations.SLIDE_REVERSE);
    }

    @Override
    public void setParameters(Map<String, Object> params) {
        instanceId = (Long) params.get("instanceId");
        definitionId = (String) params.get("definitionId");
    }

//    @Override
//    public MTextBox getInstanceIdText() {
//        return instanceIdText;
//    }
//
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
//    public MTextBox getDefinitionVersionText() {
//        return definitionVersionText;
//    }
//
//    @Override
//    public MTextBox getDeploymentText() {
//        return deploymentText;
//    }
//
//    @Override
//    public MTextBox getInstanceStateText() {
//        return instanceStateText;
//    }
//
//    @Override
//    public MTextArea getCurrentActivitiesText() {
//        return currentActivitiesText;
//    }
//
//    @Override
//    public MTextArea getInstanceLogText() {
//        return instanceLogText;
//    }

    @Override
    public Button getAbortButton() {
        return abortButton;
    }

	@Override
	public HasText getInstanceIdText() {
		// TODO Auto-generated method stub
		return null;
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
	public HasText getDefinitionVersionText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getDeploymentText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getInstanceStateText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getCurrentActivitiesText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HasText getInstanceLogText() {
		// TODO Auto-generated method stub
		return null;
	}

}