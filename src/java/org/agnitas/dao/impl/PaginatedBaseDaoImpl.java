package org.agnitas.dao.impl;

import java.util.List;

import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DbUtilities;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * Helper class which hides the dependency injection variables and eases some select and update actions and logging.
 * But still the datasource or the SimpleJdbcTemplate can be used directly if needed.
 * 
 * The logger of this class is not used for db actions to log, because it would hide the calling from the derived classes.
 * Therefore every simplified update and select method demands an logger delivered as parameter.
 * 
 * @author aso
 *
 */
public abstract class PaginatedBaseDaoImpl extends BaseDaoImpl {
	public <T> PaginatedListImpl<T> selectPaginatedList(Logger logger, String selectStatement, String sortTable, String sortColumn, boolean sortDirectionAscending, int pageNumber, int pageSize, ParameterizedRowMapper<T> rowMapper, Object... parameters) {
		// Only alphanumeric values may be sorted with upper or lower,
		// which always returns a string value.
		// For selecting ordered numeric values use without lower
		String sortClause;
		try {
			if (DbUtilities.getColumnDataType(getDataSource(), sortTable, sortColumn).getCharacterLength() > 0) {
				sortClause = " ORDER BY LOWER(" + sortColumn + ")";
			} else {
				sortClause = " ORDER BY " + sortColumn;
			}
			sortClause = sortClause + " " + (sortDirectionAscending ? "asc" : "desc");
		} catch (Exception e) {
			sortClause = "";
		}

		// Get number of available items to show
		String countQuery;
		if (isOracleDB()) {
			countQuery = "SELECT COUNT(*) FROM (" + selectStatement + ")";
		} else {
			countQuery = "SELECT COUNT(*) FROM (" + selectStatement + ") selection";
		}
		int totalRows = selectInt(logger, countQuery, parameters);

		// Check pageSize validity
		if (pageSize < 1) {
			pageSize = 10;
			pageNumber = 1;
		}

		// Check pagenumber validity
		if (pageNumber < 1) {
			// Pagenumber starts with 1, not 0
			pageNumber = 1;
		} else if (pageNumber != 1) {
			// Check pagenumber lies beneath the maximum of available pages
			int maximumPagenumber = (int) Math.ceil(totalRows / (float) pageSize);
			if (maximumPagenumber < pageNumber) {
				// pagenumber exceeds maximum, so set it to first page
				pageNumber = 1;
			}
		}

		String selectDataStatement;
		if (isOracleDB()) {
			// Borders in oracle dbstatement "between" are included in resultset
			int rowStart = (pageNumber - 1) * pageSize + 1;
			int rowEnd_inclusive = rowStart + pageSize - 1;
			selectDataStatement = "SELECT * FROM (SELECT selection.*, rownum AS r FROM (" + selectStatement + sortClause + ") selection) WHERE r BETWEEN ? AND ?";
			parameters = AgnUtils.extendObjectArray(parameters, rowStart, rowEnd_inclusive);
		} else {
			int rowStart = (pageNumber - 1) * pageSize;
			selectDataStatement = "SELECT * FROM (" + selectStatement + sortClause + ") selection LIMIT ?, ?";
			parameters = AgnUtils.extendObjectArray(parameters, rowStart, pageSize);
		}

		List<T> resultList = select(logger, selectDataStatement, rowMapper, parameters);
		return new PaginatedListImpl<T>(resultList, totalRows, pageSize, pageNumber, sortColumn, sortDirectionAscending);
	}
}
