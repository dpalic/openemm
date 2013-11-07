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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.DatasourceDescription;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.impl.DynaBeanPaginatedListImpl;
import org.agnitas.dao.RecipientDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CsvColInfo;
import org.agnitas.util.SafeString;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.displaytag.pagination.PaginatedList;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Nicole Serek, Andreas Rehak
 */
public class RecipientDaoImpl implements RecipientDao {
	private static Integer	maxRecipient=null;

	private int	getMaxRecipient() {
		if(maxRecipient == null) {
			synchronized(this) {
				if(maxRecipient == null) {
					maxRecipient = new Integer(AgnUtils.getDefaultIntValue("recipient.maxRows"));
				}	
			}
		}
		if(maxRecipient == null) {
			return 0;
		}
		return maxRecipient.intValue();
	}

	public boolean mayAdd(int companyID, int count) {
		if(getMaxRecipient() != 0) {
			JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
			String sql = "select count(customer_id) from customer_" + companyID + "_tbl";
			int current = jdbc.queryForInt(sql);
			int max = getMaxRecipient();

			if(max == 0 || current+count <= max) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	public boolean	isNearLimit(int companyID, int count) {
		if(getMaxRecipient() != 0) {
			JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
			String sql = "select count(customer_id) from customer_" + companyID + "_tbl";
			int current=jdbc.queryForInt(sql);
			int max=(int) (getMaxRecipient()*0.9);

			if(max == 0 || current+count <= max) {
				return false;
			}
			return true;
		} else {
			return true;
		}
	}

    /**
     * Gets new customerID from Database-Sequence an stores it in member-variable "customerID"
     *
     * @return true on success
     */
    public int getNewCustomerID(int companyID) {
        String sqlStatement = null;
        int customerID = 0;
        Dialect dialect = AgnUtils.getHibernateDialect();

        if(companyID == 0) {
        	return customerID;
        }
        if(mayAdd(companyID, 1) == false) {
        	return customerID;
        }
        try {
            if(dialect.supportsSequences()) {
                JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                sqlStatement = "select customer_" + companyID + "_tbl_seq.nextval FROM dual";
                customerID = tmpl.queryForInt(sqlStatement);
            } else {
                sqlStatement = "insert into customer_" + companyID + "_tbl_seq () values ()";
                SqlUpdate updt = new SqlUpdate((DataSource)this.applicationContext.getBean("dataSource"), sqlStatement);
                updt.setReturnGeneratedKeys(true);
                GeneratedKeyHolder key = new GeneratedKeyHolder();
                customerID = updt.update(null, key);
                customerID = key.getKey().intValue();
            }
        } catch (Exception e) {
            customerID = 0;
            System.err.println("Exception:" + e);
            System.err.println(AgnUtils.getStackTrace(e));
        }
        AgnUtils.logger().debug("new customerID: "+ customerID);
        return customerID;
    }
    
    /**
     * Inserts new customer record in Database with a fresh customer-id
     *
     * @return true on success
     */
    public int insertNewCust(Recipient cust) {
        StringBuffer Columns = new StringBuffer("(");
        StringBuffer Values = new StringBuffer(" VALUES (");
        String aColumn = null;
        String aParameter = null;
        String ColType = null;
        int intValue = 0;
        int day, month, year, hour, minute, second=0;
        StringBuffer insertCust = new StringBuffer("INSERT INTO customer_" + cust.getCompanyID() + "_tbl ");
        boolean exitNow = false;
        boolean appendIt = false;
        boolean hasDefault = false;
        String appendColumn = null;
        String appendValue = null;
        NumberFormat aFormat1 = null;
        NumberFormat aFormat2 = null;

        if(mayAdd(cust.getCompanyID(), 1) == false) {
        	return 0;
        }

        if(cust.getCustDBStructure() == null) {
        	cust.loadCustDBStructure();
        }

        int customerID = getNewCustomerID(cust.getCompanyID()); 
        if(customerID == 0) {
        	return 0;
        } else {
        	cust.setCustomerID(customerID);
        }

        Columns.append("customer_id");
        Values.append(Integer.toString(cust.getCustomerID()));

        Iterator<String> i = cust.getCustDBStructure().keySet().iterator();
        while(i.hasNext()) {
            aColumn = i.next();
            ColType = cust.getCustDBStructure().get(aColumn);
            appendIt = false;
			hasDefault = false;
			if(!aColumn.equalsIgnoreCase("customer_id")) { 
				if(aColumn.equalsIgnoreCase("creation_date") || aColumn.equalsIgnoreCase("timestamp") || aColumn.equalsIgnoreCase("change_date")) {
					appendValue = new String("current_timestamp");
					appendColumn = new String(aColumn);
					appendIt = true;
				} else if(ColType.equalsIgnoreCase("DATE")) {
					if(cust.getCustParameters(aColumn + "_DAY_DATE") != null && cust.getCustParameters(aColumn + "_MONTH_DATE") != null && cust.getCustParameters(aColumn + "_YEAR_DATE") != null) {
						aFormat1 = new DecimalFormat("00");
						aFormat2 = new DecimalFormat("0000");
						try {
							if(!cust.getCustParameters(aColumn + "_DAY_DATE").trim().equals("")) {
								day = Integer.parseInt(cust.getCustParameters(aColumn+"_DAY_DATE"));
								month = Integer.parseInt(cust.getCustParameters(aColumn+"_MONTH_DATE"));
								year = Integer.parseInt(cust.getCustParameters(aColumn+"_YEAR_DATE"));
								hour = extractInt(aColumn+"_HOUR_DATE", 0, cust);
								minute = extractInt(aColumn+"_MINUTE_DATE", 0, cust);
								second = extractInt(aColumn+"_SECOND_DATE", 0, cust);
                            
								if ( AgnUtils.isOracleDB() ) {
									appendValue = new String("to_date('"+ aFormat1.format(day) +"."+aFormat1.format(month)+"."+aFormat2.format(year)+" "+ aFormat1.format(hour)+":"+aFormat1.format(minute)+":"+aFormat1.format(second)+"', 'DD.MM.YYYY HH24:MI:SS')");
								} else {
									appendValue = new String("STR_TO_DATE('"+ aFormat1.format(day) +"-"+aFormat1.format(month)+"-"+aFormat2.format(year)+"', '%d-%m-%Y')");								
								}
								appendColumn = new String(aColumn);
								appendIt = true;
							} else {
								Map tmp = (Map) cust.getCustDBProfileStructure().get(aColumn);
								if (tmp != null) {
									String defaultValue = (String)tmp.get("default");
									if (!isBlank(defaultValue)) {
										appendValue = "'" + defaultValue + "'";
										hasDefault = true;
									}
								}
								if (!hasDefault) {                        	
									appendValue = new String("null");
								}
								appendColumn = new String(aColumn);
								appendIt = true;
							}
						} catch (Exception e1) {
							AgnUtils.logger().error("insertNewCust: (" + aColumn + ") " + e1.getMessage());
						}
					} else {
						Map tmp = (Map) cust.getCustDBProfileStructure().get(aColumn);

						if (tmp != null) {
							String defaultValue = (String)tmp.get( "default" );

							if (!isBlank(defaultValue)) {
								appendValue = "'" + defaultValue + "'";
								hasDefault = true;
							}
						}
						if (hasDefault) {
							appendColumn=new String(aColumn);
							appendIt=true;
						}
					}
				}
				if(ColType.equalsIgnoreCase("INTEGER") || ColType.equalsIgnoreCase("DOUBLE")) {
					aParameter = cust.getCustParameters(aColumn);
					if(!StringUtils.isEmpty(aParameter)) {
						try {
							intValue = Integer.parseInt(aParameter);
						} catch (Exception e1) {
							intValue = 0;
						}
						appendValue = new String(Integer.toString(intValue));
						appendColumn = new String(aColumn);
						appendIt = true;
					} else {
						Map tmp = (Map) cust.getCustDBProfileStructure().get( aColumn );

						if (tmp != null) {
							String defaultValue = (String)tmp.get("default");

							if (!isBlank(defaultValue)) {
								appendValue = defaultValue;
								hasDefault = true;
							}
						}
						if (hasDefault) {    
							appendColumn = new String(aColumn);
							appendIt = true;
						}
					}
				}
				if(ColType.equalsIgnoreCase("VARCHAR") || ColType.equalsIgnoreCase("CHAR")) {
					aParameter = cust.getCustParameters(aColumn);
					if(!StringUtils.isEmpty(aParameter)) {
						appendValue = new String("'" + SafeString.getSQLSafeString(aParameter) + "'");
						appendColumn = new String(aColumn);
						appendIt = true;
					} else {
						Map tmp = (Map) cust.getCustDBProfileStructure().get(aColumn);
						if (tmp != null) {
							String defaultValue = (String)tmp.get("default");
							if (!isBlank(defaultValue) ) {
								appendValue = "'" + defaultValue + "'";
								hasDefault = true;
							}
						}
						if (hasDefault) { 
							appendColumn = new String(aColumn);
							appendIt = true;
						}
					}
				}

				if(appendIt) {
					Columns.append(", ");
					Values.append(", ");
					Columns.append(appendColumn.toLowerCase());
					Values.append(appendValue);
				}
			}
        }

        Columns.append(")");
        Values.append(")");

        insertCust.append(Columns.toString());
        insertCust.append(Values.toString());
        try{
            JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            tmpl.execute(insertCust.toString());
            AgnUtils.logger().debug("insertCust: "+insertCust.toString());
        } catch (Exception e3) {
            AgnUtils.logger().error("insertNewCust: " + e3.getMessage());
            AgnUtils.logger().error(AgnUtils.getStackTrace(e3));
            exitNow = true;
            cust.setCustomerID(0);
            // return 0;
        }

        if(exitNow == true)
            return 0;

        return cust.getCustomerID();
    }
    
    /**
     * Updates Customer in DB. customerID must be set to a valid id, customer-data is taken from this.customerData
     *
     * @return true on success
     */
    public boolean updateInDB(Recipient cust) {
        String currentTimestamp=AgnUtils.getSQLCurrentTimestampName();
        String aColumn;
        String colType = null;
        boolean appendIt = false;
        StringBuffer updateCust = new StringBuffer("UPDATE customer_" + cust.getCompanyID() + "_tbl SET " + AgnUtils.changeDateName() + "=" + currentTimestamp);
        NumberFormat aFormat1 = null;
        NumberFormat aFormat2 = null;
        int day, month, year;
        String aParameter = null;
        int intValue;
        String appendValue = null;
        boolean result = true;
        boolean hasDefault = false;

        if(cust.getCustDBStructure() == null) {
            cust.loadCustDBStructure();
        }
        
        if(cust.getCustomerID() == 0) {
            AgnUtils.logger().info("updateInDB: creating new customer");
            if(this.insertNewCust(cust) == 0) {
                result = false;
            }
        } else {
            if(cust.isChangeFlag()) { // only if something has changed
                Iterator<String> i = cust.getCustDBStructure().keySet().iterator();
                while(i.hasNext()) {
                    aColumn = i.next();
                    colType = (String) cust.getCustDBStructure().get(aColumn);
                    appendIt = false;
                    hasDefault = false;

                    if(aColumn.equalsIgnoreCase("customer_id") || aColumn.equalsIgnoreCase("change_date") || aColumn.equalsIgnoreCase("timestamp") || aColumn.equalsIgnoreCase("creation_date") || aColumn.equalsIgnoreCase("datasource_id")) {
                        continue;
                    }

                    if(colType.equalsIgnoreCase("DATE")) {
                        if((cust.getCustParameters().get(aColumn + "_DAY_DATE") != null) && (cust.getCustParameters().get(aColumn + "_MONTH_DATE") != null) && (cust.getCustParameters().get(aColumn + "_YEAR_DATE") != null)) {
                            aFormat1 = new DecimalFormat("00");
                            aFormat2 = new DecimalFormat("0000");
                            try {
                                if(!((String) cust.getCustParameters().get(aColumn + "_DAY_DATE")).trim().equals("")) {
                                	day = Integer.parseInt((String) cust.getCustParameters().get(aColumn + "_DAY_DATE"));
                                    month = Integer.parseInt((String) cust.getCustParameters().get(aColumn + "_MONTH_DATE"));
                                    year = Integer.parseInt((String) cust.getCustParameters().get(aColumn + "_YEAR_DATE"));
                                    if (AgnUtils.isOracleDB()) {
                                        appendValue = new String(aColumn.toLowerCase() + "=to_date('" + aFormat1.format(day) + "-" + aFormat1.format(month) + "-" + aFormat2.format(year) + "', 'DD-MM-YYYY')");
                                    } else {
                                    	appendValue = new String(aColumn.toLowerCase() + "=STR_TO_DATE('" + aFormat1.format(day) + "-" + aFormat1.format(month) + "-" + aFormat2.format(year) + "', '%d-%m-%Y')");
                                    }
                                    appendIt = true;
                                } else {
                                	Map tmp = (Map) cust.getCustDBProfileStructure().get(aColumn);
                                	if (tmp != null) {
                                		String defaultValue = (String)tmp.get("default");
                                		if (!isBlank(defaultValue) && !defaultValue.equals("null")) {
                                            appendValue = aColumn.toLowerCase() + "='" + defaultValue + "'";
                                            hasDefault = true;
                                		}
                                	}
                                	if (!hasDefault) {
                                		appendValue = new String(aColumn.toLowerCase()+"=null");
                                	}
                                    appendIt = true;
                                }
                            } catch (Exception e1) {
                                AgnUtils.logger().error("updateInDB: Could not parse Date "+aColumn + " because of "+e1.getMessage());
                            }
                        } else {
                            AgnUtils.logger().error("updateInDB: Parameter missing!");
                        }
                    } else if(colType.equalsIgnoreCase("INTEGER")) {
                        aParameter = (String) cust.getCustParameters(aColumn);
                        if(!StringUtils.isEmpty(aParameter)){
                            try {
                                intValue = Integer.parseInt(aParameter);
                            } catch (Exception e1) {
                                intValue = 0;
                            }
                            appendValue = new String(aColumn.toLowerCase() + "=" + intValue);
                            appendIt = true;
                        } else {
                        	Map tmp = (Map) cust.getCustDBProfileStructure().get( aColumn );
                        	if (tmp != null) {
                        		String defaultValue = (String)tmp.get( "default" );
                        		if (!isBlank(defaultValue)) {
                                    appendValue = aColumn.toLowerCase()+"=" + defaultValue;
                                    hasDefault = true;
                        		}
                        	}
                        	if (!hasDefault) {
                        		appendValue = new String(aColumn.toLowerCase() + "=null");
                        	}
                            appendIt = true;
                        }
                    } else if(colType.equalsIgnoreCase("DOUBLE")) {
                        double dValue;
                        aParameter = (String) cust.getCustParameters(aColumn);
                        if(!StringUtils.isEmpty(aParameter)){
                            try {
                                dValue = Double.parseDouble(aParameter);
                            } catch(Exception e1) {
                                dValue = 0;
                            }
                            appendValue = new String(aColumn.toLowerCase() + "=" + dValue);
                            appendIt = true;
                        } else {
                        	Map tmp = (Map) cust.getCustDBProfileStructure().get(aColumn);
                        	if (tmp != null) {
                        		String defaultValue = (String)tmp.get("default");
                        		if (!isBlank(defaultValue)) {
                                    appendValue = aColumn.toLowerCase() + "=" + defaultValue;
                                    hasDefault = true;
                        		}
                        	}
                        	if (!hasDefault) {
                        		appendValue = new String(aColumn.toLowerCase() + "=null");
                        	}
                            appendIt = true;
                        }
                    } else /* if(colType.equalsIgnoreCase("VARCHAR") || colType.equalsIgnoreCase("CHAR"))*/ {
                        aParameter = (String) cust.getCustParameters(aColumn);
                        if(!StringUtils.isEmpty(aParameter)) {
                            appendValue = new String(aColumn.toLowerCase() + "='" + SafeString.getSQLSafeString(aParameter) + "'");
                            appendIt = true;
                        } else {
                        	Map tmp = (Map) cust.getCustDBProfileStructure().get(aColumn);
                        	if(tmp != null) {
                        		String defaultValue = (String)tmp.get( "default" );
                        		if(!isBlank(defaultValue)) {
                                    appendValue = aColumn.toLowerCase()+"='" + defaultValue + "'";
                                    hasDefault = true;
                        		}
                        	}
                        	if(!hasDefault) {
                        		appendValue = new String(aColumn.toLowerCase() + "=null");
                        	}
                            appendIt = true;
                        }
                    }
                    if(appendIt) {
                        updateCust.append(", ");
                        updateCust.append(appendValue);
                    }
                }

                updateCust.append(" WHERE customer_id=" + cust.getCustomerID());

                try {
                    JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                    AgnUtils.logger().info("updateInDB: " + updateCust.toString());
                    tmpl.execute(updateCust.toString());
                    
                    if(cust.getCustParameters("DATASOURCE_ID") != null) {
                    	String sql = "select datasource_id from customer_" + cust.getCompanyID() + "_tbl where customer_id = ?";
                    	List list = tmpl.queryForList(sql, new Object[] {new Integer(cust.getCompanyID())});
                    	Iterator id = list.iterator();
                    	if(!id.hasNext()) {
                    		aParameter = (String) cust.getCustParameters("DATASOURCE_ID");
                    		if(!StringUtils.isEmpty(aParameter)){
                    			try {
                    				intValue = Integer.parseInt(aParameter);
                    				sql = "update customer_" + cust.getCompanyID() + "_tbl set datasource_id = " + intValue + " where customer_id = " + cust.getCustomerID();
                    				tmpl.execute(sql);
                    			} catch (Exception e1) {}
                    		}
                    	}
                    }
                } catch(Exception e3) {
                    // Util.SQLExceptionHelper(e3,dbConn);
                    AgnUtils.logger().error("updateInDB: " + e3.getMessage());
                    result = false;
                }
            } else {
                AgnUtils.logger().info("updateInDB: nothing changed");
            }
        }
        return result;
    }
    
    /**
     * Find Subscriber by providing a column-name and a value. Only exact machtes possible.
     *
     * @return customerID or 0 if no matching record found
     * @param col Column-Name
     * @param value Value to search for in col
     */
    public int findByKeyColumn(Recipient cust, String col, String value) {
        int val = 0;
        String aType = null;
        String getCust = null;

        try {
            if(cust.getCustDBStructure() == null) {
                cust.loadCustDBStructure();
            }
    
            if(col.toLowerCase().equals("email")) {
                value = value.toLowerCase();
            }
    
            aType = (String) cust.getCustDBStructure().get(col);

            if(aType != null) {
                if(aType.equalsIgnoreCase("DECIMAL") || aType.equalsIgnoreCase("INTEGER") || aType.equalsIgnoreCase("DOUBLE")) {
                	try {
                        val = Integer.parseInt(value);
                    } catch (Exception e) {
                        val = 0;
                    }
                    getCust = "SELECT customer_id FROM customer_" + cust.getCompanyID() + "_tbl cust WHERE cust." + SafeString.getSQLSafeString(col, 30) + "=" + val;
                }
    
                if(aType.equalsIgnoreCase("VARCHAR") || aType.equalsIgnoreCase("CHAR")) {
                	getCust = "SELECT customer_id FROM customer_" + cust.getCompanyID() + "_tbl cust WHERE cust." + SafeString.getSQLSafeString(col, 30) + "='" + SafeString.getSQLSafeString(value) + "'";
                }
                AgnUtils.logger().error("Query: "+getCust);
                JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                // cannot use queryForInt, because of possible existing doublettes
                List<Map<String,Integer>> custList = (List<Map<String,Integer>>) tmpl.queryForList(getCust);
                if(custList.size() > 0) {
                	Map map = new CaseInsensitiveMap(custList.get(0));
                	cust.setCustomerID(((Number) map.get("customer_id")).intValue());
                } else {
                	cust.setCustomerID(0);
                }
            }
        } catch (Exception e) {
            System.err.println("findByKeyColumn: "+e.getMessage());
            System.err.println("Query: "+getCust);
            System.err.println(AgnUtils.getStackTrace(e));
            cust.setCustomerID(0);
        }
        return cust.getCustomerID();
    }

    public int findByColumn(int companyID, String col, String value) {
    	Recipient cust = (Recipient) applicationContext.getBean("Recipient");
    	cust.setCompanyID(companyID);
        int custID = 0;
        int val = 0;
        String aType = null;
        String getCust = null;

        if(cust.getCustDBStructure() == null) {
            cust.loadCustDBStructure();
        }
        if(col.toLowerCase().equals("email")) {
            value=value.toLowerCase();
        }

        aType = (String) cust.getCustDBStructure().get(col.toLowerCase());

        if(aType != null) {
            if(aType.equalsIgnoreCase("VARCHAR") || aType.equalsIgnoreCase("CHAR")) {
                getCust = "select customer_id from customer_" + companyID + "_tbl cust where lower(cust." + SafeString.getSQLSafeString(col, 30) + ")=lower('" + SafeString.getSQLSafeString(value) + "')";
            } else {
                try {
                    val = Integer.parseInt(value);
                } catch (Exception e) {
                    val = 0;
                }
                getCust = "select customer_id from customer_" + companyID + "_tbl cust where cust."+SafeString.getSQLSafeString(col, 30)+"="+val;
            }
            try {
                JdbcTemplate tmpl = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
                custID = tmpl.queryForInt(getCust);
            } catch (Exception e) {
                custID = 0;
            }
        }
        return custID;
    }
    
    /**
     * Find Subscriber by providing a username and password. Only exact machtes possible.
     *
     * @return customerID or 0 if no matching record found
     * @param userCol Column-Name for Username
     * @param userValue Value for Username
     * @param passCol Column-Name for Password
     * @param passValue Value for Password
     */
    public int findByUserPassword(int companyID, String userCol, String userValue, String passCol, String passValue) {
        String getCust = null;
        int customerID = 0;

        if(userCol.toLowerCase().equals("email")) {
            userValue = userValue.toLowerCase();
        }

        getCust = "SELECT customer_id FROM customer_" + companyID + "_tbl cust WHERE cust."+SafeString.getSQLSafeString(userCol, 30)+"='"+SafeString.getSQLSafeString(userValue)+"' AND cust."+SafeString.getSQLSafeString(passCol, 30)+"='"+SafeString.getSQLSafeString(passValue)+"'";

        try {
            JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            customerID = tmpl.queryForInt(getCust);
        } catch (Exception e) {
            customerID = 0;
            AgnUtils.logger().error("findByUserPassword: " + e.getMessage());
        }
        return customerID;
    }

    /**
     * Load complete Subscriber-Data from DB. customerID must be set first for this method.
     *
     * @return Map with Key/Value-Pairs of customer data
     */
    public Map getCustomerDataFromDb(int companyID, int customerID) {
        String aName = null;
        String aValue = null;
        int a;
        java.sql.Timestamp aTime = null;
        Recipient cust = (Recipient) applicationContext.getBean("Recipient");
        
        if(cust.getCustParameters() == null) {
            cust.setCustParameters(new CaseInsensitiveMap());
        }

        String getCust = "SELECT * FROM customer_" + companyID + "_tbl WHERE customer_id=" + customerID;

        if(cust.getCustDBStructure() == null) {
            cust.loadCustDBStructure();
        }

        DataSource ds = (DataSource)this.applicationContext.getBean("dataSource");
        Connection con = DataSourceUtils.getConnection(ds);

        try {
            Statement stmt = con.createStatement();
            ResultSet rset = stmt.executeQuery(getCust);
            AgnUtils.logger().info("getCustomerDataFromDb: "+getCust);

            if(rset.next()) {
                ResultSetMetaData aMeta = rset.getMetaData();

                for(a = 1; a <= aMeta.getColumnCount(); a++) {
                    aValue = null;
                    aName = new String(aMeta.getColumnName(a).toLowerCase());
                    switch(aMeta.getColumnType(a)) {
                        case java.sql.Types.TIMESTAMP:
                        case java.sql.Types.TIME:
                        case java.sql.Types.DATE:
                        	try {
                        		aTime = rset.getTimestamp(a);
                        	} catch(Exception e) {
                        		aTime = null;
                        	}
                            if(aTime == null) {
                                cust.getCustParameters().put(aName + "_DAY_DATE", new String(""));
                                cust.getCustParameters().put(aName + "_MONTH_DATE", new String(""));
                                cust.getCustParameters().put(aName + "_YEAR_DATE", new String(""));
                                cust.getCustParameters().put(aName + "_HOUR_DATE", new String(""));
                                cust.getCustParameters().put(aName + "_MINUTE_DATE", new String(""));
                                cust.getCustParameters().put(aName + "_SECOND_DATE", new String(""));
                                cust.getCustParameters().put(aName, new String(""));
                            } else {
                                GregorianCalendar aCal = new GregorianCalendar();
                                aCal.setTime(aTime);
                                cust.getCustParameters().put(aName + "_DAY_DATE", Integer.toString(aCal.get(GregorianCalendar.DAY_OF_MONTH)));
                                cust.getCustParameters().put(aName + "_MONTH_DATE", Integer.toString(aCal.get(GregorianCalendar.MONTH) + 1));
                                cust.getCustParameters().put(aName + "_YEAR_DATE", Integer.toString(aCal.get(GregorianCalendar.YEAR)));
                                cust.getCustParameters().put(aName + "_HOUR_DATE", Integer.toString(aCal.get(GregorianCalendar.HOUR_OF_DAY)));
                                cust.getCustParameters().put(aName + "_MINUTE_DATE", Integer.toString(aCal.get(GregorianCalendar.MINUTE)));
                                cust.getCustParameters().put(aName + "_SECOND_DATE", Integer.toString(aCal.get(GregorianCalendar.SECOND)));
                                SimpleDateFormat bdfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                cust.getCustParameters().put(aName, bdfmt.format(aCal.getTime()));
                            }
                            break;

                        default:
                            aValue = rset.getString(a);
                            if(aValue == null) {
                                aValue = "";
                            }
                            cust.getCustParameters().put(aName, aValue);
                            break;
                    }
                }
            }
            rset.close();
            stmt.close();

        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + getCust, e);
            AgnUtils.logger().error("getCustomerDataFromDb: "+e.getMessage());
        }
        DataSourceUtils.releaseConnection(con, ds);
        cust.setChangeFlag(false);
        return cust.getCustParameters();
    }
    
    /**
     * Delete complete Subscriber-Data from DB. customerID must be set first for this method.
     */
    public void deleteCustomerDataFromDb(int companyID, int customerID) {
        String sql = null;
        Object[] params = new Object[] { new Integer(customerID) };

        try {
            JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));

            sql = "DELETE FROM customer_" + companyID + "_binding_tbl WHERE customer_id=?";
            tmpl.update(sql, params);

            sql = "DELETE FROM customer_" + companyID + "_tbl WHERE customer_id=?";
            tmpl.update(sql, params);
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
            AgnUtils.logger().error("deleteCustomerDataFromDb: " + e.getMessage());
        }
    }
    
    /**
     * Loads complete Mailinglist-Binding-Information for given customer-id from Database
     *
     * @return Map with key/value-pairs as combinations of mailinglist-id and BindingEntry-Objects
     */
    public Hashtable loadAllListBindings(int companyID, int customerID) {
    	Recipient cust = (Recipient) applicationContext.getBean("Recipient");
        cust.setListBindings(new Hashtable()); // MailingList_ID as keys
        Hashtable mTable = new Hashtable(); // Media_ID as key, contains rest of data (user type, status etc.)
        String sqlGetLists = null;
        BindingEntry aEntry = null;
        int tmpMLID = 0;

        try {
            sqlGetLists = "SELECT mailinglist_id, user_type, user_status, user_remark, "+AgnUtils.changeDateName()+", mediatype FROM customer_" + companyID + "_binding_tbl WHERE customer_id=" +
                    customerID + " ORDER BY mailinglist_id, mediatype";
            JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            List list = tmpl.queryForList(sqlGetLists);
            Iterator i = list.iterator();

            while(i.hasNext()) {
                Map map = (Map) i.next();
                int listID = ((Number) map.get("mailinglist_id")).intValue();
                Integer mediaType = new Integer(((Number) map.get("mediatype")).intValue());

                aEntry = (BindingEntry) applicationContext.getBean("BindingEntry");
                aEntry.setCustomerID(customerID);
                aEntry.setMailinglistID(listID);
                aEntry.setUserType((String) map.get("user_type"));
                aEntry.setUserStatus(((Number) map.get("user_status")).intValue());
                aEntry.setUserRemark((String) map.get("user_remark"));
                aEntry.setChangeDate((java.sql.Timestamp) map.get(AgnUtils.changeDateName()));
                aEntry.setMediaType(mediaType.intValue());

                if(tmpMLID != listID) {
                    if(tmpMLID != 0) {
                        cust.getListBindings().put(new Integer(tmpMLID).toString(), mTable);
                        mTable = new Hashtable();
                        mTable.put(mediaType.toString(), aEntry);
                        tmpMLID = listID;
                    } else {
                        mTable.put(mediaType.toString(), aEntry);
                        tmpMLID = listID;
                    }
                } else {
                    mTable.put(mediaType.toString(), aEntry);
                }
            }
            cust.getListBindings().put(new Integer(tmpMLID).toString() , mTable);
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sqlGetLists, e);
            AgnUtils.logger().error("loadAllListBindings: " + e.getMessage());
            return null;
        }
        return cust.getListBindings();
    }
    
    /**
     * Checks if E-Mail-Adress given in customerData-HashMap is registered in blacklist(s)
     *
     * @return true if E-Mail-Adress is blacklisted
     */
    public boolean blacklistCheck(String email) {
        boolean returnValue = false;
        String sqlSelect = null;
        
        try {
            JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            List list = null;

            sqlSelect = "SELECT email FROM cust_ban_tbl WHERE '" + SafeString.getSQLSafeString(email) + "' LIKE email";
            list = tmpl.queryForList(sqlSelect);
            if(list.size() > 0) {
                returnValue=true;
            }
            sqlSelect = "SELECT email FROM cust_ban_tbl WHERE '" + SafeString.getSQLSafeString(email) + "' LIKE email";
            list = tmpl.queryForList(sqlSelect);
            if(list.size() > 0) {
                returnValue = true;
            }
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sqlSelect, e);
            AgnUtils.logger().error(e);
            returnValue = true;
        }
        return returnValue;
    }
    
    private boolean	isBlank(String s) {
		if(StringUtils.isEmpty(s)) {
			return true;
		}
		if(s.trim().length() <= 0) {
			return true;
		}
		return false;
	}
    
    /**
     * Extract an int parameter from CustParameters
     *
     * @return the int value or the default value in case of an exception
     * @param column Column-Name
     * @param defaultValue Value to be returned in case of exception
     */
    private int extractInt(String column, int defaultValue, Recipient cust) {
		try {
		    return Integer.parseInt(cust.getCustParameters(column));
		} catch (Exception e1) {
		    return defaultValue;
		}
	}

	public String getField(String selectVal, int recipientID, int companyID)	{
		JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
		String sql = "SELECT " + selectVal + " value FROM customer_" + companyID + "_tbl cust WHERE cust.customer_id=?";

		try {
			List list = jdbc.queryForList(sql, new Object[]{ new Integer(recipientID)});

			if(list.size() > 0) {
				Map map = (Map) list.get(0);
				Object temp = map.get("value");
				if(temp != null) {
					return temp.toString();
				}
			}
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
           	AgnUtils.logger().error("processTag: "+e.getMessage());
           	return null;
        }
		return "";
	}

	public Map<Integer, Map> getAllMailingLists(int customerID, int companyID) {
		Map<Integer, Map> result = new HashMap();
		String sql = "SELECT mailinglist_id, user_type, user_status, user_remark, " + AgnUtils.changeDateName()+", mediatype FROM customer_" + companyID + "_binding_tbl WHERE customer_id=? ORDER BY mailinglist_id, mediatype";
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		AgnUtils.logger().info("getAllMailingLists: " + sql);

		try	{
			List list = jdbc.queryForList(sql, new Object[]{new Integer(customerID)});
			Iterator i = list.iterator();
			BindingEntry entry = null;

			while(i.hasNext()) {
				Map map = (Map) i.next();
				int listID = ((Number) map.get("mailinglist_id")).intValue();
				int mediaType = ((Number) map.get("mediatype")).intValue();
				Map sub = (Map) result.get(new Integer(listID));

				if(sub == null) {
					sub = new HashMap();
				}
				entry = (BindingEntry) applicationContext.getBean("BindingEntry");
				entry.setCustomerID(customerID);
                entry.setMailinglistID(listID);
				entry.setUserType((String) map.get("user_type"));
				entry.setUserStatus(((Number) map.get("user_status")).intValue());
				entry.setUserRemark((String) map.get("user_remark"));
				entry.setChangeDate((java.sql.Timestamp) map.get(AgnUtils.changeDateName()));
				entry.setMediaType(mediaType);
				sub.put(new Integer(mediaType), entry);
				result.put(new Integer(listID), sub);
			}
		} catch(Exception e) {
			AgnUtils.sendExceptionMail("sql:" + sql + ", " + customerID, e);
			AgnUtils.logger().error("getAllMailingLists: " + e.getMessage());
		}
		return result;
	}

	public boolean createImportTables(int companyID, int datasourceID, CustomerImportStatus status) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String prefix = "cust_" + companyID + "_tmp";
		String tabName = prefix+datasourceID+"_tbl";
		String keyIdx = prefix+datasourceID+"$KEYCOL$IDX";
		String custIdx = prefix+datasourceID+"$CUSTID$IDX";
		String sql = null;

		try {
			sql = "create temporary table " + tabName + " as (select * from customer_" + companyID + "_tbl where 1=0)";
			jdbc.execute(sql);

			sql = "alter table " + tabName + " modify change_date timestamp null default null";
			jdbc.execute(sql);

			sql = "alter table " + tabName + " modify creation_date timestamp null default current_timestamp";
			jdbc.execute(sql);

			sql = "create index " + keyIdx + " on " + tabName + " (" + SafeString.getSQLSafeString(status.getKeycolumn()) + ")";
			jdbc.execute(sql);

			sql = "create index " + custIdx +" on " + tabName + " (customer_id)";
			jdbc.execute(sql);
		}   catch (Exception e) {
			AgnUtils.logger().error("createTemporaryTables: " + e.getMessage());
			AgnUtils.logger().error("Statement: " + sql);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean deleteImportTables(int companyID, int datasourceID) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String tabName = "cust_" + companyID + "_tmp" + datasourceID + "_tbl";

		if(AgnUtils.isOracleDB()) {
			try {
				jdbc.execute("drop table "+tabName);
			} catch (Exception e) {
				AgnUtils.logger().error("deleteTemporaryTables: " + e.getMessage());
				AgnUtils.logger().error("Table: " + tabName);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieves new Datasource-ID for newly imported Subscribers
	 *
	 * @return new Datasource-ID or 0
	 * @param aContext
	 */
	private DatasourceDescription getNewDatasourceDescription(int companyID, String description) {
		HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)applicationContext.getBean("sessionFactory"));
		DatasourceDescription dsDescription=(DatasourceDescription) applicationContext.getBean("DatasourceDescription");

		dsDescription.setId(0);
		dsDescription.setCompanyID(companyID);
		dsDescription.setSourcegroupID(2);
		dsDescription.setCreationDate(new java.util.Date());
		dsDescription.setDescription(description);
		tmpl.save("DatasourceDescription", dsDescription);
		return dsDescription;
	}

	
	public int sumOfRecipients(int companyID, String target) {
        int recipients = 0;

        String sql = "select count(customer_id) from customer_" + companyID + "_tbl cust where " + target;
        try {
        	JdbcTemplate tmpl = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
            recipients = tmpl.queryForInt(sql);
        } catch (Exception e) {
            recipients = 0;
        }
        return recipients;
    }
	
	public boolean deleteRecipients(int companyID, String target) {
		boolean returnValue = false;
		JdbcTemplate tmpl = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));		
		String sql;
		
		sql= "DELETE FROM customer_" + companyID + "_binding_tbl WHERE customer_id in (select customer_id from customer_" + companyID + "_tbl cust where " + target + ")";
        try {
        	tmpl.execute(sql);
        } catch (Exception e) {
        	System.err.println("error deleting recipient bindings: " + e.getMessage());
        	returnValue = false;
        }
		
        sql = "delete ";
        if(AgnUtils.isMySQLDB()) {
        	sql = sql + "cust ";
        }
        sql = sql + "from customer_" + companyID + "_tbl cust where" + target;
        try {
        	tmpl.execute(sql);
        	returnValue = true;
        } catch (Exception e) {
        	System.err.println("error deleting recipients: " + e.getMessage());
        	returnValue = false;
        }
        return returnValue;
    }
	
	public PaginatedList getRecipientList( String sqlStatement, String sort, String direction , int page, int rownums, int previousFullListSize ) throws IllegalAccessException, InstantiationException {
    	
		List<String>  charColumns = Arrays.asList(new String[]{"firstname","lastname","email" });
		String[] columns = new String[] {"","firstname","lastname","email","" };
		String upperSort = getUpperSort(charColumns, sort); 
		
		// TODO use RecipientQueryBuilder inside DAO 
    	JdbcTemplate aTemplate = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
    	int totalRows = aTemplate.queryForInt("SELECT count(*) FROM ( " + sqlStatement + " ) agn" );
     	if( previousFullListSize == 0 || previousFullListSize != totalRows ) {
     		page = 1;
     	}
    	 
    	
     	int offset =  ( page - 1) * rownums;  
 	
    	if ( AgnUtils.isMySQLDB()) {
    		sqlStatement = sqlStatement + " LIMIT  " + offset + " , " + rownums;
    	}
    	
    	if ( AgnUtils.isOracleDB()) {
    		sqlStatement = "SELECT * from ( select customer_id, gender, firstname,lastname, email ,rownum r from ( " + sqlStatement + " )  where 1=1 ) where r between " + offset + " and " + ( offset+ rownums );
    	}
    	
    	List<Map> tmpList = aTemplate.queryForList(sqlStatement);
	     DynaProperty[] properties = new DynaProperty[] {
	    		  new DynaProperty("customerid", Integer.class),
	    		  new DynaProperty("gender", Integer.class),
	    		  new DynaProperty("firstname", String.class),
	    		  new DynaProperty("lastname", String.class),
	    		  new DynaProperty("email",String.class)   		  
	      };
	     
	      if( AgnUtils.isOracleDB()) {
	    	  properties = new DynaProperty[] {
		    		  new DynaProperty("customerid", BigDecimal.class),
		    		  new DynaProperty("gender",BigDecimal.class),
		    		  new DynaProperty("firstname", String.class),
		    		  new DynaProperty("lastname", String.class),
		    		  new DynaProperty("email",String.class) 
	      };
	      }
	      
	      BasicDynaClass dynaClass = new BasicDynaClass("recipient", null, properties);
	      List<DynaBean> result = new ArrayList<DynaBean>();
	      for(Map row:tmpList) {
	    	  DynaBean newBean = dynaClass.newInstance();    	
	    	  newBean.set("customerid", row.get("CUSTOMER_ID"));
	    	  newBean.set("gender", row.get("GENDER"));
	    	  newBean.set("firstname", row.get("FIRSTNAME"));
	    	  newBean.set("lastname", row.get("LASTNAME"));
	    	  newBean.set("email",row.get("EMAIL"));
	    	  result.add(newBean);
	      }    
	   
	      DynaBeanPaginatedListImpl paginatedList = new DynaBeanPaginatedListImpl(result, totalRows, rownums, page, sort, direction );
	      return paginatedList;    	
    }
	
	private String getUpperSort(List<String> charColumns, String sort) {
		String upperSort = sort;
		if (charColumns.contains( sort )) {
	    	upperSort =   "upper( " +sort + " )";
	     }
		return upperSort;
	}
	
	

	/**
	 * Holds value of property applicationContext.
	 */
	protected ApplicationContext applicationContext;
    
	/**
	 * Setter for property applicationContext.
	 * @param applicationContext New value of property applicationContext.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Map readDBColumns(int companyID ) {
		String sqlGetTblStruct = "SELECT * FROM customer_" + companyID + "_tbl WHERE 1=0";
		CsvColInfo aCol = null;
		String colType = null;
		Map dbAllColumns = new CaseInsensitiveMap();
		DataSource ds = (DataSource)this.applicationContext.getBean("dataSource");
		Connection con = DataSourceUtils.getConnection(ds);
		try {
			Statement stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery(sqlGetTblStruct);
			ResultSetMetaData meta = rset.getMetaData();

			for (int i = 1; i <= meta.getColumnCount(); i++) {
					if (!meta.getColumnName(i).equals("change_date")
							&& !meta.getColumnName(i).equals("creation_date")
							&& !meta.getColumnName(i).equals("datasource_id")) {
//						if (meta.getColumnName(i).equals("customer_id")) {
//							if (status == null) {
//								initStatus(getWebApplicationContext());
//							}
//							if (!( mode == ImportWizardServiceImpleImpl.MODE_ONLY_UPDATE && status.getKeycolumn().equals("customer_id"))) {
//								continue;
//							}			
//						}

			aCol = new CsvColInfo();
			aCol.setName(meta.getColumnName(i));
			aCol.setLength(meta.getColumnDisplaySize(i));
			aCol.setType(CsvColInfo.TYPE_UNKNOWN);
			aCol.setActive(false);
			colType = meta.getColumnTypeName(i);
			if (colType.startsWith("VARCHAR")) {
				aCol.setType(CsvColInfo.TYPE_CHAR);
			} else if (colType.startsWith("CHAR")) {
				aCol.setType(CsvColInfo.TYPE_CHAR);
			} else if (colType.startsWith("NUM")) {
				aCol.setType(CsvColInfo.TYPE_NUMERIC);
			} else if (colType.startsWith("INTEGER")) {
				aCol.setType(CsvColInfo.TYPE_NUMERIC);
			} else if (colType.startsWith("DOUBLE")) {
				aCol.setType(CsvColInfo.TYPE_NUMERIC);
			} else if (colType.startsWith("TIME")) {
				aCol.setType(CsvColInfo.TYPE_DATE);
			} else if (colType.startsWith("DATE")) {
				aCol.setType(CsvColInfo.TYPE_DATE);
			}
			dbAllColumns.put(meta.getColumnName(i), aCol);
		}
	}
			rset.close();
			stmt.close();
		} catch (Exception e) {
			AgnUtils.logger().error("readDBColumns: " + e);
		}
		DataSourceUtils.releaseConnection(con, ds);
		return dbAllColumns;
	}

	public Set loadBlackList(int companyID) throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate( (DataSource) applicationContext.getBean("dataSource"));
		SqlRowSet rset = null;
		Object[] params = new Object[] { new Integer(companyID) };
	    Set blacklist = new HashSet();
	    try {
	       rset = jdbcTemplate.queryForRowSet("SELECT email FROM cust_ban_tbl WHERE company_id=? OR company_id=0", params);
	     	while (rset.next()) {
	     		blacklist.add(rset.getString(1).toLowerCase());
	     	}
	    } catch (Exception e) {
	       AgnUtils.logger().error("loadBlacklist: "+e);
	       throw new Exception(e.getMessage());
	    }	    
	    
	    return blacklist;
	} 

    public Map<Integer, String> getAdminAndTestRecipientsDescription(int companyId, int mailingId) {
        String sql = "SELECT bind.customer_id, cust.email, cust.firstname, cust.lastname FROM mailing_tbl mail, " +
                "customer_" + companyId + "_tbl cust, customer_" + companyId + "_binding_tbl bind WHERE " +
                "(bind.user_type='A' OR bind.user_type='T') AND bind.user_status=1 AND bind.mailinglist_id=" +
                "mail.mailinglist_id AND bind.customer_id=cust.customer_id and mail.mailing_id=" + mailingId +
                " ORDER BY bind.user_type, bind.customer_id";
        JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
        List<Map> tmpList = jdbcTemplate.queryForList(sql);
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        for (Map map : tmpList) {
            int id = ((Number) map.get("customer_id")).intValue();
            String email = (String) map.get("email");
            String firstName = (String) map.get("firstname");
            String lastName = (String) map.get("lastname");
            result.put(id, firstName + " " + lastName + " &lt;" + email + "&gt;");
        }
        return result;
    }
}
