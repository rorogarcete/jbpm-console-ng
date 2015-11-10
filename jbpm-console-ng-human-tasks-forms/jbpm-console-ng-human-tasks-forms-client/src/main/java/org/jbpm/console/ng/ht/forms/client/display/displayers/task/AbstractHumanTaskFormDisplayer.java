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
package org.jbpm.console.ng.ht.forms.client.display.displayers.task;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ga.forms.display.FormDisplayerConfig;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.gc.forms.client.display.displayers.util.ActionRequest;
import org.jbpm.console.ng.gc.forms.client.display.displayers.util.JSNIHelper;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskFormDisplayer;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;

/**
 *
 * @author salaboy
 */
public abstract class AbstractHumanTaskFormDisplayer implements HumanTaskFormDisplayer {
    public static final String ACTION_CLAIM_TASK = "claimTask";
    public static final String ACTION_START_TASK = "startTask";
    public static final String ACTION_RELEASE_TASK = "releaseTask";
    public static final String ACTION_SAVE_TASK = "saveTask";
    public static final String ACTION_COMPLETE_TASK = "completeTask";

    protected long taskId = -1;
    protected String formContent;
    protected String opener;
    protected String taskName;
    protected String deploymentId;

    final protected FormPanel container = new FormPanel();
    final protected FlowPanel buttonsContainer = new FlowPanel();
    final protected FlowPanel formContainer = new FlowPanel();

    private Command onClose;

    private Command onRefresh;

    protected FormContentResizeListener resizeListener;

    protected Constants constants = GWT.create(Constants.class);

    @Inject
    protected ErrorPopupPresenter errorPopup;

    @Inject
    protected Caller<TaskLifeCycleService> taskServices;

    @Inject
    protected Caller<TaskOperationsService> taskOperationServices;

    @Inject
    protected Event<TaskRefreshedEvent> taskRefreshed;

    @Inject
    protected User identity;

    @Inject
    protected JSNIHelper jsniHelper;

    protected abstract void initDisplayer();

    protected abstract void completeFromDisplayer();

    protected abstract void saveStateFromDisplayer();

    protected abstract void startFromDisplayer();

    protected abstract void claimFromDisplayer();

    protected abstract void releaseFromDisplayer();

    @PostConstruct
    protected void init() {
        container.getElement().setId("form-data");
    }

    @Override
    public void init(FormDisplayerConfig<TaskKey> config, Command onCloseCommand, Command onRefreshCommand, FormContentResizeListener resizeListener) {
        this.taskId = config.getKey().getTaskId();
        this.formContent = config.getFormContent();
        this.opener = config.getFormOpener();
        this.resizeListener = resizeListener;
        this.onClose = onCloseCommand;
        this.onRefresh = onRefreshCommand;
        if ( formContainer.getParent() != container ) {
            container.add(formContainer);
        }

        if (formContent == null || formContent.length() == 0) {
            return;
        }
        taskOperationServices.call(new RemoteCallback<TaskSummary>() {
            @Override
            public void callback(final TaskSummary task) {
                if (task == null) {
                    return;
                }
                buttonsContainer.clear();
                taskName = task.getTaskName();
                deploymentId = task.getDeploymentId();
                if (opener != null) {
                    injectEventListener(AbstractHumanTaskFormDisplayer.this);
                } else {
                    FlowPanel wrapperFlowPanel = new FlowPanel();
                    wrapperFlowPanel.setStyleName("wrapper form-actions");
                    buttonsContainer.add(wrapperFlowPanel);

                    if (task.getStatus().equals("Ready")) {
                        Button claimButton = new Button();
                        claimButton.setType(ButtonType.PRIMARY);
                        claimButton.setText(constants.Claim());
                        claimButton.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                claimFromDisplayer();
                            }
                        });
                        wrapperFlowPanel.add(claimButton);
                        buttonsContainer.add(wrapperFlowPanel);
                    }

                    if (task.getStatus().equals("Reserved") && task.getActualOwner().equals(identity.getIdentifier())) {

                        Button releaseButton = new Button();
                        releaseButton.setText(constants.Release());
                        releaseButton.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                releaseFromDisplayer();
                            }
                        });
                        wrapperFlowPanel.add(releaseButton);

