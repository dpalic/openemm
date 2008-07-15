/*
 * MailloopForm.java
 *
 * Created on 17. Jan 2006, 14:17
 */

package org.agnitas.web;


import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.apache.struts.upload.*;
import org.agnitas.util.*;
import java.util.*;
import java.io.*;
import java.text.*;
import org.agnitas.beans.*;


public final class MailloopForm extends StrutsFormBase {
    
    /**
     * Holds value of property shortname.
     */
    private String shortname;
    
    /**
     * Holds value of property description.
     */
    private String description;
    
    /**
     * Holds value of property action.
     */
    
    private int action;
    
    public MailloopForm() {
    }
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.shortname="";
        this.description="";
    }
    
    /**
     * Initializes shortname and description.
     */
    public void clearData() {
        this.shortname="";
        this.description="";
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
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
        
        if(request.getParameter("save.x")!=null) {
            if(this.shortname!=null && this.shortname.length()<3) {
                errors.add("shortname", new ActionMessage("error.nameToShort"));
            }    
        }
        return errors;
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
     * Holds value of property mailloopID.
     */
    private int mailloopID;

    /**
     * Getter for property mailloopID.
     *
     * @return Value of property mailloopID.
     */
    public int getMailloopID() {

        return this.mailloopID;
    }

    /**
     * Setter for property mailloopID.
     *
     * @param mailloopID New value of property mailloopID.
     */
    public void setMailloopID(int mailloopID) {

        this.mailloopID = mailloopID;
    }

    /**
     * Holds value of property doForward.
     */
    private boolean doForward;

    /**
     * Getter for property doForward.
     *
     * @return Value of property doForward.
     */
    public boolean isDoForward() {

        return this.doForward;
    }

    /**
     * Setter for property doForward.
     *
     * @param doForward New value of property doForward.
     */
    public void setDoForward(boolean doForward) {

        this.doForward = doForward;
    }

    /**
     * Holds value of property doAutoresponder.
     */
    private boolean doAutoresponder;

    /**
     * Getter for property doAutoresponder.
     *
     * @return Value of property doAutoresponder.
     */
    public boolean isDoAutoresponder() {

        return this.doAutoresponder;
    }

    /**
     * Setter for property doAutoresponder.
     *
     * @param doAutoresponder New value of property doAutoresponder.
     */
    public void setDoAutoresponder(boolean doAutoresponder) {

        this.doAutoresponder = doAutoresponder;
    }

    /**
     * Holds value of property arSubject.
     */
    private String arSubject;

    /**
     * Getter for property arSubject.
     *
     * @return Value of property arSubject.
     */
    public String getArSubject() {

        return this.arSubject;
    }

    /**
     * Setter for property arSubject.
     *
     * @param arSubject New value of property arSubject.
     */
    public void setArSubject(String arSubject) {

        this.arSubject = arSubject;
    }

    /**
     * Holds value of property arSender.
     */
    private String arSender;

    /**
     * Getter for property arSender.
     *
     * @return Value of property arSender.
     */
    public String getArSender() {

        return this.arSender;
    }

    /**
     * Setter for property arSender.
     *
     * @param arSender New value of property arSender.
     */
    public void setArSender(String arSender) {

        this.arSender = arSender;
    }

    /**
     * Holds value of property forwardEmail.
     */
    private String forwardEmail;

    /**
     * Getter for property forwardEmail.
     *
     * @return Value of property forwardEmail.
     */
    public String getForwardEmail() {

        return this.forwardEmail;
    }

    /**
     * Setter for property forwardEmail.
     *
     * @param forwardEmail New value of property forwardEmail.
     */
    public void setForwardEmail(String forwardEmail) {

        this.forwardEmail = forwardEmail;
    }

    /**
     * Holds value of property arText.
     */
    private String arText;

    /**
     * Getter for property arText.
     *
     * @return Value of property arText.
     */
    public String getArText() {

        return this.arText;
    }

    /**
     * Setter for property arText.
     *
     * @param arText New value of property arText.
     */
    public void setArText(String arText) {

        this.arText = arText;
    }

    /**
     * Holds value of property arHtml.
     */
    private String arHtml;

    /**
     * Getter for property arHtml.
     *
     * @return Value of property arHtml.
     */
    public String getArHtml() {

        return this.arHtml;
    }

    /**
     * Setter for property arHtml.
     *
     * @param arHtml New value of property arHtml.
     */
    public void setArHtml(String arHtml) {

        this.arHtml = arHtml;
    }

    /**
     * Holds value of property mailloops.
     */
    private List mailloops;

    /**
     * Getter for property mailloops.
     *
     * @return Value of property mailloops.
     */
    public List getMailloops() {

        return this.mailloops;
    }

    /**
     * Setter for property mailloops.
     *
     * @param mailloops New value of property mailloops.
     */
    public void setMailloops(List mailloops) {

        this.mailloops = mailloops;
    }
}
