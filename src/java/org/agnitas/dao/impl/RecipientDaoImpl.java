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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.ListIterator;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.agnitas.util.CsvColInfo;
import org.agnitas.web.ImportWizardForm;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.beans.DatasourceDescription;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.RecipientDao;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

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
					maxRecipient=new Integer(AgnUtils.getDefaultIntValue("recipient.maxRows"));
				}	
			}
		}
		if(maxRecipient == null) {
			return 0;
		}
		return maxRecipient.intValue();
	}

	public boolean	mayAdd(int companyID, int count) {
                JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                String sql = "select count(customer_id) from customer_" + companyID + "_tbl";
		int current=jdbc.queryForInt(sql);
		int max=getMaxRecipient();

		if(max == 0 || current+count < max) {
			return true;
		}
		return false;
	}

	public boolean	isNearLimit(int companyID, int count) {
                JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                String sql = "select count(customer_id) from customer_" + companyID + "_tbl";
		int current=jdbc.queryForInt(sql);
		int max=(int) (getMaxRecipient()*0.9);

		if(max == 0 || current+count < max) {
			return false;
		}
		return true;
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
        StringBuffer insertCust=new StringBuffer("INSERT INTO customer_" + cust.getCompanyID() + "_tbl ");
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
									appendValue = new String("STR_TO_DATE('"+ aFormat1.format(day) +"."+aFormat1.format(month)+"."+aFormat2.format(year)+" "+ aFormat1.format(hour)+":"+aFormat1.format(minute)+":"+aFormat1.format(second)+"',  '%d.%m.%Y %H:%i:%s')");
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
                result=false;
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
                                	day = Integer.parseInt((String) cust.getCustParameters().get(aColumn+"_DAY_DATE"));
                                    month = Integer.parseInt((String) cust.getCustParameters().get(aColumn+"_MONTH_DATE"));
                                    year = Integer.parseInt((String) cust.getCustParameters().get(aColumn+"_YEAR_DATE"));
                                    if (AgnUtils.isOracleDB()) {
                                        appendValue = new String(aColumn.toLowerCase() + "=to_date('" + aFormat1.format(day) + "-" + aFormat1.format(month) + "-" + aFormat2.format(year) + "', 'DD-MM-YYYY')");
                                    } else {
                                    	appendValue = new String(aColumn.toLowerCase() + "=STR_TO_DATE('" + aFormat1.format(day) + "-" + aFormat1.format(month) + "-" + aFormat2.format(year) + "',  '%d-%m-%Y')");
                                    }
                                    appendIt = true;
                                } else {
                                	Map tmp = (Map) cust.getCustDBProfileStructure().get(aColumn);
                                	if (tmp != null) {
                                		String defaultValue = (String)tmp.get("default");
                                		if (!isBlank(defaultValue) && !defaultValue.equals("null")) {
                                            appendValue = aColumn.toLowerCase()+"='" + defaultValue + "'";
                                            hasDefault = true;
                                		}
                                	}
                                	if (!hasDefault) {
                                		appendValue=new String(aColumn.toLowerCase()+"=null");
                                	}
                                    appendIt=true;
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
                        	if ( tmp != null ) {
                        		String defaultValue = (String)tmp.get( "default" );
                        		if (!isBlank(defaultValue)) {
                                    appendValue = aColumn.toLowerCase()+"=" + defaultValue;
                                    hasDefault = true;
                        		}
                        	}
                        	if ( !hasDefault ) {
                        		appendValue=new String(aColumn.toLowerCase() + "=null");
                        	}
                            appendIt=true;
                        }

                    } else if(colType.equalsIgnoreCase("DOUBLE")) {
                        double dValue;

                        aParameter = (String) cust.getCustParameters(aColumn);
                        if(!StringUtils.isEmpty(aParameter)){
                            try {
                                dValue = Double.parseDouble(aParameter);
                            } catch (Exception e1) {
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
                        	if ( tmp != null ) {
                        		String defaultValue = (String)tmp.get( "default" );
                        		if (!isBlank(defaultValue)) {
                                    appendValue = aColumn.toLowerCase()+"='" + defaultValue + "'";
                                    hasDefault = true;
                        		}
                        	}
                        	if ( !hasDefault ) {
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
                    JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                    AgnUtils.logger().info("updateInDB: " + updateCust.toString());
                    tmpl.execute(updateCust.toString());
                    
                    if(cust.getCustParameters("DATASOURCE_ID") != null) {
                    	String sql = "select datasource_id from customer_" + cust.getCompanyID() + "_tbl where customer_id = ?";
                    	List list = tmpl.queryForList(sql, new Object[] {new Integer(cust.getCompanyID())});
                    	Iterator id = list.iterator();
                    	if(!id.hasNext()) {
                    		aParameter=(String) cust.getCustParameters("DATASOURCE_ID");
                    		if(!StringUtils.isEmpty(aParameter)){
                    			try {
                    				intValue=Integer.parseInt(aParameter);
                    				sql = "update customer_" + cust.getCompanyID() + "_tbl set datasource_id = " + intValue + " where customer_id = " + cust.getCustomerID();
                    				tmpl.execute(sql);
                    			} catch (Exception e1) {}
                    		}
                    	}
                    }
                } catch (Exception e3) {
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
                	Map  map=new CaseInsensitiveMap(custList.get(0));

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
        String getCust=null;
        int customerID = 0;

        if(userCol.toLowerCase().equals("email")) {
            userValue=userValue.toLowerCase();
        }

        getCust="SELECT customer_id FROM customer_" + companyID + "_tbl cust WHERE cust."+SafeString.getSQLSafeString(userCol, 30)+"='"+SafeString.getSQLSafeString(userValue)+"' AND cust."+SafeString.getSQLSafeString(passCol, 30)+"='"+SafeString.getSQLSafeString(passValue)+"'";

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
					aTime=null;
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
                                cust.getCustParameters().put(aName + "_MONTH_DATE", Integer.toString(aCal.get(GregorianCalendar.MONTH)+1));
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

                aEntry=(BindingEntry) applicationContext.getBean("BindingEntry");
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
            AgnUtils.logger().error("loadAllListBindings: "+e.getMessage());
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
        boolean returnValue=false;
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

	public String	getField(String selectVal, int recipientID, int companyID)	{
		JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
		String sql="SELECT "+selectVal+" value FROM customer_"+companyID+"_tbl cust WHERE cust.customer_id=?";

		try {
			List list=jdbc.queryForList(sql, new Object[]{ new Integer(recipientID)});

			if(list.size() > 0) {
				Map map=(Map) list.get(0);
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

	public Map<Integer, Map>	getAllMailingLists(int customerID, int companyID) {
		Map<Integer, Map>	result=new HashMap();
		String sql="SELECT mailinglist_id, user_type, user_status, user_remark, "+AgnUtils.changeDateName()+", mediatype FROM customer_" + companyID + "_binding_tbl WHERE customer_id=? ORDER BY mailinglist_id, mediatype";
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		AgnUtils.logger().info("getAllMailingLists: "+sql);

		try	{
			List list=jdbc.queryForList(sql, new Object[]{new Integer(customerID)});
			Iterator i=list.iterator();
			BindingEntry entry=null;

			while(i.hasNext()) {
				Map map=(Map) i.next();
				int listID=((Number) map.get("mailinglist_id")).intValue();
				int mediaType=((Number) map.get("mediatype")).intValue();
				Map sub=(Map) result.get(new Integer(listID));

				if(sub == null) {
					sub=new HashMap();
				}
				entry=(BindingEntry) applicationContext.getBean("BindingEntry");
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
			AgnUtils.logger().error("getAllMailingLists: "+e.getMessage());
		}
		return result;
	}

	public boolean createImportTables(int companyID, int datasourceID, CustomerImportStatus status) {
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String prefix = "cust_" + companyID + "_tmp";
		String tabName = prefix+datasourceID+"_tbl";
		String keyIdx = prefix+datasourceID+"$KEYCOL$IDX";
		String custIdx = prefix+datasourceID+"$CUSTID$IDX";
		String sql=null;

		try {
			sql="create temporary table "+tabName+" as (select * from customer_" + companyID + "_tbl where 1=0)";
			jdbc.execute(sql);

			sql="alter table "+tabName+" modify change_date timestamp null default null";
			jdbc.execute(sql);

			sql="alter table "+tabName+" modify creation_date timestamp null default current_timestamp";
			jdbc.execute(sql);

			sql="create index " + keyIdx + " on "+tabName+" ("+SafeString.getSQLSafeString(status.getKeycolumn())+")";
			jdbc.execute(sql);

			sql="create index " + custIdx +" on "+tabName+" (customer_id)";
			jdbc.execute(sql);
		}   catch (Exception e) {
			AgnUtils.logger().error("createTemporaryTables: "+e.getMessage());
			AgnUtils.logger().error("Statement: "+sql);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean deleteImportTables(int companyID, int datasourceID) {
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String tabName = "cust_" + companyID + "_tmp"+datasourceID+"_tbl";

		if(AgnUtils.isOracleDB()) {
			try {
				jdbc.execute("drop table "+tabName);
			} catch (Exception e) {
				AgnUtils.logger().error("deleteTemporaryTables: "+e.getMessage());
				AgnUtils.logger().error("Table: "+tabName);
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
		HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)applicationContext.getBean("sessionFactory"));
		DatasourceDescription dsDescription=(DatasourceDescription) applicationContext.getBean("DatasourceDescription");

		dsDescription.setId(0);
		dsDescription.setCompanyID(companyID);
		dsDescription.setSourcegroupID(2);
		dsDescription.setCreationDate(new java.util.Date());
		dsDescription.setDescription(description);
		tmpl.save("DatasourceDescription", dsDescription);
		return dsDescription;
	}

	private void createTemporaryTables(ImportWizardForm aForm, int companyID) {
		createImportTables(companyID, aForm.getDatasourceID(), aForm.getStatus());
	}

	private void deleteTemporaryTables(ImportWizardForm aForm, int companyID) {
		deleteImportTables(companyID, aForm.getDatasourceID());
	}

	/** 
	 * Writes new Subscriber-Data through temporary tables to DB
	 *
	 * @param aForm InputForm for actual import process
	 * @param jdbc valid JdbcTemplate to build temporary tables on
	 * @param req The HttpServletRequest that caused this action
	 */
	public void writeContent(ImportWizardForm aForm, int companyID) {
                JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		HibernateTransactionManager tm=(HibernateTransactionManager) (applicationContext.getBean("transactionManager"));
		String currentTimestamp=AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName();
		StringBuffer usedColumnsString=new StringBuffer();
		StringBuffer copyColumnsString=new StringBuffer();
		DefaultTransactionDefinition tdef=new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus ts=null;
		ListIterator aIt=null;
		CsvColInfo aInfo=null;
		DatasourceDescription dsDescription=getNewDatasourceDescription(companyID, aForm.getCsvFile().getFileName());
		
		aForm.getStatus().setDatasourceID(dsDescription.getId());
		
		tm.setDataSource(jdbc.getDataSource());
		AgnUtils.logger().info("Starting transaction");
		ts=tm.getTransaction(tdef);
		try {
			this.createTemporaryTables(aForm, companyID);
			
			int errorsOnInsert=0;
			StringBuffer errorLines=new StringBuffer();
			
			// CUSTOMER_XX_TBL inserts:
			String customer_body = "INSERT INTO cust_" + companyID + "_tmp"+dsDescription.getId()+"_tbl ( datasource_id, change_date, creation_date";
			
			ArrayList usedColumns=new ArrayList();
			aIt=aForm.getCsvAllColumns().listIterator();
			int numFields=0;
			while (aIt.hasNext()) {
				aInfo=(CsvColInfo)aIt.next();
				
				if(aForm.getColumnMapping().containsKey(aInfo.getName())) {
					String curCol=((CsvColInfo)aForm.getColumnMapping().get(aInfo.getName())).getName();
					customer_body += ", " + curCol;
					numFields++;
					usedColumns.add(aInfo);
					usedColumnsString.append(curCol+", ");
					copyColumnsString.append("cust." + curCol + "=temp." + curCol + ", ");
				}
			}
			
			customer_body += " ) VALUES " + "(" + aForm.getDatasourceID() + ", "+currentTimestamp+ ", "+currentTimestamp;
			for(int a=1; a<=numFields; a++) {
				customer_body+=", ?";
			}
			customer_body+=")";
			// values:
			int x=0;
			Object aVal=null;
			try {
				ListIterator contentIterator=aForm.getParsedContent().listIterator();
				LinkedList aLine=null;
				
				while(contentIterator.hasNext()) {
					try {
						Vector params=new Vector();
						
						aLine=(LinkedList)contentIterator.next();
						for(int a=0; a<numFields; a++) {
							aInfo=(CsvColInfo)usedColumns.get(a);
							aVal=aLine.get(a);
							if(aInfo.getType()==CsvColInfo.TYPE_CHAR) {
								params.add((String)aVal);
							} else if(aInfo.getType()==CsvColInfo.TYPE_NUMERIC) {
								if(aVal!=null) {
									params.add(new Double(((Double)aVal).doubleValue()));
								} else {
									params.add(new Integer(0));
								}
							} else if(aInfo.getType()==CsvColInfo.TYPE_DATE) {
								if(aVal!=null) {
									params.add((java.util.Date)aVal);
								} else {
									params.add(new Integer(0));
								}
							}
						}
						jdbc.update(customer_body, params.toArray());
					} catch (Exception e1) {
						errorsOnInsert++;
						AgnUtils.logger().error("writeContent: "+e1);
						e1.printStackTrace();
					}
					aForm.setDbInsertStatus((int)((((double)x)/aForm.getLinesOK())*100.0));
					x++;
				}
			} catch (Exception e) {
				AgnUtils.logger().error("writeContent: "+e);
				e.printStackTrace();
			}
			
			aForm.setError(ImportWizardForm.DBINSERT_ERROR, errorLines.toString());
			tm.commit(ts);
		}   catch (Exception e) {
			tm.rollback(ts);
			AgnUtils.logger().error("writeContent: "+e);
			e.printStackTrace();
		}
		
		ts=tm.getTransaction(tdef);
		try {
			String sql=null;
			
			if(aForm.getStatus().getDoubleCheck() == CustomerImportStatus.DOUBLECHECK_FULL) {
				try {
					sql = "UPDATE cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp SET customer_id = (SELECT customer_id FROM customer_" + companyID + "_tbl cust WHERE cust." + SafeString.getSQLSafeString(aForm.getStatus().getKeycolumn()) + "=temp." + SafeString.getSQLSafeString(aForm.getStatus().getKeycolumn()) + " LIMIT 1), datasource_id=0 WHERE temp." + SafeString.getSQLSafeString(aForm.getStatus().getKeycolumn()) + " in (SELECT " + SafeString.getSQLSafeString(aForm.getStatus().getKeycolumn()) + " FROM customer_" + companyID + "_tbl)";
					aForm.setDbInsertStatus(200);
					aForm.addDbInsertStatusMessage("csv_delete_double_email");
					jdbc.execute(sql);
					
				} catch (Exception e) {
					AgnUtils.logger().error("writeContent: "+e);
					AgnUtils.logger().error("Statement: "+sql);
					e.printStackTrace();
				}
				
			}
			
			aForm.getStatus().setInserted(0);
			aForm.getStatus().setUpdated(0);
			try {
				sql = "SELECT count(temp.datasource_id) FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp WHERE datasource_id<>0";
				aForm.getStatus().setInserted(jdbc.queryForInt(sql));
				
				sql = "SELECT count(temp.datasource_id) FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp WHERE datasource_id=0";
				aForm.getStatus().setUpdated(jdbc.queryForInt(sql));
			} catch (Exception e) {
				AgnUtils.logger().error("writeContent: "+e);
				AgnUtils.logger().error("Statement: "+sql);
				e.printStackTrace();
			}
			
			if(aForm.getMode()==ImportWizardForm.MODE_ADD_UPDATE || aForm.getMode()==ImportWizardForm.MODE_ONLY_UPDATE) {
				// update existing records
				if(aForm.getStatus().getIgnoreNull()==ImportWizardForm.MODE_DONT_IGNORE_NULL_VALUES) {
					try {
						String tempSubTabName = "cust_" + companyID + "_tmp2_sub" + aForm.getDatasourceID() + "_tbl";
						sql="CREATE TEMPORARY TABLE "+tempSubTabName+" AS (SELECT * from cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl tmp WHERE tmp.datasource_id=0)";
						jdbc.execute(sql);
						
						sql = "UPDATE " +
									"customer_" + companyID + "_tbl cust, " +
									"cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp " +
								"SET " +
									copyColumnsString.toString()+ "cust." + AgnUtils.changeDateName() + "=" + currentTimestamp + " " +
								"WHERE " +
									"temp.customer_id=cust.customer_id " +
								"AND " +
									"cust.customer_id in " +
										"(SELECT subtmp.customer_id from " + tempSubTabName + " subtmp)";
						aForm.setDbInsertStatus(250);
						aForm.addDbInsertStatusMessage("import.update_existing_records");
						jdbc.execute(sql);
						sql = "DROP TABLE " + tempSubTabName;
					}   catch (Exception e) {
						AgnUtils.logger().error("writeContent: "+e);
						AgnUtils.logger().error("Statement: "+sql);
						e.printStackTrace();
					}
				} else {
					
					aForm.setDbInsertStatus(250);
					aForm.addDbInsertStatusMessage("import.update_existing_records");
					
					aIt=aForm.getCsvAllColumns().listIterator();
					
					aInfo=null;
					
					try {
						while (aIt.hasNext()) {
							aInfo=(CsvColInfo)aIt.next();
							if(aForm.getColumnMapping().containsKey(aInfo.getName())) {
								aInfo.getName();
								String tempSubTabName = "cust_" + companyID + "_tmp_3_sub" + aForm.getDatasourceID() + "_tbl";
								sql="CREATE TEMPORARY TABLE "+tempSubTabName+" AS (SELECT customer_id from cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl tmp WHERE datasource_id=0 AND "+((CsvColInfo)aForm.getColumnMapping().get(aInfo.getName())).getName()+" is not null)";
								jdbc.execute(sql);
								sql = "UPDATE customer_" + companyID + "_tbl cust SET "+ ((CsvColInfo)aForm.getColumnMapping().get(aInfo.getName())).getName() +" = (SELECT "+((CsvColInfo)aForm.getColumnMapping().get(aInfo.getName())).getName() + " FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl temp WHERE cust.customer_id=temp.customer_id), " + AgnUtils.changeDateName() + "=" + currentTimestamp + " WHERE cust.customer_id in (SELECT customer_id from " + tempSubTabName + " )";
								jdbc.execute(sql);
								sql = "DROP TABLE " + tempSubTabName;
								jdbc.execute(sql);
								
							}
						}
					}   catch (Exception e) {
						AgnUtils.logger().error("writeContent: "+e);
						AgnUtils.logger().error("Statement: "+sql);
						e.printStackTrace();
					}
				}
				
			}
			
			// Move CUSTOMER_XX_TEMP_TBL contents into CUSTOMER_XX_TBL
			// only if adding some subscribers
			if(aForm.getMode()==ImportWizardForm.MODE_ADD || aForm.getMode()==ImportWizardForm.MODE_ADD_UPDATE) {
				try {
					sql = "select count(customer_id) from cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id<>0";
					sql = "INSERT INTO customer_" + companyID + "_tbl ("+usedColumnsString.toString()+"datasource_id, customer_id, change_date, creation_date) SELECT "+usedColumnsString.toString()+"datasource_id, customer_id, change_date, creation_date FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id<>0";
					aForm.setDbInsertStatus(300);
					aForm.addDbInsertStatusMessage("import.save_new_records");
					jdbc.execute(sql);
					sql = "SELECT max( cust.customer_id ) max_cust, max( cust_seq.customer_id) max_seq from customer_" + companyID + "_tbl_seq cust_seq, customer_" + companyID + "_tbl cust";
					List<Map<String, Number>> maxIds = jdbc.queryForList( sql );
					if ( maxIds.size() > 0 ) {
						long maxCust = maxIds.get(0).get("max_cust").longValue();
						Number maxSeqNumber = maxIds.get(0).get("max_seq");
						long maxSeq = ( maxSeqNumber != null ) ? maxSeqNumber.longValue() : 0;
						if ( maxCust > maxSeq ) {
							sql = "INSERT INTO customer_" + companyID + "_tbl_seq (customer_id) SELECT max(customer_id) FROM customer_" + companyID + "_tbl";
							jdbc.execute(sql);
						}
					}
				}   catch (Exception e) {
					AgnUtils.logger().error("writeContent: "+e);
					AgnUtils.logger().error("Statement: "+sql);
					e.printStackTrace();
				}
				
			}
			
			// BINDINGS (for inserted subscribers only, not updating existing bindings):
			String bindingStmt=null;
			String binding2=null;
			String tmpTblCreate=null;
			String tmpTblRemove=null;
			String tmpTblStat=null;
			String optout=null;
			String bounce=null;
			int mailinglistAdd=0;
			Hashtable mailinglistStat=new Hashtable();
			Enumeration mailingLists=aForm.getMailingLists().elements();
			
			aForm.addDbInsertStatusMessage("import.update_status");
			
			while(mailingLists.hasMoreElements()) {
				Object aObject=mailingLists.nextElement();
				
				try {
					switch(aForm.getMode()) {
						case ImportWizardForm.MODE_ADD:
						case ImportWizardForm.MODE_ADD_UPDATE:
							aForm.setDbInsertStatus(350);
							mailinglistAdd=aForm.getStatus().getInserted();
							bindingStmt = new String("INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) (SELECT customer_id, 'W', 1, 'CSV File Upload', "+currentTimestamp+", 0," + aObject + " FROM customer_" + companyID + "_tbl WHERE datasource_id = " + aForm.getDatasourceID() + ")");
							jdbc.execute(bindingStmt);
							tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
							jdbc.execute(tmpTblCreate);
							tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+")");
							jdbc.execute(tmpTblRemove);
							tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
							mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
							binding2=new String("INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) (SELECT customer_id, 'W', 1, 'CSV File Upload', "+currentTimestamp+", 0," + aObject + " FROM cust_" + companyID + "_exist1_tmp"+aForm.getDatasourceID()+"_tbl)");
							jdbc.execute(binding2);
							mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
							break;
							
						case ImportWizardForm.MODE_ONLY_UPDATE:
							aForm.setDbInsertStatus(350);
							mailinglistAdd=0;
							tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
							jdbc.execute(tmpTblCreate);
							tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+")");
							jdbc.execute(tmpTblRemove);
							tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
							mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
							binding2="INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) (SELECT customer_id, 'W', 1, 'CSV File Upload', "+currentTimestamp+", 0," + aObject + " FROM cust_" + companyID + "_exist1_tmp"+aForm.getDatasourceID()+"_tbl)";
							jdbc.execute(binding2);
							mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
							break;
							
						case ImportWizardForm.MODE_UNSUBSCRIBE:
						case ImportWizardForm.MODE_BLACKLIST:
							aForm.setDbInsertStatus(350);
							mailinglistAdd=0;
							tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
							jdbc.execute(tmpTblCreate);
							tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+" AND user_status<>1)");
							jdbc.execute(tmpTblRemove);
							tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
							mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
							optout=new String("UPDATE customer_" + companyID + "_binding_tbl SET user_status="+BindingEntry.USER_STATUS_ADMINOUT+", exit_mailing_id=0, user_remark='Mass Opt-Out by Admin', " + AgnUtils.changeDateName() + "=now() WHERE customer_id IN (SELECT customer_id FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl)");
							jdbc.execute(optout);
							mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
							break;
							
						case ImportWizardForm.MODE_BOUNCE:
							aForm.setDbInsertStatus(350);
							mailinglistAdd=0;
							tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
							jdbc.execute(tmpTblCreate);
							tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+" AND user_status<>1)");
							jdbc.execute(tmpTblRemove);
							tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
							mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
							bounce=new String("UPDATE customer_" + companyID + "_binding_tbl SET user_status="+BindingEntry.USER_STATUS_BOUNCED+", exit_mailing_id=0, user_remark='Mass Bounce by Admin' WHERE customer_id IN (SELECT customer_id FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl)");
							jdbc.execute(bounce);
							mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
							break;
							
						case ImportWizardForm.MODE_REMOVE_STATUS:
							aForm.setDbInsertStatus(350);
							mailinglistAdd=0;
							tmpTblCreate=new String("CREATE TEMPORARY TABLE cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl AS (SELECT customer_id FROM cust_" + companyID + "_tmp"+aForm.getDatasourceID()+"_tbl WHERE datasource_id=0)");
							jdbc.update(tmpTblCreate);
							tmpTblRemove=new String("DELETE FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl WHERE customer_id IN (SELECT customer_id FROM customer_"+companyID+"_binding_tbl WHERE mailinglist_id="+aObject+" AND mediatype=0 AND user_status<>1)");
							jdbc.execute(tmpTblRemove);
							tmpTblStat=new String("SELECT count(*) FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl");
							mailinglistAdd+=jdbc.queryForInt(tmpTblStat);
							bounce=new String("DELETE FROM customer_" + companyID + "_binding_tbl WHERE mailinglist_id=" + aObject + " AND customer_id IN (SELECT customer_id FROM cust_"+companyID+"_exist1_tmp"+aForm.getDatasourceID()+"_tbl)");
							jdbc.execute(bounce);
							mailinglistStat.put(aObject, Integer.toString(mailinglistAdd));
							break;
					}
				}   catch (Exception f) {
					AgnUtils.logger().error("writeContent: "+f);
					f.printStackTrace();
				}
			}
			
			aForm.setResultMailingListAdded(mailinglistStat);
			tm.commit(ts);
			this.deleteTemporaryTables(aForm, companyID);
		}   catch (Exception e) {
			tm.rollback(ts);
			AgnUtils.logger().error("writeContent: "+e);
			e.printStackTrace();
		}
		
		aForm.addDbInsertStatusMessage("csv_completed");
		aForm.setDbInsertStatus(1000);
		
		aForm.setParsedContent(null);
		aForm.setParsedData(null);
		aForm.setCsvAllColumns(null);
		aForm.getErrorMap().remove(ImportWizardForm.BLACKLIST_ERROR);
		aForm.getErrorMap().remove(ImportWizardForm.DATE_ERROR);
		aForm.getErrorMap().remove(ImportWizardForm.EMAIL_ERROR);
		aForm.getErrorMap().remove(ImportWizardForm.MAILTYPE_ERROR);
		aForm.getErrorMap().remove(ImportWizardForm.NUMERIC_ERROR);
		aForm.getErrorMap().remove(ImportWizardForm.STRUCTURE_ERROR);

		aForm.setCsvFile(null);
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
}