                        Button startButton = new Button();
                        startButton.setType(ButtonType.PRIMARY);
                        startButton.setText(constants.Start());
                        startButton.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                startFromDisplayer();
                            }
                        });
                        wrapperFlowPanel.add(startButton);

                        buttonsContainer.add(wrapperFlowPanel);
                    } else if (task.getStatus().equals("InProgress") && task.getActualOwner().equals(identity.getIdentifier())) {
                        Button saveButton = new Button();
                        saveButton.setText(constants.Save());
                        saveButton.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                saveStateFromDisplayer();

                            }
                        });
                        wrapperFlowPanel.add(saveButton);

                        Button releaseButton = new Button();
                        releaseButton.setText(constants.Release());
                        releaseButton.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                releaseFromDisplayer();
                            }
                        });
                        wrapperFlowPanel.add(releaseButton);

                        Button completeButton = new Button();
                        completeButton.setType(ButtonType.PRIMARY);
                        completeButton.setText(constants.Complete());
                        completeButton.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                completeFromDisplayer();
                            }
                        });

                        wrapperFlowPanel.add(completeButton);
                        buttonsContainer.add(wrapperFlowPanel);

                    }
                }
                initDisplayer();
            }
        }, getUnexpectedErrorCallback()).getTaskDetails(taskId);
    }

    @Override
    public void complete(Map<String, Object> params) {
        taskServices.call(getCompleteTaskRemoteCallback(), getUnexpectedErrorCallback())
                .complete(taskId, identity.getIdentifier(), params);
    }

    @Override
    public void claim() {
        taskServices.call(getClaimTaskCallback(), getUnexpectedErrorCallback()).claim(taskId, identity.getIdentifier(), deploymentId);
    }

    @Override
    public void release() {
        taskServices.call(getReleaseTaskRemoteCallback(), getUnexpectedErrorCallback()).release(taskId, identity.getIdentifier());
    }

    @Override
    public void saveState(Map<String, Object> state) {
        taskOperationServices.call(getSaveTaskStateCallback(), getUnexpectedErrorCallback()).saveContent(taskId, state);
    }

    @Override
    public void start() {
        taskServices.call(getStartTaskRemoteCallback(), getUnexpectedErrorCallback()).start(taskId, identity.getIdentifier());
    }

    @Override
    public Panel getContainer() {
        return container;
    }

    @Override
    public IsWidget getFooter() {
        return buttonsContainer;
    }

    protected RemoteCallback getStartTaskRemoteCallback() {
        return new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                taskRefreshed.fire(new TaskRefreshedEvent(taskId));
                jsniHelper.notifySuccessMessage(opener, "Task: " + taskId + " was started!");
                refresh();
            }
        };
    }

    protected RemoteCallback getClaimTaskCallback() {
        return new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                taskRefreshed.fire(new TaskRefreshedEvent(taskId));
                jsniHelper.notifySuccessMessage(opener, "Task: " + taskId + " was claimed!");
                refresh();
            }
        };
    }

    protected RemoteCallback getSaveTaskStateCallback() {
        return new RemoteCallback<Long>() {
            @Override
            public void callback(Long contentId) {
                taskRefreshed.fire(new TaskRefreshedEvent(taskId));
                jsniHelper.notifySuccessMessage(opener, "Task: " + taskId + " was saved!");
                refresh();
            }
        };
    }

    protected RemoteCallback getReleaseTaskRemoteCallback() {
        return new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                taskRefreshed.fire(new TaskRefreshedEvent(taskId));
                jsniHelper.notifySuccessMessage(opener, "Task: " + taskId + " was released!");
                refresh();
            }
        };
    }

    protected RemoteCallback<Void> getCompleteTaskRemoteCallback() {
        return new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                taskOperationServices.call(new RemoteCallback<Boolean>() {
                    @Override
                    public void callback(Boolean response) {
                        close();
                    }
                }, getUnexpectedErrorCallback()).existInDatabase(taskId);
                taskRefreshed.fire(new TaskRefreshedEvent(taskId));
                jsniHelper.notifySuccessMessage(opener, "Task: " + taskId + " was completed!");


            }
        };
    }

    protected ErrorCallback<Message> getUnexpectedErrorCallback() {
        return new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                String notification = "Unexpected error encountered : " + throwable.getMessage();
                errorPopup.showMessage(notification);
                jsniHelper.notifyErrorMessage(opener, notification);
                return true;
            }
        };
    }

    @Override
    public void addOnCloseCallback(Command callback) {
        this.onClose = callback;
    }

    @Override
    public void addOnRefreshCallback(Command callback) {
        this.onRefresh = callback;
    }


    protected void refresh(){
        if(this.onRefresh != null){
            this.onRefresh.execute();
        }
    }

    @Override
    public void close() {
        if(this.onClose != null){
            this.onClose.execute();
        }
        clearStatus();
    }

    protected void clearStatus() {
        taskId = -1;
        formContent = null;
        opener = null;
        taskName = null;
        deploymentId = null;

        buttonsContainer.clear();
        formContainer.clear();

        onClose = null;
        onRefresh = null;
        resizeListener = null;
    }

    protected void eventListener(String origin, String request) {
        if (origin == null || !origin.endsWith("//" + opener)) return;

        ActionRequest actionRequest = JsonUtils.safeEval(request);

        if (ACTION_CLAIM_TASK.equals(actionRequest.getAction())) claimFromDisplayer();
        else if (ACTION_START_TASK.equals(actionRequest.getAction())) startFromDisplayer();
        else if (ACTION_RELEASE_TASK.equals(actionRequest.getAction())) releaseFromDisplayer();
        else if (ACTION_SAVE_TASK.equals(actionRequest.getAction())) saveStateFromDisplayer();
        else if (ACTION_COMPLETE_TASK.equals(actionRequest.getAction())) completeFromDisplayer();
    }

    private native void injectEventListener(AbstractHumanTaskFormDisplayer fdp) /*-{
        function postMessageListener(e) {
            fdp.@org.jbpm.console.ng.ht.forms.client.display.displayers.task.AbstractHumanTaskFormDisplayer::eventListener(Ljava/lang/String;Ljava/lang/String;)(e.origin, e.data);
        }

        if ($wnd.addEventListener) {
            $wnd.addEventListener("message", postMessageListener, false);
        } else {
            $wnd.attachEvent("onmessage", postMessageListener, false);
        }
    }-*/;

    @Override
    public String getOpener() {
        return opener;
    }
}
