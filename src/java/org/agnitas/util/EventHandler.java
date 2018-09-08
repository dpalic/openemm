/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.util;


import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.introspection.Info;

public class EventHandler implements InvalidReferenceEventHandler,
        NullSetEventHandler, MethodExceptionEventHandler {

      ActionErrors errors = new ActionErrors();

      public EventHandler(Context ctx) {
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(this);
        ec.attachToContext(ctx);
      }
    @Override
    public Object methodException(Class aClass, String method, Exception e) throws Exception {
    	String exceptionMessage = e.getMessage() != null ? e.getMessage() : "<no exception message>";

        String error = "an " + e.getClass().getName() + " was thrown by the " + method
        + " method of the " + aClass.getName() + " class [" + StringEscapeUtils.escapeHtml(exceptionMessage.split("\n")[0]) + "]";
        errors.add(error, new ActionMessage("Method exception: " + error));
        return error;
    }

    @Override
    public boolean shouldLogOnNullSet(String s, String s1) {
        return false;
    }

    @Override
    public Object invalidGetMethod(Context context, String s, Object o, String s1, Info info) {
        String str = "Error in line " + info.getLine() + ", column " + info.getColumn() + ": ";
        errors.add(str,new ActionMessage(str + "Null reference " + s + "."));
        return null;
    }

    @Override
    public boolean invalidSetMethod(Context context, String s, String s1, Info info) {
        return false;
    }

    @Override
    public Object invalidMethod(Context context, String s, Object o, String s1, Info info) {
        String str = "Error in line " + info.getLine() + ", column " + info.getColumn() + ": ";
        errors.add(str, new ActionMessage(str + "Invalid method "+s+"."));
        return null;
    }

    /**
     * Returns errors collected by the event handler.
     *
     * @return errors
     */
    public ActionErrors getErrors() {
        return errors;
    }
}

