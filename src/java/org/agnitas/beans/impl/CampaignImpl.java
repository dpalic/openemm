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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Campaign;
import org.agnitas.dao.TargetDao;
import org.agnitas.stat.CampaignStatEntry;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class CampaignImpl implements Campaign {
	public class StatsImpl implements Campaign.Stats {
        int clicks=0;
        int opened=0;
        int optouts=0;
        int bounces=0;
        int subscribers=0;
        Hashtable mailingData = new Hashtable();
        int maxBounces=0;
        int maxClicks=0;
        int maxOpened=0;
        int maxOptouts=0;
        int maxSubscribers=0;
        double maxClickRate=0.0;
        double maxOpenRate=0.0;

        public int getBounces() {
            return bounces;
        }

        public int getClicks() {
            return clicks;
        }

        public int getOpened() {
            return opened;
        }

        public int getOptouts() {
            return optouts;
        }

        public int getSubscribers() {
            return subscribers;
        }
        public Hashtable getMailingData() {
            return mailingData;
        }

        public int getMaxBounces() {
            return maxBounces;
        }

        public int getMaxClicks() {
            return maxClicks;
        }

        public int getMaxOpened() {
            return maxOpened;
        }

        public int getMaxOptouts() {
            return maxOptouts;
        }

        public int getMaxSubscribers() {
            return maxSubscribers;
        }

        public void setMaxClickRate(double maxClickRate) {
            this.maxClickRate=maxClickRate;
        }

        public void setMaxOpenRate(double maxOpenRate) {
            this.maxOpenRate=maxOpenRate;
        }
    };
    
    /** Holds value of property id. */
    private int id;
    
    /** Holds value of property companyID. */
    private int companyID;
    
    /** Holds value of property targetID. */
    protected int targetID;    
    
    /** Holds value of property netto. */    
    private boolean netto;

    /** Holds value of property shortname. */
    private String shortname;
    
    /** Holds value of property description. */
    private String description;
    
    /** Holds value of property csvfile. */
    private String csvfile="";

    public String getCsvfile() {
        return csvfile;
    }
    
    // CONSTRUCTORS:
    public CampaignImpl() {
        id = 0;
        companyID = 0;
    }
    
    // automatically generated
    // get & set methods:

    public int getId() {
        return id;
    }
    
    public int getCompanyID() {
        return companyID;
    }
    
    public int getTargetID() {
        return targetID;
    }
    
    public String getShortname() {
        return this.shortname;
    }
    
    public String getDescription() {
        return description;
    }

    /** Getter for property netto.
     * @return Value of property netto.
     *
     */
    public boolean isNetto() {
        return this.netto;
    }
    
    /** Setter for property netto.
     * @param netto New value of property netto.
     *
     */
    public void setNetto(boolean netto) {
        this.netto = netto;
    }    
    
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
    
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Stats getStats(boolean useMailtracking, Locale aLocale, LinkedList mailingIDs, ApplicationContext con) {
        JdbcTemplate jdbc=new JdbcTemplate((DataSource) con.getBean("dataSource"));
        StatsImpl stats=new StatsImpl();
        String uniqueStr = "";
        // boolean useMailtracking = req.getSession().getAttribute("use_mailtracking").equals("1");
        Target aTarget = null;
        CampaignStatEntry aktEntry = null;
        long aTime;
        StringBuffer mailIDs=null;
        String mailingSelection=null;
        boolean isFirst=true;

        // aLocale

        csvfile = "\"" + SafeString.getLocaleString("CampaignStats", aLocale) + "\"\r\n\r\n";

        if(mailingIDs!=null) {
            Iterator aIt=mailingIDs.iterator();
            Integer tmpInt=null;
            while(aIt.hasNext()) {
                tmpInt=(Integer)aIt.next();
                if(mailIDs==null) {
                    mailIDs=new StringBuffer();
                }
                if(isFirst) {
                    mailIDs.append(tmpInt.intValue());
                    isFirst=false;
                } else {
                    mailIDs.append(", "+tmpInt.intValue());
                }
            }
        }

        if(mailIDs!=null) {
            mailingSelection=new String(mailIDs.toString());
        }

        // * * * * * * * * * * * *
        // *  LOAD TARGET GROUP  *
        // * * * * * * * * * * * *

        //woher kommt die targetID - vorher nicht deklariert!!!
        if(targetID!=0) {
            TargetDao targetDao=(TargetDao) con.getBean("TargetDao");
            aTarget=targetDao.getTarget(targetID, getCompanyID());
            csvfile += "\"" + SafeString.getLocaleString("Target", aLocale) + "\";\"" + aTarget.getTargetName() + "\"\r\n\r\n";

        } else {
            csvfile += "\"" + SafeString.getLocaleString("Target", aLocale) + "\";\"" + SafeString.getLocaleString("All_Subscribers", aLocale) + "\"\r\n\r\n";
        }

        // * * * * * * * * * *
        // *  SET NETTO SQL  *
        // * * * * * * * * * *
        if(isNetto()) {
            uniqueStr=new String("distinct ");
            csvfile += "\"" + SafeString.getLocaleString("Unique_Clicks", aLocale) + "\"\r\n\r\n";
        }

        // * * * * * * * * * * * * * * * *
        // *  M A I L I N G   N A M E S  *
        // * * * * * * * * * * * * * * * *

        String MailingNameQuery = "select mailing_id, shortname, description from mailing_tbl where company_id=" + getCompanyID() + " and campaign_id=" + getId() + " and deleted<>1 and is_template=0 order by mailing_id desc";
        if(mailingSelection!=null) {
            MailingNameQuery="select mailing_id, shortname, description from mailing_tbl where company_id="+getCompanyID()+" AND MAILING_ID IN ("+mailingSelection+") order by mailing_id desc";
        }
        AgnUtils.logger().info("MailingNameQuery: " + MailingNameQuery);

        try {
            aTime=System.currentTimeMillis();
            List list=jdbc.queryForList(MailingNameQuery);
            AgnUtils.logger().debug("time: " + (System.currentTimeMillis()-aTime));
            Iterator i=list.iterator();

            isFirst=true;
            mailIDs=new StringBuffer();
            while(i.hasNext()) {
                Map map=(Map) i.next();
                Integer id=new Integer(((Number) map.get("mailing_id")).intValue());
                // create CampaignStatEntry...
                aktEntry = (CampaignStatEntry) con.getBean("CampaignStatEntry");
                aktEntry.setShortname((String) map.get("shortname"));
                if(map.get("description")!=null) {
                    aktEntry.setName((String) map.get("description"));
                } else {
                    aktEntry.setName(" ");
                }
                //...and put it into the Hashtable
                stats.mailingData.put(id, aktEntry);
                if(isFirst) {
                    mailIDs.append(id.intValue());
                    isFirst=false;
                } else {
                    mailIDs.append(", "+id.intValue());
                }
            }

        } catch (Exception e) {
            AgnUtils.logger().error("MailingNameQuery error1: " + e);
        }

        if(mailingSelection==null) {
            mailingSelection=new String(mailIDs.toString());
        }

        // * * * * * * * * *
        // *  C L I C K S  *
        // * * * * * * * * *

        String TotalClicksQuery = "select rdir.mailing_id as mailing_id, count(" + uniqueStr + " rdir.customer_id) as amount from rdir_log_tbl rdir, rdir_url_tbl url where rdir.mailing_id in ("+mailingSelection+") and rdir.url_id=url.url_id";
        TotalClicksQuery += " and url.relevance=0 group by rdir.mailing_id";
        AgnUtils.logger().info("TotalClicksQuery: " + TotalClicksQuery);

        try {
            aTime=System.currentTimeMillis();
            List list=jdbc.queryForList(TotalClicksQuery);
            AgnUtils.logger().debug("time: " + (System.currentTimeMillis()-aTime));
            Iterator i=list.iterator();

            while(i.hasNext()) {
                Map map=(Map) i.next();
                Integer mailingID=new Integer(((Number) map.get("mailing_id")).intValue());
                int clicks=((Number) map.get("amount")).intValue();

                // get CampaignStatEntry...
                aktEntry = (CampaignStatEntry) stats.mailingData.get(mailingID);
                //...fill in total clicks...
                aktEntry.setClicks(clicks);
                //...put it back...
                stats.mailingData.put(mailingID, aktEntry);
                //...and add value to global value:
                stats.clicks += clicks;
                // look for max. value
                if(clicks > stats.maxClicks) {
                    stats.maxClicks=clicks;
                }
            }
        } catch (Exception e) {
            AgnUtils.logger().error("TotalClicksQuery error1: " + e);
            AgnUtils.logger().error(AgnUtils.getStackTrace(e));
        }

        // * * * * * * * * * * * * *
        // O P E N E D   M A I L S *
        // * * * * * * * * * * * * *
        String OnePixelQueryByCust = "select onepix.mailing_id as mailing_id, count(onepix.customer_id) as amount from onepixel_log_tbl onepix";
        if(useMailtracking && targetID!=0)
            OnePixelQueryByCust += ", customer_" + getCompanyID() + "_tbl cust";

        OnePixelQueryByCust += " where onepix.mailing_id in ("+mailingSelection+")";
        if(useMailtracking && targetID!=0)
            OnePixelQueryByCust += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=onepix.customer_id)";
        OnePixelQueryByCust += " group by onepix.mailing_id";
        AgnUtils.logger().info("OnePixelQueryByCust: " + OnePixelQueryByCust);

        try {
            aTime=System.currentTimeMillis();
            List list=jdbc.queryForList(OnePixelQueryByCust);
            AgnUtils.logger().debug("time: " + (System.currentTimeMillis()-aTime));
            Iterator i=list.iterator();

            while(i.hasNext()) {
                Map map=(Map) i.next();
                Integer mailingID=new Integer(((Number) map.get("mailing_id")).intValue());
                int opened=((Number) map.get("amount")).intValue();

                // get CampaignStatEntry...
                aktEntry = (CampaignStatEntry) stats.mailingData.get(mailingID);
                //...fill in opened mails...
                aktEntry.setOpened(opened);
                //...put it back...
                stats.mailingData.put(mailingID, aktEntry);
                //...and add value to global value:
                stats.opened+=opened;
                // check for max. value:
                if(opened > stats.maxOpened) {
                    stats.maxOpened=opened;
                }
            }
        } catch (Exception e) {
            AgnUtils.logger().error("OnePixelQueryByCust error1: " + e);
        }

        // * * * * * * * * *
        //  O P T O U T S  *
        // * * * * * * * * *
        String OptoutQuery = "select bind.exit_mailing_id as mailing_id, count(bind.customer_id) as amount from customer_" + getCompanyID() + "_binding_tbl bind";
        if(useMailtracking && targetID!=0)
            OptoutQuery += ", customer_" + getCompanyID() + "_tbl cust";
        OptoutQuery += " where bind.exit_mailing_id in ("+mailingSelection+")";
        if(useMailtracking && targetID!=0)
            OptoutQuery += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=bind.customer_id)";
        OptoutQuery += " and bind.user_status in (" + BindingEntry.USER_STATUS_ADMINOUT + ", " + BindingEntry.USER_STATUS_OPTOUT + ") group by bind.exit_mailing_id";
        AgnUtils.logger().info("OptoutQuery: " + OptoutQuery);

        try {
            aTime=System.currentTimeMillis();
            List list=jdbc.queryForList(OptoutQuery);
            AgnUtils.logger().debug("time: " + (System.currentTimeMillis()-aTime));
            Iterator i=list.iterator();

            while(i.hasNext()) {
                Map map=(Map) i.next();
                Integer mailingID=new Integer(((Number) map.get("mailing_id")).intValue());
                int optouts=((Number) map.get("amount")).intValue();

                // get CampaignStatEntry...
                aktEntry = (CampaignStatEntry) stats.mailingData.get(mailingID);
                //...fill in optouts...
                aktEntry.setOptouts(optouts);
                //...put it back...
                stats.mailingData.put(mailingID, aktEntry);
                //...and add value to global value:
                stats.optouts += optouts;
                // check for max. value:
                if(optouts > stats.maxOptouts) {
                    stats.maxOptouts=optouts;
                }

            }
        } catch (Exception e) {
            AgnUtils.logger().error("OptoutQuery error1: " + e);
        }

        // get mailing_id's from Hashtable
        Enumeration keys = stats.mailingData.keys();
        int aktMailingID = 0;
        long totalAdmMails = 0;
        long totalMails = 0;
        int aktBounces=0;
        // loop over every mailing_id
        while(keys.hasMoreElements()) {
            totalAdmMails = 0;
            totalMails = 0;
            aktBounces=0;
            aktMailingID = ((Number) keys.nextElement()).intValue();

            // * * * * * * * * *
            //  B O U N C E S  *
            // * * * * * * * * *
            String BounceQuery = "select bind.mailinglist_id as mailinglist_id, count(bind.customer_id) as amount from customer_" + getCompanyID() + "_binding_tbl bind";
            if(useMailtracking && targetID != 0)
                BounceQuery += ", customer_" + getCompanyID() + "_tbl cust";
            BounceQuery += " where bind.exit_mailing_id=" + aktMailingID;
            if(useMailtracking && targetID != 0)
                BounceQuery += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=bind.customer_id)";
            BounceQuery += " and bind.user_status = " + BindingEntry.USER_STATUS_BOUNCED + " group by bind.mailinglist_id";
            AgnUtils.logger().info("BounceQuery: " + BounceQuery);

            try {
                aTime=System.currentTimeMillis();
                List list=jdbc.queryForList(BounceQuery);
                AgnUtils.logger().debug("time: " + (System.currentTimeMillis()-aTime));
                Iterator i=list.iterator();

                // get entry...
                aktEntry = (CampaignStatEntry) stats.mailingData.get(new Integer(aktMailingID));
                while(i.hasNext()) {
                    Map map=(Map) i.next();
                    int bounces=((Number) map.get("amount")).intValue();

                    if(bounces > aktBounces) {
                        aktBounces = bounces;
                    }
                }
                //...set value...
                aktEntry.setBounces(aktBounces);
                //...put it back...
                stats.mailingData.put(new Integer(aktMailingID), aktEntry);
                //...and add value to global value:
                stats.bounces += aktBounces;
                // check for max. value:
                if(aktBounces>stats.maxBounces) {
                    stats.maxBounces=aktBounces;
                }

            } catch (Exception e) {
                AgnUtils.logger().error("BounceQuery error1: " + e);
            }

            // * * * * * * * * * * * * * * * * *
            //  T O T A L  S E N T  M A I L S  *
            // * * * * * * * * * * * * * * * * *

            // * * * * * * * * * *
            // case mail_tracking:
            if(useMailtracking) {
                String mailtrackTbl = AgnUtils.isOracleDB() ? "mailtrack_" + getCompanyID() + "_tbl" : "mailtrack_tbl";
				String mailtrackQuery = "select count(distinct mailtrack.customer_id) from " + mailtrackTbl  + " mailtrack";
                if(targetID != 0)
                    mailtrackQuery += ", customer_" + getCompanyID() + "_tbl cust";
                mailtrackQuery += " where mailtrack.status_id in (select status_id from maildrop_status_tbl where mailing_id=" + aktMailingID;
                mailtrackQuery += " and company_id=" + getCompanyID() + ")";
                if(targetID != 0)
                    mailtrackQuery += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=mailtrack.customer_id)";
                AgnUtils.logger().info("mailtrackQuery: " + mailtrackQuery);
                try {
                    aTime=System.currentTimeMillis();
                    long subscribers=jdbc.queryForLong(mailtrackQuery);
                    AgnUtils.logger().debug("time: " + (System.currentTimeMillis()-aTime));
                    // get CampaignStatEntry...
                    aktEntry = (CampaignStatEntry)(stats.mailingData.get(new Integer(aktMailingID)));
                    //...fill in subscribers...
                    aktEntry.setTotalMails((int) subscribers);
                    //...write it back...
                    stats.mailingData.put(new Integer(aktMailingID), aktEntry);
                    //... and add value to global value:
                    stats.subscribers += subscribers;
                    // check for max. value:
                    if(subscribers > stats.maxSubscribers) {
                        stats.maxSubscribers=(int) subscribers;
                    }
                } catch (Exception e) {
                    AgnUtils.logger().error("mailtrackQuery error1: " + e);
                }
            } else {
                // * * * * * * * * * * * *
                // case no_mail_tracking:
                // look for world mailing:
                String SentMailsQuery = "select sum(no_of_mailings) from mailing_account_tbl where mailing_id=";
                SentMailsQuery += aktMailingID + " and company_id=" + getCompanyID() + " and status_field in ('W', 'C', 'R')";
                AgnUtils.logger().info("SentMailsQuery: " + SentMailsQuery);
                try {
                    aTime=System.currentTimeMillis();
                    totalMails=jdbc.queryForLong(SentMailsQuery);
                    AgnUtils.logger().debug("time: " + (System.currentTimeMillis()-aTime));

                } catch (Exception e) {
                    totalMails=0;
                    AgnUtils.logger().error("SentMailsQuery error1: " + e);
                }


                // look for admin or test mailing only:
                String sentAdmMailsQuery = "select max(tmp.mailing_count ) as amount from (select sum(no_of_mailings) as mailing_count from mailing_account_tbl mac where mac.mailing_id=";
                sentAdmMailsQuery += aktMailingID + " and mac.company_id=" + getCompanyID() + " and mac.status_field in ('A', 'T') group by mac.change_date) tmp";
                AgnUtils.logger().info("SentAdmMailsQuery: " + sentAdmMailsQuery);
                try {
                    aTime=System.currentTimeMillis();
                    totalAdmMails=jdbc.queryForLong(sentAdmMailsQuery);
                    AgnUtils.logger().debug("time: " + (System.currentTimeMillis()-aTime));
                } catch (Exception e) {
                    totalAdmMails=0;
                    AgnUtils.logger().error("SentAdmMailsQuery error1: " + e);
                }

                // take the bigger value for displaying:
                // get CampaignStatEntry...
                aktEntry = (CampaignStatEntry)(stats.mailingData.get(new Integer(aktMailingID)));
                //...fill in subscribers...
                if(totalAdmMails>totalMails) {
                    aktEntry.setTotalMails((int) totalAdmMails);
                    // add value to global value:
                    stats.subscribers += totalAdmMails;
                    // check for max. value:
                    if(totalAdmMails>stats.maxSubscribers) {
                        stats.maxSubscribers=(int) totalAdmMails;
                    }

                } else {
                    aktEntry.setTotalMails((int) totalMails);
                    // add value to global value:
                    stats.subscribers += totalMails;
                    // check for max. value:
                    if(totalMails>stats.maxSubscribers) {
                        stats.maxSubscribers=(int) totalMails;
                    }

                }
                //...and write it back:
                stats.mailingData.put(new Integer(aktMailingID), aktEntry);
            }

        }

        // look for max values and set them to 1 if 0:
        if(stats.maxClicks==0) {
            stats.maxClicks=1;
        }
        if(stats.maxBounces==0) {
            stats.maxBounces=1;
        }
        if(stats.maxOpened==0) {
            stats.maxOpened=1;
        }
        if(stats.maxOptouts==0) {
            stats.maxOptouts=1;
        }
        if(stats.maxSubscribers==0) {
            stats.maxSubscribers=1;
        }

        return stats;
    }
}
