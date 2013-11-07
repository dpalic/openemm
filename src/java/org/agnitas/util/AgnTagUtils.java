package org.agnitas.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.TagDetails;
import org.agnitas.beans.Title;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.TitleDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

public class AgnTagUtils {

	 public static String processTag(TagDetails aDetail, int customerID, ApplicationContext con, int mailingID,int mailinglistID, int companyID) {
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
	        selectVal = SafeString.replace(selectVal, "[company-id]", Integer.toString(companyID));
	        selectVal = SafeString.replace(selectVal, "[mailinglist-id]", Integer.toString(mailinglistID));
	        selectVal = SafeString.replace(selectVal, "[mailing-id]", Integer.toString(mailingID));
	        if(selectVal.contains("[rdir-domain]")) {
	            CompanyDao cDao = (CompanyDao)con.getBean("CompanyDao");
	            selectVal = SafeString.replace(selectVal, "[rdir-domain]", cDao.getCompany(companyID).getRdirDomain());
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
	                return generateSalutation(titleID, customerID, aDetail.getTagName(), con,  companyID );
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

	        			component = dao.getMailingComponentByName(mailingID, companyID, value);
					urlID=component.getUrlID();
				} catch(Exception e) {
					urlID=0;
				}
	        		UID uid = (UID) con.getBean("UID");
	        		uid.setCompanyID(companyID);
	        		uid.setCustomerID(customerID);
	        		uid.setMailingID(mailingID);
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
	
	 public static String generateSalutation(int titleID, int customerID, String titleType, ApplicationContext con, int companyID) {
	        String returnValue = "";
	        TitleDao tDao = (TitleDao) con.getBean("TitleDao");
	        Title title = tDao.getTitle(titleID, companyID);

	        if(title == null) {
	            return null;
	        }

	        Recipient cust = (Recipient) con.getBean("Recipient");
	        cust.setCompanyID(companyID);
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

	 
}
