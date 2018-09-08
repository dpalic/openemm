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
package org.agnitas.beans.factory.impl;

import javax.sql.DataSource;

import org.agnitas.beans.factory.MailingStatEntryFactory;
import org.agnitas.beans.factory.MailingStatFactory;
import org.agnitas.beans.factory.URLStatEntryFactory;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.stat.MailingStat;
import org.agnitas.stat.impl.MailingStatImpl;


public class MailingStatFactoryImpl implements MailingStatFactory {

    private TargetDao targetDao;
    private MailingDao mailingDao;
    private MailingStatEntryFactory mailingStatEntryFactory;
    private URLStatEntryFactory urlStatEntryFactory;
    private DataSource dataSource;

    @Override
    public MailingStat newMailingStat() {
        MailingStatImpl mailingStat = new MailingStatImpl();
        mailingStat.setTargetDao(targetDao);
        mailingStat.setMailingDao(mailingDao);
        mailingStat.setMailingStatEntryFactory(mailingStatEntryFactory);
        mailingStat.setUrlStatEntryFactory(urlStatEntryFactory);
        mailingStat.setDataSource(dataSource);
        return mailingStat;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public void setMailingStatEntryFactory(MailingStatEntryFactory mailingStatEntryFactory) {
        this.mailingStatEntryFactory = mailingStatEntryFactory;
    }

    public void setUrlStatEntryFactory(URLStatEntryFactory urlStatEntryFactory) {
        this.urlStatEntryFactory = urlStatEntryFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
