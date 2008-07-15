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

import org.agnitas.dao.MailinglistDao;
import org.springframework.context.*;
import org.springframework.orm.hibernate3.*;
import org.hibernate.*;
import org.agnitas.beans.*;
import org.agnitas.util.*;
import java.util.*;

/**
 *
 * @author mhe
 */
public class MailinglistDaoImpl implements MailinglistDao  {
    
    /** Creates a new instance of MailingDaoImpl */
    public MailinglistDaoImpl() {
    }
    
    public Mailinglist getMailinglist(int listID, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        Mailinglist list=null;
        
        if(listID==0 || companyID==0) {
            return null;
        }
        
        list=(Mailinglist)AgnUtils.getFirstResult(tmpl.find("from Mailinglist where id = ? and companyID = ?", new Object [] {new Integer(listID), new Integer(companyID)} ));
        
        if(list!=null) {
            list.setApplicationContext(this.applicationContext);
        }
        
        return list;
    }
    
    public int saveMailinglist(Mailinglist list) {
        int result=0;
        Mailinglist tmpList=null;
        
        if(list==null || list.getCompanyID()==0) {
            return 0;
        }
        
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        if(list.getId()!=0) {
            tmpList=(Mailinglist)AgnUtils.getFirstResult(tmpl.find("from Mailinglist where id = ? and companyID = ?", new Object [] {new Integer(list.getId()), new Integer(list.getCompanyID())} ));
            if(tmpList==null) {
                list.setId(0);
            }
        }
        
        tmpl.saveOrUpdate("Mailinglist", list);
        result=list.getId();
        
        return result;
    }
    
    public boolean deleteMailinglist(int listID, int companyID) {
        Mailinglist tmp=null;
        boolean result=true;
        
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        tmpl.bulkUpdate("delete Mailinglist where id = ? and companyID = ?", new Object [] {new Integer(listID), new Integer(companyID)} );
        
        return result;
    }
    
    public List getMailinglists(int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        return tmpl.find("from Mailinglist where companyID = ?", new Object [] {new Integer(companyID)} );
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
