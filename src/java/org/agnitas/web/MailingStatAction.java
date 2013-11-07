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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.stat.MailingStat;
import org.agnitas.util.AgnUtils;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;



public class MailingStatAction extends StrutsActionBase {

    public static final int ACTION_MAILINGSTAT = ACTION_LAST+1;
    public static final int ACTION_WEEKSTAT = ACTION_LAST+2;
    public static final int ACTION_DAYSTAT = ACTION_LAST+3;
    public static final int ACTION_CLEAN_QUESTION = ACTION_LAST+4;
    public static final int ACTION_CLEAN = ACTION_LAST+5;
    public static final int ACTION_SPLASH = ACTION_LAST+6;
    public static final int ACTION_OPENEDSTAT = ACTION_LAST+7;
    public static final int ACTION_OPENEDSTAT_SPLASH = ACTION_LAST+8;
    public static final int ACTION_BOUNCESTAT = ACTION_LAST+9;
    public static final int ACTION_BOUNCESTAT_SPLASH = ACTION_LAST+10;
    public static final int ACTION_BOUNCE = ACTION_LAST + 11;
    public static final int ACTION_OPEN_TIME = ACTION_LAST + 12;
	public static final int ACTION_OPEN_DAYSTAT = ACTION_LAST + 13;
    public static final int ACTION_MAILING_STAT_LAST = ACTION_LAST+13;


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param form
     * @param req
     * @param res
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */

    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        MailingStatForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;

        if(!this.checkLogon(req))
            return mapping.findForward("logon");

        if(form!=null) {
            aForm=(MailingStatForm)form;
        } else {
            aForm=new MailingStatForm();
        }

        AgnUtils.logger().info("Action: " + aForm.getAction());

        if(!allowed("stats.mailing", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }


        try {
            switch(aForm.getAction()) {

                case ACTION_LIST:
                	if ( aForm.getColumnwidthsList() == null) {
                    	aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
                    }	
                    destination=mapping.findForward("list");
                    break;

                case ACTION_MAILINGSTAT:
                    if(aForm.isStatInProgress()==false) {
                        if(aForm.isStatReady()) {
                            destination=mapping.findForward("mailing_stat");
                            aForm.setStatReady(false);
                            break;
                        } else {
                            // display splash in browser
                            RequestDispatcher dp=req.getRequestDispatcher(mapping.findForward("splash").getPath());
                            dp.forward(req, res);
                            res.flushBuffer();
                            destination=null;
                            // get stats
                            aForm.setStatInProgress(true);
                            loadMailingStat(aForm, req);

                            aForm.setStatInProgress(false);
                            aForm.setStatReady(true);
                            break;
                        }

                    }
                    break;

                case ACTION_SPLASH:
                    if(aForm.isStatReady()) {
                        destination=mapping.findForward("mailing_stat");
                    }
                    // just display splash
                    destination=mapping.findForward("splash");
                    break;

                case ACTION_OPENEDSTAT_SPLASH:
                    if(aForm.isStatReady()) {
                        destination=mapping.findForward("opened_stat");
                    }
                    // just display splash
                    destination=mapping.findForward("splash");
                    break;

                case ACTION_BOUNCESTAT_SPLASH:
                    if(aForm.isStatReady()) {
                        destination=mapping.findForward("bounce_stat");
                    }
                    // just display splash
                    destination=mapping.findForward("splash");
                    break;


                case ACTION_WEEKSTAT:
                    loadWeekStat(aForm, req);
                    destination=mapping.findForward("week_stat");
                    break;

                case ACTION_DAYSTAT:
                    loadDayStat(aForm, req);
                    destination=mapping.findForward("day_stat");
                    break;

                case ACTION_CLEAN_QUESTION:
                    destination=mapping.findForward("clean_question");
                    break;

                case ACTION_CLEAN:
                    cleanAdminClicks(aForm, req);
                    loadMailingStat(aForm, req);
                    destination=mapping.findForward("mailing_stat");
                    break;

                case ACTION_OPENEDSTAT:
                    if(aForm.isStatInProgress()==false) {
                        if(aForm.isStatReady()) {
                            destination=mapping.findForward("opened_stat");
                            aForm.setStatReady(false);
                            break;
                        } else {
                            RequestDispatcher dp=req.getRequestDispatcher(mapping.findForward("splash").getPath());
                            dp.forward(req, res);
                            res.flushBuffer();
                            destination=null;
                            // get stats
                            aForm.setStatInProgress(true);
                            loadOpenedStat(aForm, req);
                            aForm.setStatInProgress(false);
                            aForm.setStatReady(true);
                            break;
                        }
                    }
                    break;

                case ACTION_BOUNCESTAT:
                    if(aForm.isStatInProgress()==false) {
                        if(aForm.isStatReady()) {
                            destination=mapping.findForward("bounce_stat");
                            aForm.setStatReady(false);
                            break;
                        } else {
                            RequestDispatcher dp=req.getRequestDispatcher(mapping.findForward("splash").getPath());
                            dp.forward(req, res);
                            res.flushBuffer();
                            destination=null;
                            // get stats
                            aForm.setStatInProgress(true);
                            loadBounceStat(aForm, req);
                            aForm.setStatInProgress(false);
                            aForm.setStatReady(true);
                            break;
                        }
                    }
                    break;
                case ACTION_BOUNCE:
    				destination = mapping.findForward("bounce");
    				break;
    				
                case ACTION_OPEN_TIME:
                    loadOpenWeekStat(aForm, req);
                    destination=mapping.findForward("open_week");
                    break;

                case ACTION_OPEN_DAYSTAT:
                    loadOpenDayStat(aForm, req);
                    destination=mapping.findForward("open_day");
                    break;

                default:
                    aForm.setAction(MailingStatAction.ACTION_MAILINGSTAT);
                    loadMailingStat(aForm, req);
                    destination=mapping.findForward("list");
            }
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        if(destination != null &&  "list".equals(destination.getName())) {
        	try {
				req.setAttribute("mailingStatlist", getMailingStats(req));
				setNumberOfRows(req, aForm);
			} catch(Exception e) {
				AgnUtils.logger().error("mailingStatlist: "+e+"\n"+AgnUtils.getStackTrace(e));
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			}        	
        }
        

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }

        return destination;
    }

