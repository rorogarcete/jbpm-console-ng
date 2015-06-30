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
package org.jbpm.console.ng.mobile.pr.client.deployment.newunit;

import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.form.Form;
import com.googlecode.mgwt.ui.client.widget.form.FormEntry;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
import com.googlecode.mgwt.ui.client.widget.list.widgetlist.WidgetList;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;
import com.googlecode.mgwt.ui.client.widget.panel.scroll.ScrollPanel;

import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;

/**
 *
 * @author rorogarcete
 */
@Dependent
public class NewDeploymentViewGwtImpl extends AbstractView implements NewDeploymentPresenter.NewDeploymentView {

    private final MTextBox groupTextBox = new MTextBox();
    private final MTextBox artifactTextBox = new MTextBox();
    private final MTextBox versionTextBox = new MTextBox();
    
    private final Button deployButton;

    private NewDeploymentPresenter presenter;

    @Inject
    private MGWTPlaceManager placeManager;

    public NewDeploymentViewGwtImpl() {

        title.setTitle("New Deployment");

        ScrollPanel scrollPanel = new ScrollPanel();
        rootFlexPanel.add(scrollPanel);

        FlexPanel newDeploymentPanel = new FlexPanel();

        Form newDeploymentForm = new Form();
        newDeploymentForm.setRound(true); 
        newDeploymentForm.add(new FormEntry("Group", groupTextBox));
        newDeploymentForm.add(new FormEntry("Artifact", artifactTextBox));
        newDeploymentForm.add(new FormEntry("Version", versionTextBox));
        
        newDeploymentPanel.add(newDeploymentForm);

        deployButton = new Button("Deploy");
        deployButton.setConfirm(true);
        
        newDeploymentPanel.add(deployButton);
        scrollPanel.add(newDeploymentPanel);
    }

    @Override
    public void init(final NewDeploymentPresenter presenter) {
        this.presenter = presenter;

        deployButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                    String group = groupTextBox.getText();
                    String artifact = artifactTextBox.getText();
                    String version = versionTextBox.getText();

                    presenter.deployUnit(group, artifact, version);

                    groupTextBox.setText("");
                    artifactTextBox.setText("");
                    versionTextBox.setText("");

                    placeManager.goTo("Deployment List", Animations.SLIDE_REVERSE);
            }
        });

        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
            	placeManager.goTo("Deployment List", Animations.SLIDE_REVERSE);
            }
        });
    }

    @Override
    public void refresh() {
    	
    }

    @Override
    public void setParameters(Map<String, Object> params) {

    }

	@Override
	public void goBackToDeploymentList() {
		placeManager.goTo("Deployment List", Animations.SLIDE);
	}

}