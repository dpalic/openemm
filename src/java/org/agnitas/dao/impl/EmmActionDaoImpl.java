/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.dao.impl;

import org.agnitas.dao.EmmActionDao;
import org.springframework.context.*;
import org.springframework.orm.hibernate3.*;
import org.hibernate.*;
import org.agnitas.actions.*;
import org.agnitas.util.*;
import java.util.*;

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
