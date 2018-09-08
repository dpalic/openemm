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

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.agnitas.dao.TargetDao;
import org.agnitas.dao.exception.target.TargetGroupPersistenceException;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.target.Target;
import org.agnitas.target.TargetFactory;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.target.TargetRepresentationFactory;
import org.agnitas.util.AgnUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
 * 
 * @author aso
 */
public class TargetDaoImpl extends BaseDaoImpl implements TargetDao {

	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(TargetDaoImpl.class);

	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	protected TargetFactory targetFactory;

	public void setTargetFactory(TargetFactory targetFactory) {
		this.targetFactory = targetFactory;
	}

	protected TargetRepresentationFactory targetRepresentationFactory;

	public void setTargetRepresentationFactory(TargetRepresentationFactory factory) {
		targetRepresentationFactory = factory;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic

	/** Creates a new instance of MailingDaoImpl */
	public TargetDaoImpl() {
	}

	@Override
	public Target getTarget(int targetID, @VelocityCheck int companyID) {
		try {
			if (targetID == 0 || companyID == 0) {
				return null;
			}

			return selectObjectDefaultNull(logger, "SELECT target_id, company_id, target_description, target_shortname, target_representation, target_sql, deleted, creation_date, change_date FROM dyn_target_tbl WHERE target_id = ? AND company_id = ?", new Target_RowMapper(), targetID, companyID);
		} catch (Exception e) {
			logger.error("Target load error (company ID:" + companyID + ", target ID: " + targetID + ")", e);
			return null;
		}
	}

	/**
	 * Getter for target by target name and company id.
	 * 
	 * @return target.
	 */
	@Override
	public Target getTargetByName(String targetName, @VelocityCheck int companyID) {
		targetName = targetName.trim();

		if (targetName.length() == 0 || companyID == 0) {
			return null;
		}
		
		try {
			return selectObjectDefaultNull(logger, "SELECT target_id, company_id, target_description, target_shortname, target_representation, target_sql, deleted, creation_date, change_date FROM dyn_target_tbl WHERE target_shortname = ? AND (company_id = ? OR company_id = 0)", new Target_RowMapper(), targetName, companyID);
		} catch (Exception e) {
			logger.error("Target load error (company ID:" + companyID + ", targetName: " + targetName + ")", e);
			return null;
		}
	}

	@Override
	public int saveTarget(Target target) throws TargetGroupPersistenceException {
		if (target == null || target.getCompanyID() == 0) {
			return 0;
		} else if (StringUtils.isBlank(target.getTargetName())) {
			throw new RuntimeException("Target is missing targetname");
		}
		
		try {
			target.setChangeDate(new Date());
			
			if (target.getId() == 0) {
				if (target.getCreationDate() == null) {
					// some tests set the creationdate
					target.setCreationDate(new Date());
				}
				
				if (AgnUtils.isOracleDB()) {
					int nextTargetId = selectInt(logger, "SELECT dyn_target_tbl_seq.NEXTVAL FROM DUAL");
					target.setId(nextTargetId);
					update(logger, "INSERT INTO dyn_target_tbl (target_id, company_id, target_sql, target_shortname, target_description, creation_date, change_date, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
							target.getId(),
							target.getCompanyID(),
							target.getTargetSQL(),
							target.getTargetName(),
							target.getTargetDescription(),
							target.getCreationDate(),
							target.getChangeDate(),
							target.getDeleted()
					);
				} else {
					SqlUpdate sqlUpdate = new SqlUpdate(
							getDataSource(),
							"INSERT INTO dyn_target_tbl (company_id, target_sql, target_shortname, target_description, creation_date, change_date, deleted) VALUES (?, ?, ?, ?, ?, ?, ?)",
							new int[] { Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.INTEGER });

					sqlUpdate.setReturnGeneratedKeys(true);
					sqlUpdate.setGeneratedKeysColumnNames(new String[] { "target_id" });
					sqlUpdate.compile();
					GeneratedKeyHolder key = new GeneratedKeyHolder();

					Object[] paramsWithNext = new Object[7];
					paramsWithNext[0] = target.getCompanyID();
					paramsWithNext[1] = target.getTargetSQL();
					paramsWithNext[2] = target.getTargetName();
					paramsWithNext[3] = target.getTargetDescription();
					paramsWithNext[4] = target.getCreationDate();
					paramsWithNext[5] = target.getChangeDate();
					paramsWithNext[6] = target.getDeleted();

					sqlUpdate.update(paramsWithNext, key);
					int targetID = key.getKey().intValue();
					target.setId(targetID);
				}
			} else {
				update(logger, "UPDATE dyn_target_tbl SET target_sql = ?, target_shortname = ?, target_description = ?, deleted = ?, change_date = ? WHERE target_id = ? AND company_id = ?",
					target.getTargetSQL(),
					target.getTargetName(),
					target.getTargetDescription(),
					target.getDeleted(),
					target.getChangeDate(),
					target.getId(),
					target.getCompanyID());
			}
			
			// store target representation serialized in blob
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream representationOutputStream = new ObjectOutputStream(baos);
			representationOutputStream.writeObject(target.getTargetStructure());
			representationOutputStream.flush();
			representationOutputStream.close();
			byte[] representationBytes = baos.toByteArray();
						
			updateBlob(logger, "UPDATE dyn_target_tbl SET target_representation = ? WHERE target_id = ?", representationBytes, target.getId());
		} catch (Exception e) {
			logger.error("Error in storing target", e);
			target.setId(0);
		}
		
		return target.getId();
	}

	@Override
	public boolean deleteTarget(int targetID, @VelocityCheck int companyID) throws TargetGroupPersistenceException {
		int touchedLines = update(logger, "UPDATE dyn_target_tbl SET deleted = 1, change_date = CURRENT_TIMESTAMP WHERE target_id = ? AND company_id = ? AND deleted = 0", targetID, companyID);
		
		return touchedLines > 0;
	}

	@Override
	public List<Target> getTargets(@VelocityCheck int companyID) {
		return getTargets(companyID, false);
	}

	@Override
	public List<Target> getTargets(@VelocityCheck int companyID, boolean includeDeleted) {
		List<Target> targetList = select(logger, "SELECT target_id, company_id, target_description, target_shortname, target_representation, target_sql, deleted, creation_date, change_date FROM dyn_target_tbl WHERE company_id = ?" + (includeDeleted ? "" : " AND deleted = 0") + " ORDER BY target_shortname", new Target_RowMapper(), companyID);

		Collections.sort(targetList, new Comparator<Target>() {
			@Override
			public int compare(Target target1, Target target2) {
				return target1.getTargetName().compareToIgnoreCase(target2.getTargetName());
			}

		});
		
		return targetList;
	}

	@Override
	public List<Integer> getDeletedTargets(@VelocityCheck int companyID) {
		List<Integer> resultList = new ArrayList<Integer>();
		String sql = "SELECT target_id FROM dyn_target_tbl WHERE company_id = ? AND deleted = 1";
		List<Map<String, Object>> list = select(logger, sql);
		for (Map<String, Object> map : list) {
			int targetId = ((Number) map.get("target_id")).intValue();
			resultList.add(new Integer(targetId));
		}
		return resultList;
	}

	@Override
	public List<String> getTargetNamesByIds(@VelocityCheck int companyID, Set<Integer> targetIds) {
		List<String> resultList = new ArrayList<String>();
		if (targetIds.size() <= 0) {
			return resultList;
		}
		String sql = "SELECT target_shortname FROM dyn_target_tbl WHERE company_id = ? AND target_id in (" + StringUtils.join(targetIds, ", ") + ")";
		List<Map<String, Object>> list = select(logger, sql, companyID);
		for (Map<String, Object> map : list) {
			String targetName = (String) map.get("target_shortname");
			resultList.add(targetName);
		}
		return resultList;
	}

	@Override
	public Map<Integer, Target> getAllowedTargets(@VelocityCheck int companyID) {
		Map<Integer, Target> targets = new HashMap<Integer, Target>();
		String sql = "SELECT target_id, target_shortname, target_description, target_sql FROM dyn_target_tbl WHERE company_id = ? ORDER BY target_id";

		try {
			List<Map<String, Object>> list = select(logger, sql, companyID);

			for (Map<String, Object> map : list) {
				int id = ((Number) map.get("target_id")).intValue();
				String shortname = (String) map.get("target_shortname");
				String description = (String) map.get("target_description");
				String targetsql = (String) map.get("target_sql");
				Target target = targetFactory.newTarget();

				target.setCompanyID(companyID);
				target.setId(id);
				if (shortname != null) {
					target.setTargetName(shortname);
				}
				if (description != null) {
					target.setTargetDescription(description);
				}
				if (targetsql != null) {
					target.setTargetSQL(targetsql);
				}
				targets.put(new Integer(id), target);
			}
		} catch (Exception e) {
			logger.error("getAllowedTargets (sql: " + sql + ")", e);
			AgnUtils.sendExceptionMail("sql:" + sql, e);
			return null;
		}
		return targets;
	}

	@Override
	public List<Target> getTargetGroup(@VelocityCheck int companyID, Collection<Integer> targetIds, boolean includeDeleted) {
		if (targetIds == null || targetIds.size() <= 0) {
			return new ArrayList<Target>();
		}
		
		String deleted = includeDeleted ? "" : " AND deleted=0 ";
		
		return select(logger, "SELECT target_id, company_id, target_description, target_shortname, target_representation, target_sql, deleted, creation_date, change_date FROM dyn_target_tbl WHERE company_id = ? " + deleted + " AND target_id IN (" + StringUtils.join(targetIds, ", ") + ") ORDER BY target_shortname", new Target_RowMapper(), companyID);
	}

	@Override
	public List<Target> getUnchoosenTargets(@VelocityCheck int companyID, Collection<Integer> targetIds) {
		if (targetIds == null || targetIds.size() <= 0) {
			return getTargets(companyID, false);
		}
		
		List<Target> resultList = select(logger, "SELECT target_id, company_id, target_description, target_shortname, target_representation, target_sql, deleted, creation_date, change_date FROM dyn_target_tbl WHERE company_id = ? AND deleted = 0 AND target_id NOT IN (" + StringUtils.join(targetIds, ", ") + ") ORDER BY target_shortname", new Target_RowMapper(), companyID);
		
		return resultList;
	}
	
	protected class Target_RowMapper implements ParameterizedRowMapper<Target> {
		@Override
		public Target mapRow(ResultSet resultSet, int row) throws SQLException {
			try {
				Target readTarget = targetFactory.newTarget();
				readTarget.setId(resultSet.getInt("target_id"));
				readTarget.setCompanyID(resultSet.getInt("company_id"));
				readTarget.setTargetDescription(resultSet.getString("target_description"));
				readTarget.setTargetName(resultSet.getString("target_shortname"));
				readTarget.setTargetSQL(resultSet.getString("target_sql"));
				readTarget.setDeleted(resultSet.getInt("deleted"));
				readTarget.setCreationDate(resultSet.getTimestamp("creation_date"));
				readTarget.setChangeDate(resultSet.getTimestamp("change_date"));
				
				Blob targetRepresentationBlob = resultSet.getBlob("target_representation");
				if (targetRepresentationBlob != null && targetRepresentationBlob.length() > 0) {
					ObjectInputStream aStream = new ObjectInputStream(targetRepresentationBlob.getBinaryStream());
					TargetRepresentation rep = (TargetRepresentation) aStream.readObject();
					aStream.close();
					readTarget.setTargetStructure(rep);
				} else {
					readTarget.setTargetStructure(targetRepresentationFactory.newTargetRepresentation());
				}
				
				return readTarget;
			} catch (Exception e) {
				throw new SQLException("Cannot create Target item from ResultSet row", e);
			}
		}
	}
}