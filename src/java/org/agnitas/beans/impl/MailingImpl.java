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

package org.agnitas.beans.impl;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.agnitas.backend.Mailgun;
import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mediatype;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.TagDetails;
import org.agnitas.beans.Title;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.dao.TitleDao;
import org.agnitas.dao.DynamicTagDao;
import org.agnitas.dao.MaildropStatusDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.util.TimeoutLRUMap;
import org.agnitas.util.UID;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.ibm.icu.text.SimpleDateFormat;

import bsh.Interpreter;

/**
 *
 * @author Martin Helff, Nicole Serek
 */
public class MailingImpl implements Mailing {

    private static final long serialVersionUID = -6126128329645532973L;
	protected int mailinglistID;
    protected int mailTemplateID;
    protected int id;
    protected int companyID;
    protected int campaignID;
    protected int targetID;
    protected Map dynTags;
    protected Map components;
    protected Hashtable attachments;
    protected Map trackableLinks;
    protected int searchPos;

    /**
     * Holds value of property description.
     */
    protected String description;

    /**
     * Holds value of property shortname.
     */
    protected String shortname;

    /**
     * Holds value of property creationDate.
     */
    protected java.sql.Timestamp creationDate;

    /**
     * Holds value of property allowedTargets.
     */
    protected Map allowedTargets = null;

    /**
     * Holds value of property mailingType.
     */
    protected int mailingType;

    /**
     * Holds value of property targetGroups.
     */
    protected Collection targetGroups;
    protected int maildropID;

    /**
     * Holds value of property templateOK.
     */
    protected int templateOK;

    /**
     * Holds value of property isTemplate.
     */
    protected boolean isTemplate;

    /**
     * Holds value of property targetMode.
     */
    protected int targetMode;

    /**
     * Creates new Mailing
     */
    public MailingImpl() {
    }

    public boolean parseTargetExpression(String tExp) {
        boolean result=true;
        int posA=0;
        int posB=0;
        String targetID=null;
        Integer tmpInt=null;
        char tmpOp='|';

        this.targetMode=MailingImpl.TARGET_MODE_OR;

        if(tExp==null) {
            return result;
        }

        if(tExp.indexOf('&')!=-1) {
            this.targetMode=MailingImpl.TARGET_MODE_AND;
            tmpOp='&';
        }

        while((posB=tExp.indexOf(tmpOp, posA))!=-1) {
            targetID=tExp.substring(posA, posB).trim();
            posA=posB+1;
            try {
                tmpInt=new Integer(Integer.parseInt(targetID));
                if(this.targetGroups==null) {
                    this.targetGroups=new HashSet();
                }
                this.targetGroups.add(tmpInt);
            } catch (Exception e) {
                AgnUtils.logger().error("parseTargetExpression: "+e.getMessage());
                tmpInt=null;
            }
        }

        if(posA<tExp.length()) {
            targetID=tExp.substring(posA).trim();
            try {
                tmpInt=new Integer(Integer.parseInt(targetID));
                if(this.targetGroups==null) {
                    this.targetGroups=new HashSet();
                }
                this.targetGroups.add(tmpInt);
            } catch (Exception e) {
                AgnUtils.logger().error("parseTargetExpression: "+e.getMessage());
                tmpInt=null;
            }
        }

        return result;
    }

    protected String generateTargetExpression() {
        StringBuffer tmp=new StringBuffer("");
        Integer tmpInt=null;
        boolean isFirst=true;
        String opTmp=new String(" | ");

        if(this.targetMode==MailingImpl.TARGET_MODE_AND) {
            opTmp=new String(" & ");
        }

        if(this.targetGroups==null) {
            return new String("");
        }

        Iterator aIt=this.targetGroups.iterator();
        while(aIt.hasNext()) {
            tmpInt=(Integer)aIt.next();
            if(tmpInt.intValue()!=0) {
                if(!isFirst) {
                    tmp.append(opTmp);
                } else {
                    isFirst=false;
                }
                tmp.append(tmpInt.toString());
            }
        }

        return tmp.toString();
    }

    public void addComponent(MailingComponent aComp) {

        if(components==null)
            components=new HashMap();

        if(!components.containsKey(aComp.getComponentName())) {
            components.put(aComp.getComponentName(), aComp);
        }
    }

    public void addAttachment(MailingComponent aComp) {

        if(attachments==null)
            attachments=new Hashtable();

        attachments.put(aComp.getComponentName(), aComp);
    }

    public void setCompanyID(int tmpid) {
        companyID=tmpid;
    }

    public void setCampaignID(int tmpid) {
        campaignID=tmpid;
    }

    public void setId(int tmpid) {
        id=tmpid;
    }

    public void setTargetID(int tmpid) {
        targetID=tmpid;
    }

    public void setMailTemplateID(int tmpid) {
        mailTemplateID=tmpid;
    }

    public void setMailinglistID(int tmpid) {
        mailinglistID=tmpid;
    }


    public java.util.Map getDynTags() {
        return dynTags;
    }

    public Vector findDynTagsInTemplates(String aTemplate, ApplicationContext con) throws Exception {
        DynamicTag aktTag=null;
        Vector addedTags=new Vector();

        searchPos=0;

        if(aTemplate!=null) {
            while((aktTag=findNextDynTag(aTemplate, con)) != null) {
                addDynamicTag(aktTag);
                addedTags.add(aktTag.getDynName());
            }
        }

        return addedTags;
    }

    public Vector findDynTagsInTemplates(ApplicationContext con) throws Exception {
        Vector addedTags=new Vector();
        MailingComponent tmpComp=null;
        searchPos=0;

        Iterator it=this.components.values().iterator();
        while(it.hasNext()) {
            tmpComp=(MailingComponent)it.next();
            if(tmpComp.getType()==MailingComponent.TYPE_TEMPLATE) {
                addedTags.addAll(this.findDynTagsInTemplates(tmpComp.getEmmBlock(), con));
            }
        }

        return addedTags;
    }

    public Vector scanForComponents(ApplicationContext con) throws Exception {
        Vector addedTags=new Vector();
        MailingComponent tmpComp=null;
        DynamicTag dyntag=null;
        DynamicTagContent dyncontent=null;

        searchPos=0;
        Iterator it2=null;

        Iterator it=this.components.values().iterator();
        while(it.hasNext()) {
            tmpComp=(MailingComponent)it.next();
            if(tmpComp.getType()==MailingComponent.TYPE_TEMPLATE) {
                addedTags.addAll(this.scanForComponents(tmpComp.getEmmBlock(), con));
            }
        }

        it=this.dynTags.values().iterator();
        while(it.hasNext()) {
            dyntag=(DynamicTag)it.next();
            it2=dyntag.getDynContent().values().iterator();
            while(it2.hasNext()) {
                dyncontent=(DynamicTagContent)it2.next();
                addedTags.addAll(this.scanForComponents(dyncontent.getDynContent(), con));
            }
        }

        return addedTags;
    }

