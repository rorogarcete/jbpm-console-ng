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
package org.jbpm.console.ng.mobile.core.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.button.ImageButton;
import com.googlecode.mgwt.ui.client.widget.dialog.Dialogs;
import com.googlecode.mgwt.ui.client.widget.header.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.header.HeaderTitle;
import com.googlecode.mgwt.ui.client.widget.panel.flex.RootFlexPanel;

/**
 *
 * @author livthomas
 */
public abstract class AbstractView implements IsWidget {

    protected final String[] priorities = {"0 - High", "1", "2", "3", "4", "5 - Medium", "6", "7", "8", "9", "10 - Low"};

    protected final RootFlexPanel rootFlexPanel;

    protected final HeaderPanel headerPanel;

    protected final ImageButton headerBackButton;

    protected final HeaderTitle title;

    public AbstractView() {
        rootFlexPanel = new RootFlexPanel();
        headerPanel = new HeaderPanel();

        title = new HeaderTitle();
        //headerPanel.setCenterWidget(title);

        headerBackButton = new ImageButton();
        //headerBackButton.setBackButton(true);
        headerBackButton.setText("Back");
        //headerPanel.setLeftWidget(headerBackButton);
        headerPanel.add(headerBackButton);

        rootFlexPanel.add(headerPanel);
    }

    @Override
    public Widget asWidget() {
        return rootFlexPanel;
    }

    public void displayNotification(String title, String text) {
        Dialogs.alert(title, text, null);
    }

    public HasTapHandlers getBackButton() {
        return headerBackButton;
    }

}