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

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.jbpm.console.ng.gc.client.util.DataGridUtils;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.ListDataProvider;

public abstract class BaseViewImpl<T, P> extends Composite implements GridViewContainer, ButtonsPanelContainer,
        PagerContainer, RequiresResize {

    protected static Constants constants = GWT.create(Constants.class);

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
    
    protected void initializeComponents(P presenter, ListDataProvider<T> dataProvider){
        this.presenter = presenter;
        this.initializeGridView(dataProvider);
        this.initializeLeftButtons();
        this.initializeRightButtons();
    }
    
    protected void initializeGridView(ListDataProvider<T> dataProvider){
        viewContainer.clear();

        myListGrid = new DataGrid<T>();
        myListGrid.setStyleName(GRID_STYLE);

        pager.setDisplay(myListGrid);
        pager.setPageSize(DataGridUtils.pageSize);

        viewContainer.add(myListGrid);
        myListGrid.setEmptyTableWidget(new HTMLPanel(constants.No_Items_Found()));

        sortHandler = new ColumnSortEvent.ListHandler<T>(dataProvider.getList());

        myListGrid.getColumnSortList().setLimit(1);

        this.setSelectionModel();
        this.setGridEvents();
        this.initGridColumns();

        myListGrid.addColumnSortHandler(sortHandler);

        dataProvider.addDataDisplay(myListGrid);

        this.refreshItems();
    }

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
