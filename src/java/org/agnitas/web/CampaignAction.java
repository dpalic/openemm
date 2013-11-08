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

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.Campaign;
import org.agnitas.beans.Company;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.impl.CampaignImpl;
import org.agnitas.beans.impl.CampaignStatsImpl;
import org.agnitas.dao.CampaignDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.service.CampaignQueryWorker;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.CampaignForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;


public class CampaignAction extends StrutsActionBase { 
    
	public static final String FUTURE_TASK = "GET_CAMPAIGN_LIST";
    public static final int ACTION_STAT = ACTION_LAST+1;
    public static final int ACTION_SPLASH = ACTION_LAST+2;
    
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param req The HTTP request we are processing
     * @param res The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
        
    	ApplicationContext aContext  = this.getWebApplicationContext();	// our application Context
    	CampaignImpl campaignImpl = new CampaignImpl();
    	CampaignStatsImpl stats = campaignImpl.getCampaignStats();
    	
        // Validate the request parameters specified by the user        
        CampaignForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;        
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }       
        if(form!=null) {
            aForm=(CampaignForm)form;
        } else {
            aForm=new CampaignForm();
        }       
        
        AgnUtils.logger().info("Action: "+aForm.getAction());
        
        if(req.getParameter("delete.x")!=null) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }
        
        try {
            switch(aForm.getAction()) {
                case CampaignAction.ACTION_LIST:
                    if(allowed("campaign.show", req)) {
						if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
                    	}
                        destination=mapping.findForward("list");
                        aForm.reset(mapping, req);                       
                        aForm.setAction(CampaignAction.ACTION_LIST);	// reset Action!
                    }
                    break;
                    
                case CampaignAction.ACTION_VIEW:
                    if(allowed("campaign.show", req)) {
                    	aForm.reset(mapping, req);
                        loadCampaign(aForm, req);
                        aForm.setAction(CampaignAction.ACTION_SAVE);                        
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                    
                case CampaignAction.ACTION_SAVE:
                    if(allowed("campaign.change", req)) {
                        saveCampaign(aForm, req);
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("list");
                    break;
                    
                case CampaignAction.ACTION_NEW:
                    if(allowed("campaign.show", req)) {
                        aForm.reset(mapping, req);
                        aForm.setAction(CampaignAction.ACTION_SAVE);
                        aForm.setCampaignID(0);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        destination=mapping.findForward("list");
                    }
                    break;
                    
                case CampaignAction.ACTION_CONFIRM_DELETE:
                    loadCampaign(aForm, req);
                    aForm.setAction(CampaignAction.ACTION_DELETE);
                    destination=mapping.findForward("delete");
                    break;
                    
                case CampaignAction.ACTION_DELETE:
                    if(allowed("campaign.show", req)) {
                        if(AgnUtils.parameterNotEmpty(req, "kill")) {
                            this.deleteCampaign(aForm, req);
                            aForm.setAction(CampaignAction.ACTION_LIST);
                            
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    destination=mapping.findForward("list");
                    break;
                    
                case CampaignAction.ACTION_STAT:
                	destination = mapping.findForward("splash");	// default is splash-screen.
            		aForm.setAction(CampaignAction.ACTION_SPLASH);
            		setNumberOfRows(req,(StrutsFormBase)form);   // could change till next call on this variable is done.
            		loadCampaign(aForm, req);
            		try {
            			 AbstractMap<String,Future> futureHolder = (AbstractMap<String, Future>)getBean("futureHolder");
           			     String key =  FUTURE_TASK+"@"+ req.getSession(false).getId();
                		// look if we have a Future, if not, get one. 
                		if (!futureHolder.containsKey(key)) { 
                			 Future campaignFuture = getCampaignListFuture(req, aContext, aForm);
          				     futureHolder.put(key,campaignFuture);
                		}     
                		// look if we are already done. 
                		if (futureHolder.containsKey(key)  && futureHolder.get(key).isDone()) {//                			
                				stats = (CampaignStatsImpl) futureHolder.get(key).get();	// get the results.                				
                				if(stats != null) {
                					setFormStat(aForm, stats);                					
                				}        		
                				setSortedMailingList(stats, req, aForm);
                				aForm.setStatReady(true);
                				aForm.setAction(CampaignAction.ACTION_STAT);
                				destination = mapping.findForward("stat");	// set destination to Statistic-page.
                				futureHolder.remove(key);	// reset Future because we are already done.
                				aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS); // set refresh-time to default.                				
//                			}
                		} else {       
                			// increment Refresh-Rate. if it is a very long request,
                			// we dont have to refresh every 250ms, then 1 second is enough.
                			if( aForm.getRefreshMillis() < 1000 ) { // raise the refresh time
                				aForm.setRefreshMillis( aForm.getRefreshMillis() + 50 );
                			}
                			
                		}   
        			} catch (NullPointerException e) {
        				AgnUtils.logger().error("getCampaignList: "+e+"\n"+AgnUtils.getStackTrace(e));
        	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        			} 
        			
        			break;                  
                    
                case CampaignAction.ACTION_SPLASH:
                	 AbstractMap<String,Future> futureHolder = (AbstractMap<String, Future>)getBean("futureHolder");
       			     String key =  FUTURE_TASK+"@"+ req.getSession(false).getId();
                	if ( futureHolder.containsKey(key) && futureHolder.get(key).isDone()) {
                		aForm.setAction(CampaignAction.ACTION_STAT);
                		destination=mapping.findForward("stat");
                	} else  {
                		loadCampaign(aForm, req);
                		aForm.setAction(CampaignAction.ACTION_SPLASH);
                		destination=mapping.findForward("splash");
                	}
                	break;
                    
                default:
                    aForm.setAction(CampaignAction.ACTION_LIST);
                    destination=mapping.findForward("list");                    
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        if( destination != null && "list".equals(destination.getName())) {
        	try {
        		setNumberOfRows(req,(StrutsFormBase)form);        		
				req.setAttribute("campaignlist", getCampaignList(req ));
			} catch (Exception e) {
				AgnUtils.logger().error("getCampaignList: "+e+"\n"+AgnUtils.getStackTrace(e));
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			} 
        }
       
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }
        
        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }
       
        return destination;
    }


    /*
     * this method sets the Form-Stats. It would be good, if stat is not null
     */
	private void setFormStat(CampaignForm aForm, CampaignStatsImpl stat) {
		if (stat != null) {
			aForm.setOpened(stat.getOpened());
			aForm.setOptouts(stat.getOptouts());
			aForm.setBounces(stat.getBounces());
			aForm.setSubscribers(stat.getSubscribers());
			aForm.setClicks(stat.getClicks());			
			aForm.setMaxClicks(stat.getMaxClicks());
			aForm.setMaxOpened(stat.getMaxOpened());
			aForm.setMaxOptouts(stat.getMaxOptouts());
			aForm.setMaxSubscribers(stat.getMaxSubscribers());
			aForm.setMaxBounces(stat.getMaxBounces());
			aForm.setMailingData(stat.getMailingData());			
		}
	}
	
	/*
	 * this method creates a List with all Mailing-IDs in a sorted order an writes it into the
	 * CampaignForm.
	 */
	private void setSortedMailingList(CampaignStatsImpl stat, HttpServletRequest req, CampaignForm aForm) {
		LinkedList<Number> resultList = new LinkedList<Number>();
		MailingDao mailDao = (MailingDao) getBean("MailingDao");
		
		// this hashmap contains the mapping from a Date back to the Mail-ID.
		HashMap<Date, Number> tmpDate2MailIDMapping = new HashMap<Date, Number>();
		LinkedList<Date> sortedMailingList = new LinkedList<Date>();
		
		Hashtable map = stat.getMailingData();	// holds the complete mailing Data
		map.keySet();	// all keys for the mailingData (mailIDs)
		
		Number tmpMailID = null;	
		MaildropEntry tmpEntry = null;
		Mailing tmpMailing = null;
		
		// loop over all keys.
		Iterator it = map.keySet().iterator();		
		while (it.hasNext()) {
			LinkedList<Date> sortDates = new LinkedList<Date>();
			tmpMailID = (Number)it.next();	// get the mailID	
			// get one Mailing with tmpMailID
			tmpMailing = (Mailing)mailDao.getMailing(tmpMailID.intValue(), getCompanyID(req));
			// check if it is a World-Mailing. We have testmailings and dont care about them!
			if (tmpMailing.isWorldMailingSend() == true) {
				// loop over all tmpMailingdropStatus.
				// we look over all mails and take the first send mailing Time.
				// unfortunately is the set not sorted, so we have to sort it ourself.
				Iterator<MaildropEntry> it2 = tmpMailing.getMaildropStatus().iterator();
				while(it2.hasNext()) {
					tmpEntry = it2.next();		            
					sortDates.add(tmpEntry.getSendDate());		            		            
				}			 
				// check if sortDates has entries and put the one into the Hashmap.
				if (sortDates.size() != 0) {
					Collections.sort(sortDates);
					tmpDate2MailIDMapping.put(sortDates.get(0), tmpMailID);
					sortedMailingList.add(sortDates.get(0));
				}			 
			}
		}
		// at this point, we have a Hashmap with all Dates and Mailing ID's and a List with all Date's.
		// now we sort this List and put the result into the Form (sort with reverse Order ;-) ).
		Collections.sort(sortedMailingList, Collections.reverseOrder());
		// loop over the List and put the corresponding MailID into the List.
		for (int i=0; i < sortedMailingList.size(); i++) {
			resultList.add(tmpDate2MailIDMapping.get(sortedMailingList.get(i)));
		}		
		aForm.setSortedKeys(resultList);
	}
    
    /**
     * Loads campaign.
     */    
    protected void loadCampaign(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        CampaignDao campaignDao = (CampaignDao) getBean("CampaignDao");
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
        if(myCamp != null) {
            aForm.setShortname(myCamp.getShortname());
            aForm.setDescription(myCamp.getDescription());
        } else {
            AgnUtils.logger().error("could not load campaign: "+aForm.getTargetID());
        }
    }
    
    /**
     * Saves campaign.
     */    
    protected void saveCampaign(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        CampaignDao campaignDao = (CampaignDao) getBean("CampaignDao");
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
        if(myCamp == null) {
            aForm.setCampaignID(0);
            myCamp=(Campaign) getBean("Campaign");
            myCamp.setCompanyID(companyID);
        }
        
        myCamp.setShortname(aForm.getShortname());
        myCamp.setDescription(aForm.getDescription());
        
        campaignID = campaignDao.save(myCamp);
        myCamp.setId(campaignID);
    }
    
    /**
     * Deletes campaign.
     */    
    protected void deleteCampaign(CampaignForm aForm, HttpServletRequest req) {
        int campaignID=aForm.getCampaignID();
        int companyID = getCompanyID(req);
        CampaignDao campaignDao = (CampaignDao) getBean("CampaignDao");
        Campaign myCamp = campaignDao.getCampaign(campaignID, companyID);
        
        if(myCamp!=null) {
           campaignDao.delete(myCamp);
        }
    } 
    
    /**
     * loads the campaigns
     * @throws InstantiationException 
     * @throws IllegalAccessException 
     *   
     * 
     */
    
    public List<Campaign> getCampaignList(HttpServletRequest request ) throws IllegalAccessException, InstantiationException {
    	ApplicationContext aContext= getWebApplicationContext();
	    JdbcTemplate aTemplate=new JdbcTemplate( (DataSource)aContext.getBean("dataSource"));
	    
	    List<Integer>  charColumns = Arrays.asList(new Integer[]{0,1 });
		String[] columns = new String[] { "shortname","description","" };
		  
	         
     	int sortcolumnindex = 0; 
     	if( request.getParameter(new ParamEncoder("campaign").encodeParameterName(TableTagParameters.PARAMETER_SORT)) != null ) {
     		sortcolumnindex = Integer.parseInt(request.getParameter(new ParamEncoder("campaign").encodeParameterName(TableTagParameters.PARAMETER_SORT))); 
     	}	    	
     

	     String sort =  columns[sortcolumnindex];
	     if (charColumns.contains(sortcolumnindex)) {
	    	 sort =   "upper( " +sort + " )";
	     }
	     	
     	
     	int order = 1; 
     	if( request.getParameter(new ParamEncoder("campaign").encodeParameterName(TableTagParameters.PARAMETER_ORDER)) != null ) {
     		order = new Integer(request.getParameter(new ParamEncoder("campaign").encodeParameterName(TableTagParameters.PARAMETER_ORDER)));
     	}
     
     	String sqlStatement = "SELECT campaign_id, shortname, description FROM campaign_tbl WHERE company_id="+AgnUtils.getCompanyID(request)+" ORDER BY "+sort+ " " +(order == 2 ? "DESC":"ASC")    ;
     	List<Map> tmpList = aTemplate.queryForList(sqlStatement);
        
	     
	      
	      List<Campaign> result = new ArrayList<Campaign>();
	      for(Map row:tmpList) {
	    	  
	    	  Campaign campaign = new CampaignImpl();
	    	  campaign.setId(((Number)row.get("CAMPAIGN_ID")).intValue());
	    	  campaign.setShortname( (String) row.get("SHORTNAME"));
	    	  campaign.setDescription( (String) row.get("DESCRIPTION"));
	    	  result.add(campaign);
	    	  
	      } 
	      return result;    	
    }
    
    /*
     * returns a Future for asynchronous computation of the CampaignStats.
     */
    public Future getCampaignListFuture( HttpServletRequest req, ApplicationContext aContext, CampaignForm aForm  ) throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {  	
    	
    	CampaignDao campaignDao = (CampaignDao) aContext.getBean("CampaignDao");
    	CompanyDao compDao = (CompanyDao) getBean("CompanyDao");
        Company comp = (Company) compDao.getCompany(AgnUtils.getCompanyID(req));
        Locale aLoc = (Locale) req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        boolean mailtracking;
        // i dont know why mailtracking returns an int here, but i use boolean though its not handsome but it works (hopefully).
        if (comp.getMailtracking() == 0) {
        	mailtracking = false;
        } else {
        	mailtracking = true;
        }          	
        
     	// now we start get the data. But we start that as background job.
        // the result is available via future.get().
     	ExecutorService service = (ExecutorService) aContext.getBean("workerExecutorService");     	
     	Future future = service.submit(	new CampaignQueryWorker(campaignDao, aLoc, aForm, req, mailtracking, aContext, aForm.getTargetID()));
     	
    	return future;  
    }
}
