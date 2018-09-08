package org.agnitas.emm.core.autoimport.dao.impl;

import org.agnitas.emm.core.autoimport.bean.AutoImport;
import org.agnitas.emm.core.autoimport.dao.AutoImportDao;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AutoImportDaoImpl implements AutoImportDao {

	private DataSource dataSource;

	@Override
	public List<AutoImport> getAutoImportsOverview(int companyId) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String sql = "select auto_import_id, shortname, description, active, file_server from auto_import_tbl where company_id = ?";
		List<Map<String, Object>> resultList = template.queryForList(sql, new Object[]{companyId});
		List<AutoImport> autoImports = new ArrayList<AutoImport>();
		for (Map<String, Object> row : resultList) {
			AutoImport autoImport = new AutoImport();
			autoImport.setAutoImportId(((Number)row.get("auto_import_id")).intValue());
			autoImport.setShortname((String) row.get("shortname"));
			autoImport.setDescription((String) row.get("description"));
			autoImport.setFileServer((String) row.get("file_server"));
			autoImport.setActive(((Number)row.get("active")).intValue() == 1);
			autoImports.add(autoImport);
		}
		return autoImports;
	}

	@Override
	public List<AutoImport> getAutoImportsToRun() {
		String currentDayOfWeek = AgnUtils.isOracleDB() ? "to_char(current_timestamp, 'D')" : "DAYOFWEEK(current_timestamp)";
		String currentHour = AgnUtils.isOracleDB() ? "to_char(current_timestamp, 'HH24')" : "HOUR(current_timestamp)";
		String query = "select * from auto_import_tbl ai where " +
				" (exists(select 1 from auto_import_time_tbl ai_time where ai_time.import_day_of_week = " + currentDayOfWeek +
				" and ai_time.import_hour = " + currentHour + " and ai_time.auto_import_id = ai.auto_import_id)" +
                " OR ai.auto_activation_date < current_timestamp)" +
				" and not (ai.one_time = 1 and ai.executed = 1) and ai.active = 1";
		JdbcTemplate template = new JdbcTemplate(dataSource);
		List<AutoImport> list = template.query(query, new AutoImportRowMapper());
		return list;
	}

	@Override
	public AutoImport getAutoImport(int autoImportId, int companyId) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String query = "select * from auto_import_tbl where auto_import_id = ? and company_id = ?";
		AutoImport autoImport  = (AutoImport) template.queryForObject(query, new Object[]{autoImportId, companyId}, new AutoImportRowMapper());

		String importTimesQuery = "select * from auto_import_time_tbl where auto_import_id = ? and company_id = ?";
		List<Map<String, Object>> importTimes = template.queryForList(importTimesQuery, new Object[]{autoImportId, companyId});
		ArrayList<AutoImport.ImportTime> times = new ArrayList<AutoImport.ImportTime>();
		for (Map<String, Object> row : importTimes) {
			AutoImport.ImportTime time = new AutoImport.ImportTime();
			time.setDayOfWeek(((Number)row.get("import_day_of_week")).intValue());
			time.setHour(((Number) row.get("import_hour")).intValue());
			times.add(time);
		}
		autoImport.setTimes(times);

		String importMlistsQuery = "select * from auto_import_mlist_bind_tbl where auto_import_id = ? and company_id = ?";
		List<Map<String, Object>> importMlists = template.queryForList(importMlistsQuery, new Object[]{autoImportId, companyId});
		ArrayList<Integer> mailinglists = new ArrayList<Integer>();
		for (Map<String, Object> row : importMlists) {
			mailinglists.add(((Number)row.get("mailinglist_id")).intValue());
		}
		autoImport.setMailinglists(mailinglists);

		return autoImport;
	}

	@Override
	public void deleteAutoImport(int autoImportId, int companyId) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		// remove mailinglist bindings
		String query = "delete from auto_import_mlist_bind_tbl where auto_import_id = ? and company_id = ?";
		template.update(query, new Object[] {autoImportId, companyId});
		// remove selected days/times
		query = "delete from auto_import_time_tbl where auto_import_id = ? and company_id = ?";
		template.update(query, new Object[] {autoImportId, companyId});
		// remove records about used import files
		query = "delete from auto_import_used_files_tbl where auto_import_id = ? and company_id = ?";
		template.update(query, new Object[] {autoImportId, companyId});
		// remove autoimport itself
		query = "delete from auto_import_tbl where auto_import_id = ? and company_id = ?";
		template.update(query, new Object[] {autoImportId, companyId});
	}

	@Override
	public List<AutoImport.UsedFile> getUsedFiles(int autoImportId, int companyId) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String query = "select * from auto_import_used_files_tbl where auto_import_id = ? and company_id = ?";
		List<Map<String, Object>> resultList = template.queryForList(query, new Object[]{autoImportId, companyId});
		ArrayList<AutoImport.UsedFile> usedFiles = new ArrayList<AutoImport.UsedFile>();
		for (Map<String, Object> row : resultList) {
			AutoImport.UsedFile usedFile = new AutoImport.UsedFile();
			usedFile.setFileSize(((Number) row.get("file_size")).longValue());
			usedFile.setFileDate((Date) row.get("file_date"));
			usedFiles.add(usedFile);
		}
		return usedFiles;
	}

	@Override
	public void addUsedFile(AutoImport.UsedFile usedFile, int autoImportId, int companyId) {
		String usedFileId = AgnUtils.isOracleDB() ? "auto_import_used_files_tbl_seq.nextval" : "0";
		String query = "insert into auto_import_used_files_tbl(auto_import_used_file_id, auto_import_id, file_size, file_date, company_id) " +
				"values (" + usedFileId + ", ?, ?, ?, ?)";
		JdbcTemplate template = new JdbcTemplate(dataSource);
		template.update(query, new Object[] {autoImportId, usedFile.getFileSize(), usedFile.getFileDate(), companyId});
	}

	@Override
	public void changeActiveStatus(int autoImportId, int companyId, boolean active) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String updateQuery = "update auto_import_tbl set active = ? where auto_import_id = ? and company_id = ?";
		template.update(updateQuery, new Object[] { active, autoImportId, companyId });
	}

	@Override
	public void updateAutoImport(AutoImport autoImport) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String updateQuery = "update auto_import_tbl set import_profile_id = ?, shortname = ?, description = ?, " +
                "file_path = ?, file_server = ?, one_time = ?, auto_resume = ?, executed = ?, active = ?, admin_id = ?, " +
                "auto_activation_date = ?, allow_unknown_hostkeys = ? where auto_import_id = ? and company_id = ?";
		template.update(updateQuery, new Object[] { autoImport.getImportProfileId(), autoImport.getShortname(),
                autoImport.getDescription(), autoImport.getFilePath(), autoImport.getFileServer(),
                autoImport.isOneTime() ? 1 : 0, autoImport.isAutoResume() ? 1 : 0, autoImport.isExecuted() ? 1 : 0,
                autoImport.isActive() ? 1 : 0, autoImport.getAdminId(), autoImport.getAutoActivationDate(), autoImport.isAllowUnknownHostKeys() ? 1 : 0,
                autoImport.getAutoImportId(), autoImport.getCompanyId() });
		updateAutoImportTimes(autoImport);
		updateAutoImportMailinglists(autoImport);
	}

	@Override
	public void createAutoImport(AutoImport autoImport) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		int newId;
		List<Object> params = new ArrayList<Object>();
		params.add(autoImport.getImportProfileId());
		params.add(autoImport.getShortname());
		params.add(autoImport.getDescription());
		params.add(autoImport.getFilePath());
		params.add(autoImport.getFileServer());
		params.add(autoImport.isOneTime() ? 1 : 0);
		params.add(autoImport.isAutoResume() ? 1 : 0);
		params.add(autoImport.isExecuted() ? 1 : 0);
		params.add(autoImport.isActive() ? 1 : 0);
		params.add(autoImport.getCompanyId());
		params.add(autoImport.getAdminId());
		params.add(autoImport.getAutoActivationDate());
        params.add(autoImport.isAllowUnknownHostKeys() ? 1 : 0);
		if (AgnUtils.isOracleDB()) {
			String insertQuery = "insert into auto_import_tbl(auto_import_id, import_profile_id, shortname, description, file_path, file_server, " +
					"one_time, auto_resume, executed, active, company_id, admin_id, auto_activation_date, allow_unknown_hostkeys) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			newId = getNextAutoImportId();
			params.add(0, newId);
			template.update(insertQuery, params.toArray());
		}
		else {
			String insertQuery = "insert into auto_import_tbl(import_profile_id, shortname, description, file_path, file_server, " +
					"one_time, auto_resume, executed, active, company_id, admin_id, auto_activation_date, allow_unknown_hostkeys) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			SqlUpdate sqlUpdate = new SqlUpdate(dataSource, insertQuery,  new int[] { Types.INTEGER, Types.VARCHAR, Types.VARCHAR,
					Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,
                    Types.INTEGER, Types.TIMESTAMP, Types.INTEGER});
			sqlUpdate.setReturnGeneratedKeys(true);
			sqlUpdate.setGeneratedKeysColumnNames(new String[]{"auto_import_id"});
			sqlUpdate.compile();
			GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
			sqlUpdate.update(params.toArray(), generatedKeyHolder);
			newId = generatedKeyHolder.getKey().intValue();
		}
		autoImport.setAutoImportId(newId);
		updateAutoImportTimes(autoImport);
		updateAutoImportMailinglists(autoImport);
	}

    private void updateAutoImportMailinglists(AutoImport autoImport) {
		JdbcTemplate template = new JdbcTemplate(dataSource);

		String deleteQuery = "delete from auto_import_mlist_bind_tbl where auto_import_id = ? and company_id = ?";
		template.update(deleteQuery, new Object[] {autoImport.getAutoImportId(), autoImport.getCompanyId() } );

		String insertQuery = "insert into auto_import_mlist_bind_tbl (auto_import_id, mailinglist_id, company_id) values (?, ?, ?)";
		List<Integer> mailinglists = autoImport.getMailinglists();
		for (Integer mailinglistId : mailinglists) {
			template.update(insertQuery, new Object[] { autoImport.getAutoImportId(), mailinglistId, autoImport.getCompanyId() });
		}
	}

	private void updateAutoImportTimes(AutoImport autoImport) {
		JdbcTemplate template = new JdbcTemplate(dataSource);

		String deleteQuery = "delete from auto_import_time_tbl where auto_import_id = ? and company_id = ?";
		template.update(deleteQuery, new Object[] {autoImport.getAutoImportId(), autoImport.getCompanyId() } );

		String insertQuery = "insert into auto_import_time_tbl (auto_import_id, import_day_of_week, import_hour, company_id) values (?, ?, ?, ?)";
		List<AutoImport.ImportTime> importTimes = autoImport.getTimes();
		for (AutoImport.ImportTime importTime : importTimes) {
			template.update(insertQuery, new Object[] { autoImport.getAutoImportId(), importTime.getDayOfWeek(), importTime.getHour(), autoImport.getCompanyId() });
		}
	}

	protected int getNextAutoImportId() {
		String newIDQuery = "SELECT auto_import_tbl_seq.nextval FROM dual";
		return new JdbcTemplate(dataSource).queryForInt(newIDQuery);
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	private class AutoImportRowMapper implements ParameterizedRowMapper<AutoImport> {
		@Override
		public AutoImport mapRow(ResultSet resultSet, int row) throws SQLException {
			AutoImport autoImport = new AutoImport();
			autoImport.setCompanyId(resultSet.getBigDecimal("company_id").intValue());
			autoImport.setAutoImportId(resultSet.getBigDecimal("auto_import_id").intValue());
			autoImport.setAdminId(resultSet.getBigDecimal("admin_id").intValue());
			autoImport.setImportProfileId(resultSet.getBigDecimal("import_profile_id").intValue());
			autoImport.setShortname(resultSet.getString("shortname"));
			autoImport.setDescription(resultSet.getString("description"));
			autoImport.setFilePath(resultSet.getString("file_path"));
			autoImport.setFileServer(resultSet.getString("file_server"));
			autoImport.setOneTime(resultSet.getBigDecimal("one_time").intValue() == 1);
			autoImport.setAutoResume(resultSet.getBigDecimal("auto_resume").intValue() == 1);
			autoImport.setExecuted(resultSet.getBigDecimal("executed").intValue() == 1);
			autoImport.setActive(resultSet.getBigDecimal("active").intValue() == 1);
			autoImport.setAutoActivationDate(resultSet.getTimestamp("auto_activation_date"));
            autoImport.setAllowUnknownHostKeys(resultSet.getInt("allow_unknown_hostkeys") == 1);
			return autoImport;
		}
	}
}
