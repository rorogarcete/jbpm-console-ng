/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "Home Perspective", isDefault = true)
public class HomePerspective extends FlowPanel {

//    @Inject
//    @WorkbenchPanel(parts = "Home Screen")
//    FlowPanel homeScreen;
//
//    @PostConstruct
//    private void init() {
//        Layouts.setToFillParent( homeScreen );
//        add( homeScreen );
//    }
    
    @Perspective
    public PerspectiveDefinition getPerspective() {
//        final PerspectiveDefinition p = new PerspectiveDefinitionImpl(PanelType.ROOT_STATIC);
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName("Home Perspective");
        p.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("MobilePresenter")));
        //p.setTransient(true);
        return p;
    }
}
