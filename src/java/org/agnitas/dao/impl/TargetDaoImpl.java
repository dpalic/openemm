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

import java.util.List;

import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class TargetDaoImpl implements TargetDao {
    
    /** Creates a new instance of MailingDaoImpl */
    public TargetDaoImpl() {
    }
    
    public Target getTarget(int targetID, int companyID) {
        Target ret=null;
try {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        if(targetID==0 || companyID==0) {
            return null;
        }
        
        ret=(Target)AgnUtils.getFirstResult(tmpl.find("from Target where id = ? and companyID = ?", new Object [] {new Integer(targetID), new Integer(companyID)} ));
} catch(Exception e) {
	System.err.println("Target load error: "+e);
	e.printStackTrace();
}
        return ret;
    }

    /**
     * Getter for target by target name and company id.
     *
     * @return target.
     */
    public Target getTargetByName(String targetName, int companyID){
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        targetName=targetName.trim();
        
        if(targetName.length()==0 || companyID==0) {
            return null;
        }
        
        return (Target)AgnUtils.getFirstResult(tmpl.find("from Target where targetName = ? and (companyID = ? or companyID=0)", new Object [] {new String(targetName), new Integer(companyID)} ));
    }
    
    public int saveTarget(Target target) {
        int result=0;
        Target tmpTarget=null;
        
        if(target==null || target.getCompanyID()==0) {
            return 0;
        }
        
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        if(target.getId()!=0) {
            tmpTarget=(Target)AgnUtils.getFirstResult(tmpl.find("from Target where id = ? and companyID = ?", new Object [] {new Integer(target.getId()), new Integer(target.getCompanyID())} ));
            if(tmpTarget==null) {
                target.setId(0);
            }
        }
        
        tmpl.saveOrUpdate("Target", target);
        result=target.getId();
        
        return result;
    }
    
    public boolean deleteTarget(int targetID, int companyID) {
        Target tmp=null;
        boolean result=false;
        
        if((tmp=this.getTarget(targetID, companyID))!=null) {
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
    
    public List getTargets(int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
                
        return tmpl.find("from Target where companyID = ?", new Object [] {new Integer(companyID)} );
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
