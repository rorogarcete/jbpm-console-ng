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
package org.jbpm.console.ng.mobile.ht.client.taskinputmappings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.form.Form;
import com.googlecode.mgwt.ui.client.widget.form.FormEntry;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
import com.googlecode.mgwt.ui.client.widget.panel.scroll.ScrollPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.jbpm.console.ng.mobile.core.client.AbstractView;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;

/**
 * @author rorogarcete
 */
public class TaskInputMappingListViewImpl extends AbstractView implements 
        TaskInputMappingListPresenter.TaskInputMappingListView {

    @Inject
    private MGWTPlaceManager placeManager;
    
    private TaskInputMappingListPresenter presenter;
    
    private String deploymentId;
    private String processId;
    private String taskName;
    private long taskId=0;
    private int count;
    
    private Map<String, Object> params;
    private Map<String, String> inputMappings;
    private List<MTextBox> inputTextBoxs;
    
    private Form form;
    private FlowPanel flowPanel;
    private MTextBox inputTextBox;
    private final Button completeTask;
    
    public TaskInputMappingListViewImpl() {
        title.setText("Task Input Mappings");
        
        ScrollPanel scrollPanel = new ScrollPanel();
        flowPanel = new FlowPanel();
        
        form = new Form();
        form.setRound(true);
        
        completeTask = new Button("Complete"); 
        completeTask.setConfirm(true);
        
        scrollPanel.setWidget(flowPanel);
        scrollPanel.setScrollingEnabledX(false);
        scrollPanel.setUsePos(MGWT.getOsDetection().isAndroid());
        rootFlexPanel.add(scrollPanel);
    }

    @Override
    public void init(final TaskInputMappingListPresenter p) {
        this.presenter = p;
        
        completeTask.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
            	setParams();
                presenter.completeTask(taskId, params);
                reset();
            }
        });
        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
            	reset();
                placeManager.goTo("Home", Animations.SLIDE_REVERSE);
            }
        }); 
        reset();
        refresh();
    }

    @Override
    public void refresh() {
    	presenter.getInputMappings(deploymentId, processId, taskName);
    }

    @Override
    public void setParameters(Map<String, Object> params) {
    	deploymentId = (String) params.get("deploymentId");
    	processId = (String) params.get("processId"); 
    	taskName = (String) params.get("taskName");
    	taskId = (Long) params.get("taskId");
    }
    
    private void reset(){
    	inputMappings = null;
    	inputTextBoxs = null;
    	params = null;
    	inputTextBox = null;
    	count = 0;
    	form.clear();
    }
    
    private Map<String, Object> setParams(){
		params = new HashMap<String, Object>();
		int i = 0;
		
		for (Iterator<Entry<String, String>> it = inputMappings.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> entry = it.next();
			if (i <= count) {
				params.put(entry.getKey(), inputTextBoxs.get(i).getText());
				GWT.log("clave: " + entry.getKey() + " - Indice: " + i + " valor: " + inputTextBoxs.get(i).getText());
				i++;
			}	
		}
		
    	return params;	
    }

	@Override
	public void render(Map<String, String> im) {
		this.inputMappings = im;
		inputTextBoxs = new ArrayList<MTextBox>();
		
		for (Iterator<Entry<String, String>> it = inputMappings.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> entry = it.next();
			inputTextBox = new MTextBox();
			form.add(new FormEntry(entry.getKey(), inputTextBox));
			inputTextBoxs.add(count, inputTextBox);
			count++;
	
			GWT.log("clave: " + entry.getKey() + " valor: " + entry.getValue());
		}
		
		flowPanel.add(form);
		flowPanel.add(completeTask);
	}

}