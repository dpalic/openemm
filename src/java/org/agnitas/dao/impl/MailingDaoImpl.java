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

package org.agnitas.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mediatype;
import org.agnitas.beans.TrackableLink;
import org.agnitas.beans.impl.DynaBeanPaginatedListImpl;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TrackableLinkDao;
import org.agnitas.util.AgnUtils;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.displaytag.pagination.PaginatedList;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe, Nicole Serek
 */
public class MailingDaoImpl implements MailingDao {
    
	
	protected DataSource dataSource;

	public Mailing getMailing(int mailingID, int companyID) {
        Mailing mailing = null;
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        mailing = (Mailing)AgnUtils.getFirstResult(tmpl.find("from Mailing where id = ? and companyID = ? and deleted <> 1", new Object [] {new Integer(mailingID), new Integer(companyID)} ));
        if(mailing != null) {
            Map map = mailing.getMediatypes();
            Iterator it = map.keySet().iterator();
    
            while(it.hasNext()) {
                Integer key = (Integer) it.next();
            
                if(map.get(key) instanceof org.agnitas.beans.impl.MediatypeImpl) {
                    Mediatype mt = null;
                    Mediatype src = (Mediatype) map.get(key);
            
                    switch(key.intValue()) {
                        case 0: mt = (Mediatype) this.applicationContext.getBean("MediatypeEmail");
                                break;
                        case 1: mt = (Mediatype) this.applicationContext.getBean("MediatypeFax");
                                break;
                        case 2: mt = (Mediatype) this.applicationContext.getBean("MediatypePrint");
                                break;
                        case 3: mt = (Mediatype) this.applicationContext.getBean("MediatypeMMS");
                                break;
                        case 4: mt = (Mediatype) this.applicationContext.getBean("MediatypeSMS");
                                break;
                        default: mt = (Mediatype) this.applicationContext.getBean("Mediatype");
                    }
                    mt.setPriority(src.getPriority()); 
                    mt.setStatus(src.getStatus()); 
                    try {
                        mt.setParam(src.getParam()); 
                    } catch(Exception e) {
                        AgnUtils.logger().error("Exception: "+e);
                        AgnUtils.logger().error(AgnUtils.getStackTrace(e));
                       
                    }
                    map.put(key, mt);
                }
            }
        }
        return mailing;
    }
    
    public int saveMailing(Mailing mailing) {
        int result = 0;

        Mailing tmpMailing = null;
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

        if(mailing.getId()!=0) {
        	System.err.println("Clearing mailing");
            tmpMailing = (Mailing) AgnUtils.getFirstResult(tmpl.find("from Mailing where id = ? and companyID = ? and deleted <> 1", new Object [] {new Integer(mailing.getId()), new Integer(mailing.getCompanyID())} ));
            if(tmpMailing == null) {
                mailing.setId(0);
            }
        }

        Map map = mailing.getMediatypes();
        Map dst = new HashMap();
        Iterator i = map.keySet().iterator();

        while(i.hasNext()) {
            Integer idx = (Integer) i.next();
            Mediatype mt = (Mediatype) map.get(idx);
            Mediatype tgt = (Mediatype) this.applicationContext.getBean("Mediatype");

            try {
                tgt.setPriority(mt.getPriority()); 
                tgt.setStatus(mt.getStatus()); 
                tgt.setParam(mt.getParam()); 
            } catch(Exception e) {
                AgnUtils.logger().error("Exception "+e);
                AgnUtils.logger().error(AgnUtils.getStackTrace(e));
            }
            dst.put(idx, tgt);
        }
        mailing.setMediatypes(dst);

        JdbcTemplate jdbc = AgnUtils.getJdbcTemplate(this.applicationContext);
        Map components = mailing.getComponents();
        Iterator iter = components.keySet().iterator();
        while (iter.hasNext()) {
			MailingComponent entry = (MailingComponent) components.get(iter.next());
			if (entry.getType() != 0) {
				if (entry.getLink() != null && !entry.getLink().equals("")) {
					Map trackableLinks = new HashMap();
					TrackableLinkDao linkDao = (TrackableLinkDao) applicationContext.getBean("TrackableLinkDao");
					TrackableLink trkLink = null;
					trkLink = linkDao.getTrackableLink(entry.getLink(), entry.getCompanyID(), mailing.getId());
					if(trkLink == null) {
						trkLink = (TrackableLink) applicationContext.getBean("TrackableLink");
					}
					trkLink.setCompanyID(entry.getCompanyID());
					trkLink.setFullUrl(entry.getLink());
					trkLink.setMailingID(mailing.getId());
					trkLink.setUsage(TrackableLink.TRACKABLE_TEXT_HTML);
					trkLink.setActionID(0);
					linkDao.saveTrackableLink(trkLink);
					
					String sql = "select url_id from rdir_url_tbl where mailing_id = ? and company_id = ? and full_url = ?";
					int id = jdbc.queryForInt(sql,new Object[] { new Integer(mailing.getId()), new Integer(entry.getCompanyID()), entry.getLink() });
					entry.setUrlID(id);
				}
			}
        }
        
        tmpl.saveOrUpdate("Mailing", mailing);
        result=mailing.getId();
        tmpl.flush();
        return result;
    }
    
