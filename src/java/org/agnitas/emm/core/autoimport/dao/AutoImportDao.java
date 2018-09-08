package org.agnitas.emm.core.autoimport.dao;

import org.agnitas.emm.core.autoimport.bean.AutoImport;

import java.util.List;

public interface AutoImportDao {

	public List<AutoImport> getAutoImportsOverview(int companyId);

	public AutoImport getAutoImport(int autoImportId, int companyId);

	public List<AutoImport.UsedFile> getUsedFiles(int autoImportId, int companyId);

	public void deleteAutoImport(int autoImportId, int companyId);

	public void addUsedFile(AutoImport.UsedFile usedFile, int autoImportId, int companyId);

	public void changeActiveStatus(int autoImportId, int companyId, boolean active);

	public void updateAutoImport(AutoImport autoImport);

	public void createAutoImport(AutoImport autoImport);

	public List<AutoImport> getAutoImportsToRun();

}
