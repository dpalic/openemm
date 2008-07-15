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

import org.agnitas.beans.ExportPredef;
import org.agnitas.dao.ExportPredefDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class ExportPredefDaoImpl implements ExportPredefDao {
    
    /** 
     * Creates a new instance of MailingDaoImpl
     */
    public ExportPredefDaoImpl() {
    }
    
    public ExportPredef get(int id, int companyID) {
        HibernateTemplate tmpl=getHibernateTemplate();
        ExportPredef exportPredef=null;

        if(companyID != 0) {
            if(id != 0) {
                exportPredef=(ExportPredef) AgnUtils.getFirstResult(tmpl.find("from ExportPredef where id = ? and companyID = ?", new Object [] {new Integer(id), new Integer(companyID)} ));
            } else {
                exportPredef=(ExportPredef) aContext.getBean("ExportPredef");
                exportPredef.setId(0);
                exportPredef.setCompanyID(companyID);
                Integer newId=(Integer) tmpl.save("ExportPredef", (Object) exportPredef); 
                exportPredef.setId(newId.intValue());
            }
        }
        return exportPredef;
    }
    
    public int save(ExportPredef src) {
        ExportPredef tmpExportPredef=null;
        
        if(src==null || src.getCompanyID()==0) {
            return 0;
        }

        HibernateTemplate tmpl=getHibernateTemplate();
        if(src.getId() != 0) {
            tmpExportPredef=(ExportPredef)AgnUtils.getFirstResult(tmpl.find("from ExportPredef where id = ? and companyID = ?", new Object [] {new Integer(src.getId()), new Integer(src.getCompanyID())} ));
            if(tmpExportPredef==null) {
                src.setId(0);
                return 0;
            }
        }
        
        tmpl.update("ExportPredef", src);
        return src.getId();
    }
    
    public boolean delete(ExportPredef src) {
        try {
            getHibernateTemplate().delete(src);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public boolean delete(int id, int companyID) {
        ExportPredef tmp=null;
        
        if((tmp=this.get(id, companyID))!=null) {
            return this.delete(tmp);
        }
        
        return false;
    }
    
    /**
     * Holds value of property aContext.
     */
    private ApplicationContext aContext;
    
    /**
     * Setter for property aContext.
     *
     * @param aContext New value of property aContext.
     */
    public void setApplicationContext(ApplicationContext aContext) {
        
        this.aContext = aContext;
    }
    
    public HibernateTemplate getHibernateTemplate() {
        return new HibernateTemplate((SessionFactory)this.aContext.getBean("sessionFactory"));
    }
}
