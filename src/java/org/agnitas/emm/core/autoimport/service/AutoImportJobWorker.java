package org.agnitas.emm.core.autoimport.service;

import org.agnitas.emm.core.autoimport.bean.AutoImport;
import org.agnitas.service.JobWorker;

import java.util.List;

public class AutoImportJobWorker extends JobWorker {

	@Override
	public void runJob() throws Exception {
		AutoImportService autoImportService = (AutoImportService) applicationContext.getBean("AutoImportService");
		List<AutoImport> autoImportsToRun = autoImportService.getAutoImportsToRun();
		// as we are already in a separate thread we can execute the imports one after another
		for (AutoImport autoImport : autoImportsToRun) {
			autoImportService.doImport(autoImport.getAutoImportId(), autoImport.getCompanyId(), applicationContext);
		}
	}
}
