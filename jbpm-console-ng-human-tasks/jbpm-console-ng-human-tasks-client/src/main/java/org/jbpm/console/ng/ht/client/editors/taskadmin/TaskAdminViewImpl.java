/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.ht.client.editors.taskadmin;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskAdminViewImpl.html")
public class TaskAdminViewImpl extends Composite implements TaskAdminPresenter.TaskAdminView {

    private TaskAdminPresenter presenter;

    @Inject
    @DataField
    public Anchor adminDetailsAccordionLabel;

    @Inject
    @DataField
    public Label adminUserOrGroupLabel;

    @Inject
    @DataField
    public Label adminUsersGroupsControlsLabel;

    @Inject
    @DataField
    public TextBox adminUserOrGroupText;

    @Inject
    @DataField
    public Button adminForwardButton;

    @Inject
    @DataField
    public Label adminUsersGroupsControlsPanel;

    @Inject
    @DataField
    public FormLabel reminderDetailsAccordionLabel;

    @Inject
    @DataField
    public Label actualOwnerLabel;

    @Inject
    @DataField
    public Label actualOwnerPanel;

    @Inject
    @DataField
    public Button adminReminderButton;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( TaskAdminPresenter presenter ) {
        this.presenter = presenter;
        adminUserOrGroupLabel.setText( constants.Delegate_User() );
        adminDetailsAccordionLabel.setText( constants.Details() );
        adminForwardButton.setText( constants.Forward() );
        adminUsersGroupsControlsLabel.setText( constants.Potential_Owners() );
        adminUsersGroupsControlsPanel.setStyleName( "" );

        reminderDetailsAccordionLabel.setText( constants.Reminder_Details() );
        actualOwnerLabel.setText( constants.Actual_Owner() );
        actualOwnerPanel.setStyleName( "" );
        adminReminderButton.setText( constants.Reminder() );
    }

    @EventHandler("adminForwardButton")
    public void adminForwardButton( ClickEvent e ) {
        String userOrGroup = adminUserOrGroupText.getText();
        if ( !userOrGroup.equals( "" ) ) {
            presenter.forwardTask( userOrGroup );
            adminForwardButton.setEnabled( false );
        } else {
            displayNotification( "Please enter a user or a group to delegate the task" );
        }
    }

    @EventHandler("adminReminderButton")
    public void adminReminderButton( ClickEvent e ) {
        presenter.reminder();
    }

    @Override
    public Label getUsersGroupsControlsPanel() {
        return adminUsersGroupsControlsPanel;
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public Button getForwardButton() {
        return adminForwardButton;
    }

    @Override
    public TextBox getUserOrGroupText() {
        return adminUserOrGroupText;
    }

    @Override
    public Button getReminderButton() {
        return adminReminderButton;
    }

    @Override
    public Label getActualOwnerPanel() {
        return actualOwnerPanel;
    }
}
