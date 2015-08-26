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
package org.jbpm.console.ng.mobile.pr.client.deployment.list;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.button.ImageButton;
import com.googlecode.mgwt.ui.client.widget.image.ImageHolder;
import com.googlecode.mgwt.ui.client.widget.list.celllist.BasicCell;
import com.googlecode.mgwt.ui.client.widget.list.celllist.CellList;
import com.googlecode.mgwt.ui.client.widget.list.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.list.celllist.CellSelectedHandler;
import com.googlecode.mgwt.ui.client.widget.panel.pull.PullArrowHeader;
import com.googlecode.mgwt.ui.client.widget.panel.pull.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.panel.pull.PullPanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;
import org.jbpm.console.ng.mobile.core.client.AbstractView;

/**
 *
 * @author rorogarcete
 */
public class DeploymentUnitsListViewImpl extends AbstractView implements
        DeploymentUnitsListPresenter.DeploymentUnitsListView {
	
	private final ImageButton newDeployButton;

    private PullPanel pullPanel;

    private PullArrowHeader pullArrowHeader;

    private final CellList<KModuleDeploymentUnitSummary> cellList;

    private List<KModuleDeploymentUnitSummary> deploymentsList;

    private DeploymentUnitsListPresenter presenter;

    @Inject
    private MGWTPlaceManager placeManager;

    public DeploymentUnitsListViewImpl() {
        title.setText("Deployment List");
        
        newDeployButton = new ImageButton(ImageHolder.get().newItem());
        newDeployButton.setText("New Deploy");
        headerPanel.add(newDeployButton);

        pullPanel = new PullPanel();
        pullArrowHeader = new PullArrowHeader();
        pullPanel.setHeader(pullArrowHeader);
        rootFlexPanel.add(pullPanel);

        cellList = new CellList<KModuleDeploymentUnitSummary>(new BasicCell<KModuleDeploymentUnitSummary>() {
            @Override
            public String getDisplayString(KModuleDeploymentUnitSummary deploymentUnitSummary) {
                return deploymentUnitSummary.getGroupId()+ " : " + deploymentUnitSummary.getArtifactId() 
                		+ " : " + deploymentUnitSummary.getVersion();
            }
            
            @Override
			public boolean canBeSelected(KModuleDeploymentUnitSummary deploymentUnitSummary) {
				return true;
			}
        });
        pullPanel.add(cellList);

        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Home", Animations.SLIDE_REVERSE);
            }
        });
    }

    @Override
    public void render(List<KModuleDeploymentUnitSummary> deploymentUnitSummary) {
    	deploymentsList = deploymentUnitSummary;
        cellList.render(deploymentsList);
        pullPanel.refresh();
    }

    @Override
    public void init(final DeploymentUnitsListPresenter presenter) {
        this.presenter = presenter;
        
        newDeployButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                placeManager.goTo("Deployment", Animations.SLIDE);
            }
        });

        pullArrowHeader.setHTML("pull down");

        PullArrowStandardHandler headerHandler = new PullArrowStandardHandler(pullArrowHeader, pullPanel);

        headerHandler.setErrorText("Error");
        headerHandler.setLoadingText("Loading");
        headerHandler.setNormalText("pull down");
        headerHandler.setPulledText("release to load");
        headerHandler.setPullActionHandler(new PullArrowStandardHandler.PullActionHandler() {
            @Override
            public void onPullAction(final AsyncCallback<Void> callback) {
                new Timer() {
                    @Override
                    public void run() {
                        presenter.refresh();
                    }
                }.schedule(1000);

            }
        });
        pullPanel.setHeaderPullHandler(headerHandler);

        cellList.addCellSelectedHandler(new CellSelectedHandler() {
            @Override
            public void onCellSelected(CellSelectedEvent event) {
                Map<String, Object> params = new HashMap<String, Object>();
                KModuleDeploymentUnitSummary deploymentUnitSummary = deploymentsList.get(event.getIndex());
                params.put("deploymentUnitId", deploymentUnitSummary.getId());
                params.put("groupId", deploymentUnitSummary.getGroupId());
                params.put("artifactId", deploymentUnitSummary.getArtifactId());
                params.put("versionId", deploymentUnitSummary.getVersion());
                placeManager.goTo("Deployment", Animations.SLIDE, params);
            }
        });

        presenter.refresh();
    }

    @Override
    public void refresh() {
        presenter.refresh();
    }

    @Override
    public void setParameters(Map<String, Object> params) {

    }

}