    public Vector scanForLinks(ApplicationContext con) throws Exception {
        Vector addedLinks = new Vector();
        MailingComponent tmpComp = null;
        DynamicTag dyntag = null;
        DynamicTagContent dyncontent = null;

        searchPos = 0;
        Iterator it2 = null;

        Iterator it = this.components.values().iterator();
        while(it.hasNext()) {
            tmpComp = (MailingComponent)it.next();
            if(tmpComp.getType() == MailingComponent.TYPE_TEMPLATE) {
                addedLinks.addAll(this.scanForLinks(tmpComp.getEmmBlock(), con));
            }
        }

        it = this.dynTags.values().iterator();
        while(it.hasNext()) {
            dyntag = (DynamicTag)it.next();
            it2 = dyntag.getDynContent().values().iterator();
            while(it2.hasNext()) {
                dyncontent = (DynamicTagContent)it2.next();
                addedLinks.addAll(this.scanForLinks(dyncontent.getDynContent(), con));
            }
        }

        return addedLinks;
    }

    public void addDynamicTag(DynamicTag aTag) {

        if(!this.dynTags.containsKey(aTag.getDynName())) {
            dynTags.put(aTag.getDynName(), aTag);
        }
        DynamicTag dbTag=(DynamicTag) this.dynTags.get(aTag.getDynName());
        if(dbTag.getGroup() != aTag.getGroup()) {
            dbTag.setGroup(aTag.getGroup());
        }
    }

    public DynamicTag findNextDynTag(String aTemplate, ApplicationContext con) throws Exception {
        int valueTagStartPos;
        int oldPos;
        DynamicTag aDynTag=null;
        TagDetails aStartTag=null;
        TagDetails aEndTag=null;
        TagDetails aValueTag=null;

        aStartTag=getOneTag(aTemplate, "agnDYN", searchPos, con);
        if(aStartTag==null)
            return null;

        aStartTag.analyzeParameters();
        aStartTag.findTagParameters();

        searchPos=aStartTag.getEndPos();

        aDynTag=(DynamicTag)con.getBean("DynamicTag");
        aDynTag.setCompanyID(companyID);
        aDynTag.setMailingID(id);
        aDynTag.setComplex(aStartTag.isComplex());
        aDynTag.setDynName(aStartTag.getName());
        int group=0;

        Map params=aStartTag.getTagParameters();

        if(params != null) {
            String gname=(String) params.get("group");

            if(gname != null) {
                DynamicTagDao	dao=(DynamicTagDao) con.getBean("DynamicTagDao");
                group=dao.getIdForName(this.id, gname);
            }
        }
        aDynTag.setGroup(group);

        if(aStartTag.isComplex()) {
            oldPos=searchPos;
            do {
                aEndTag=getOneTag(aTemplate, "/agnDYN", searchPos, con);
                if(aEndTag==null) {
                    LineNumberReader aReader=new LineNumberReader(new StringReader(aTemplate));
                    aReader.skip(searchPos);
                    throw new Exception("NoEndTag$"+aReader.getLineNumber()+"$"+aStartTag.getName());
                }
                searchPos=aEndTag.getEndPos();
                aEndTag.analyzeParameters();
            } while(aStartTag.getName().compareTo(aEndTag.getName())!=0);
            String valueArea=aTemplate.substring(aStartTag.getEndPos(), aEndTag.getStartPos());
            valueTagStartPos=0;
            do {
                aValueTag=getOneTag(valueArea, "agnDVALUE", valueTagStartPos, con);
                if(aValueTag==null) {
                    LineNumberReader aReader=new LineNumberReader(new StringReader(aTemplate));
                    aReader.skip(searchPos);
                    throw new Exception("NoValueTag$"+aReader.getLineNumber()+"$"+aStartTag.getName());
                }
                valueTagStartPos=aValueTag.getEndPos();
                aValueTag.analyzeParameters();
            } while(aStartTag.getName().compareTo(aValueTag.getName())!=0);
            searchPos=oldPos;
            aDynTag.setStartTagStart(aStartTag.getStartPos());
            aDynTag.setStartTagEnd(aStartTag.getEndPos());
            aDynTag.setValueTagStart(aStartTag.getEndPos()+aValueTag.getStartPos());
            aDynTag.setValueTagEnd(aStartTag.getEndPos()+aValueTag.getEndPos());
            aDynTag.setEndTagStart(aEndTag.getStartPos());
            aDynTag.setEndTagEnd(aEndTag.getEndPos());
        } else {
            aDynTag.setStartTagStart(aStartTag.getStartPos());
            aDynTag.setStartTagEnd(aStartTag.getEndPos());
        }

        return aDynTag;
    }

    protected TagDetails getOneTag(String aTemplate, String TagName, int startPos, ApplicationContext con)
    throws Exception {
        return getOneTag(aTemplate, TagName, startPos, "[", "]", con);
    }

    protected TagDetails getOneTag(String aTemplate, String TagName, int startPos, String startMark, String endMark, ApplicationContext con)
    throws Exception {
        int posOfDynTag=0;
        int endOfDynTag=0;
        TagDetails det=(TagDetails)con.getBean("TagDetails");

        posOfDynTag=aTemplate.indexOf(startMark+TagName, startPos); // Search for next DYN-Tag

        if(posOfDynTag==-1) // if not DYN-Tag is found, return null
            return null;

        endOfDynTag=aTemplate.indexOf(endMark, posOfDynTag+7); // Search for a closing bracket

        if(endOfDynTag==-1) // if the Tag-Closing Bracket is missing, throw a exception
            throw new Exception("Missing Bracket$"+startPos);

        endOfDynTag++;

        det.setStartPos(posOfDynTag);
        det.setEndPos(endOfDynTag);
        det.setFullText(aTemplate.substring(posOfDynTag, endOfDynTag));

        return det;
    }

