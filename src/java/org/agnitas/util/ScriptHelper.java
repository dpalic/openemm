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
/*
 * ScriptHelper.java
 *
 * Created on 22. January 2008, 10:29
 */

package org.agnitas.util;

import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.springframework.context.ApplicationContext;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.dao.MailingDao;

/**
 *
 * @author  Martin Helff, Andreas Rehak
 */
public class ScriptHelper {

	/**
	 * Holds value of property con.
	 */
	private ApplicationContext con=null;

	/** Creates a new instance of ScriptHelper */
	public ScriptHelper(ApplicationContext con) {
		this.con=con;
	}

	public ApplicationContext	getApplicationContext(){
		return con;
	}

	private HashMap buildRecipient(NodeList allMessageChilds) throws Exception {
		HashMap result=new HashMap();
		Node aNode=null;
		String nodeName=null;
		NodeList recipientNodes=null;
		Node recipientNode=null;
		String recipientNodeName=null;
		NamedNodeMap allAttr=null;
		
		for(int i=0; i<allMessageChilds.getLength(); i++) {
			aNode=allMessageChilds.item(i);
			nodeName=aNode.getNodeName();
			
			if(nodeName.equals("recipient")) {
				// System.out.println("found node: "+nodeName);
				recipientNodes=aNode.getChildNodes();
				for(int j=0; j<recipientNodes.getLength(); j++) {
					recipientNode=recipientNodes.item(j);
					recipientNodeName=recipientNode.getNodeName();
					if(recipientNodeName.equals("gender") || recipientNodeName.equals("firstname") || recipientNodeName.equals("lastname") || recipientNodeName.equals("mailtype") || recipientNodeName.equals("email")) {
						try {
							result.put(recipientNodeName.toUpperCase(), recipientNode.getFirstChild().getNodeValue());
						} catch (Exception e) {
							// do nothing
						}
					}
					if(recipientNodeName.equals("extracol")) {
						allAttr=recipientNode.getAttributes();
						try {
							result.put(allAttr.getNamedItem("name").getNodeValue().toUpperCase(), recipientNode.getFirstChild().getNodeValue());
						} catch (Exception e) {
							// do nothing
						}
					}
				}
			}
			if(nodeName.equals("content")) {
				//System.out.println("found node: "+nodeName);
				allAttr=aNode.getAttributes();
				try {
					result.put(allAttr.getNamedItem("name").getNodeValue().toUpperCase(), aNode.getFirstChild().getNodeValue());
				} catch (Exception e) {
					// do nothing
				}
			}
		}
		
		return result;
	}

	public LinkedList parseTransactionMailXml(String xmlInput) {
		LinkedList result=new LinkedList();
		boolean validation = false;
		boolean ignoreWhitespace = true;
		boolean ignoreComments   = true;
		boolean putCDATAIntoText = true;
		boolean createEntityRefs = false;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		// set the configuration options
		dbf.setValidating(validation);
		dbf.setIgnoringComments(ignoreComments);
		dbf.setIgnoringElementContentWhitespace(ignoreWhitespace);
		dbf.setCoalescing(putCDATAIntoText);
		// The opposite of creating entity ref nodes is expanding them inline
		dbf.setExpandEntityReferences(!createEntityRefs);
		
		DocumentBuilder db=null;
		Document doc=null;
		try {
			db=dbf.newDocumentBuilder();
			doc=db.parse(new InputSource(new StringReader(xmlInput)));
			Element base=doc.getDocumentElement();
			NodeList allMessages=base.getChildNodes();
			NodeList allMessageChilds=null;
			Node aMessage=null;
			NamedNodeMap allAttr=null;
			String nodeName=null;
			int messageType=0;
			HashMap messageEntry=null;
			
			for(int i=0; i<allMessages.getLength(); i++) {
				aMessage=allMessages.item(i);
				nodeName=aMessage.getNodeName();
				
				if(nodeName.equals("message")) {
					// System.out.println("found node: "+nodeName);
					messageEntry=new HashMap();
					allAttr=aMessage.getAttributes();
					messageType=Integer.parseInt(allAttr.getNamedItem("type").getNodeValue());
					messageEntry.put("messageType", new Integer(messageType));
					allMessageChilds=aMessage.getChildNodes();
					messageEntry.put("recipient", buildRecipient(allMessageChilds));
					result.add(messageEntry);
				}
			}
			
		} catch (Exception e) {
			AgnUtils.logger().error(AgnUtils.getStackTrace(e));
			result=null;
		}
	
		return result;
	}
	
	public boolean sendEmail(String from_adr, String to_adr, String subject, String body_text, String body_html, int mailtype, String charset) {
		return AgnUtils.sendEmail(from_adr, to_adr, subject, body_text, body_html, mailtype, charset);
	}
	
	public Map newHashMasp() {
		return new HashMap();
	}
	
	public void println(String output) {
		System.err.println(output);
	}

	/**
	 * Finds the last newsletter that would have been sent to the given
	 * customer. The newsletter also gets a new entry maildrop_status_tbl 
	 * to allow it to be sent as action mail.
	 * @param customerID Id of the recipient for the newsletter.
	 * @param companyID the company to look in.
	 * @return The mailingID of the last newsletter that would have been
	 *		sent to this recipient.
	 */
	public int	findLastNewsletter(int customerID, int companyID)	{
		MailingDao	dao=(MailingDao) con.getBean("MailingDao");
		int	mailingID=dao.findLastNewsletter(customerID, companyID);
		Mailing mailing=dao.getMailing(mailingID, companyID);

		if(mailing == null) {
			return 0;
		}

		Iterator	i=mailing.getMaildropStatus().iterator();
		MaildropEntry	entry=(MaildropEntry) i.next();

		while(i.hasNext()) {
			entry=(MaildropEntry) i.next();
			if(entry.getStatus()==MaildropEntry.STATUS_ACTIONBASED) {
				return mailingID;
			}
		}
		MaildropEntry drop=(MaildropEntry) con.getBean("MaildropEntry");

		drop.setStatus(MaildropEntry.STATUS_ACTIONBASED);
		drop.setSendDate(new java.util.Date());
		drop.setGenDate(new java.util.Date());
		drop.setGenStatus(1);
		drop.setGenChangeDate(new java.util.Date());
		drop.setMailingID(mailingID);
		drop.setCompanyID(companyID);
		mailing.getMaildropStatus().add(drop);
		dao.saveMailing(mailing);
		return mailingID;
	}
}
