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

package org.agnitas.cms.utils.dataaccess;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.agnitas.cms.webservices.generated.CMTemplate;
import org.agnitas.cms.webservices.generated.RemoteCMTemplateManagerServiceLocator;
import org.agnitas.cms.webservices.generated.RemoteCMTemplateManager_PortType;
import org.apache.log4j.Logger;

/**
 * Provide remote functionality to CMTemplateManager.
 * Invokes service from central content repository.
 *
 * @author Igor Nesterenko
 * @see org.agnitas.cms.webservices.CMTemplateService
 */
public class RemoteCMTemplateManager implements CMTemplateManager {
	private static final transient Logger logger = Logger.getLogger(RemoteCMTemplateManager.class);

	RemoteCMTemplateManager_PortType cmTemplateService;


	public RemoteCMTemplateManager() {
		super();
	}

	public void setPortUrl(String portUrlString) {
		RemoteCMTemplateManagerServiceLocator serviceLocator = new RemoteCMTemplateManagerServiceLocator();
		try {
			cmTemplateService = serviceLocator
					.getRemoteCMTemplateManager(new URL(portUrlString));
		} catch(ServiceException e) {
			logger.error("Error while creation remote connection " + e, e);
		} catch(MalformedURLException e) {
			logger.error("Error while parsing port address " + e, e);
		}
	}

	public CMTemplate createCMTemplate(CMTemplate template) {
		CMTemplate newCmTemplate = null;
		try {
			newCmTemplate = cmTemplateService.createCMTemplate(template);
		} catch(Exception e) {
			logger.error("Error while creation of CM Template: " + e, e);
		}
		return newCmTemplate;
	}

	public CMTemplate getCMTemplate(int id) {
		CMTemplate cmTemplate = null;

		try {
			cmTemplate = cmTemplateService.getCMTemplate(id);
		} catch(RemoteException e) {
			logger.error("Error while getting CM Template: " + e, e);
		}

		return cmTemplate;
	}

	public List<CMTemplate> getCMTemplates(int companyId) {
		List<CMTemplate> cmTemplateList = new ArrayList<CMTemplate>();
		try {
			Object[] cmTemplates = cmTemplateService.getCMTemplates(companyId);
			for(int index = 0; index < cmTemplates.length; index++) {
				CMTemplate cmTemplate = (CMTemplate) cmTemplates[index];
				cmTemplateList.add(cmTemplate);
			}
		} catch(RemoteException e) {
			logger.error("Error while getting CM Template`s list: " + e, e);
		}
		return cmTemplateList;
	}

	public void deleteCMTemplate(int id) {
		try {
			cmTemplateService.deleteCMTemplate(id);
		} catch(RemoteException e) {
			logger.error("Error while deleting CM Template: " + e, e);
		}
	}

	public boolean updateCMTemplate(int id, String name, String description) {
		try {
			return cmTemplateService.updateCMTemplate(id, name, description);
		} catch(RemoteException e) {
			logger.error("Error while update CM Template: " + e, e);
		}
		return false;
	}

	public boolean updateContent(int id, byte[] content) {
		try {
			return cmTemplateService.updateContent(id, content);
		} catch(RemoteException e) {
			logger.error("Error while update content of CM Template: " + e, e);
		}
		return false;
	}

	public Map<Integer, Integer> getMailingBinding(int cmTemplateId) {
		final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try {
			final Object[] objects = cmTemplateService
					.getMailingBindingWrapper(cmTemplateId);
			for(int index = 0; index < objects.length; index++) {
				map.put((Integer) objects[index], cmTemplateId);
			}
			return map;
		} catch(RemoteException e) {
			logger.error("Error while get mail binding for CM Template: " + e, e);
		}
		return map;
	}

	public Map<Integer, Integer> getMailingBinding(List<Integer> mailingIds) {
		final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try {
			final Object[] mailingTemplateArrays = cmTemplateService
					.getMailingBindingArrayWrapper(mailingIds.toArray());
			Object[] mailings = (Object[]) mailingTemplateArrays[0];
			Object[] templates = (Object[]) mailingTemplateArrays[1];
			for(int index = 0; index < mailings.length; index++) {
				final Integer mailingId = (Integer) mailings[index];
				final Integer templateId = (Integer) templates[index];
				map.put(mailingId, templateId);
			}
		} catch(RemoteException e) {
			logger.error("Error while update content of CM Template: " + e, e);
		}
		return map;
	}

	public void addMailingBindings(int cmTemplateId, List<Integer> mailingIds) {
		try {
			cmTemplateService.addMailingBindings(cmTemplateId, mailingIds.toArray());
		} catch(RemoteException e) {
			logger.error("Error while add mail binding to CM Template: " + e, e);
		}
	}

	public void removeMailingBindings(List<Integer> mailingIds) {
		try {
			cmTemplateService.removeMailingBindings(mailingIds.toArray());
		} catch(RemoteException e) {
			logger.error("Error while remove mail binding to CM Template: " + e, e);
		}
	}

	public CMTemplate getCMTemplateForMailing(int mailingId) {
		try {
			return cmTemplateService.getCMTemplateForMailing(mailingId);
		} catch(RemoteException e) {
			logger.error("Error while getting CM Template: " + e, e);
		}
		return null;
	}

	public String getTextVersion(int adminId) {
		try {
			return cmTemplateService.getTextVersion(adminId);
		} catch(RemoteException e) {
			logger.error("Error while getting text version" + e, e);
		}
		return null;
	}

	public void removeTextVersion(int adminId) {
		try {
			cmTemplateService.removeTextVersion(adminId);
		} catch(RemoteException e) {
			logger.error("Error while remove text version" + e, e);
		}
	}

	public void saveTextVersion(int adminId, String text) {
		try {
			cmTemplateService.saveTextVersion(adminId, text);
		} catch(RemoteException e) {
			logger.error("Error while save text version" + e, e);
		}
	}

	public List<Integer> getMailingWithCmsContent(List<Integer> mailingIds,
												  int companyId) {
		try {
			final Object[] cmsMailingIds = cmTemplateService
					.getMailingWithCmsContent(mailingIds.toArray(), companyId);
			final ArrayList<Integer> cmsMailingIdList = new ArrayList<Integer>();
			for(Object cmsMailingIdObject : cmsMailingIds) {
				cmsMailingIdList.add(((Integer) cmsMailingIdObject));
			}
			return cmsMailingIdList;
		} catch(RemoteException e) {
			logger.error("Error while save text version" + e, e);
		}
		return null;
	}
}
