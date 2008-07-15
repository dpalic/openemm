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

import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class MailingComponentDaoImpl implements MailingComponentDao {

    /** Creates a new instance of MailingDaoImpl */
    public MailingComponentDaoImpl() {
    }

    public MailingComponent getMailingComponent(int compID, int companyID) {
        MailingComponent comp=null;
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

        comp=(MailingComponent)AgnUtils.getFirstResult(tmpl.find("from MailingComponent where id = ? and companyID = ?", new Object [] {new Integer(compID), new Integer(companyID)} ));

        return comp;
    }

    public MailingComponent getMailingComponentByName(int mailingID, int companyID, String name) {
        MailingComponent comp=null;
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

        comp=(MailingComponent)AgnUtils.getFirstResult(tmpl.find("from MailingComponent where (mailingID = ? or mailingID = 0) and companyID = ? and compname = ?", new Object [] {new Integer(mailingID), new Integer(companyID), name} ));

        return comp;
    }

    public void saveMailingComponent(MailingComponent comp) {
    	HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

    	tmpl.saveOrUpdate("MailingComponent", comp);
        tmpl.flush();
    }

    public void deleteMailingComponent(MailingComponent comp) {
    	HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

    	tmpl.delete(comp);
        tmpl.flush();
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
