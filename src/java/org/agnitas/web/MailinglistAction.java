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

package org.agnitas.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.impl.DynaBeanPaginatedListImpl;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.dao.BindingEntryDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public final class MailinglistAction extends StrutsActionBase {
    class MListCompare implements Comparator {
        public int compare (Object o1, Object o2) {
            Mailinglist m1 = (Mailinglist) o1,
                    m2 = (Mailinglist) o2;
            
            return m2.getId () - m1.getId ();
        }
    }
        
    // --------------------------------------------------------- Public Methods

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param form
     * @param req
     * @param res
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        MailinglistForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;

        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }

        if(form!=null) {
            aForm=(MailinglistForm)form;
        } else {
            aForm=new MailinglistForm();
        }


        AgnUtils.logger().info("Action: "+aForm.getAction());
        if(req.getParameter("delete.x")!=null) {
            aForm.setAction(ACTION_CONFIRM_DELETE);
        }

        try {
            switch(aForm.getAction()) {
                case MailinglistAction.ACTION_LIST:
                    if(allowed("mailinglist.show", req)) {
                        if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
                    	destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailinglistAction.ACTION_VIEW:
                    if(allowed("mailinglist.show", req)) {
                        loadMailinglist(aForm, req);
                        aForm.setAction(MailinglistAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailinglistAction.ACTION_NEW:
                    if(allowed("mailinglist.new", req)) {
                        aForm.setMailinglistID(0);
                        aForm.setAction(MailinglistAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                        
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;


                case MailinglistAction.ACTION_SAVE:
                    if(allowed("mailinglist.change", req)) {
                        String targetId = req.getParameter( "targetID" );
                    	if(req.getParameter("save.x")!=null) {
                    		// Always go back to overview	
                    		destination = mapping.findForward("list");
                    		saveMailinglist(aForm, req);
                    		
                            if ( StringUtils.isNotEmpty( targetId ) ) {
                            	// create MailingList from Target
                            	createMailingListFromTarget( targetId, req, aForm );
    						}
                            
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailinglistAction.ACTION_CONFIRM_DELETE:
                    if(allowed("mailinglist.delete", req)) {
                        
                        loadMailinglist(aForm, req);
                        MailingDao mDao=(MailingDao) getBean("MailingDao");
                        List mlids=mDao.getMailingsForMLID(getCompanyID(req), aForm.getMailinglistID());

                        if(mlids.size() > 0) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.mailinglist.cannot_delete"));
                            //aForm.setAction(MailinglistAction.ACTION_SAVE);
                            list(aForm, req);
                            destination=mapping.findForward("list");
                       } else {
                            aForm.setAction(MailinglistAction.ACTION_DELETE);
                            destination=mapping.findForward("delete");                          
                        }
                    
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }

                    break;

                case MailinglistAction.ACTION_DELETE:
                    if(allowed("mailinglist.delete", req)) {
                        deleteMailinglist(aForm, req);
                        aForm.setAction(MailinglistAction.ACTION_LIST);
                        list(aForm, req);
                        destination=mapping.findForward("list");
                        
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("changes_saved"));
                   } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                default:
                    aForm.setAction(MailinglistAction.ACTION_LIST);
                    destination=mapping.findForward("list");
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        if(destination != null && "list".equals(destination.getName())) {
        	try {
        		setNumberOfRows(req,(StrutsFormBase)form);
        		//List<DynaBean> mailinglistList = getMailinglist(req,(MailinglistForm)aForm);
        		req.setAttribute("mailinglistList",getMailinglist(req,(MailinglistForm)aForm));
			} catch (Exception e) {
				AgnUtils.logger().error("getMailinglistList: "+e+"\n"+AgnUtils.getStackTrace(e));
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			} 
        }  

        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }
        
        // Report any message (non-errors) we have discovered
        if(!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

        return destination;
    }

    /**
     * creates a mailingList from a given Target
     * @param req 
     * @param form 
     * @param targetID the targetId
     */
	private void createMailingListFromTarget( String targetIdString, HttpServletRequest req, MailinglistForm form ) {
		Integer targetId = Integer.valueOf( targetIdString );

		if ( targetId == null ) {
			return;
		}
		TargetDao targetDao=(TargetDao) getBean("TargetDao");
		Target target = targetDao.getTarget( targetId, getCompanyID( req ) );

		if ( target != null ) {
			BindingEntryDao	bindingDao=(BindingEntryDao) getBean("BindingEntryDao");

			bindingDao.addTargetsToMailinglist(getCompanyID(req), form.getMailinglistID(), target);
		}
	}

	/**
     * Sets attributes for mailingslists request.
     * @deprecated replaced by getMailinglistList
     */
    protected void list(MailinglistForm aForm, HttpServletRequest req) {
        MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
        List mlist = mDao.getMailinglists(this.getCompanyID(req));
        Object[] temp = mlist.toArray ();
        ArrayList alist;
        Arrays.sort (temp, new MListCompare ());

        alist = new ArrayList (temp.length);
        for (int n = 0; n < temp.length; ++n) {
            alist.add (temp[n]);
        }
        req.setAttribute("mailinglists", alist);
    }

    /**
     * Loads mailingslist.
     */
    protected void loadMailinglist(MailinglistForm aForm, HttpServletRequest req) {
        MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
        Mailinglist aMailinglist=mDao.getMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));

        if(aMailinglist!=null) {
            aForm.setShortname(aMailinglist.getShortname());
            aForm.setDescription(aMailinglist.getDescription());
        } else if(aForm.getMailinglistID() != 0) {
            AgnUtils.logger().warn("loadMailinglist: could not load mailinglist: "+aForm.getMailinglistID());
        }
    }

    /**
     * Saves mailinglist.
     */
    protected boolean saveMailinglist(MailinglistForm aForm, HttpServletRequest req) {

        MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
        Mailinglist aMailinglist=mDao.getMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));
        boolean is_new=false;

        if(aMailinglist==null) {
            aForm.setMailinglistID(0);
            aMailinglist=(Mailinglist) getBean("Mailinglist");
            aMailinglist.setCompanyID(this.getCompanyID(req));
            is_new=true;
        }
        aMailinglist.setShortname(aForm.getShortname());
        aMailinglist.setDescription(aForm.getDescription());

        mDao.saveMailinglist(aMailinglist);

        aForm.setMailinglistID(aMailinglist.getId());
        AgnUtils.logger().info("saveMailinglist: save mailinglist id: "+aMailinglist.getId());
        return is_new;
    }

    /**
     * Removes mailinglist.
     */
    protected void deleteMailinglist(MailinglistForm aForm, HttpServletRequest req) {

        if(aForm.getMailinglistID()!=0) {
            MailinglistDao mDao=(MailinglistDao) getBean("MailinglistDao");
            Mailinglist aMailinglist=mDao.getMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));
            if(aMailinglist!=null) {
                mDao.deleteBindings(aMailinglist.getId(), aMailinglist.getCompanyID());
                mDao.deleteMailinglist(aForm.getMailinglistID(), this.getCompanyID(req));
            }
        }
    }
    
    public PaginatedList getMailinglist(HttpServletRequest request, MailinglistForm aForm ) throws IllegalAccessException, InstantiationException {
    	
    	ApplicationContext aContext= getWebApplicationContext();
	    JdbcTemplate aTemplate=new JdbcTemplate( (DataSource)aContext.getBean("dataSource"));
	    List<Integer>  charColumns = Arrays.asList(new Integer[]{1,2 });
		String direction = request.getParameter("dir");
     	String sort =  getSort(request, aForm);
     	
     	if( sort == null || "".equals(sort.trim()) ) {
     		sort ="shortname";
     	}
     	
	    String upperSort = "upper( " +sort + " )";
    	    	
    	int rownums = aForm.getNumberofRows();	
    	
     	if( direction == null ) {
     		direction = aForm.getOrder();     		
     	} else {
     		aForm.setOrder(direction);
     	}
     	
     	String pageStr = request.getParameter("page");
     	if ( pageStr == null || "".equals(pageStr.trim()) ) {
     		if ( aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
     				aForm.setPage("1");
     		} 
     		pageStr = aForm.getPage();
     	}
     	else {
     		aForm.setPage(pageStr);
     	}
     	
     	if( aForm.isNumberOfRowsChanged() ) {
     		aForm.setPage("1");
     		aForm.setNumberOfRowsChanged(false);
     		pageStr = "1";
     	}
    	
    	int offset =  ( Integer.parseInt(pageStr)  - 1) * rownums;  
    		
    	
	    String sqlStatement = "SELECT mailinglist_id, shortname, description "; 
	    String sqlStatementFrompart = " FROM mailinglist_tbl WHERE company_id="+AgnUtils.getCompanyID(request);
		String orderBypart = " ORDER BY "+ upperSort +" "+direction;
				
		int totalRows = aTemplate.queryForInt("SELECT count(mailinglist_id) " + sqlStatementFrompart );
				
		if( AgnUtils.isOracleDB() ) {
			sqlStatement += ", rownum r";		
			sqlStatement = "SELECT * FROM (" + sqlStatement + sqlStatementFrompart + orderBypart + ") WHERE r between " + offset + " and " + ( offset+ rownums );
		}
		if ( AgnUtils.isMySQLDB()) {
			sqlStatement = sqlStatement + sqlStatementFrompart + orderBypart + " LIMIT  " + offset + " , " + rownums;
		}
	        
	    
	    List<Map> tmpList = aTemplate.queryForList(sqlStatement);
        
	      DynaProperty[] properties = new DynaProperty[] {
	    		  new DynaProperty("mailinglistId",  Long.class),
	    		  new DynaProperty("shortname", String.class),	    		  
	    		  new DynaProperty("description", String.class)	    		     		  
	      };
	      
	      if( AgnUtils.isOracleDB()) {
	    	  properties = new DynaProperty[] {
		    		  new DynaProperty("mailinglistId",  BigDecimal.class),
		    		  new DynaProperty("shortname", String.class),	    		  
		    		  new DynaProperty("description", String.class)
	    	  };
	      }
	      
	      
	      BasicDynaClass dynaClass = new BasicDynaClass("campaign", null, properties);
	      
	      List<DynaBean> result = new ArrayList<DynaBean>();
	      for(Map row:tmpList) {
	    	  DynaBean newBean = dynaClass.newInstance();    	
	    	  newBean.set("mailinglistId", row.get("MAILINGLIST_ID"));
	    	  newBean.set("shortname", row.get("SHORTNAME"));
	    	  newBean.set("description", row.get("DESCRIPTION"));
	    	  result.add(newBean);
	    	  
	      } 
	      
	      DynaBeanPaginatedListImpl paginatedList = new DynaBeanPaginatedListImpl(result, totalRows, rownums, Integer.parseInt(pageStr), sort, direction );
	      return paginatedList;
	      //return result;
	      
    }
    protected String getSort(HttpServletRequest request, StrutsFormBase aForm) {
		String sort = request.getParameter("sort");  
		 if( sort == null ) {
			 sort = aForm.getSort();			 
		 } else {
			 aForm.setSort(sort);
		 }
		return sort;
	}
    
}

