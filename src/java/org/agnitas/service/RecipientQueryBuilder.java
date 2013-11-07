package org.agnitas.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.RecipientForm;
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
	public static String getSQLStatement(HttpServletRequest request, ApplicationContext context,RecipientForm aForm) {
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
	    
	    if(request.getParameter("user_status")!=null){
	        aForm.setUser_status(Integer.parseInt(request.getParameter("user_status")));
	    } 
	    user_status = aForm.getUser_status();
	    
	    RecipientForm rec = (RecipientForm) request.getSession().getAttribute("recipientForm");
	    TargetRepresentation targetRep=rec.getTarget();
		Vector  condition=new Vector();
		
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
		
		if(targetRep.generateSQL().length() > 0 && targetRep.checkBracketBalance()) {
			condition.add(targetRep.generateSQL());
		}

		String sql="select cust.customer_id, cust.gender, cust.firstname, cust.lastname, cust.email FROM customer_" + AgnUtils.getCompanyID(request) + "_tbl cust";
		
		if(condition.size() > 0) {
			Iterator i=condition.iterator();
			String  custWhere = "";
			String  bindWhere = "";
		
			while(i.hasNext()) {
				String  s=(String) i.next();
		
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
}