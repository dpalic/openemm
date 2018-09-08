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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.dao.impl;

import java.util.List;

import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.util.AgnUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 
 * @author mhe
 */
public class MailingComponentDaoImpl extends BaseHibernateDaoImpl implements MailingComponentDao {
	@Override
	public MailingComponent getMailingComponent(int compID, @VelocityCheck int companyID) {
		@SuppressWarnings("unchecked")
		MailingComponent comp = AgnUtils.getFirstResult((List<MailingComponent>) getHibernateTemplate().find("from MailingComponent where id = ? and companyID = ?", new Object[] { new Integer(compID), new Integer(companyID) }));
		return comp;
	}

	@Override
	public MailingComponent getMailingComponentByName(int mailingID, @VelocityCheck int companyID, String name) {
		@SuppressWarnings("unchecked")
		MailingComponent comp = AgnUtils.getFirstResult((List<MailingComponent>) getHibernateTemplate().find("from MailingComponent where (mailingID = ? or mailingID = 0) and companyID = ? and compname = ?",
				new Object[] { new Integer(mailingID), new Integer(companyID), name }));

		return comp;
	}

	@Override
	public void saveMailingComponent(MailingComponent comp) {
		HibernateTemplate tmpl = getHibernateTemplate();
		tmpl.saveOrUpdate("MailingComponent", comp);
		tmpl.flush();
	}

	@Override
	public void deleteMailingComponent(MailingComponent comp) {
		HibernateTemplate tmpl = getHibernateTemplate();
		tmpl.delete(comp);
		tmpl.flush();
	}

	@Override
	public List<MailingComponent> getMailingComponents(int mailingID, @VelocityCheck int companyID, int componentType) {
		@SuppressWarnings("unchecked")
		List<MailingComponent> returnList = getHibernateTemplate().find("from MailingComponent where mailingID = ? AND companyID = ? AND type=?", new Object[] { mailingID, companyID, componentType });
		return returnList;
	}

	@Override
	public List<MailingComponent> getMailingComponents(int mailingID, @VelocityCheck int companyID) {
		@SuppressWarnings("unchecked")
		List<MailingComponent> returnList = getHibernateTemplate().find("from MailingComponent where mailingID = ? AND companyID = ? ORDER BY componentName", new Object[] { mailingID, companyID });
		return returnList;
	}

	@Override
	public List<MailingComponent> getPreviewHeaderComponents(int mailingID, @VelocityCheck int companyID) {
		@SuppressWarnings("unchecked")
		List<MailingComponent> returnList =  getHibernateTemplate().find("from MailingComponent where (type=? OR type=?) AND mailingID = ? AND companyID = ? ORDER BY id", new Object[] { MailingComponent.TYPE_ATTACHMENT,
				MailingComponent.TYPE_PERSONALIZED_ATTACHMENT, mailingID, companyID });
		return returnList;
	}
}