    public Vector scanForComponents(String aText1, ApplicationContext con) {
        Vector addComps = new Vector();
        String aText = null;
        String aLink = null;
        int startMatch = 0;
        int endMatch = 0;
        MailingComponent tmpComp = null;

        if(aText1 == null) {
            return addComps;
        }

        try {
            aText = aText1.toLowerCase();
            Pattern aRegExp = Pattern.compile("https?://[0-9A-Za-z_.+-]+(:[0-9]+)?(/[^ \t\n\r<>\"]*)?");
            Matcher aMatch = aRegExp.matcher(aText);
            while(true) {
                if(!aMatch.find(endMatch)) {
                    break;
                }
                startMatch = aMatch.start();
                endMatch = aMatch.end();
                if((startMatch == 0 && companyID != 282) || aText.regionMatches(false, startMatch-5, "src=\"", 0, 5) || aText.regionMatches(false, startMatch-4, "src=", 0, 4) || aText.regionMatches(false, startMatch-11, "background=", 0, 11) || aText.regionMatches(false, startMatch-12, "background=\"", 0, 12)) {
                    aLink = aText1.substring(startMatch, endMatch);

                    tmpComp = (MailingComponent) con.getBean("MailingComponent");
                    tmpComp.setCompanyID(companyID);
                    tmpComp.setMailingID(this.id);
                    tmpComp.setComponentName(aLink);
                    tmpComp.setType(MailingComponentImpl.TYPE_IMAGE);
                    if(!components.containsKey(aLink)) {
                        tmpComp.loadContentFromURL();
                        if(tmpComp.getMimeType().startsWith("image")) {
                            addComponent(tmpComp);
                        }
                    } else {
                        tmpComp = (MailingComponent) this.components.get(aLink);
                    }
                    if(tmpComp.getMimeType().startsWith("image")) {
                        addComps.add(aLink);
                    }
                }
            }

        } catch (Exception e) {
            AgnUtils.logger().error("scanForComponents: " + e);
        }

        return addComps;
    }

    public Vector scanForLinks(String aText1, ApplicationContext con) {
        String aLink = null;
        int start = 0;
        int end = 0;
        MailingComponent tmpComp = null;
        TrackableLink trkLink = null;
        Vector addedLinks = new Vector();

        if(aText1 == null) {
            return addedLinks;
        }

        try {
            Pattern aRegExp = Pattern.compile("https?://[0-9A-Za-z_.+-]+(:[0-9]+)?(/[^ \t\n\r<>\"]*)?");
            Matcher aMatch = aRegExp.matcher(aText1);
            while(true) {
                if(!aMatch.find(end)) {
                    break;
                }
                start = aMatch.start();
                end = aMatch.end();
                if((start == 0) || aText1.regionMatches(false, start-5, "src=\"", 0, 5) || aText1.regionMatches(false, start-4, "src=", 0, 4) || aText1.regionMatches(false, start-11, "background=", 0, 11) || aText1.regionMatches(false, start-12, "background=\"", 0, 12)) {
                    aLink = aText1.substring(start, end);
                    if(!components.containsKey(aLink) && !trackableLinks.containsKey(aLink)) {
                        tmpComp = (MailingComponent)con.getBean("MailingComponent");
                        tmpComp.setCompanyID(companyID);
                        tmpComp.setMailingID(id);
                        tmpComp.setComponentName(aLink);
                        tmpComp.setType(MailingComponent.TYPE_IMAGE);
                        tmpComp.loadContentFromURL();
                        if(!tmpComp.getMimeType().startsWith("image")) {
                         //   if(start == 0) {
                           //     aLink = aText1.substring(start, end);
                                if(!trackableLinks.containsKey(aLink)) {
                                    trkLink = (TrackableLink)con.getBean("TrackableLink");
                                    trkLink.setCompanyID(this.companyID);
                                    trkLink.setFullUrl(aLink);
                                    trkLink.setMailingID(this.id);
                                    trkLink.setUsage(TrackableLink.TRACKABLE_TEXT_HTML);
                                    trkLink.setActionID(0);
                                    trackableLinks.put(aLink, trkLink);
                                }
                                addedLinks.add(aLink);
                           // }
                        }
                    }
                } else {
                    aLink = aText1.substring(start, end);
                    if(!trackableLinks.containsKey(aLink)) {
                        trkLink = (TrackableLink)con.getBean("TrackableLink");
                        trkLink.setCompanyID(this.companyID);
                        trkLink.setFullUrl(aLink);
                        trkLink.setMailingID(this.id);
                        trkLink.setUsage(TrackableLink.TRACKABLE_TEXT_HTML);
                        trkLink.setActionID(0);
                        trackableLinks.put(aLink, trkLink);
                    }
                    addedLinks.add(aLink);
                }
            }

        } catch (Exception e) {
            AgnUtils.logger().error("scanForLinks: " + e.getMessage());
        }

        return addedLinks;
    }

    /** Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return description;
    }

    /** Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Getter for property shortname.
     * @return Value of property shortname.
     */
    public String getShortname() {
        return shortname;
    }

