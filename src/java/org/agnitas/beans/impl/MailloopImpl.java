/*
 * Mailloop.java
 *
 * Created on 17. Januar 2006, 13:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.agnitas.beans.impl;

import org.agnitas.beans.Mailloop;

/**
 *
 * @author mhe
 */
public class MailloopImpl implements Mailloop {
    
    /** Creates a new instance of Mailloop */
    public MailloopImpl() {
    }

    /**
     * Holds value of property id.
     */
    private int id;

    /**
     * Getter for property mailloopID.
     * @return Value of property mailloopID.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Setter for property mailloopID.
     * @param id New value of property mailloopID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Holds value of property shortname.
     */
    private String shortname;

    /**
     * Getter for property shortname.
     * @return Value of property shortname.
     */
    public String getShortname() {

        return this.shortname;
    }

    /**
     * Setter for property shortname.
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {

        this.shortname = shortname;
    }

    /**
     * Holds value of property description.
     */
    private String description;

    /**
     * Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {

        return this.description;
    }

    /**
     * Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * Holds value of property companyID.
     */
    private int companyID;

    /**
     * Getter for property companyID.
     * @return Value of property companyID.
     */
    public int getCompanyID() {

        return this.companyID;
    }

    /**
     * Setter for property companyID.
     * @param companyID New value of property companyID.
     */
    public void setCompanyID(int companyID) {

        this.companyID = companyID;
    }

    /**
     * Holds value of property forwardEmail.
     */
    private String forwardEmail;

    /**
     * Getter for property forwardEmail.
     * @return Value of property forwardEmail.
     */
    public String getForwardEmail() {

        return this.forwardEmail;
    }

    /**
     * Setter for property forwardEmail.
     * @param forwardEmail New value of property forwardEmail.
     */
    public void setForwardEmail(String forwardEmail) {

        this.forwardEmail = forwardEmail;
    }

    /**
     * Holds value of property arSender.
     */
    private String arSender;

    /**
     * Getter for property arSender.
     * @return Value of property arSender.
     */
    public String getArSender() {

        return this.arSender;
    }

    /**
     * Setter for property arSender.
     * @param arSender New value of property arSender.
     */
    public void setArSender(String arSender) {

        this.arSender = arSender;
    }

    /**
     * Holds value of property arSubject.
     */
    private String arSubject;

    /**
     * Getter for property arSubject.
     * @return Value of property arSubject.
     */
    public String getArSubject() {

        return this.arSubject;
    }

    /**
     * Setter for property arSubject.
     * @param arSubject New value of property arSubject.
     */
    public void setArSubject(String arSubject) {

        this.arSubject = arSubject;
    }

    /**
     * Holds value of property arText.
     */
    private String arText;

    /**
     * Getter for property arText.
     * @return Value of property arText.
     */
    public String getArText() {

        return this.arText;
    }

    /**
     * Setter for property arText.
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
     * @return Value of property arHtml.
     */
    public String getArHtml() {

        return this.arHtml;
    }

    /**
     * Setter for property arHtml.
     * @param arHtml New value of property arHtml.
     */
    public void setArHtml(String arHtml) {

        this.arHtml = arHtml;
    }

    /**
     * Holds value of property doForward.
     */
    private boolean doForward;

    /**
     * Getter for property doForward.
     * @return Value of property doForward.
     */
    public boolean isDoForward() {

        return this.doForward;
    }

    /**
     * Setter for property doForward.
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
     * @return Value of property doAutoresponder.
     */
    public boolean isDoAutoresponder() {

        return this.doAutoresponder;
    }

    /**
     * Setter for property doAutoresponder.
     * @param doAutoresponder New value of property doAutoresponder.
     */
    public void setDoAutoresponder(boolean doAutoresponder) {

        this.doAutoresponder = doAutoresponder;
    }
    
}
