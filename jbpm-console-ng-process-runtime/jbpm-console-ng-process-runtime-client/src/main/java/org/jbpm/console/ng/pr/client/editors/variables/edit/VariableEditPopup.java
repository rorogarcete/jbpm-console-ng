/*
 * Copyright 2014 JBoss Inc
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

package org.jbpm.console.ng.pr.client.editors.variables.edit;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class VariableEditPopup extends BaseModal {

    interface Binder
            extends
            UiBinder<Widget, VariableEditPopup> {

    }

    @UiField
    public FormControlStatic variableNameTextBox;

    @UiField
    public TextBox variableValueTextBox;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public FormGroup errorMessagesGroup;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;

    @Inject
    private Event<ProcessInstancesUpdateEvent> processInstancesUpdateEvent;

    private static Binder uiBinder = GWT.create( Binder.class );

    private long processInstanceId;

    public VariableEditPopup() {
        setTitle( Constants.INSTANCE.Edit() );

        setBody( uiBinder.createAndBindUi( this ) );
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( Constants.INSTANCE.Clear(),
                          new Command() {
                              @Override
                              public void execute() {
                                  variableValueTextBox.setText( "" );
                              }
                          }, null,
                          ButtonType.DEFAULT );

        footer.addButton( Constants.INSTANCE.Save(),
                          new Command() {
                              @Override
                              public void execute() {
                                  setProcessVariable();
                              }
                          }, null,
                          ButtonType.PRIMARY );

        add( footer );
    }

    public void show( long processInstanceId,
                      String variableId,
                      String variableValue ) {
        this.processInstanceId = processInstanceId;
        this.variableNameTextBox.setText( variableId );
        this.variableValueTextBox.setText( variableValue );
        cleanErrorMessages();
        super.show();
    }

    private void cleanErrorMessages() {
        errorMessages.setText( "" );
        errorMessagesGroup.setValidationState( ValidationState.NONE );
    }

    public void closePopup() {
        hide();
        super.hide();
        processInstancesUpdateEvent.fire( new ProcessInstancesUpdateEvent() );
    }

    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    public void setProcessVariable() {

        kieSessionServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {
                displayNotification( Constants.INSTANCE.VariableValueUpdated( variableNameTextBox.getText() ) );
                closePopup();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                errorMessages.setText( throwable.getMessage() );
                errorMessagesGroup.setValidationState( ValidationState.ERROR );
                //ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).setProcessVariable( processInstanceId, variableNameTextBox.getText(), variableValueTextBox.getValue() );
    }

}
