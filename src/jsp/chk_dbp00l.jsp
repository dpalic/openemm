<%@page import="java.util.*" %>
<%@page import="java.io.*" %>
<%@page import="org.agnitas.util.*" %>
<%@page import="org.apache.commons.dbcp.*" %>

 
<%
      BasicDataSource aSource=(BasicDataSource)AgnUtils.retrieveDataSource(config.getServletContext());
      out.write("Used/Idle Connections on "+AgnUtils.getDefaultValue("system.instancename")+": "+aSource.getNumActive()+"/"+aSource.getNumIdle()+"<hr>");
        
%>
