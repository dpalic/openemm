package org.agnitas.emm.core.autoexport.service;

import org.agnitas.emm.core.autoexport.bean.AutoExport;
import org.agnitas.service.JobWorker;

import java.util.List;

public class AutoExportJobWorker extends JobWorker {

    @Override
    public void runJob() throws Exception {
        AutoExportService autoExportService = (AutoExportService) applicationContext.getBean("AutoExportService");
        List<AutoExport> autoExportsToRun = autoExportService.getAutoExportsToRun();
        // as we are already in a separate thread we can execute the exports one after another
        for (AutoExport autoExport : autoExportsToRun) {
            autoExportService.doExport(autoExport.getAutoExportId(), autoExport.getCompanyId(), applicationContext);
        }
    }
}
