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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.actions.ops;

import java.io.*;
import java.util.HashMap;

import org.agnitas.actions.ActionOperation;
import org.agnitas.beans.*;
import org.agnitas.dao.MailingDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ScriptHelper;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author  Martin Helff, Andreas Rehak
 * @version
 */
public class ExecuteScript extends ActionOperation implements Serializable {

	static final long serialVersionUID = -2943748993810034889L;
    
	/**
	 * Holds the script code.
	 */
	private String script="";

	/**
	 * Initialize object from input stream.
	 * @param in The input stream to read from.
	 */
	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField allFields=null;

		allFields=in.readFields();
		script=(String) allFields.get("script", new String(""));
	}

	/**
	 * Executes the script for this action with the give form values.
	 * @param con the applicartion context.
	 * @param companyID
	 * @param params parameters given to the request
	 * @return true on success
	 */
	public boolean executeOperation(ApplicationContext con, int companyID, HashMap params) {
		boolean result=false;
		Recipient cust=(Recipient) con.getBean("Recipient");
		cust.setCompanyID(companyID);
		params.put("Customer", cust);

		// neu von ma
		BindingEntry binding=(BindingEntry) con.getBean("BindingEntry");
		params.put("BindingEntry", binding);

		Mailing mail=(Mailing) con.getBean("Mailing");
		mail.setCompanyID(companyID);
		params.put("Mailing", mail);

		MailingDao mailingdao=(MailingDao) con.getBean("MailingDao");
		params.put("MailingDao", mailingdao);

		if(!params.containsKey("ScriptHelper")) {
			params.put("ScriptHelper", new ScriptHelper(con));
		}

		StringWriter aWriter=new StringWriter();

		try {
			Velocity.setProperty("runtime.log", AgnUtils.getDefaultValue("system.logdir")+"/velocity.log");
			Velocity.init();
			Velocity.evaluate(new VelocityContext(params), aWriter, null, this.script);
		} catch(Exception e) {
			System.err.println("velocity error: "+e);
			System.err.println(AgnUtils.getStackTrace(e));
		}

		if(params.containsKey("scriptResult")) {
			if(params.get("scriptResult").equals("1")) {
				result=true;
			}
		}
		return result;
	}

	/**
	 * Get the script for this action.
	 * @return The script for this action.
	 */
	public String getScript() {
		return this.script;
	}

	/**
	 * Setter for property Script.
	 * @param keyColumn New value of property Script.
	 */
	public void setScript(String script) {
		this.script = script;
	}
}
