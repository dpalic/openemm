package org.agnitas.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.agnitas.beans.ProfileField;
import org.agnitas.dao.ColumnInfoDao;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.util.AgnUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class ColumnInfoDaoImpl implements ColumnInfoDao {
	private DataSource dataSource;
	
	public Map getColumnInfo(int companyID, String column) throws Exception {
		Connection con=null;
        LinkedHashMap<String,Hashtable<String,Object>> list=new LinkedHashMap<String, Hashtable<String,Object>>();
        ResultSet rset=null;	        
        con=DataSourceUtils.getConnection(dataSource);
        try {
            if(AgnUtils.isOracleDB()) {
                rset=con.getMetaData().getColumns(null, AgnUtils.getDefaultValue("jdbc.username").toUpperCase(), "CUSTOMER_"+companyID+"_TBL", column.toUpperCase());
            } else {
                rset=con.getMetaData().getColumns(null, null, "customer_"+companyID+"_tbl", column);
            }
            if(rset!=null) {
                while(rset.next()) {
                    String type=null;
                    String col=rset.getString(4).toLowerCase();
                    Hashtable m=new Hashtable();

                    m.put("column", col);
                    m.put("shortname", col);
                    type=dbtype2string(rset.getInt(5));
                    m.put("type", type);
                    m.put("length", new Integer(rset.getInt(7)));
                    if(rset.getInt(11) == DatabaseMetaData.columnNullable)
                        m.put("nullable", new Integer(1));
                    else
                        m.put("nullable", new Integer(0));
                    
                    list.put((String)m.get("shortname"), m);
                }
            }
            rset.close();
        } catch ( Exception e) {
            DataSourceUtils.releaseConnection(con, dataSource);
            throw e;
        }
        DataSourceUtils.releaseConnection(con, dataSource);
        return list;
	}
	
	private static String	dbtype2string(int type)	{
		switch(type) {
			case java.sql.Types.BIGINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.SMALLINT:
				return new String("INTEGER");

			case java.sql.Types.DECIMAL:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.FLOAT:
			case java.sql.Types.NUMERIC:
			case java.sql.Types.REAL:
				return new String("DOUBLE");
                            
			case java.sql.Types.CHAR:
				return new String("CHAR");

			case java.sql.Types.VARCHAR:
			case java.sql.Types.LONGVARCHAR:
			case java.sql.Types.CLOB:
				return new String("VARCHAR");

			case java.sql.Types.DATE:
			case java.sql.Types.TIMESTAMP:
			case java.sql.Types.TIME:
				return new String("DATE");
		}
		return new String("UNKNOWN("+type+")");
	}
	

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
