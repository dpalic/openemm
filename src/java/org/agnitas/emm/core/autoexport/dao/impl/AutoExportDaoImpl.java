package org.agnitas.emm.core.autoexport.dao.impl;

import org.agnitas.emm.core.autoexport.bean.AutoExport;
import org.agnitas.emm.core.autoexport.dao.AutoExportDao;
import org.agnitas.util.AgnUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoExportDaoImpl implements AutoExportDao {

    private DataSource dataSource;
    
    @Override
    public List<AutoExport> getAutoExportsOverview(int companyId) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        String sql = "select auto_export_id, shortname, description, active, file_server from auto_export_tbl where company_id = ?";
        List<Map<String, Object>> resultList = template.queryForList(sql, new Object[]{companyId});
        List<AutoExport> autoExports = new ArrayList<AutoExport>();
        for (Map<String, Object> row : resultList) {
            AutoExport autoExport = new AutoExport();
            autoExport.setAutoExportId(((Number)row.get("auto_export_id")).intValue());
            autoExport.setShortname((String) row.get("shortname"));
            autoExport.setDescription((String) row.get("description"));
            autoExport.setFileServer((String) row.get("file_server"));
            autoExport.setActive(((Number)row.get("active")).intValue() == 1);
            autoExports.add(autoExport);
        }
        return autoExports;
    }

    @Override
    public List<AutoExport> getAutoExportsToRun() {
        String currentDayOfWeek = AgnUtils.isOracleDB() ? "to_char(current_timestamp, 'D')" : "DAYOFWEEK(current_timestamp)";
        String currentHour = AgnUtils.isOracleDB() ? "to_char(current_timestamp, 'HH24')" : "HOUR(current_timestamp)";
        String query = "select * from auto_export_tbl ai where " +
                " (exists(select 1 from auto_export_time_tbl ai_time where ai_time.export_day_of_week = " + currentDayOfWeek +
                " and ai_time.export_hour = " + currentHour + " and ai_time.auto_export_id = ai.auto_export_id)" +
                " OR ai.auto_activation_date < current_timestamp)" +
                " and not (ai.one_time = 1 and ai.executed = 1) and ai.active = 1";
        JdbcTemplate template = new JdbcTemplate(dataSource);
        List<AutoExport> list = template.query(query, new AutoExportRowMapper());
        return list;
    }

    @Override
    public AutoExport getAutoExport(int autoExportId, int companyId) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        String query = "select * from auto_export_tbl where auto_export_id = ? and company_id = ?";
        AutoExport autoExport  = (AutoExport) template.queryForObject(query, new Object[]{autoExportId, companyId}, new AutoExportRowMapper());

        String exportTimesQuery = "select * from auto_export_time_tbl where auto_export_id = ? and company_id = ?";
        List<Map<String, Object>> exportTimes = template.queryForList(exportTimesQuery, new Object[]{autoExportId, companyId});
        ArrayList<AutoExport.ExportTime> times = new ArrayList<AutoExport.ExportTime>();
        for (Map<String, Object> row : exportTimes) {
            AutoExport.ExportTime time = new AutoExport.ExportTime();
            time.setDayOfWeek(((Number)row.get("export_day_of_week")).intValue());
            time.setHour(((Number) row.get("export_hour")).intValue());
            times.add(time);
        }
        autoExport.setTimes(times);

        return autoExport;
    }
    
    @Override
    public void createAutoExport(AutoExport autoExport) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        int newId;
        List<Object> params = new ArrayList<Object>();
        params.add(autoExport.getExportProfileId());
        params.add(autoExport.getShortname());
        params.add(autoExport.getDescription());
        params.add(autoExport.getFilePath());
        params.add(autoExport.getFileServer());
        params.add(autoExport.isOneTime() ? 1 : 0);
        params.add(autoExport.isExecuted() ? 1 : 0);
        params.add(autoExport.isActive() ? 1 : 0);
        params.add(autoExport.getCompanyId());
        params.add(autoExport.getAdminId());
        params.add(autoExport.getAutoActivationDate());
        params.add(autoExport.isAllowUnknownHostKeys() ? 1 : 0);
        if (AgnUtils.isOracleDB()) {
            String insertQuery = "insert into auto_export_tbl(auto_export_id, export_profile_id, shortname, description, file_path, file_server, " +
                    "one_time, executed, active, company_id, admin_id, auto_activation_date, allow_unknown_hostkeys) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            newId = getNextAutoExportId();
            params.add(0, newId);
            template.update(insertQuery, params.toArray());
        }
        else {
            String insertQuery = "insert into auto_export_tbl(export_profile_id, shortname, description, file_path, file_server, " +
                    "one_time, executed, active, company_id, admin_id, auto_activation_date, allow_unknown_hostkeys) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SqlUpdate sqlUpdate = new SqlUpdate(dataSource, insertQuery,  new int[] { Types.INTEGER, Types.VARCHAR, Types.VARCHAR,
                    Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.TIMESTAMP, Types.INTEGER});
            sqlUpdate.setReturnGeneratedKeys(true);
            sqlUpdate.setGeneratedKeysColumnNames(new String[]{"auto_export_id"});
            sqlUpdate.compile();
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            sqlUpdate.update(params.toArray(), generatedKeyHolder);
            newId = generatedKeyHolder.getKey().intValue();
        }
        autoExport.setAutoExportId(newId);
        updateAutoExportTimes(autoExport);
    }

    @Override
    public void updateAutoExport(AutoExport autoExport) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        String updateQuery = "update auto_export_tbl set export_profile_id = ?, shortname = ?, description = ?, " +
                "file_path = ?, file_server = ?, one_time = ?, executed = ?, active = ?, admin_id = ?, " +
                "auto_activation_date = ?, allow_unknown_hostkeys = ? where auto_export_id = ? and company_id = ?";
        template.update(updateQuery, new Object[] { autoExport.getExportProfileId(), autoExport.getShortname(),
                autoExport.getDescription(), autoExport.getFilePath(), autoExport.getFileServer(),
                autoExport.isOneTime() ? 1 : 0, autoExport.isExecuted() ? 1 : 0, autoExport.isActive() ? 1 : 0,
                autoExport.getAdminId(), autoExport.getAutoActivationDate(),
                autoExport.isAllowUnknownHostKeys() ? 1 : 0,
                autoExport.getAutoExportId(), autoExport.getCompanyId() });
        updateAutoExportTimes(autoExport);
    }

    private void updateAutoExportTimes(AutoExport autoExport) {
        JdbcTemplate template = new JdbcTemplate(dataSource);

        String deleteQuery = "delete from auto_export_time_tbl where auto_export_id = ? and company_id = ?";
        template.update(deleteQuery, new Object[] {autoExport.getAutoExportId(), autoExport.getCompanyId() } );

        String insertQuery = "insert into auto_export_time_tbl (auto_export_id, export_day_of_week, export_hour, company_id) values (?, ?, ?, ?)";
        List<AutoExport.ExportTime> exportTimes = autoExport.getTimes();
        for (AutoExport.ExportTime exportTime : exportTimes) {
            template.update(insertQuery, new Object[] { autoExport.getAutoExportId(), exportTime.getDayOfWeek(), exportTime.getHour(), autoExport.getCompanyId() });
        }
    }

    @Override
    public void deleteAutoExport(int autoExportId, int companyId) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        // remove selected days/times
        String query = "delete from auto_export_time_tbl where auto_export_id = ? and company_id = ?";
        template.update(query, new Object[] {autoExportId, companyId});
        // remove autoexport itself
        query = "delete from auto_export_tbl where auto_export_id = ? and company_id = ?";
        template.update(query, new Object[] {autoExportId, companyId});
    }
    
    @Override
    public void changeActiveStatus(int autoExportId, int companyId, boolean active) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        String updateQuery = "update auto_export_tbl set active = ? where auto_export_id = ? and company_id = ?";
        template.update(updateQuery, new Object[] { active, autoExportId, companyId });
    }

    protected int getNextAutoExportId() {
        String newIDQuery = "SELECT auto_export_tbl_seq.nextval FROM dual";
        return new JdbcTemplate(dataSource).queryForInt(newIDQuery);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private class AutoExportRowMapper implements ParameterizedRowMapper<AutoExport> {
        @Override
        public AutoExport mapRow(ResultSet resultSet, int row) throws SQLException {
            AutoExport autoExport = new AutoExport();
            autoExport.setCompanyId(resultSet.getBigDecimal("company_id").intValue());
            autoExport.setAutoExportId(resultSet.getBigDecimal("auto_export_id").intValue());
            autoExport.setAdminId(resultSet.getBigDecimal("admin_id").intValue());
            autoExport.setExportProfileId(resultSet.getBigDecimal("export_profile_id").intValue());
            autoExport.setShortname(resultSet.getString("shortname"));
            autoExport.setDescription(resultSet.getString("description"));
            autoExport.setFilePath(resultSet.getString("file_path"));
            autoExport.setFileServer(resultSet.getString("file_server"));
            autoExport.setOneTime(resultSet.getBigDecimal("one_time").intValue() == 1);
            autoExport.setExecuted(resultSet.getBigDecimal("executed").intValue() == 1);
            autoExport.setActive(resultSet.getBigDecimal("active").intValue() == 1);
            autoExport.setAutoActivationDate(resultSet.getTimestamp("auto_activation_date"));
            autoExport.setAllowUnknownHostKeys(resultSet.getInt("allow_unknown_hostkeys") == 1);
            return autoExport;
        }
    }
    
}
