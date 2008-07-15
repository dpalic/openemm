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
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.actions.EmmAction;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class EmmActionDaoImpl implements EmmActionDao {
    
    /** Creates a new instance of MailingDaoImpl */
    public EmmActionDaoImpl() {
    }
    
    public EmmAction getEmmAction(int actionID, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        EmmAction ret=null;
        
        if(actionID==0 || companyID==0) {
            return null;
        }
       
        try { 
            ret=(EmmAction)AgnUtils.getFirstResult(tmpl.find("from EmmAction where id = ? and companyID = ?", new Object [] {new Integer(actionID), new Integer(companyID)} ));
        } catch(org.springframework.orm.hibernate3.HibernateSystemException he) {
            org.hibernate.type.SerializationException se=(org.hibernate.type.SerializationException) he.getCause();
            if(se.getCause() != null && se.getCause() instanceof ClassNotFoundException) {
                ClassNotFoundException e=(ClassNotFoundException) se.getCause();

                System.err.println("Cause: "+e.getCause());
                System.err.println("Message: "+e.getMessage());
            } else if(se.getCause() != null) {
                System.err.println("Cause: "+se.getCause());
                System.err.println("CauseClass: "+se.getCause().getClass());
            } else {
                System.err.println("Null Cause");
            }
        }
        return ret;
    }
    
    public int saveEmmAction(EmmAction action) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        int result=0;
        
        if(action==null || action.getCompanyID()==0) {
            return 0;
        }
        
        tmpl.saveOrUpdate("EmmAction", action);
        result=action.getId();
        
        return result;
    }
    
    public boolean deleteEmmAction(int actionID, int companyID) {
        EmmAction tmp=null;
        boolean result=false;
        
        if((tmp=this.getEmmAction(actionID, companyID))!=null) {
            HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
            try {
                tmpl.delete(tmp);
                tmpl.flush();
                result=true;
            } catch (Exception e) {
                result=false;
            }
        }
        return result;
    }
    
    public List getEmmActions(int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        return tmpl.find("from EmmAction where companyID = ?", new Object [] {new Integer(companyID)} );
    }
    
    public Map loadUsed(int companyID) {
    	Map used = new HashMap();
    	JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
    	
    	String stmt = "select action_id from rdir_action_tbl where company_id = ?";
    	try {
    		List list = jdbc.queryForList(stmt, new Object[] {new Integer(companyID)});
    		for(int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                int action_id = ((Number) map.get("action_id")).intValue();
                stmt = "select count(*) from userform_tbl where company_id = ? and (startaction_id = ? or endaction_id = ?)";
                int count = jdbc.queryForInt(stmt, new Object [] {new Integer(companyID), new Integer(action_id), new Integer(action_id)});
                used.put(action_id, count);
    		}
    	} catch (Exception e) {
    		AgnUtils.sendExceptionMail("sql:" + stmt, e);
    		System.err.println(e.getMessage());
    		System.err.println(AgnUtils.getStackTrace(e));
    	}
    	return used;
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
