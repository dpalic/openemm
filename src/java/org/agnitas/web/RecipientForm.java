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

package org.agnitas.web;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.BindingEntry;
import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.target.impl.TargetNodeDate;
import org.agnitas.target.impl.TargetNodeNumeric;
import org.agnitas.target.impl.TargetNodeString;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

public class RecipientForm extends StrutsFormBase {
    private static final long serialVersionUID = 3876045401212665105L;
    protected int action;
	protected int recipientID = 0;
    protected int gender;
    protected int mailtype;
    protected int user_status;
    protected int listID;
    protected int all;
    
    protected String title = new String("");
    protected String firstname = new String("");
    protected String lastname = new String("");
    protected String email = new String("");
    protected String user_type = new String("E");
    
    protected Map column = new CaseInsensitiveMap();
    protected TargetRepresentation target =null;
    protected Map mailing = new HashMap();
    
    protected int targetID;
    
    protected boolean overview = true;	// recipient overview or recipient search?

    protected ActionMessages messages;

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
        TargetNode aNode = null;
        int index = 1;
        String name = null;
        String type = null;
        String colAndType = null;

        if(request.getParameter("trgt_clear") != null) {
            this.target = null;
            setRecipientID(0);
            if( getCurrentFuture() == null ){ // reset filter fields only if there is no future running
            	setUser_status(0);
            	setUser_type("E");
            	setTargetID(0);
               	setListID(0);
            }
        }
        
        if(this.target == null) {
            this.target = (TargetRepresentation) getWebApplicationContext().getBean("TargetRepresentation");
            if(action == RecipientAction.ACTION_VIEW
                   && getRecipientID() == 0) {
                this.column = new CaseInsensitiveMap();
                gender = 0;
                mailtype = 1;
                user_status = 0;
                listID = 0;
                title = new String("");
                firstname = new String("");
                lastname = new String("");
                email = new String("");
            }
        }
        while(index != -1) {
            name = new String("trgt_column"+index);
            if((colAndType = request.getParameter(name))!=null) {
                type = colAndType.substring(colAndType.indexOf('#')+1).trim();
                if(index>0 && request.getParameter("trgt_remove"+index+".x") != null) {
                	target.deleteNode(index-1);
                } else if(index == 0 && request.getParameter("trgt_add.x") != null) {
                    if(type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("CHAR")) {
                        aNode = createStringNode(request, index, errors);
                    } else if(type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("DOUBLE")) {
                        aNode = createNumericNode(request, index, errors);
                    } else if(type.equalsIgnoreCase("DATE")) {
                        aNode = createDateNode(request, index, errors);
                    }
                    target.addNode(aNode);
                }
                index++;
                if(index == 1) {
                    index = -1;
                }
            } else {
                if(index > 0) {
                    index = 0;
                } else {
                    index = -1;
                }
            }
        }

