/*
 * Copyright 2010 Traction Software, Inc.
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
package org.jbpm.console.ng.gc.client.util;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;

/**
 * Interface for UTCDateBox implementations that are quite different
 * in appearance (HTML4 vs HTML5).
 * 
 * @author andy
 */
public interface UTCDateBoxImpl extends IsWidget, HasValue<Long>, HasValueChangeHandlers<Long>, HasText, HasEnabled {
    
    /**
     * Sets the DateTimeFormat for this UTCDateBox. The HTML5
     * implementation will ignore this.
     */
    public void setDateFormat(DateTimeFormat dateFormat);

    /**
     * Sets the visible length of the date input. The HTML5
     * implementation will ignore this.
     */
    public void setVisibleLength(int length);    

    /**
     * Sets the tab index for the control. 
     */
    public void setTabIndex(int tabIndex);

    /**
     * Returns the DateBox (if any) that this implementation uses. For
     * HTML5, this will return null. This was only added to make my
     * r52 tree compile and I don't intend to check it in.
     */
    public DatePicker getDateBox();

}