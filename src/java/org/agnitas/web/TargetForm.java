/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.web;

import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.target.impl.TargetNodeDate;
import org.agnitas.target.impl.TargetNodeNumeric;
import org.agnitas.target.impl.TargetNodeString;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;

public class TargetForm extends StrutsFormBase {
    
    private static final long serialVersionUID = 45877020863407141L;
	private String shortname;
    private String description;
    private int targetID;
    private int action;
    private TargetRepresentation target;
    
    public TargetForm() {
        //target=new TargetRepresentation();
    }
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.targetID = 0;
        Locale aLoc=(Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        
        MessageResources text=(MessageResources)this.getServlet().getServletContext().getAttribute(org.apache.struts.Globals.MESSAGES_KEY);
        //MessageResources text=this.getServlet().getResources();
        
        shortname=text.getMessage(aLoc, "default.target.shortname");
        
    }
    
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return errors
     */
    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
        TargetNode aNode=null;
        int index=1;
        String name=null;
        String type=null;
        String colAndType=null;

        target=(TargetRepresentation) getWebApplicationContext().getBean("TargetRepresentation");
        while(index!=-1) {
            name=new String("trgt_column"+index);
            if((colAndType=request.getParameter(name))!=null) {
                type=colAndType.substring(colAndType.indexOf('#')+1);
                if((index>0 && request.getParameter("trgt_remove"+index+".x")==null) || (index==0 && request.getParameter("trgt_add.x")!=null)) {
                    if(type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("CHAR")) {
                        aNode=createStringNode(request, index, errors);
                    }
                    if(type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("DOUBLE") || type.equalsIgnoreCase("NUMBER")) {
                        aNode=createNumericNode(request, index, errors);
                    }
                    if(type.equalsIgnoreCase("DATE")) {
                        aNode=createDateNode(request, index, errors);
                    }
                    target.addNode(aNode);
                }
                index++;
                if(index==1) {
                    index=-1;
                }
            } else {
                if(index>0) {
                    index=0;
                } else {
                    index=-1;
                }
            }
        }
        
        if(request.getParameter("save.x")!=null) {
            if(!this.target.checkBracketBalance()) {
                errors.add("brackets", new ActionMessage("error.target.bracketbalance"));
            }
            if(this.shortname!=null && this.shortname.length()<1) {
                errors.add("shortname", new ActionMessage("error.nameToShort"));
            }
            if(this.target.getAllNodes()==null || this.target.getAllNodes().isEmpty()) {
                errors.add("norule", new ActionMessage("error.target.norule"));
            }
        }
        
        return errors;
    }
    
    /**
     * Creates a node (String)
     */
    TargetNode createStringNode(HttpServletRequest req, int index, ActionErrors errors) {
        TargetNodeString aNode=(TargetNodeString) getWebApplicationContext().getBean("TargetNodeString");
        
        aNode.setChainOperator(Integer.parseInt(req.getParameter("trgt_chainop"+index)));
        aNode.setOpenBracketBefore(req.getParameter("trgt_bracketopen"+index).equals("1"));
        aNode.setPrimaryField(req.getParameter("trgt_column"+index).substring(0, req.getParameter("trgt_column"+index).indexOf('#')));
        aNode.setPrimaryFieldType(req.getParameter("trgt_column"+index).substring(req.getParameter("trgt_column"+index).indexOf('#')+1));
        aNode.setPrimaryOperator(Integer.parseInt(req.getParameter("trgt_operator"+index)));
        aNode.setPrimaryValue(req.getParameter("trgt_value"+index));
        aNode.setCloseBracketAfter(req.getParameter("trgt_bracketclose"+index).equals("1"));
        
        return aNode;
    }
    
    /**
     * Creates a node (numeric)
     */
    TargetNode createNumericNode(HttpServletRequest req, int index, ActionErrors errors) {
        TargetNodeNumeric aNode=(TargetNodeNumeric) getWebApplicationContext().getBean("TargetNodeNumeric");
        
        aNode.setChainOperator(Integer.parseInt(req.getParameter("trgt_chainop"+index)));
        aNode.setOpenBracketBefore(req.getParameter("trgt_bracketopen"+index).equals("1"));
        aNode.setPrimaryField(req.getParameter("trgt_column"+index).substring(0, req.getParameter("trgt_column"+index).indexOf('#')));
        aNode.setPrimaryFieldType(req.getParameter("trgt_column"+index).substring(req.getParameter("trgt_column"+index).indexOf('#')+1));
        aNode.setPrimaryOperator(Integer.parseInt(req.getParameter("trgt_operator"+index)));
        aNode.setPrimaryValue(req.getParameter("trgt_value"+index));
        aNode.setCloseBracketAfter(req.getParameter("trgt_bracketclose"+index).equals("1"));
        if(aNode.getPrimaryOperator()==TargetNode.OPERATOR_MOD) {
            try {
                aNode.setSecondaryOperator(Integer.parseInt(req.getParameter("trgt_sec_operator"+index)));
            } catch (Exception e) {
                aNode.setSecondaryOperator(TargetNode.OPERATOR_EQ);
            }
            try {
                aNode.setSecondaryValue(Integer.parseInt(req.getParameter("trgt_sec_value"+index)));
            } catch (Exception e) {
                aNode.setSecondaryValue(0);
            }
        }
        
        return aNode;
    }
    
    /**
     * Creates a node (date)
     */
    TargetNode createDateNode(HttpServletRequest req, int index, ActionErrors errors) {
        TargetNodeDate aNode=new TargetNodeDate();
        
        aNode.setChainOperator(Integer.parseInt(req.getParameter("trgt_chainop"+index)));
        aNode.setOpenBracketBefore(req.getParameter("trgt_bracketopen"+index).equals("1"));
        aNode.setPrimaryField(req.getParameter("trgt_column"+index).substring(0, req.getParameter("trgt_column"+index).indexOf('#')));
        aNode.setPrimaryFieldType(req.getParameter("trgt_column"+index).substring(req.getParameter("trgt_column"+index).indexOf('#')+1));
        aNode.setPrimaryOperator(Integer.parseInt(req.getParameter("trgt_operator"+index)));
        aNode.setDateFormat(req.getParameter("trgt_dateformat"+index));
        aNode.setPrimaryValue(req.getParameter("trgt_value"+index));
        aNode.setCloseBracketAfter(req.getParameter("trgt_bracketclose"+index).equals("1"));
        
        return aNode;
    }
    
    /**
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    public String getShortname() {
        return this.shortname;
    }
    
     /**
     * Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    /**
     * Getter for property description.
     *
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }
    
    /**
     * Setter for property targetID.
     *
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
    
    /**
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /**
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /**
     * Getter for property target.
     *
     * @return Value of property target.
     */
    public TargetRepresentation getTarget() {
        return this.target;
    }
    
    /**
     * Setter for property target.
     *
     * @param target New value of property target.
     */
    public void setTarget(TargetRepresentation target) {
        this.target = target;
    }

    /**
     * Getter for property allNodes.
     *
     * @return Value of property allNodes.
     */
    public ArrayList getAllNodes() {
        if(target != null) {
            return target.getAllNodes();
        }
        return new ArrayList();
    }
    
}
