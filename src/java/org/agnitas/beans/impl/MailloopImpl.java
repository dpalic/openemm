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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Holds value of property shortname.
     */
    private String shortname;

    public String getShortname() {

        return this.shortname;
    }

    public void setShortname(String shortname) {

        this.shortname = shortname;
    }

    /**
     * Holds value of property description.
     */
    private String description;

    public String getDescription() {

        return this.description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * Holds value of property companyID.
     */
    private int companyID;

    public int getCompanyID() {

        return this.companyID;
    }

    public void setCompanyID(int companyID) {

        this.companyID = companyID;
    }

    /**
     * Holds value of property forwardEmail.
     */
    private String forwardEmail;

    public String getForwardEmail() {

        return this.forwardEmail;
    }

    public void setForwardEmail(String forwardEmail) {

        this.forwardEmail = forwardEmail;
    }

    /**
     * Holds value of property arSender.
     */
    private String arSender;

    public String getArSender() {

        return this.arSender;
    }

    public void setArSender(String arSender) {

        this.arSender = arSender;
    }

    /**
     * Holds value of property arSubject.
     */
    private String arSubject;

    public String getArSubject() {

        return this.arSubject;
    }

    public void setArSubject(String arSubject) {

        this.arSubject = arSubject;
    }

    /**
     * Holds value of property arText.
     */
    private String arText;

    public String getArText() {

        return this.arText;
    }

    public void setArText(String arText) {

        this.arText = arText;
    }

    /**
     * Holds value of property arHtml.
     */
    private String arHtml;

    public String getArHtml() {

        return this.arHtml;
    }

    public void setArHtml(String arHtml) {

        this.arHtml = arHtml;
    }

    /**
     * Holds value of property doForward.
     */
    private boolean doForward;

    public boolean isDoForward() {

        return this.doForward;
    }

    public void setDoForward(boolean doForward) {

        this.doForward = doForward;
    }

    /**
     * Holds value of property doAutoresponder.
     */
    private boolean doAutoresponder;

    public boolean isDoAutoresponder() {

        return this.doAutoresponder;
    }

    public void setDoAutoresponder(boolean doAutoresponder) {

        this.doAutoresponder = doAutoresponder;
    }

    /**
     * Holds value of property changedate.
     */
    private java.sql.Timestamp changedate;

    public java.sql.Timestamp getChangedate() {

    	return this.changedate;
    }

    public void setChangedate(java.sql.Timestamp changedate) {

    	this.changedate = changedate;
    }

}
