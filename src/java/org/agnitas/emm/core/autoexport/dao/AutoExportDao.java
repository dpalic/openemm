package org.agnitas.emm.core.autoexport.dao;

import org.agnitas.emm.core.autoexport.bean.AutoExport;
import org.agnitas.emm.core.autoimport.bean.AutoImport;

import java.util.List;

public interface AutoExportDao {

    public List<AutoExport> getAutoExportsToRun();

    public void changeActiveStatus(int autoExportId, int companyId, boolean active);

    public AutoExport getAutoExport(int autoExportId, int companyId);

    public void createAutoExport(AutoExport autoExport);

    public void updateAutoExport(AutoExport autoExport);

    public List<AutoExport> getAutoExportsOverview(int companyId);

    public void deleteAutoExport(int autoExportId, int companyId);

}
