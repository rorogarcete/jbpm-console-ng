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
package org.jbpm.console.ng.ht.client.perspectives;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.kie.workbench.common.widgets.client.search.SearchBehavior;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;

/**
 * A perspective to show a displayer based task list
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "Tasks (new)")
public class DashTasksListPerspective {

    @Inject
    private ContextualSearch contextualSearch;

    @Inject
    private Event<SearchEvent> searchEvents;

    @Inject
    DisplayerSettingsJSONMarshaller jsonHelper;

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        p.setName("Tasks");
        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "Task List (new)" ) ) );
        return p;
    }

    @OnStartup
    public void init() {
        contextualSearch.setSearchBehavior( new SearchBehavior() {
            @Override
            public void execute( String searchFilter ) {
                searchEvents.fire( new SearchEvent( searchFilter ) );
            }

        } );
    }
}
