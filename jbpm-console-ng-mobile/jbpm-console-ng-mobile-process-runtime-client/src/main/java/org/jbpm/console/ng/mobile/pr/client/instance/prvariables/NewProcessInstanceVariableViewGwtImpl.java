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
package org.jbpm.console.ng.mobile.pr.client.instance.prvariables;

import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.form.Form;
import com.googlecode.mgwt.ui.client.widget.form.FormEntry;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
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
public class NewProcessInstanceVariableViewGwtImpl extends AbstractView implements NewProcessInstanceVariablePresenter.NewProcessInstanceVariableView {

    private final MTextBox nameTextBox = new MTextBox();
    private final MTextBox valueTextBox = new MTextBox();
    
    private final Button newInstanceButton;

    private NewProcessInstanceVariablePresenter presenter;

    @Inject
    private MGWTPlaceManager placeManager;

    public NewProcessInstanceVariableViewGwtImpl() {

        title.setText("New Process Instance Variable");

        ScrollPanel scrollPanel = new ScrollPanel();
        rootFlexPanel.add(scrollPanel);

        FlowPanel newProcessVariablePanel = new FlowPanel();

        Form newProcessInstanceForm = new Form();
        newProcessInstanceForm.setRound(true); 
        newProcessInstanceForm.add(new FormEntry("Name", nameTextBox));
        newProcessInstanceForm.add(new FormEntry("Value", valueTextBox));
        
        newProcessVariablePanel.add(newProcessInstanceForm);

        newInstanceButton = new Button("Start");
        newInstanceButton.setConfirm(true);
        
        newProcessVariablePanel.add(newInstanceButton);
        scrollPanel.add(newProcessVariablePanel);
    }

    @Override
    public void init(final NewProcessInstanceVariablePresenter presenter) {
        this.presenter = presenter;

        newInstanceButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                    String name = nameTextBox.getText();
                    String value = valueTextBox.getText();

                    //presenter.startProcess(deploymentId, processId);

                    nameTextBox.setText("");
                    valueTextBox.setText("");

                    placeManager.goTo("Process Instance List", Animations.SLIDE_REVERSE);
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
	public void goBackToProcessDefinitionDetails() {
		
	}
}