package org.agnitas.service;

import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.Mailinglist;
import org.agnitas.web.forms.NewImportWizardForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author viktor 11-Aug-2010 2:50:45 PM
 */
public class ImportRecipientsAssignMailinglistsWorker implements Callable, Serializable {

    private NewImportWizardForm aForm;

    public ImportRecipientsAssignMailinglistsWorker(NewImportWizardForm aForm) {

        this.aForm = aForm;
    }

    public Object call() throws Exception {
        assignMailinglists(aForm);

        return null;
    }

    private void assignMailinglists(NewImportWizardForm aForm) {
        storeAssignedMailingLists(aForm.getListsToAssign(), aForm);
    }

    /**
     * Stores mailing list assignments to DB, stored assignment statistics to form
     *
     * @param assignedLists mailing lists that were assigned by user
     * @param req           request
     * @param aForm         form
     */
    private void storeAssignedMailingLists(List<Integer> assignedLists, NewImportWizardForm aForm) {
        final NewImportWizardService wizardHelper = aForm.getImportWizardHelper();
        ImportProfile profile = wizardHelper.getImportProfile();

        Map<Integer, Integer> statisitcs = aForm.getImportWizardHelper().getImportRecipientsDao().assiggnToMailingLists(assignedLists,
                wizardHelper.getCompanyId(), aForm.getDatasourceId(), profile.getImportMode(), wizardHelper.getAdminId(), wizardHelper);
        // store data to form for result page
        aForm.setMailinglistAssignStats(statisitcs);
        List<Mailinglist> allMailingLists = aForm.getAllMailingLists();
        List<Mailinglist> assignedMailingLists = new ArrayList<Mailinglist>();
        for (Mailinglist mailinglist : allMailingLists) {
            if (assignedLists.contains(mailinglist.getId())) {
                assignedMailingLists.add(mailinglist);
            }
        }
        aForm.setAssignedMailingLists(assignedMailingLists);
    }


}
