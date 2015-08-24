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
package org.jbpm.console.ng.mobile.pr.client.variable.list;

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
 *
 * @author rorogarcete
 */
public class ProcessVariableListViewImpl extends AbstractView implements 
        ProcessVariableListPresenter.ProcessVariableListView {

    @Inject
    private MGWTPlaceManager placeManager;
    
    private ProcessVariableListPresenter presenter;
    
    private String deploymentId;
    private String processId;
    private int count;
    
    private Map<String, Object> params;
    private Map<String, String> processVariables;
    private List<MTextBox> listInputs;
    
    private Form form;
    private FlowPanel flowPanel;
    private MTextBox values;
    private final Button startProcessInstance;
    
    public ProcessVariableListViewImpl() {
        title.setText("Process Variables");
        
        ScrollPanel scrollPanel = new ScrollPanel();
        flowPanel = new FlowPanel();
        
        form = new Form();
        form.setRound(true);
        
        startProcessInstance = new Button("Start"); 
        startProcessInstance.setConfirm(true);
        flowPanel.add(startProcessInstance);
        
        scrollPanel.setWidget(flowPanel);
        scrollPanel.setScrollingEnabledX(false);
        scrollPanel.setUsePos(MGWT.getOsDetection().isAndroid());
        rootFlexPanel.add(scrollPanel);
    }

    @Override
    public void init(final ProcessVariableListPresenter p) {
        this.presenter = p;

        count = 0;
        listInputs = null;
        processVariables = null;
        params = null;
        
        startProcessInstance.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
            	GWT.log("Paso aqui 1");
            	llenarCampos();
                presenter.startProcess(deploymentId, processId, params);
                GWT.log("VALORES " + deploymentId + " : " + processId + " : " + params.toString());
                //placeManager.goTo("Process Instances List", Animations.SLIDE_REVERSE);
            }
        });
        
        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Home", Animations.SLIDE_REVERSE);
            }
        }); 
        
        refresh();
    }

    @Override
    public void refresh() {
    	presenter.getProcessVariables(deploymentId, processId);
    }

    @Override
    public void setParameters(Map<String, Object> params) {
    	deploymentId = (String) params.get("deploymentId");
    	processId = (String) params.get("processId");  
    }
    
    private Map<String, Object> llenarCampos(){
    	if (params == null) {
			params = new HashMap<String, Object>();
			
			for (Iterator<Entry<String, String>> it = processVariables.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> entry = it.next();
				GWT.log("ENTRO EN LLENAR CAMPOS");
			
				for (int i = 0; i < listInputs.size(); i++) {
					params.put(entry.getKey(), listInputs.get(i).getText());
					GWT.log("clave: " + entry.getKey() + " valor: " + listInputs.get(count).getText());
				}
			}
		}
    	GWT.log("Retorna el Map cargado..");
    	processVariables.clear();
    	listInputs.clear();
    	form.clear();
    	return params;	
    }

	@Override
	public void render(Map<String, String> pv) {
		this.processVariables = pv;
		listInputs = new ArrayList<MTextBox>();
		
		for (Iterator<Entry<String, String>> it = processVariables.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> entry = it.next();
			GWT.log("Paso aqui 2");
			values = new MTextBox();
			form.add(new FormEntry(entry.getKey(), values));
			listInputs.add(count, values);
			count++;
			GWT.log("Paso en el render  :" + count);
			//testes
			String numero = entry.getKey();
			String cadena = entry.getValue();
			GWT.log("clave: " + numero + " valor: " + cadena);
		}
		
		flowPanel.add(form);
	}

}