    /** Setter for property shortname.
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public java.sql.Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.sql.Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public boolean triggerMailing(int maildropStatusID, Hashtable opts, ApplicationContext con) {
    	Mailgun aMailgun=null;
    	DataSource ds=(DataSource)con.getBean("dataSource");
    	Connection dbCon=DataSourceUtils.getConnection(ds);
        boolean exitValue=true;

        try {
		if(maildropStatusID == 0) {
			throw new Exception("maildropStatusID is 0");
		}
                aMailgun=(Mailgun)con.getBean("Mailgun");
                aMailgun.initializeMailgun(Integer.toString(maildropStatusID), null);
                aMailgun.prepareMailgun(dbCon, new Hashtable());

		if(aMailgun!=null) {
			aMailgun.executeMailgun(dbCon, opts);
		} else {
			AgnUtils.logger().error("triggerMailing: Mailgun " + id + " could not be created");
		}
        } catch (Exception e) {
            System.err.println("triggerMailing: " + e);
            System.err.println(AgnUtils.getStackTrace(e));
            exitValue=false;
        }
		DataSourceUtils.releaseConnection(dbCon, ds);
        return exitValue;
    }

    public boolean isWorldMailingSend() {
        boolean returnValue=false;
        char status=MaildropEntry.STATUS_WORLD;
        MaildropEntry drop=null;

        switch(this.mailingType) {
            case Mailing.TYPE_ACTIONBASED:
                status=MaildropEntry.STATUS_ACTIONBASED;
                break;

            case Mailing.TYPE_DATEBASED:
                status=MaildropEntry.STATUS_DATEBASED;
                break;
        }

        Iterator it=this.maildropStatus.iterator();
        while(it.hasNext()) {
            drop=(MaildropEntry)it.next();
            if(drop.getStatus()==status) {
                returnValue=true;
            }
        }

        return returnValue;
    }

    public Map getComponents() {
        return this.components;
    }

    public Map getTrackableLinks() {
        return this.trackableLinks;
    }

    public int getMailinglistID() {

        return mailinglistID;
    }

    public int getTargetID() {
        return targetID;
    }

    public int getCompanyID() {
        return companyID;
    }

    public int getCampaignID() {
        return campaignID;
    }

    public boolean sendEventMailing(int customerID, int delayMinutes, String userStatus, Hashtable overwrite, ApplicationContext con) {
        boolean exitValue=true;
        Mailgun aMailgun=null;
        TimeoutLRUMap mailgunCache=(TimeoutLRUMap)con.getBean("mailgunCache");
        MaildropEntry entry=null;
        int maildropStatusID=0;
        try {
            aMailgun=(Mailgun)mailgunCache.get(Integer.toString(this.companyID)+"_"+Integer.toString(this.id));

            if(aMailgun==null) {
                Iterator it=this.getMaildropStatus().iterator();
                while(it.hasNext()) {
                    entry=(MaildropEntry)it.next();
                    if(entry.getStatus()==MaildropEntry.STATUS_ACTIONBASED) {
                        maildropStatusID=entry.getId();
                    }
                }
		if(maildropStatusID == 0) {
			throw new Exception("maildropStatusID is 0");
		}
                aMailgun=(Mailgun)con.getBean("Mailgun");
                aMailgun.initializeMailgun(Integer.toString(maildropStatusID), null);
                aMailgun.prepareMailgun(null, new Hashtable());

                mailgunCache.put(Integer.toString(this.companyID)+"_"+Integer.toString(this.id), aMailgun);
            }

            if(aMailgun!=null) {
                Hashtable opts=new Hashtable();
                opts.put("customer-id", Integer.toString(customerID));
                if(overwrite!=null) {
                    opts.put("overwrite", overwrite);
                }
                java.util.Date aDate=new java.util.Date();
                long millis=aDate.getTime();
                millis+=(delayMinutes*60000);
                aDate.setTime(millis);
                opts.put("send-date", aDate);

                if(userStatus!=null) {
                    opts.put("user-status", userStatus);
                }
                aMailgun.executeMailgun(null, opts);
            } else {
                AgnUtils.logger().error("Mailgun could not be created: "+this.id);
            }

        } catch (Exception e) {
            System.err.println("Fire Campaign-Mail: "+e);
            System.err.println(AgnUtils.getStackTrace(e));
            AgnUtils.logger().error("Fire Campaign-Mail: "+e);
            exitValue=false;
        }

        return exitValue;
    }

    /** Getter for property mailingType.
     * @return Value of property mailingType.
     */
    public int getMailingType() {
        return this.mailingType;
    }

    /** Setter for property mailingType.
     * @param mailingType New value of property mailingType.
     */
    public void setMailingType(int mailingType) {
        this.mailingType = mailingType;
    }

	public boolean cleanupMaildrop(ApplicationContext con) {
		Iterator it=this.maildropStatus.iterator();
		MaildropEntry entry=null;
		LinkedList del=new LinkedList();
		MaildropStatusDao	dao=(MaildropStatusDao) con.getBean("MaildropStatusDao");

		while(it.hasNext()) {
			entry=(MaildropEntry)it.next();
			if(entry.getStatus()=='E' || entry.getStatus()=='R') {
				del.add(entry);

				if ( AgnUtils.isOracleDB() ) {
					dao.delete(entry.getId());
				}
			}
		}

		it=del.iterator();
		while(it.hasNext()) {
			entry=(MaildropEntry)it.next();
			this.maildropStatus.remove(entry);
		}
		return true;
	}

    public String getPreview(String input, int inputType, int customerID, ApplicationContext con) throws Exception {
        return getPreview(input, inputType, customerID, false, con);
    }

    public String getPreview(String input_org, int inputType, int customerID, boolean overwriteMailtype, ApplicationContext con) throws Exception {
        DynamicTag aktTag=null;
        DynamicTag tmpTag=null;
        DynamicTag contentTag=null;
        Map contentMap=null;
        String contentString=null;
        DynamicTagContent aContent=null;
        DynamicTagContent aTmpContent=null;
        Target aTarget=null;
        int aTargetID=0;
        Hashtable allTags=new Hashtable();
        Hashtable allTargets=new Hashtable();
        Iterator it2=null;
        Interpreter aBsh=null;
        String input=new String(input_org);
        TargetDao tDao=(TargetDao)con.getBean("TargetDao");
        StringBuffer output=new StringBuffer(this.personalizeText(input, customerID, con));

        searchPos=0;
        aBsh=AgnUtils.getBshInterpreter(companyID, customerID, con);
        if(overwriteMailtype) {
            aBsh.set("mailtype", new Integer(inputType));
        }

        if(aBsh==null) {
            throw new Exception("error.template.dyntags");
        }

        Iterator it=this.dynTags.values().iterator();
        while(it.hasNext()) {
            aktTag=(DynamicTag)it.next();
            tmpTag=(DynamicTag)con.getBean("DynamicTag");
            tmpTag.setDynName(aktTag.getDynName());
            contentMap=aktTag.getDynContent();
            if(contentMap!=null) {
                it2=contentMap.values().iterator();
                while(it2.hasNext()) {
                    aContent=(DynamicTagContent)it2.next();
                    aTmpContent=(DynamicTagContent)con.getBean("DynamicTagContent");
                    aTmpContent.setDynName(aContent.getDynName());
                    aTmpContent.setDynOrder(aContent.getDynOrder());
                    aTmpContent.setDynContent(this.personalizeText(aContent.getDynContent(), customerID, con));
                    aTmpContent.setTargetID(aContent.getTargetID());
                    aTmpContent.setId(aContent.getId());
                    tmpTag.addContent(aTmpContent);
                }
            }
            allTags.put(aktTag.getDynName(), tmpTag);
        }

        contentMap=null;
        aContent=null;

        this.searchPos=0;

        while((aktTag=findNextDynTag(output.toString(), con))!=null) {
            searchPos=aktTag.getStartTagStart(); // always search from beginning of last found tag
            if(allTags.containsKey(aktTag.getDynName())) {
                contentTag=(DynamicTag)allTags.get(aktTag.getDynName());
                contentMap=contentTag.getDynContent();
                contentString=null; // reset always
                if(contentMap!=null) {
                    it=contentMap.values().iterator();
                    while(it.hasNext()) {
                        aContent=(DynamicTagContent)it.next();
                        aTargetID=aContent.getTargetID();
                        if(allTargets.containsKey(Integer.toString(aTargetID))) {
                            aTarget=(Target)allTargets.get(Integer.toString(aTargetID));
                        } else {
                            aTarget=tDao.getTarget(aTargetID, this.companyID);
                            if(aTarget == null) {
                                aTarget=(Target)con.getBean("Target");
                                aTarget.setCompanyID(this.companyID);
                                aTarget.setId(aTargetID);
                            }
                            allTargets.put(Integer.toString(aTarget.getId()), aTarget);
                        }
                        aTargetID=aTarget.getId();
                        if(aTargetID==0) { // Target fits
                            contentString=aContent.getDynContent();
                            break; // stop processing list of content
                        } else {
                            if(aTarget.isCustomerInGroup(aBsh)) {
                                contentString=aContent.getDynContent();
                                break; // stop processing list of content
                            }
                        }
                    }
                }
                // insert content if found content
                if(contentString!=null) {
                    if(aktTag.isComplex()) {
                        output.delete(aktTag.getEndTagStart(), aktTag.getEndTagEnd());
                        output.replace(aktTag.getValueTagStart(), aktTag.getValueTagEnd(), contentString);
                        output.delete(aktTag.getStartTagStart(), aktTag.getStartTagEnd());
                    } else {
                        output.replace(aktTag.getStartTagStart(), aktTag.getStartTagEnd(), contentString);
                    }
                } else {
                    if(aktTag.isComplex()) {
                        output.delete(aktTag.getStartTagStart(), aktTag.getEndTagEnd());
                    } else {
                        output.delete(aktTag.getStartTagStart(), aktTag.getStartTagEnd());
                    }
                }
            } else { // dyntag not found in list, throw exception!
                throw new Exception("error.template.dyntags");
            }
        }
        if(inputType==MailingImpl.INPUT_TYPE_TEXT) {
            if(this.getEmailParam(con).getLinefeed()>0) {
                output=new StringBuffer(SafeString.cutLineLength(SafeString.removeHTMLTags(output.toString()), this.getEmailParam(con).getLinefeed()));
            } else {
                output=new StringBuffer(SafeString.removeHTMLTags(output.toString()));
            }
        }

        output=new StringBuffer(this.insertTrackableLinks(output.toString(), customerID, con));

        return output.toString();
    }

