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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.Mediatype;
import org.agnitas.dao.MailingDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class MailingDaoImpl implements MailingDao {
    
    /** Creates a new instance of MailingDaoImpl */
    public MailingDaoImpl() {
    }
    
    public Mailing getMailing(int mailingID, int companyID) {
        Mailing mailing=null;
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        mailing=(Mailing)AgnUtils.getFirstResult(tmpl.find("from Mailing where id = ? and companyID = ? and deleted <> 1", new Object [] {new Integer(mailingID), new Integer(companyID)} ));
        if(mailing != null) {
            Map map=mailing.getMediatypes();
            Iterator it=map.keySet().iterator();
    
            while(it.hasNext()) {
                Integer key=(Integer) it.next();
            
                if(map.get(key) instanceof org.agnitas.beans.impl.MediatypeImpl) {
                    Mediatype mt=null;
                    Mediatype src=(Mediatype) map.get(key);
            
                    switch(key.intValue()) {
                        case 0: mt=(Mediatype) this.applicationContext.getBean("MediatypeEmail");
                                break;
                        case 1: mt=(Mediatype) this.applicationContext.getBean("MediatypeFax");
                                break;
                        case 2: mt=(Mediatype) this.applicationContext.getBean("MediatypePrint");
                                break;
                        case 3: mt=(Mediatype) this.applicationContext.getBean("MediatypeMMS");
                                break;
                        case 4: mt=(Mediatype) this.applicationContext.getBean("MediatypeSMS");
                                break;
                        default: mt=(Mediatype) this.applicationContext.getBean("Mediatype");
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
        int result=0;
/*
        JdbcTemplate jdbc = AgnUtils.getJdbcTemplate(this.applicationContext);
        String sql=null;
        Object[] param=null;

        if(mailing.getId() != 0) {
            sql="UPDATE mailing_tbl SET ";
            sql+="shortname=?, description=?, mailing_type=?, is_template=?, ";
            sql+="needs_target=?, mailtemplate_id=?, mailinglist_id=?, ";
            sql+="deleted=?, archived=?, test_lock=?, target_expression=?";
            sql+=" WHERE mailing_id=? AND company_id=?";
            param=new Object[] {
		mailing.getShortname(), mailing.getDescription(),
                new Integer(mailing.getMailingType()),
                new Integer(mailing.isIsTemplate()?1:0),
                new Integer(mailing.getNeedsTarget()?1:0),
                new Integer(mailing.getMailTemplateID()),
                new Integer(mailing.getMailinglistID()),
                new Integer(mailing.getDeleted()),
                new Integer(((ComMailing) mailing).getArchived()),
                new Integer(mailing.getLocked()),
                mailing.getTargetExpression(),
                new Integer(mailing.getId()),
                new Integer(mailing.getCompanyID())
            };
        }
        jdbc.update(sql, param); 
        result=mailing.getId();
*/
        Mailing tmpMailing=null;
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

        if(mailing.getId()!=0) {
System.err.println("Clearing mailing");
            tmpMailing=(Mailing)AgnUtils.getFirstResult(tmpl.find("from Mailing where id = ? and companyID = ? and deleted <> 1", new Object [] {new Integer(mailing.getId()), new Integer(mailing.getCompanyID())} ));
            if(tmpMailing==null) {
                mailing.setId(0);
            }
        }

        Map map=mailing.getMediatypes();
        Map dst=new HashMap();
        Iterator i=map.keySet().iterator();

        while(i.hasNext()) {
            Integer idx=(Integer) i.next();
            Mediatype mt=(Mediatype) map.get(idx);
            Mediatype tgt=(Mediatype) this.applicationContext.getBean("Mediatype");

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

        tmpl.saveOrUpdate("Mailing", mailing);
        result=mailing.getId();
        tmpl.flush();
        return result;
    }
    
    public boolean deleteMailing(int mailingID, int companyID) {
        Mailing tmpMailing=null;
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        tmpMailing=this.getMailing(mailingID, companyID);
        if(tmpMailing==null) {
            return false;
        }
        
        tmpMailing.setDeleted(1);
        tmpl.flush();
        
        return true;
    }

    public List getMailingsForMLID(int companyID, int mailinglistID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        return tmpl.find("from Mailing where companyID = ? and mailinglistID = ? and deleted = 0", new Object [] {new Integer(companyID), new Integer(mailinglistID)} );
        
    }
    
    public Map<String, String> loadAction(int mailingID, int companyID) {
        Map<String, String> actions = new HashMap<String, String>();
        JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
    	
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
    
}