        if(request.getParameter("trgt_save.x") != null) {
        	if(!this.target.checkBracketBalance()) {
        		errors.add("brackets", new ActionMessage("error.target.bracketbalance"));
		}
        
		List list = this.target.getAllNodes();

		if(list == null || list.isEmpty()) {
			errors.add("norule", new ActionMessage("error.target.norule"));
		} else {
			for(index = 1; index <= list.size(); index++) {
				name = new String("trgt_column"+index);
				if((colAndType = request.getParameter(name)) != null) {
					type = colAndType.substring(colAndType.indexOf('#') + 1).trim();
					if(type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("CHAR")) {
						aNode = createStringNode(request, index, errors);
					} else if(type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("DOUBLE")) {
						aNode = createNumericNode(request, index, errors);
					} else if(type.equalsIgnoreCase("DATE")) {
						aNode = createDateNode(request, index, errors);
					}
					list.set(index-1, aNode);
				}
			}
		}
        }
        return errors;
    }

    /**
     * Creates nodes (String)
     */
    protected TargetNode createStringNode(HttpServletRequest req, int index, ActionErrors errors) {
        TargetNodeString aNode = (TargetNodeString) getWebApplicationContext().getBean("TargetNodeString");

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
     * Creates nodes (numeric)
     */
    protected TargetNode createNumericNode(HttpServletRequest req, int index, ActionErrors errors) {
        TargetNodeNumeric aNode = (TargetNodeNumeric) getWebApplicationContext().getBean("TargetNodeNumeric");

        aNode.setChainOperator(Integer.parseInt(req.getParameter("trgt_chainop"+index)));
        aNode.setOpenBracketBefore(req.getParameter("trgt_bracketopen"+index).equals("1"));
        aNode.setPrimaryField(req.getParameter("trgt_column"+index).substring(0, req.getParameter("trgt_column"+index).indexOf('#')));
        aNode.setPrimaryFieldType(req.getParameter("trgt_column"+index).substring(req.getParameter("trgt_column"+index).indexOf('#')+1));
        aNode.setPrimaryOperator(Integer.parseInt(req.getParameter("trgt_operator"+index)));
        aNode.setPrimaryValue(req.getParameter("trgt_value"+index));
        aNode.setCloseBracketAfter(req.getParameter("trgt_bracketclose"+index).equals("1"));
        if(aNode.getPrimaryOperator() == TargetNode.OPERATOR_MOD) {
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
     * Creates nodes (date)
     */
    protected TargetNode createDateNode(HttpServletRequest req, int index, ActionErrors errors) {
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
     * Getter for property recipientID.
     *
     * @return Value of property recipientID.
     */
    public int getRecipientID() {
        return this.recipientID;
    }

    /**
     * Setter for property recipientID.
     *
     * @param recipientID New value of property recipientID.
     */
    public void setRecipientID(int recipientID) {
        this.recipientID=recipientID;
    }

    /**
     * Getter for property gender.
     *
     * @return Value of property gender.
     */
    public int getGender() {
        return this.gender;
    }

    /**
     * Setter for property gender.
     *
     * @param gender New value of property gender.
     */
    public void setGender(int gender) {
        this.gender=gender;
    }

    /**
     * Getter for property mailtype.
     *
     * @return Value of property mailtype.
     */
    public int getMailtype() {
        return this.mailtype;
    }

    /**
     * Setter for property mailtype.
     *
     * @param mailtype New value of property mailtype.
     */
    public void setMailtype(int mailtype) {
        this.mailtype=mailtype;
    }

    /**
     * Getter for property user_status.
     *
     * @return Value of property user_status.
     */
    public int getUser_status() {
        return this.user_status;
    }

   /**
     * Setter for property user_status.
     *
     * @param user_status New value of property user_status.
     */
    public void setUser_status(int user_status) {
        this.user_status=user_status;
    }

    /**
     * Getter for property listID.
     *
     * @return Value of property listID.
     */
    public int getListID() {
        return this.listID;
    }

    /**
     * Setter for property listID.
     *
     * @param listID New value of property listID.
     */
    public void setListID(int listID) {
        this.listID=listID;
    }

    /**
     * Getter for property title.
     *
     * @return Value of property title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for property title.
     *
     * @param title New value of property title.
     */
    public void setTitle(String title) {
        this.title=title;
    }

    /**
     * Getter for property firstname.
     *
     * @return Value of property firstname.
     */
    public String getFirstname() {
        return this.firstname;
    }

    /**
     * Setter for property firstname.
     *
     * @param firstname New value of property firstname.
     */
    public void setFirstname(String firstname) {
        this.firstname=firstname;
    }

    /**
     * Getter for property lastname.
     *
     * @return Value of property lastname.
     */
    public String getLastname() {
        return this.lastname;
    }

    /**
     * Setter for property lastname.
     *
     * @param lastname New value of property lastname.
     */
    public void setLastname(String lastname) {
        this.lastname=lastname;
    }

    /**
     * Getter for property email.
     *
     * @return Value of property email.
     */
    public String getEmail() {
        return this.email.toLowerCase();
    }

    /**
     * Setter for property email.
     *
     * @param email New value of property email.
     */
    public void setEmail(String email) {
        this.email=email;
    }

    /**
     * Getter for property user_type.
     *
     * @return Value of property user_type.
     */
    public String getUser_type() {
        return this.user_type;
    }

    /**
     * Setter for property user_type.
     *
     * @param user_type New value of property user_type.
     */
    public void setUser_type(String user_type) {
        this.user_type=user_type;
    }

    /**
     * Getter for property columnMap.
     *
     * @return Value of property columnsMap.
     */
    public Map getColumnMap() {
        return column;
    }

    /**
     * Getter for property columns.
     *
     * @return Value of property column.
     */
    public Object getColumn(String key) {
        return column.get(key);
    }

    /**
     * Setter for property column.
     *
     * @param key The name of the column to set.
     * @param value New value for the column.
     */
    public void setColumn(String key, Object value) {
        column.put(key, value);
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
     * Getter for property bindingEntry.
     *
     * @return Value of property bindingEntry.
     */
    public BindingEntry getBindingEntry(int id) {
        Map sub=null;

        sub=(Map) mailing.get(new Integer(id));
        if(sub == null) {
            sub=new HashMap();
            mailing.put(new Integer(id), sub);
        }

        if(sub.get(new Integer(0)) == null) {
            BindingEntry entry=(BindingEntry) getWebApplicationContext().getBean("BindingEntry");

            entry.setMailinglistID(id);
            entry.setMediaType(0);
            sub.put(new Integer(0), entry);
        }
        return (BindingEntry) sub.get(new Integer(0));
    }

    /**
     * Setter for property bindingEntry.
     *
     * @param id New value of property bindingEntry.
     */
    public void setBindingEntry(int id, BindingEntry info) {
        Map sub=null;
        Integer mt=new Integer(info.getMediaType());

        sub=(Map) mailing.get(new Integer(id));
        if(sub == null) {
            sub=new HashMap();
        }
        if(info == null) {
            sub.remove(mt);
        } else {
            sub.put(mt, info);
        }
        mailing.put(new Integer(id), sub);
    }

    /**
     * Getter for property allBindings.
     *
     * @return Value of property allBindings.
     */
    public Map getAllBindings() {
        return mailing;
    }

	
	public int getTargetID() {
		return targetID;
	}

	public void setTargetID(int targetID) {
		this.targetID = targetID;
	}

	public int getAll() {
		return all;
	}

	public void setAll(int all) {
		this.all = all;
	}
	
	/**
	 * if overview = true, we have the recipient overview.
	 * if overview = false, we have the recipient search.
	 * @return
	 */
	public boolean isOverview() {
		return overview;
	}
	
	public void setOverview(boolean overview) {
		this.overview = overview;
	}
	
	public void setMessages(ActionMessages messages) {
		this.messages = messages;
	}
	
	public ActionMessages getMessages() {
		return this.messages;
	}
}
