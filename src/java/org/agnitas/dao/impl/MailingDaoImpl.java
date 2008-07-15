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

import org.agnitas.dao.MailingDao;
import org.springframework.context.*;
import org.springframework.orm.hibernate3.*;
import org.hibernate.*;
import org.agnitas.beans.*;
import org.agnitas.util.*;

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
        
        return mailing;
    }
    
    public int saveMailing(Mailing mailing) {
        int result=0;
        Mailing tmpMailing=null;
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        if(mailing.getId()!=0) {
            tmpMailing=(Mailing)AgnUtils.getFirstResult(tmpl.find("from Mailing where id = ? and companyID = ? and deleted <> 1", new Object [] {new Integer(mailing.getId()), new Integer(mailing.getCompanyID())} ));
            if(tmpMailing==null) {
                mailing.setId(0);
            }
        }
       
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
