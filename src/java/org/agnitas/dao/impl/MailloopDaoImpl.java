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


import org.agnitas.dao.MailloopDao;
import org.springframework.context.*;
import org.springframework.orm.hibernate3.*;
import org.hibernate.*;
import org.agnitas.beans.*;
import org.agnitas.util.*;
import java.util.List;

/**
 *
 * @author mhe
 */
public class MailloopDaoImpl implements MailloopDao {
    
    /** Creates a new instance of MailingDaoImpl */
    public MailloopDaoImpl() {
    }
    
    public Mailloop getMailloop(int mailloopID, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        if(mailloopID==0 || companyID==0) {
            return null;
        }
        
        return (Mailloop)AgnUtils.getFirstResult(tmpl.find("from Mailloop where id = ? and companyID = ?", new Object [] {new Integer(mailloopID), new Integer(companyID)} ));
    }
    
    public List getMailloops(int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

        return tmpl.find("from Mailloop where companyID = ?", new Object[] {new Integer(companyID) });
    }
    
    public int saveMailloop(Mailloop loop) {
        int result=0;
        Mailloop tmpLoop=null;
        
        if(loop==null || loop.getCompanyID()==0) {
            return 0;
        }
        
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        if(loop.getId()!=0) {
            tmpLoop=(Mailloop)AgnUtils.getFirstResult(tmpl.find("from Mailloop where id = ? and companyID = ?", new Object [] {new Integer(loop.getId()), new Integer(loop.getCompanyID())} ));
            if(tmpLoop==null) {
                loop.setId(0);
            }
        }
        
        tmpl.saveOrUpdate("Mailloop", loop);
        result=loop.getId();
        tmpl.flush();
        
        return result;
    }
    
    public boolean deleteMailloop(int loopID, int companyID) {
        Mailloop tmp=null;
        boolean result=false;
        
        if((tmp=this.getMailloop(loopID, companyID))!=null) {
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