    /**
     * Scans a textblock for trackable links and replaces them with encoded
     * rdir-links.
     */
    public String insertTrackableLinks(String aText1, int customerID, ApplicationContext con) {
        if(this.trackableLinks==null) {
             return aText1;
        }

	/* trackableLinks is an unordered HashMap. When there are 2 links in
	 * the Map, where one is part of the other, this could lead to an
	 * link replacement, depending on the map ordering.
	 *
	 * Link 1: http://www.mydomain.de
	 * Link 2: http://www.mydomain.de/path/index.htm
	 *
	 * If Link 1 is returned before Link 2 from the iterator this resulted
	 * in: http://rdir.de/r.html?uid=<uid of Link1>/path/index.htm
	 */ 
	Comparator<String>	reverseString=new Comparator<String>() {
		public int	compare(String o1, String o2) {
			return o2.compareTo(o1);
		}
	};

	Set	sorted = new TreeSet(reverseString);
	sorted.addAll(this.trackableLinks.keySet());

        Iterator i = sorted.iterator();
        String aLink = null;
        int start_link = 0;
        int end_link = 0;
        TrackableLink aLinkObj = null;
        StringBuffer aBuf = new StringBuffer(aText1);
        boolean isHref = false;

        if(aText1 == null) {
            return null;
        }

        while(i.hasNext()) {
            aLink = (String) i.next();
            end_link = 0;
            while((start_link = aBuf.indexOf(aLink, end_link)) != -1) {
                end_link = start_link+1;
                isHref = false;
                if(start_link > 5 && (aBuf.substring(start_link-6, start_link).equalsIgnoreCase("href=\""))) {
                    isHref = true;
                }
                if(start_link > 6 && (aBuf.substring(start_link-7, start_link).equalsIgnoreCase("href=\""))) {
                    isHref = true;
                }
                if (aBuf.length() > (start_link+aLink.length())) {
                	if(!(aBuf.charAt(start_link+aLink.length())==' ' || aBuf.charAt(start_link+aLink.length())=='\'' || aBuf.charAt(start_link+aLink.length())=='"')) {
                		isHref = false;
                	}
                }
                if(isHref) {
                    aLinkObj = (TrackableLink) this.trackableLinks.get(aLink);
                    aBuf.replace(start_link, start_link+aLink.length(), aLinkObj.encodeTagStringLinkTracking(con, customerID));
                }
            }
        }
        return aBuf.toString();
    }

    public MailingComponent getTemplate(String type) {
        return (MailingComponent)this.components.get("agn"+type);
    }

    public MailingComponent getHtmlTemplate() {
        return getTemplate("Html");
    }

    public MailingComponent getTextTemplate() {
        return getTemplate("Text");
    }

    public String personalizeText(String input, int customerID, ApplicationContext con) throws Exception {
        StringBuffer output=new StringBuffer(new String(input));
        TagDetails aDetail=null;
        searchPos=0;
        String aValue=null;

        while((aDetail = this.getOneTag(output.toString(), "agn", searchPos, con))!=null) {
            searchPos = aDetail.getStartPos()+1;
            aDetail.findTagName();
            if(!aDetail.getTagName().equals("agnDYN") && !aDetail.getTagName().equals("agnDVALUE")) {
                if(!aDetail.findTagParameters()) {
                    throw new Exception("error.personalization_tag_parameter");
                }
                aValue = this.processTag(aDetail, customerID, con);
                if(aValue != null) {
                    output.replace(aDetail.getStartPos(), aDetail.getEndPos(), aValue);
                }
                AgnUtils.logger().info("personalizeText: Tag value from DB '"+aValue+"'");
            }
        }
        return output.toString();
    }

