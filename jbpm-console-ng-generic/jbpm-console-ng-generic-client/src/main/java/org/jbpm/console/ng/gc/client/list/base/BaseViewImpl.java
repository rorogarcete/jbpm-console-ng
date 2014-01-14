/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.gc.client.list.base;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;

@Dependent
public abstract class BaseViewImpl<T, P> extends ActionsCellTaskList implements GridViewContainer, ButtonsPanelContainer,
        PagerContainer, RequiresResize {

    protected P presenter;
    protected DataGrid<T> myListGrid;
    protected ListHandler<T> sortHandler;
    private String currentFilter = "";

    @Inject
    private Event<NotificationEvent> notification;

    /* Layout */
    @Inject
    @DataField
    public LayoutPanel viewContainer;

    /* pager */
    @Inject
    @DataField
    public SimplePager pager;

    @Inject
    protected PlaceManager placeManager;

    protected void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    protected DataGrid<T> getListGrid() {
        return myListGrid;
    }

    @Override
    public void onResize() {
        if ((getParent().getOffsetHeight() - 120) > 0) {
            viewContainer.setHeight(getParent().getOffsetHeight() - 120 + "px");
            viewContainer.setWidth(getParent().getOffsetWidth() + "px");
        }

    }

    protected String getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(String currentFilter) {
        this.currentFilter = currentFilter;
    }

}