    /**
     * Loads mailing statistics.
     */
    protected void loadMailingStat(MailingStatForm aForm, HttpServletRequest req) {
        //set variables from form:

        MailingStat aMailStat=(MailingStat) getBean("MailingStat");
        aMailStat.setCompanyID(getCompanyID(req));
        int tid = aForm.getTargetID();
        aMailStat.setTargetID(tid);
        int mid = aForm.getMailingID();
        aMailStat.setMailingID(mid);

        if(aForm.getTargetIDs()!=null) {
            LinkedList targets = aForm.getTargetIDs();
            int atid = aForm.getNextTargetID();
            if(targets.contains(new Integer(atid)) == false) {
                targets.add(new Integer(atid));
            }

            if(req.getParameter("delTargetID")!=null) {
                if( targets.contains(new Integer(req.getParameter("delTargetID"))) && targets.size()>1) {
                    targets.remove(new Integer(req.getParameter("delTargetID")));
                }
            }
            aMailStat.setTargetIDs(targets);
        } else {
            LinkedList targets = new LinkedList();
            targets.add(new Integer(0));
            aMailStat.setTargetIDs(targets);
        }



        // if we come from the mailstat page itself, pass statValues data:
        if(req.getParameter("add.x")!=null) {
            aMailStat.setStatValues(aForm.getStatValues());
        } else if(req.getParameter("delTargetID")!=null) {
            // delete MailingStatEntry for targetID to be deleted:
            Hashtable tmpStatVal = aForm.getStatValues();
            if(tmpStatVal.containsKey(new Integer(req.getParameter("delTargetID")))) {
                tmpStatVal.remove(new Integer(req.getParameter("delTargetID")));
            }
            // and put the statValues in the MailingStat class:
            aMailStat.setStatValues(tmpStatVal);
        } else {
            // delete all stat info:
            LinkedList targets = new LinkedList();
            targets.add(new Integer(0));
            aMailStat.setTargetIDs(targets);
            Hashtable tmpStatVal = new Hashtable();
            aMailStat.setStatValues(tmpStatVal);
        }

        if(aMailStat.getMailingStatFromDB(this.getWebApplicationContext(), (Locale)req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY))==true) {
            // write results back to form:
            aForm.setCsvfile(aMailStat.getCsvfile());
            aForm.setClickSubscribers( aMailStat.getClickSubscribers() );
            aForm.setClicks(aMailStat.getClicks());
            aForm.setOpenedMails(aMailStat.getOpenedMails());
            aForm.setOptOuts(aMailStat.getOptOuts());
            aForm.setBounces(aMailStat.getBounces());
            aForm.setTotalSubscribers(aMailStat.getTotalSubscribers());
            aForm.setValues(aMailStat.getValues());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
            aForm.setMailingID(mid);
            aForm.setStatValues(aMailStat.getStatValues());
            aForm.setTargetIDs(aMailStat.getTargetIDs());
            aForm.setUrlNames(aMailStat.getUrls());
            aForm.setUrlShortnames(aMailStat.getUrlShortnames());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setMaxNRblue(aMailStat.getMaxNRblue());
            aForm.setMaxSubscribers(aMailStat.getMaxSubscribers());
            aForm.setClickedUrls(aMailStat.getClickedUrls());
            aForm.setNotRelevantUrls(aMailStat.getNotRelevantUrls());
        } else {
            AgnUtils.logger().error("loadMailingStat: could not load mailing stats.");
        }
    }

    /**
     * Loads opened statistics.
     */
    protected void loadOpenedStat(MailingStatForm aForm, HttpServletRequest req) {

        MailingStat aMailStat=(MailingStat) getBean("MailingStat");
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setTargetID(aForm.getTargetID());
        aMailStat.setMailingID(aForm.getMailingID());

        // write results back to form:
        if(aMailStat.getOpenedStatFromDB(getWebApplicationContext(), req)==true) {
            aForm.setValues(aMailStat.getValues());
            aForm.setCsvfile(aMailStat.getCsvfile());

        } else {
            AgnUtils.logger().error("loadOpenedStat: could not load opened stats.");
        }
    }

    /**
     * Loads bounce statistics.
     */
    protected void loadBounceStat(MailingStatForm aForm, HttpServletRequest req) {

        MailingStat aMailStat=(MailingStat) getBean("MailingStat");
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setTargetID(aForm.getTargetID());
        aMailStat.setMailingID(aForm.getMailingID());

        // write results back to form:
        if(aMailStat.getBounceStatFromDB(this.getWebApplicationContext(), req)==true) {
            aForm.setValues(aMailStat.getValues());
            aForm.setCsvfile(aMailStat.getCsvfile());

        } else {
            AgnUtils.logger().error("loadBounceStat: could not load bounce stats.");
        }
    }

    /**
     * Loads week statistics.
     */
    protected void loadWeekStat(MailingStatForm aForm, HttpServletRequest req) {

        //set variables from form:
        MailingStat aMailStat=(MailingStat) getBean("MailingStat");
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setTargetID(aForm.getTargetID());
        aMailStat.setMailingID(aForm.getMailingID());
        aMailStat.setUrlID((new Integer(req.getParameter("urlID"))).intValue());

        if(aForm.isNetto())
            aMailStat.setNetto(true);
        if(req.getParameter("startdate")!=null) {
            aMailStat.setStartdate(req.getParameter("startdate"));
        } else {
            aMailStat.setStartdate("no");
            aForm.setStartdate("no");
        }

        // write results back to form:
        if(aMailStat.getWeekStatFromDB(this.getWebApplicationContext(), req)==true) {
            aForm.setFirstdate(aMailStat.getFirstdate());
            aForm.setStartdate(aMailStat.getStartdate());
            aForm.setCsvfile(aMailStat.getCsvfile());
            aForm.setValues(aMailStat.getValues());
            aForm.setClicks(aMailStat.getClicks());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setAktURL(aMailStat.getAktURL());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
        } else {
            AgnUtils.logger().error("loadWeekStat: could not load week stats.");
        }
    }

    /**
     * Loads day statiitcs.
     */
    protected void loadDayStat(MailingStatForm aForm, HttpServletRequest req) {

        //set variables from form:
        MailingStat aMailStat=(MailingStat) getBean("MailingStat");
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setTargetID(aForm.getTargetID());
        aMailStat.setMailingID(aForm.getMailingID());
        aMailStat.setUrlID((new Integer(req.getParameter("urlID"))).intValue());
        if(aForm.isNetto())
            aMailStat.setNetto(true);
        if(req.getParameter("startdate")!=null) {
            aMailStat.setStartdate(req.getParameter("startdate"));
        } else {
            aMailStat.setStartdate("no");
            aForm.setStartdate("no");
        }

        // write results back to form:
        if(aMailStat.getDayStatFromDB(this.getWebApplicationContext(), req)==true) {
            aForm.setAktURL(aMailStat.getAktURL());
            aForm.setCsvfile(aMailStat.getCsvfile());
            aForm.setValues(aMailStat.getValues());
            aForm.setClicks(aMailStat.getClicks());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
        } else {
            AgnUtils.logger().error("loadDayStat: could not load day stats.");
        }
    }
    
    /**
     * Loads week statistics.
     */
    protected void loadOpenWeekStat(MailingStatForm aForm, HttpServletRequest req) {

        //set variables from form:
        MailingStat aMailStat=(MailingStat) getBean("MailingStat");
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setMailingID(aForm.getMailingID());

        if(req.getParameter("startdate")!=null) {
            aMailStat.setStartdate(req.getParameter("startdate"));
        } else {
            aMailStat.setStartdate("no");
            aForm.setStartdate("no");
        }

        // write results back to form:
        if(aMailStat.getOpenTimeStatFromDB(this.getWebApplicationContext(), req)==true) {
            aForm.setFirstdate(aMailStat.getFirstdate());
            aForm.setStartdate(aMailStat.getStartdate());
            aForm.setValues(aMailStat.getValues());
            aForm.setClicks(aMailStat.getClicks());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
        } else {
            AgnUtils.logger().error("loadWeekStat: could not load week stats.");
        }
    }
    
    /**
     * Loads day statiitcs.
     */
    protected void loadOpenDayStat(MailingStatForm aForm, HttpServletRequest req) {

        //set variables from form:
        MailingStat aMailStat=(MailingStat) getBean("MailingStat");
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setMailingID(aForm.getMailingID());

        if(req.getParameter("startdate")!=null) {
            aMailStat.setStartdate(req.getParameter("startdate"));
        } else {
            aMailStat.setStartdate("no");
            aForm.setStartdate("no");
        }

        // write results back to form:
        if(aMailStat.getOpenTimeDayStat(this.getWebApplicationContext(), req)==true) {
            aForm.setValues(aMailStat.getValues());
            aForm.setClicks(aMailStat.getClicks());
            aForm.setMaxblue(aMailStat.getMaxblue());
            aForm.setMailingShortname(aMailStat.getMailingShortname());
        } else {
            AgnUtils.logger().error("loadDayStat: could not load day stats.");
        }
    }

    /**
     * Removes the admin clicks.
     */
    protected void cleanAdminClicks(MailingStatForm aForm, HttpServletRequest req) {
        MailingStat aMailStat=(MailingStat) getBean("MailingStat");
        aMailStat.setCompanyID(getCompanyID(req));
        aMailStat.setMailingID(aForm.getMailingID());
        aMailStat.cleanAdminClicks(getWebApplicationContext());
    }
    
    public List<DynaBean> getMailingStats(HttpServletRequest request) throws IllegalAccessException, InstantiationException {
    	 
    	ApplicationContext aContext= getWebApplicationContext();
	    JdbcTemplate aTemplate=new JdbcTemplate( (DataSource)aContext.getBean("dataSource"));
    	
    	String sqlStatement = "SELECT a.mailing_id, a.shortname, a.description, b.shortname AS listname " +
    			"FROM mailing_tbl a, mailinglist_tbl b WHERE a.company_id="+AgnUtils.getCompanyID(request)+ " " +
    			"AND a.mailinglist_id=b.mailinglist_id AND a.deleted=0 AND a.is_template=0 ORDER BY mailing_id DESC";
    	
    	List<Map> tmpList = aTemplate.queryForList(sqlStatement);
    	DynaProperty[] properties = new DynaProperty[] { 
    			new DynaProperty("mailingid", Long.class ),
    			new DynaProperty("shortname", String.class ),
    			new DynaProperty("description", String.class ),
    			new DynaProperty("listname", String.class ),
    	};
    	if ( AgnUtils.isOracleDB()) {
    		properties = new DynaProperty[] { 
        			new DynaProperty("mailingid", BigDecimal.class ),
        			new DynaProperty("shortname", String.class ),
        			new DynaProperty("description", String.class ),
        			new DynaProperty("listname", String.class ),
        	};
    	}    	
    	BasicDynaClass dynaClass = new BasicDynaClass("mailingstat",null, properties);
    	List<DynaBean> result = new ArrayList<DynaBean>();
    	for(Map row: tmpList) {
    		 DynaBean newBean = dynaClass.newInstance();    	
	    	  newBean.set("mailingid", row.get("MAILING_ID"));
	    	  newBean.set("shortname", row.get("SHORTNAME"));
	    	  newBean.set("description", row.get("DESCRIPTION"));
	    	  newBean.set("listname", row.get("LISTNAME"));
	    	  result.add(newBean);
    	}
    	
    	return result;
    }
    
}
