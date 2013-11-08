package org.agnitas.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetNodeFactory;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.target.TargetRepresentationFactory;
import org.agnitas.target.impl.TargetNodeDate;
import org.agnitas.target.impl.TargetNodeNumeric;
import org.agnitas.target.impl.TargetNodeString;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.RecipientForm;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * Helper-class for building the sql-query in /recipient/list.jsp 
 * @author ms
 *
 */

public class RecipientQueryBuilder {
	
	/**
	 * construct a sql query from all the provided parameters
	 * @param request
	 * @param context
	 * @return
	 */
	public static String getSQLStatement(HttpServletRequest request, ApplicationContext context, RecipientForm aForm, TargetRepresentationFactory targetRepresentationFactory, TargetNodeFactory targetNodeFactory, boolean optimized) {
		 // helps displaytag-sorting
		 List<Integer>  charColumns = Arrays.asList(new Integer[]{1,2,3 });

		 String sort = request.getParameter("sort");
		 if(sort == null) {
			 sort = aForm.getSort();
		 }
		
		 String upperSort = sort;
		 if(charColumns.contains(sort)) {
	    	upperSort =  "upper(" + sort + ")";
	     }		 
     	
      	String direction = request.getParameter("dir");
		if(direction == null) {
			direction = aForm.getOrder();
		}
      	
		
		
		
		// stuff from JSP <%...%>
		int mailingListID = 0;
	    int targetID;
	    String user_type = null;
	    int user_status = 0;
		String firstName;
		String lastName;
		String email;

	        	
	    if(request.getParameter("listID") != null) {
	    	aForm.setListID( Integer.parseInt(request.getParameter("listID")));
	    }
	    mailingListID = aForm.getListID();

	    if(request.getParameter("targetID") != null) {
	    	aForm.setTargetID(Integer.parseInt(request.getParameter("targetID")) );
	    }
	    targetID = aForm.getTargetID();

	    if(request.getParameter("user_type")!=null){   
	       aForm.setUser_type(request.getParameter("user_type"));
	    }	
	    user_type = aForm.getUser_type();

		if(request.getParameter("searchFirstName")!=null){
	       aForm.setSearchFirstName(request.getParameter("searchFirstName"));
	    }
	    firstName = aForm.getSearchFirstName();

		if(request.getParameter("searchLastName")!=null){
	       aForm.setSearchLastName(request.getParameter("searchLastName"));
	    }
	    lastName = aForm.getSearchLastName();

		if(request.getParameter("searchEmail")!=null){
	       aForm.setSearchEmail(request.getParameter("searchEmail"));
	    }
	    email = aForm.getSearchEmail();
	    
	    if(request.getParameter("user_status")!=null){
	        aForm.setUser_status(Integer.parseInt(request.getParameter("user_status")));
	    } 
	    user_status = aForm.getUser_status();
	    
	    /*
	    RecipientForm rec = (RecipientForm) request.getSession().getAttribute("recipientForm");
	    TargetRepresentation targetRep=rec.getTarget();
	    */
	    TargetRepresentation targetRep = createTargetRepresentationFromForm(aForm, targetRepresentationFactory, targetNodeFactory);
		Vector<String>  condition = new Vector<String>();
		
		if((user_type != null) && (user_type.compareTo("E") != 0 )) {
			condition.add("bind.USER_TYPE ='" + user_type + "'");
		}
		
		if(user_status != 0) {
			condition.add("bind.user_status =" + user_status);
		}
		
		if(targetID != 0) {
			TargetDao dao = (TargetDao) context.getBean("TargetDao");
			Target target = dao.getTarget(targetID,	AgnUtils.getCompanyID(request));
			condition.add(target.getTargetSQL());
		}
		
		if(mailingListID != 0) {
			condition.add("bind.mailinglist_id=" + mailingListID);
		}

		if(!StringUtils.isEmpty(firstName)) {
			condition.add("cust.firstname='" + firstName + "'");
		}

		if(!StringUtils.isEmpty(lastName)) {
			condition.add("cust.lastname='" + lastName + "'");
		}

		if(!StringUtils.isEmpty(email)) {
			condition.add("cust.email='" + email.toLowerCase() + "'");
		}
		
		if(targetRep.generateSQL().length() > 0 && targetRep.checkBracketBalance()) {
			condition.add(targetRep.generateSQL());
		}

		
		// AGNEMM-336
		if(AgnUtils.isOracleDB() && optimized) {
			
			int maxRownum = 20;
            if (!StringUtils.isEmpty(aForm.getPage()) && StringUtils.isNumeric(aForm.getPage())) {
								
				maxRownum = ((Integer.parseInt(aForm.getPage()) - 1) * aForm.getNumberofRows()) + aForm.getNumberofRows(); 
				
				maxRownum++;
			}
			condition.add("rownum < " + maxRownum );
		}
				
		String sql="select cust.customer_id, cust.gender, cust.firstname, cust.lastname, cust.email FROM customer_" + AgnUtils.getCompanyID(request) + "_tbl cust";
		
		if(condition.size() > 0) {
			Iterator<String> i=condition.iterator();
			String  custWhere = "";
			String  bindWhere = "";
		
			while(i.hasNext()) {
				String s = i.next();
		
				if(s.indexOf("bind.")!=-1) {
					bindWhere += " and "+ s;
				} else {
					custWhere += " and "+ s;
				}
			}
			sql += " where ";
			if(custWhere.length() > 0) {
				sql += custWhere.substring(5);
				if(bindWhere.length() > 0) {
					sql += " and ";
				}
			}
			if(bindWhere.length() > 0) {
				sql += "cust.customer_id in (select customer_id from customer_" + AgnUtils.getCompanyID(request) + "_binding_tbl bind where ";
				sql += bindWhere.substring(5);
				sql += ")";
			}
		} 
				
		String sqlStatement = sql;
	    sqlStatement = sqlStatement.replaceAll("cust[.]bind", "bind");
	    sqlStatement = sqlStatement.replace("lower(cust.email)", "cust.email");
	    //sqlStatement = sqlStatement.replaceAll("lower(cust.email)", "cust.email");
	    
	    if(sort != null && !"".equals(sort.trim())) {
	    	sqlStatement += " ORDER BY " + upperSort + " " + direction;
	    }
	    
	    return sqlStatement; 
	}
	
