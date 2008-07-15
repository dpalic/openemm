<%@ page language="java" import="com.agnitas.util.*,java.sql.*" contentType="text/html; charset=utf-8" %>
<% 
   Connection dbConn=null;
   PoolManager agnDBPool=(PoolManager)application.getAttribute("ConnectionPool");
   if(agnDBPool!=null) {
        dbConn=agnDBPool.getConnection("agnitas");
        Logger.log(Logger.CAT_NORMAL, "Logout by User", request, dbConn);
        agnDBPool.freeConnection("agnitas", dbConn);
   }
   session.invalidate();
   response.sendRedirect("login.jsp"); 
%>