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
package org.jbpm.console.ng.client;

import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;

import org.uberfire.client.mvp.PlaceManager;

@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private PlaceManager placeManager;

//    @Inject
//    private WorkbenchMenuBarPresenter menubar;

//    @Inject
//    private SyncBeanManager iocManager;

//    @Inject
//    public User identity;

//    @Inject
//    private KieWorkbenchACL kieACL;

//    @Inject
//    private Caller<KieWorkbenchSecurityService> kieSecurityService;
//
//    

//    @Inject
//    private ActivityBeansCache activityBeansCache;

//    @Inject
//    private Caller<AppConfigService> appConfigService;

    @AfterInitialization
    public void startApp() {
//        kieSecurityService.call( new RemoteCallback<String>() {
//            public void callback( final String str ) {
//                KieWorkbenchPolicy policy = new KieWorkbenchPolicy( str );
//                kieACL.activatePolicy( policy );
//                loadPreferences();
//                hideLoadingPopup();
//            }
//        } ).loadPolicy();
        
        hideLoadingPopup();
     
    }

//    private void loadPreferences() {
//        appConfigService.call( new RemoteCallback<Map<String, String>>() {
//            @Override
//            public void callback( final Map<String, String> response ) {
//                ApplicationPreferences.setUp( response );
//            }
//        } ).loadPreferences();
//    }

    // Fade out the "Loading application" pop-up

    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {
            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

}