    public String processTag(TagDetails aDetail, int customerID, ApplicationContext con) {
        String result = null;
        String selectVal = null;
        String tagType = null;
        String tagName = null;
        boolean processOK = true;
        int startPos = 0;
        int endPos = 0;
        Hashtable allValues = aDetail.getTagParameters();

        if(allValues == null) {
            allValues = new Hashtable();
        }

        tagName = aDetail.getTagName();
        if(tagName.equals("agnONEPIXEL")) {
            return new String(""); // return empty value in preview
        }
        
        if(tagName.equals("agnDATE")) {
        	String lang = (String)allValues.get("language");
        	if(lang == null) {
        		lang = "de";
        	}
        	
        	String country = (String)allValues.get("country");
        	if(country == null) {
        		country = "DE";
        	}
        	
        	// look for "type" in tag. Default to 0 if no tag is found.
        	int type = 0;
        	if (allValues.get("type") != null) {
        		// if we found a "type" Attribute, take it. 
        		type = Integer.parseInt((String)allValues.get("type"));	
        	}         	
        	MailingDao dao = (MailingDao) con.getBean("MailingDao");
        	String format = dao.getFormat(type);
            
        	SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale(lang, country));
    		String date = sdf.format(new Date());
    		return date;
        }

        if (!aDetail.getTagName().equals("agnTITLE")
				&& !aDetail.getTagName().equals("agnTITLEFULL")
				&& !aDetail.getTagName().equals("agnTITLEFIRST")) {
        	MailingDao dao = (MailingDao) con.getBean("MailingDao");
			String[] values = dao.getTag(aDetail.getTagName(), companyID);

			if (values != null) {
				selectVal = values[0];
				tagType = values[1];
			} else {
				AgnUtils.logger().error("Couldn't get tag "+aDetail.getTagName());
				processOK = false;
			}
		} else {
			selectVal = new String("");
			tagType = new String("COMPLEX");
		}

        if(!processOK) { // something failed, abort
            return null;
        }

        // replace [company-id], [mailinglist-id] and [mailing-id] with real values
        selectVal = SafeString.replace(selectVal, "[company-id]", Integer.toString(this.companyID));
        selectVal = SafeString.replace(selectVal, "[mailinglist-id]", Integer.toString(this.mailinglistID));
        selectVal = SafeString.replace(selectVal, "[mailing-id]", Integer.toString(this.id));
        if(selectVal.contains("[rdir-domain]")) {
            CompanyDao cDao = (CompanyDao)con.getBean("CompanyDao");
            selectVal = SafeString.replace(selectVal, "[rdir-domain]", cDao.getCompany(this.companyID).getRdirDomain());
        }

        String value = null;

        if(tagType.equals("COMPLEX")) { // search and replace parameters
            if(aDetail.getTagName().equals("agnTITLE") || aDetail.getTagName().equals("agnTITLEFULL") || aDetail.getTagName().equals("agnTITLEFIRST")) {
                int titleID=0;
                try {
                    titleID = Integer.parseInt((String)allValues.get("type"));
                } catch (Exception e) {
                    return null;
                }
                return generateSalutation(titleID, customerID, aDetail.getTagName(), con);
            }
            while((startPos = selectVal.indexOf('{'))!=-1) {
                endPos = selectVal.indexOf('}', startPos);
                if(endPos == -1) {
                    return null;
                }
                String paramName = selectVal.substring(startPos+1, endPos);
                value = SafeString.getSQLSafeString((String)allValues.get(paramName));
                if(value == null) {
                    return null; // no value found!
                }
                StringBuffer aBuf=new StringBuffer(selectVal);
                aBuf.replace(startPos, endPos+1, value);
                selectVal=aBuf.toString();
            }
        }
        if(selectVal.contains("[agnUID]")) {
        	//create and replace agnUID
        	try {
			int	urlID=0;

			try {
        			MailingComponent component = (MailingComponent) con.getBean("MailingComponent");
        			MailingComponentDao dao = (MailingComponentDao) con.getBean("MailingComponentDao");

        			component = dao.getMailingComponentByName(id, companyID, value);
				urlID=component.getUrlID();
			} catch(Exception e) {
				urlID=0;
			}
        		UID uid = (UID) con.getBean("UID");
        		uid.setCompanyID(companyID);
        		uid.setCustomerID(customerID);
        		uid.setMailingID(id);
        		uid.setURLID(urlID);
        		String uidstr = uid.makeUID();
System.err.println("UID: "+uidstr);
        		selectVal = SafeString.replace(selectVal, "[agnUID]", uidstr);
        	} catch (Exception e) {
        		//???
        	}
        }

