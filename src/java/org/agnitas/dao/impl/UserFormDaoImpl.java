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

import org.agnitas.beans.UserForm;
import org.agnitas.dao.UserFormDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

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
        tmpl.flush();
        
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
                tmpl.flush();
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