    public boolean deleteMailing(int mailingID, int companyID) {
        Mailing tmpMailing = null;
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        tmpMailing = this.getMailing(mailingID, companyID);
        if(tmpMailing == null) {
            return false;
        }
        
        tmpMailing.setDeleted(1);
        tmpl.flush();
        
        return true;
    }

    public List getMailingsForMLID(int companyID, int mailinglistID) {
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        return tmpl.find("from Mailing where companyID = ? and mailinglistID = ? and deleted = 0", new Object [] {new Integer(companyID), new Integer(mailinglistID)} );
    }
    
    public Map<String, String> loadAction(int mailingID, int companyID) {
        Map<String, String> actions = new HashMap<String, String>();
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
    	
    	String stmt = "select action_id, shortname, full_url from rdir_url_tbl where mailing_id = ? and company_id = ?";
    	try {
    		List list = jdbc.queryForList(stmt, new Object[] {new Integer(mailingID), new Integer(companyID)});
    		for(int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                int action_id = ((Number) map.get("action_id")).intValue();
                if(action_id > 0) {
                	stmt = "select shortname from rdir_action_tbl where company_id = " + companyID + " and action_id = " + action_id;
                	String action_short = (String) jdbc.queryForObject(stmt, stmt.getClass());
                
                	String name = "";
                	if(map.get("shortname") != null) {
                		name = (String) map.get("shortname"); 
                	} else {
                		name = (String) map.get("full_url");
                	}
                	actions.put(action_short, name);
                }
    		}
    	} catch (Exception e) {
    		AgnUtils.sendExceptionMail("sql:" + stmt + ", " + mailingID + ", " + companyID, e);
    		System.err.println(e.getMessage());
    		System.err.println(AgnUtils.getStackTrace(e));
    	}
        return actions;
    }
    
    public boolean deleteContentFromMailing(Mailing mailing, int contentID){
    	JdbcTemplate jdbcTemplate = AgnUtils.getJdbcTemplate(this.applicationContext);
    	String deleteContentSQL = "DELETE from dyn_content_tbl " +
    			"WHERE " +
    			" dyn_content_id = ? AND" +
    			" company_id = ?";

    	Object[] params = new Object[]{new Integer(contentID), 1};
    	int affectedRows = 0;
    	affectedRows = jdbcTemplate.update(deleteContentSQL, params );
    	return affectedRows > 0;
    }

	
	/**
	 * Build an SQL-expression from th egiven target_expression.
	 * The expression is a list of targetIDs connected with the operators:
	 * <ul>
	 * <li>( - block start
	 * <li>) - block end
	 * <li>&amp; - AND 
	 * <li>| - OR 
	 * <li>! - NOT 
	 * </ul>
	 * @param targetExpression The expression as string.
	 * @param jdbc Template for SQL queries.
	 * @return the resulting where clause.
	 */
	static String getSQLExpression(String targetExpression, JdbcTemplate jdbc)	{	
		StringBuffer buf = new StringBuffer ();
		int	tlen = targetExpression.length ();

		if (targetExpression == null) {
			return null;
		}
		for (int n = 0; n < tlen; ++n) {
			char ch = targetExpression.charAt (n);

			if ((ch == '(') || (ch == ')')) {
				buf.append (ch);
			} else if ((ch == '&') || (ch == '|')) {
				if (ch == '&')
					buf.append (" AND");
				else
					buf.append (" OR");
				while (((n + 1) < tlen) && (targetExpression.charAt (n + 1) == ch))
					++n;
			} else if (ch == '!') {
				buf.append (" NOT");
			} else if (Character.isDigit(ch)) {
				String	temp = "";
				int	first = n;
				int	tid = (-1);

				while(n < tlen && Character.isDigit(ch)) {
					n++;
				}
				tid=Integer.parseInt(targetExpression.substring(first, n));
				temp = (String) jdbc.queryForObject("select target_sql from dyn_target_tbl where target_id = ?", new Object[] {new Integer(tid)}, temp.getClass());
				if (temp != null && temp.trim().length() > 2)
					buf.append(" (" + temp + ")");
			}
		}
		if (buf.length () >= 3)
			return buf.toString ();
		return null;
	}

