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

import org.agnitas.dao.UserFormDao;
import org.springframework.context.*;
import org.springframework.orm.hibernate3.*;
import org.hibernate.*;
import org.agnitas.beans.*;
import org.agnitas.util.*;

/**
 *
 * @author mhe
 */
public class UserFormDaoImpl implements UserFormDao {
    
    /** Creates a new instance of MailingDaoImpl */
    public UserFormDaoImpl() {
    }
    
    public UserForm getUserForm(int formID, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        if(formID==0 || companyID==0) {
            return null;
        }
        
        return (UserForm)AgnUtils.getFirstResult(tmpl.find("from UserForm where id = ? and companyID = ?", new Object [] {new Integer(formID), new Integer(companyID)} ));
    }
    
    public UserForm getUserFormByName(String name, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        if(name==null || companyID==0) {
            return null;
        }
        
        return (UserForm)AgnUtils.getFirstResult(tmpl.find("from UserForm where formName = ? and companyID = ?", new Object [] {name, new Integer(companyID)} ));
    }
    
    public int saveUserForm(UserForm form) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory) applicationContext.getBean("sessionFactory"));
        int result=0;
        
        if(form==null || form.getCompanyID()==0) {
            return 0;
        }
        
        tmpl.saveOrUpdate("UserForm", form);
        result=form.getId();
        
        return result;
    }
    
    public boolean deleteUserForm(int formID, int companyID) {
        UserForm tmp=null;
        boolean result=false;
        
        if((tmp=this.getUserForm(formID, companyID))!=null) {
            HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
            try {
                tmpl.delete(tmp);
                result=true;
            } catch (Exception e) {
                result=false;
            }
        }
        
        return result;
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
