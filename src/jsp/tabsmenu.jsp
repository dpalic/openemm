<%@ page language="java" import="org.agnitas.util.*"  contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<ul class="tabs">
        <agn:ShowNavigation navigation='<%= (String)(request.getAttribute("agnNavigationKey")) %>'
                            highlightKey='<%= (String)(request.getAttribute("agnHighlightKey")) %>'>
		    <agn:ShowByPermission token="<%= _navigation_token %>">
		    	<%
		    		String cssStyleClass = (_navigation_isHighlightKey.booleanValue()) ? "tab_right tab_active" : "tab_right";
		    	%>
	                <li class="<%= cssStyleClass %>">
                	        	<% String nav_link=new String(_navigation_href);
                    	           if(request.getAttribute("agnNavHrefAppend")!=null) {
                        	       		nav_link=new String(nav_link + request.getAttribute("agnNavHrefAppend"));
                            	   }
                          		%>
	                          	<% if( _navigation_isHighlightKey.booleanValue() ){ %>
    	                        	<html:link styleClass="tab_left" page="<%= nav_link %>">
        	                        	<bean:message key="<%= _navigation_navMsg %>"/>
            	                    </html:link>
                	            <% } else { %>
                    	            <html:link styleClass="tab_left" page="<%= nav_link %>">
                        	        	<bean:message key="<%= _navigation_navMsg %>"/>
                            	    </html:link>
	                            <% } %>
  	 	                    </li>

        	</agn:ShowByPermission>
        </agn:ShowNavigation>
</ul>