	private static TargetRepresentation createTargetRepresentationFromForm(RecipientForm form, TargetRepresentationFactory targetRepresentationFactory, TargetNodeFactory targetNodeFactory) {
        TargetRepresentation target = targetRepresentationFactory.newTargetRepresentation();
       
        int lastIndex = form.getNumTargetNodes(); 
       
        for(int index = 0; index < lastIndex; index++) {
    		String colAndType = form.getColumnAndType(index);
    		String column = colAndType.substring(0, colAndType.indexOf('#'));
    		String type = colAndType.substring(colAndType.indexOf('#') + 1);
    		
    		TargetNode node = null;
    		
    		if (type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("VARCHAR2") || type.equalsIgnoreCase("CHAR")) {
    			node = createStringNode(form, column, type, index, targetNodeFactory);
    		} else if (type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("DOUBLE") || type.equalsIgnoreCase("NUMBER")) {
    			node = createNumericNode(form, column, type, index, targetNodeFactory);
    		} else if (type.equalsIgnoreCase("DATE")) {
    			node = createDateNode(form, column, type, index, targetNodeFactory);
    		}
    		
            target.addNode(node);
        }
        
        return target;
	}
	
	private static TargetNodeString createStringNode(RecipientForm form, String column, String type, int index, TargetNodeFactory factory) {
		return factory.newStringNode(
				form.getChainOperator(index), 
				form.getParenthesisOpened(index), 
				column, 
				type, 
				form.getPrimaryOperator(index), 
				form.getPrimaryValue(index), 
				form.getParenthesisClosed(index));
	}
	
	private static TargetNodeNumeric createNumericNode(RecipientForm form, String column, String type, int index, TargetNodeFactory factory) {
		int primaryOperator = form.getPrimaryOperator(index);
		int secondaryOperator = form.getSecondaryOperator(index);
		int secondaryValue = 0;
		
    	if(primaryOperator == TargetNode.OPERATOR_MOD.getOperatorCode()) {
            try {
                secondaryOperator = Integer.parseInt(form.getSecondaryValue(index));
            } catch (Exception e) {
                secondaryOperator = TargetNode.OPERATOR_EQ.getOperatorCode();
            }
            try {
                secondaryValue = Integer.parseInt(form.getSecondaryValue(index));
            } catch (Exception e) {
                secondaryValue = 0;
            }
        }
		
		return factory.newNumericNode(
				form.getChainOperator(index), 
				form.getParenthesisOpened(index), 
				column, 
				type, 
				primaryOperator, 
				form.getPrimaryValue(index), 
				secondaryOperator, 
				secondaryValue, 
				form.getParenthesisClosed(index));
	}
	
	private static TargetNodeDate createDateNode(RecipientForm form, String column, String type, int index, TargetNodeFactory factory) {
		return factory.newDateNode(
				form.getChainOperator(index), 
				form.getParenthesisOpened(index), 
				column, 
				type, 
				form.getPrimaryOperator(index), 
				form.getDateFormat(index), 
				form.getPrimaryValue(index), 
				form.getParenthesisClosed(index));
	}
}