		RecipientDao recipientDao = (RecipientDao) con.getBean("RecipientDao");
       	result = recipientDao.getField(selectVal, customerID, companyID);
		if(result == null) {
			processOK=false;
		}
		return result;
	}

    public String generateSalutation(int titleID, int customerID, String titleType, ApplicationContext con) {
        String returnValue = "";
        TitleDao tDao = (TitleDao) con.getBean("TitleDao");
        Title title = tDao.getTitle(titleID, this.companyID);

        if(title == null) {
            return null;
        }

        Recipient cust = (Recipient) con.getBean("Recipient");
        cust.setCompanyID(this.companyID);
        cust.setCustomerID(customerID);
        Map custData = cust.getCustomerDataFromDb();

        Integer gender = new Integer(Title.GENDER_UNKNOWN);
        String firstname = new String("");
        String lastname = new String("");
        String titel = "";

        try {
            gender = new Integer(Integer.parseInt((String)custData.get("gender")));
        } catch (Exception e) {
            //do nothing
        }

        try {
            firstname = ((String) custData.get("firstname")).trim();
        } catch (Exception e) {
            //do nothing
        }

        try {
            lastname = ((String) custData.get("lastname")).trim();
        } catch (Exception e) {
            //do nothing
        }
        if (StringUtils.isEmpty(lastname)) {
        	//generate salutation for gender unknown if no lastname ist available
        	gender = new Integer(Title.GENDER_UNKNOWN);
        }
        
        try {
        	titel = ((String) custData.get("title")).trim();
        } catch (Exception e) {
        	//do nothing
        }

        returnValue = (String)title.getTitleGender().get(gender);
        if(gender.intValue() != Title.GENDER_UNKNOWN) {
        	if(!titel.equals("")) {
        		returnValue = returnValue + " " + titel;
        	}
            if(titleType.equals("agnTITLEFULL")) {
                returnValue = returnValue + " " + firstname + " " + lastname;
            } else if(titleType.equals("agnTITLE")) {
                returnValue = returnValue + " " + lastname;
            } else if(titleType.equals("agnTITLEFIRST")) {
            	returnValue = (String)title.getTitleGender().get(gender) + " " + firstname;
            }
        }
        return returnValue;
    }

    public boolean checkIfOK() {
        return true;
    }

    /**
     * Getter for property emailParam.
     *
     * @return Value of property emailParam.
     * @param con
     */
    public MediatypeEmail getEmailParam(ApplicationContext con) {
        return (MediatypeEmail) mediatypes.get(new Integer(0));
    }

    /**
     * Getter for property targetGroups.
     * @return Value of property targetGroups.
     */
    public Collection getTargetGroups() {
        return this.targetGroups;
    }

    /**
     * Setter for property targetGroups.
     * @param targetGroups New value of property targetGroups.
     */
    public void setTargetGroups(Collection targetGroups) {
        this.targetGroups = targetGroups;
        this.targetExpression = this.generateTargetExpression();
    }


    /**
     * Setter for property htmlTemplate.
     * @param htmlTemplate New value of property htmlTemplate.
     */
    public void setHtmlTemplate(MailingComponent htmlTemplate) {
        this.components.put("agnHtml", htmlTemplate);
    }

    /**
     * Setter for property dynTags.
     * @param dynTags New value of property dynTags.
     */
    public void setDynTags(java.util.Map dynTags) {
        this.dynTags = dynTags;
    }

    /**
     * Setter for property dynTags.
     *
     * @param trackableLinks
     */
    public void setTrackableLinks(java.util.Map trackableLinks) {
        this.trackableLinks = trackableLinks;
    }

    /**
     * Setter for property components.
     * @param components New value of property components.
     */
    public void setComponents(java.util.Map components) {
        this.components = components;
    }

    /**
     * Setter for property textTemplate.
     * @param textTemplate
     */
    public void setTextTemplate(MailingComponent textTemplate) {
        this.components.put("agnText", textTemplate);
    }

    /**
     * Getter for property mailTemplateID.
     * @return Value of property mailTemplateID.
     */
    public int getMailTemplateID() {
        return this.mailTemplateID;
    }

    /**
     * Getter for property templateOK.
     * @return Value of property templateOK.
     */
    public int getTemplateOK() {
        return this.templateOK;
    }

    /**
     * Setter for property templateOK.
     * @param templateOK New value of property templateOK.
     */
    public void setTemplateOK(int templateOK) {
        this.templateOK = templateOK;
    }

    /**
     * Getter for property isTemplate.
     * @return Value of property isTemplate.
     */
    public boolean isIsTemplate() {
        return this.isTemplate;
    }

    /**
     * Setter for property isTemplate.
     * @param isTemplate New value of property isTemplate.
     */
    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    /**
     * Getter for property targetMode.
     * @return Value of property targetMode.
     */
    public int getTargetMode() {
        return this.targetMode;
    }

    /**
     * Setter for property targetMode.
     * @param targetMode New value of property targetMode.
     */
    public void setTargetMode(int targetMode) {
        this.targetMode = targetMode;
    }

    /**
     * Holds value of property targetExpression.
     */
    protected String targetExpression;

    /**
     * Getter for property targetExpression.
     * @return Value of property targetExpression.
     */
    public String getTargetExpression() {

        return this.targetExpression;
    }

    /**
     * Setter for property targetExpression.
     * @param targetExpression New value of property targetExpression.
     */
    public void setTargetExpression(String targetExpression) {

        this.targetExpression = targetExpression;
        this.parseTargetExpression(this.targetExpression);
    }

    /**
     * Holds value of property mediatypes.
     */
    protected java.util.Map mediatypes;

    /**
     * Getter for property mediatypes.
     * @return Value of property mediatypes.
     */
    public java.util.Map getMediatypes() {

        return this.mediatypes;
    }

    /**
     * Setter for property mediatypes.
     * @param mediatypes New value of property mediatypes.
     */
    public void setMediatypes(java.util.Map mediatypes) {

        this.mediatypes = mediatypes;
    }

    public void init(int companyID, ApplicationContext con) {
        MailingComponent comp = null;
        Mediatype type = null;

        this.companyID = companyID;

        comp = (MailingComponent) con.getBean("MailingComponent");
        comp.setCompanyID(companyID);
        comp.setComponentName("agnText");
        comp.setType(MailingComponent.TYPE_TEMPLATE);
        comp.setEmmBlock("[agnDYN name=\"Text-Version\"/]");
        comp.setMimeType("text/plain");
        this.components.put("agnText", comp);

        comp = (MailingComponent) con.getBean("MailingComponent");
        comp.setCompanyID(companyID);
        comp.setComponentName("agnHtml");
        comp.setType(MailingComponent.TYPE_TEMPLATE);
        comp.setEmmBlock("[agnDYN name=\"HTML-Version\"/]");
        comp.setMimeType("text/html");
        this.components.put("agnHtml", comp);

        type = (Mediatype) con.getBean("MediatypeEmail");
        this.mediatypes.put(new Integer(0), type);
    }

    public void cleanupDynTags(Vector keep) {
        Vector remove = new Vector();
        Object tmp = null;

        // first find keys which should be removed
        Iterator it = this.dynTags.keySet().iterator();
        while(it.hasNext()) {
            tmp = it.next();
            if(!keep.contains(tmp)) {
                remove.add(tmp);
            }
        }

        // now remove them!
        Enumeration e = remove.elements();
        while(e.hasMoreElements()) {
            dynTags.remove(e.nextElement());
        }
    }

    public void cleanupTrackableLinks(Vector keep) {
        Vector remove = new Vector();
        Object tmp = null;

        // first find keys which should be removed
        Iterator it = this.trackableLinks.keySet().iterator();
        while(it.hasNext()) {
            tmp = it.next();
            if(!keep.contains(tmp)) {
                remove.add(tmp);
            }
        }

        // now remove them!
        Enumeration e = remove.elements();
        while(e.hasMoreElements()) {
            this.trackableLinks.remove(e.nextElement());
        }
    }

    public void cleanupMailingComponents(Vector keep) {
        Vector remove = new Vector();
        MailingComponent tmp = null;

        // first find keys which should be removed
        Iterator it = this.components.values().iterator();
        while(it.hasNext()) {
            tmp = (MailingComponent) it.next();
            if(tmp.getType() == MailingComponent.TYPE_IMAGE && !keep.contains(tmp.getComponentName())) {
                remove.add(tmp.getComponentName());
            }
        }

        // now remove them!
        Enumeration e = remove.elements();
        while(e.hasMoreElements()) {
            this.components.remove(e.nextElement());
        }
    }

    public DynamicTag getDynamicTagById(int dynId) {
        DynamicTag tmp = null;

        Iterator it = this.dynTags.values().iterator();
        while(it.hasNext()) {
            tmp = (DynamicTag)it.next();
            if(dynId==tmp.getId()) {
                return tmp;
            }
        }
        return null;
    }

    public TrackableLink getTrackableLinkById(int urlID) {
        TrackableLink tmp=null;

        Iterator it=this.trackableLinks.values().iterator();
        while(it.hasNext()) {
            tmp = (TrackableLink)it.next();
            if(urlID == tmp.getId()) {
                return tmp;
            }
        }
        return null;
    }

    public Object clone(ApplicationContext con) {
        Mailing tmpMailing = (Mailing) con.getBean("Mailing");
        MailingComponent compOrg = null;
        MailingComponent compNew = null;
        TrackableLink linkOrg = null;
        TrackableLink linkNew = null;
        DynamicTag tagOrg = null;
        DynamicTag tagNew = null;
        DynamicTagContent contentOrg = null;
        DynamicTagContent contentNew = null;
        Mediatype emailNew = null;

        try {
            // copy components
            Iterator comps = this.components.values().iterator();
            while(comps.hasNext()) {
                compOrg = (MailingComponent) comps.next();
                compNew = (MailingComponent) con.getBean("MailingComponent");
                if(compOrg.getBinaryBlock() == null) {
                    compOrg.setBinaryBlock(new byte[1]);
                }
                BeanUtils.copyProperties(compNew, compOrg);
                compNew.setId(0);
                compNew.setMailingID(0);
                tmpMailing.addComponent(compNew);
            }

            // copy dyntags
            Iterator dyntags = this.dynTags.values().iterator();
            while(dyntags.hasNext()) {
                tagOrg = (DynamicTag) dyntags.next();
                tagNew = (DynamicTag) con.getBean("DynamicTag");
                Iterator contents = tagOrg.getDynContent().values().iterator();
                while(contents.hasNext()) {
                    contentOrg = (DynamicTagContent)contents.next();
                    contentNew = (DynamicTagContent)con.getBean("DynamicTagContent");
                    BeanUtils.copyProperties(contentNew, contentOrg);
                    contentNew.setId(0);
                    contentNew.setDynNameID(0);
                    tagNew.addContent(contentNew);
                }
                tagNew.setCompanyID(tagOrg.getCompanyID());
                tagNew.setDynName(tagOrg.getDynName());
                tmpMailing.addDynamicTag(tagNew);
            }

            // copy urls
            Iterator urls=this.trackableLinks.values().iterator();
            while(urls.hasNext()) {
                linkOrg = (TrackableLink)urls.next();
                linkNew = (TrackableLink)con.getBean("TrackableLink");
                BeanUtils.copyProperties(linkNew, linkOrg);
                linkNew.setId(0);
                linkNew.setMailingID(0);
                tmpMailing.getTrackableLinks().put(linkNew.getFullUrl(), linkNew);
            }

            // copy emailparam
            emailNew = (Mediatype)con.getBean("MediatypeEmail");
            emailNew.setParam(this.getEmailParam(con).getParam());
            tmpMailing.getMediatypes().put(new Integer(0), emailNew);

        } catch (Exception e) {

            AgnUtils.logger().error("could not copy: "+e);
            return null;
        }
        return tmpMailing;
    }

    public boolean buildDependencies(boolean scanDynTags, ApplicationContext con) throws Exception {
        Vector dynTags = new Vector();
        Vector components = new Vector();
        Vector links = new Vector();

        // scan for Dyntags
        // in template-components and Mediatype-Params
        if(scanDynTags) {
            dynTags.addAll(this.findDynTagsInTemplates(con));
            dynTags.addAll(this.findDynTagsInTemplates(this.getEmailParam(con).getSubject(), con));
            dynTags.addAll(this.findDynTagsInTemplates(this.getEmailParam(con).getReplyAdr(), con));
            dynTags.addAll(this.findDynTagsInTemplates(this.getEmailParam(con).getFromAdr(), con));
            this.cleanupDynTags(dynTags);
        }
        // scan for Components
        // in template-components and dyncontent
        components.addAll(this.scanForComponents(con));
        this.cleanupMailingComponents(components);

        // scan for Links
        // in template-components and dyncontent
        links.addAll(this.scanForLinks(con));
        if(AgnUtils.isOracleDB()) {
        	//causes problem with links in OpenEMM
        	this.cleanupTrackableLinks(links);
        }

        return true;
    }

	public Map getAllowedTargets(ApplicationContext myContext) {
		if (allowedTargets != null) {
			return allowedTargets;
		}
		TargetDao dao = (TargetDao) myContext.getBean("TargetDao");
        
		allowedTargets = dao.getAllowedTargets(companyID);
		if(allowedTargets != null) {
			Target aTarget = (Target) myContext.getBean("Target");

			aTarget.setCompanyID(companyID);
			aTarget.setId(0);
			aTarget.setTargetName("All Subscribers");
			allowedTargets.put(new Integer(0), aTarget);
		}
		return allowedTargets;
	}

    /**
     * Holds value of property maildropStatus.
     */
    protected java.util.Set maildropStatus;

    /**
     * Getter for property maildropStatus.
     * @return Value of property maildropStatus.
     */
    public java.util.Set getMaildropStatus() {
        return this.maildropStatus;
    }

    /**
     * Setter for property maildropStatus.
     * @param maildropStatus New value of property maildropStatus.
     */
    public void setMaildropStatus(java.util.Set maildropStatus) {
        this.maildropStatus = maildropStatus;
    }

    /**
     * Holds value of property deleted.
     */
    protected int deleted;

    /**
     * Getter for property deleted.
     * @return Value of property deleted.
     */
    public int getDeleted() {
        return this.deleted;
    }

    /**
     * Setter for property deleted.
     * @param deleted New value of property deleted.
     */
    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    protected boolean needsTarget;

    public boolean getNeedsTarget() {
        return this.needsTarget;
    }

    public void setNeedsTarget(boolean needsTarget) {
        this.needsTarget = needsTarget;
    }

    protected boolean locked;

    public int getLocked() {
        return this.locked?1:0;
    }

    public void setLocked(int locked) {
        this.locked = (locked != 0)?true:false;
    }

    public void setSearchPos(int pos) {
        searchPos=pos;
    }
    
    protected boolean archived;

    public int getArchived() {
        return this.archived?1:0;
    }

    public void setArchived(int archived) {
        this.archived = (archived != 0)?true:false;
    }
}