	/**
	 * Finds the last newsletter that would have been sent to the given
	 * customer.
	 * @param customerID Id of the recipient for the newsletter.
	 * @param companyID the company to look in.
	 * @return The mailingID of the last newsletter that would have been
	 *              sent to this recipient.
	 */
	public int	findLastNewsletter(int customerID, int companyID) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String	sql="select m.mailing_id, m.target_expression, a."+AgnUtils.changeDateName()+" from mailing_tbl m left join mailing_account_tbl a ON a.mailing_id=m.mailing_id where m.company_id=? and m.deleted<>1 and m.is_template=0 and a.status_field='W' order by a."+AgnUtils.changeDateName()+" desc, m.mailing_id desc";

		try {
			List list = jdbc.queryForList(sql, new Object[] {new Integer(companyID)});

			for(int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				int mailing_id = ((Number) map.get("mailing_id")).intValue();
				String targetExpression = (String) map.get("target_expression");

				if(targetExpression == null || targetExpression.trim().length() == 0) {
					return mailing_id;
				}
				sql="select count(*) from customer_" + companyID + "_tbl cust where " + getSQLExpression(targetExpression, jdbc) + " and customer_id=" + customerID;
System.err.println("SQL: "+sql); 
				if(jdbc.queryForInt(sql) > 0) {
					return mailing_id;
				}
                	}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println(AgnUtils.getStackTrace(e));
		}
		return 0;
	}
	
	public String[]	getTag(String name, int companyID) {
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "select selectvalue, type from tag_tbl where tagname=? and (company_id=0 or company_id=?)";
		String[] result = null;

		try {
			List list=jdbc.queryForList(sql, new Object[]{name, new Integer(companyID)});

			if(list.size() > 0) {
				Map map = (Map) list.get(0);

				result = new String[]{ (String) map.get("selectvalue"), (String) map.get("type") };
			}
		} catch (Exception e) {
			AgnUtils.sendExceptionMail("sql:" + sql + ", "+ name + ", " + companyID, e);
			AgnUtils.logger().error("processTag: "+e.getMessage());
			result = null;
		}
		return result;
	}

	public String	getAutoURL(int mailingID)	{
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String	sql="select auto_url from mailing_tbl where mailing_id=?";

		try	{
			return (String) jdbc.queryForObject(sql, new Object[]{new Integer(mailingID)}, sql.getClass());
		} catch(Exception e) {
			AgnUtils.logger().error("getAutoURL: "+e.getMessage());
		}
		return null;
	}
	
	public String getAutoURL(int mailingID, int companyID) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String rdirdomain = null;
		String rdir_mailinglistquery = "select  ml.RDIR_DOMAIN  FROM MAILINGLIST_TBL ml JOIN MAILING_TBL m ON ( ml.MAILINGLIST_ID = m.MAILINGLIST_ID) WHERE  m.MAILING_ID=?"; 
		rdirdomain = (String) jdbc.queryForObject(rdir_mailinglistquery, new Object[]{new Integer(mailingID)}, String.class );
		if( rdirdomain != null ) {
			return rdirdomain;
		}
		String rdir_companyquery = "select RDIR_DOMAIN FROM COMPANY_TBL where company_id=?";
		rdirdomain = (String) jdbc.queryForObject(rdir_companyquery, new Object[]{new Integer(companyID)}, String.class );
			return rdirdomain;
	}
	
    /**
     * Holds value of property applicationContext.
     */
    protected ApplicationContext applicationContext;

    /**
     * Setter for property applicationContext.
     * @param applicationContext New value of property applicationContext.
     */
    public void setApplicationContext(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

	public PaginatedList getMailingList( int companyID, String types,
			boolean isTemplate, String sort, String direction, int page, int rownums)  {
	
	      JdbcTemplate aTemplate = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
	      List<String>  charColumns = Arrays.asList(new String[]{"shortname","description","mailinglist" });
		  
	      int offset =  ( page - 1) * rownums;  
          
	      String mailingTypes = "  AND  mailing_type in ("+ types +") ";
	      if(isTemplate) {
	     		mailingTypes = " ";
	      }
	      
	    String orderby = null;	
		String defaultorder = " send_null ASC, senddate DESC, mailing_id DESC  "; 
		if( sort != null && ! "".equals(sort.trim())) {
			orderby = getUpperSort(charColumns, sort);
			orderby = orderby + " " +direction;
		}
		else {
			orderby = defaultorder;
		}
		
		String sqlStatement = 
	    	  " SELECT *, case when senddate is null then 0 else 1 end as send_null " +
	    	  " FROM (   SELECT a.mailing_id , a.shortname  , a.description ,   min(c." + AgnUtils.changeDateName() + ") senddate, m.shortname mailinglist " +
	    	  " FROM  (mailing_tbl  a LEFT JOIN mailing_account_tbl c ON (a.mailing_id=c.mailing_id AND c.status_field='W')) " + " LEFT JOIN mailinglist_tbl m ON (  a.mailinglist_id=m.mailinglist_id AND  a.company_id=m.company_id) " +
			  "  WHERE a.company_id = " + companyID + " AND a.deleted<>1 AND a.is_template=" + (isTemplate?1:0)
				+ mailingTypes + "  GROUP BY  a.mailing_id, a.shortname, a.description, m.shortname ) openemm ORDER BY " + orderby;
	      
  	 
	     int totalsize = aTemplate.queryForInt("select count(*) from ( " +sqlStatement + ") agn");
   	 
	     sqlStatement = sqlStatement + " LIMIT " + offset + " , " + rownums;
	     
	     List<Map> tmpList = aTemplate.queryForList(sqlStatement);
   	 
   	 DynaProperty[] properties = new DynaProperty[] {
	    		  new DynaProperty("mailingid", Long.class),
	    		  new DynaProperty("shortname", String.class),
	    		  new DynaProperty("description", String.class),
	    		  new DynaProperty("mailinglist", String.class),
	    		  new DynaProperty("senddate",Timestamp.class)   		  
	      };
	      BasicDynaClass dynaClass = new BasicDynaClass("mailing", null, properties);
	      
	      List<DynaBean> result = new ArrayList<DynaBean>();
	      for(Map row:tmpList) {
	    	  DynaBean newBean;
			try {
				newBean = dynaClass.newInstance();
			  	
	    	  newBean.set("mailingid", row.get("MAILING_ID"));
	    	  newBean.set("shortname", row.get("SHORTNAME"));
	    	  newBean.set("description", row.get("DESCRIPTION"));
	    	  newBean.set("mailinglist", row.get("MAILINGLIST"));
	    	  newBean.set("senddate",row.get("SENDDATE"));
	    	  result.add(newBean);
			} catch (IllegalAccessException e) {
				  AgnUtils.logger().error("IllegalAccessException: "+e);
                  AgnUtils.logger().error(AgnUtils.getStackTrace(e));
				
			} catch (InstantiationException e) {
				  AgnUtils.logger().error("InstantiationException: "+e);
                  AgnUtils.logger().error(AgnUtils.getStackTrace(e));
			}
	      }    
	   
	      DynaBeanPaginatedListImpl paginatedList = new DynaBeanPaginatedListImpl(result, totalsize, rownums, page, sort, direction);
	      
	      return paginatedList;
	}
	
	protected String getUpperSort(List<String> charColumns, String sort) {
		String upperSort = sort;
		if (charColumns.contains( sort )) {
	    	upperSort = "upper( " +sort + " )";
	     }
		return upperSort;
	}

	public String getFormat(int type) {
		String format = "d.M.yyyy";
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		try {
            String sql = "SELECT format FROM date_tbl WHERE type = ?";
            format = (String) jdbc.queryForObject(sql, new Object[] {new Integer(type)}, String.class);
        } catch (Exception e) {
        	AgnUtils.logger().error("Query failed for data_tbl: " + e);
        }
		return format;
	}
	
	
	
	public int getStatusidForWorldMailing(int mailingID, int companyID) {
		int returnValue = 0;
		JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource );
		String query = "SELECT status_id  FROM maildrop_status_tbl  WHERE company_id="+companyID+" and mailing_id="+mailingID+" and status_field='W' and genstatus=3 and senddate < now()";
		try {	
			returnValue = jdbcTemplate.queryForInt(query);
		} catch (Exception e) {
			returnValue = 0;
		}
		return returnValue;
		
	}
	
	public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
	 }

	public boolean hasPreviewRecipients(int mailingID, int companyID) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String query = "SELECT DISTINCT 1 FROM mailing_tbl m, mailinglist_tbl ml, customer_" + companyID + "_binding_tbl c WHERE c.user_type in ('A', 'T') AND c.mailinglist_id = ml.mailinglist_id AND ml.company_id = " + companyID + " AND m.mailinglist_id = ml.mailinglist_id AND m.mailing_id = " + mailingID + " AND m.company_id = " + companyID + " AND c.user_status = 1";
		
		try {
			template.queryForInt(query);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
