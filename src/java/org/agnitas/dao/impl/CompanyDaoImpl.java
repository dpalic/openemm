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

import org.agnitas.beans.Company;
import org.agnitas.dao.CompanyDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class CompanyDaoImpl implements CompanyDao {

    /** Creates a new instance of MailingDaoImpl */
    public CompanyDaoImpl() {
    }

    public Company getCompany(int companyID) {
        try {
            HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

            if(companyID==0) {
                return null;
            }
            return (Company)AgnUtils.getFirstResult(tmpl.find("from Company where id = ?", new Object [] {new Integer(companyID)} ));
        } catch(Exception e) {
System.err.println("Exception: "+e+" for company "+companyID);
System.err.println(AgnUtils.getStackTrace(e));
        }
        return null;

    //    return (Company)AgnUtils.getFirstResult(tmpl.find("from Company where id = ?", new Object [] {new Integer(companyID)} ));
    }

    public void saveCompany(Company comp) {
    	HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
    	tmpl.saveOrUpdate("Company", comp);
    	return;
    }
    public void deleteCompany(Company comp) {
    	HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
    	tmpl.delete(comp);
        tmpl.flush();
    	return;
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
