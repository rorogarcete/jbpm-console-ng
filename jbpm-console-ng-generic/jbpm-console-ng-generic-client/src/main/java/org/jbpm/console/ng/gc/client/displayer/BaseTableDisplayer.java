/*
 * Copyright 2015 JBoss by Red Hat.
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
package org.jbpm.console.ng.gc.client.displayer;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.client.DataSetHandlerImpl;
import org.dashbuilder.renderer.client.table.TableDisplayer;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

/**
 * Base implementation for all the table displayers within the jBPM console
 */
public abstract class BaseTableDisplayer extends TableDisplayer {

    List<TableSettings> tableSettingsList =  new ArrayList<TableSettings>();
    protected boolean tableRefreshEnabled = false;
    protected boolean tableSelectorEnabled = false;
    protected boolean tableCreationEnabled = false;
    protected transient String _currentTable = null;

    public boolean isTableRefreshEnabled() {
        return tableRefreshEnabled;
    }

    public void setTableRefreshEnabled(boolean tableRefreshEnabled) {
        this.tableRefreshEnabled = tableRefreshEnabled;
    }

    public boolean isTableSelectorEnabled() {
        return tableSelectorEnabled;
    }

    public void setTableSelectorEnabled(boolean tableSelectorEnabled) {
        this.tableSelectorEnabled = tableSelectorEnabled;
    }

    public boolean isTableCreationEnabled() {
        return tableCreationEnabled;
    }

    public void setTableCreationEnabled(boolean tableCreationEnabled) {
        this.tableCreationEnabled = tableCreationEnabled;
    }

    public List<TableSettings> getTableSettingsList() {
        return tableSettingsList;
    }

    public void addTableSettings(String name, boolean editable, TableSettings settings) {
        settings.setTableName(name);
        settings.setEditable(editable);
        tableSettingsList.add(settings);

        // Take the first registered settings as the default one
        if (super.getDisplayerSettings() == null) {
            init(settings);
        }
    }

    public void addTableSettings(TableSettings settings) {
        tableSettingsList.add(settings);
    }

    public void removeTableSettings(TableSettings settings) {
        tableSettingsList.remove(settings);
    }

    public void clearTableSettingsList() {
        tableSettingsList.clear();
    }

    public void init(TableSettings settings) {
        super.setDisplayerSettings(settings);
        super.setDataSetHandler(new DataSetHandlerImpl(settings.getDataSetLookup()));
        _currentTable = settings.getTableName();
    }

    public void draw(TableSettings tableSettings) {
        init(tableSettings);

        super.drawn = false;
        super.draw();
    }

    public void draw(int settingsIdx) {
        if (settingsIdx >= 0 && settingsIdx < tableSettingsList.size()) {
            TableSettings tableSettings = tableSettingsList.get(settingsIdx);
            draw(tableSettings);
        }
    }

    public String missingColumn(String... columnIds) {
        for (String columnId : columnIds) {
            if (dataSet.getColumnById(columnId) == null) {
                return columnId;
            }
        }
        return null;
    }

    // TableDisplayer overrides

    @Override
    protected PagedTable<Integer> createTable() {
        PagedTable<Integer> pagedTable = super.createTable();
        if (isTableSelectorEnabled()) {
            Widget tableSelector = createTableSelector();
            pagedTable.getLeftToolbar().add(tableSelector);
        }
        if (isTableRefreshEnabled()) {
            Widget refreshWidget = createRefreshWidget();
            pagedTable.getRightToolbar().add(refreshWidget);
        }
        if (isTableCreationEnabled()) {
            // TODO: table settings creation
        }
        return pagedTable;
    }

    protected Widget createTableSelector() {
        final ListBox listBox = new ListBox();
        for (int i=0; i<tableSettingsList.size(); i++) {
            TableSettings settings = tableSettingsList.get(i);
            listBox.addItem(settings.getTableName());
            if (_currentTable != null && _currentTable.equals(settings.getTableName())) {
                listBox.setSelectedIndex(i);
            }
        }
        listBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                draw(listBox.getSelectedIndex());
            }
        });
        return listBox;
    }

    protected Widget createRefreshWidget() {
        Button refresh = new Button("Refresh");
        refresh.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                redraw();
            }
        });
        return refresh;
    }
}
