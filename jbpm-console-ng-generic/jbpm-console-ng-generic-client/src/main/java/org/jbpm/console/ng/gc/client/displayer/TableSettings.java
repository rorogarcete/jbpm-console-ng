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

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerType;

/**
 * Custom settings class holding the configuration of any jBPM table displayer
 */
public class TableSettings extends DisplayerSettings {

    protected String tableName;
    protected boolean editable;

    public TableSettings() {
        super(DisplayerType.TABLE);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean equals(Object obj) {
        try {
            TableSettings other = (TableSettings) obj;
            if (tableName == null || other.tableName == null) return false;
            if (!tableName.equals(other.tableName)) return false;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public static TableSettings create(String name, DisplayerSettings settings) {
        TableSettings tableSettings = new TableSettings();
        tableSettings.setTableName(name);
        tableSettings.setType(DisplayerType.TABLE);
        tableSettings.setUUID(settings.getUUID());
        tableSettings.setDataSet(settings.getDataSet());
        tableSettings.setDataSetLookup(settings.getDataSetLookup());
        tableSettings.setColumnSettingsList(settings.getColumnSettingsList());
        tableSettings.getSettingsFlatMap().putAll(settings.getSettingsFlatMap());
        return tableSettings;
    }
}
