<%@page contentType="text/html" import="java.util.*" %>
<html>
<head><title>Java Environment</title></head>
<body>

<% 

out.println("<b>Locale:</b>&nbsp;"+request.getLocale());
Properties props=System.getProperties();
Enumeration enum=props.keys();
String key=null;
while(enum.hasMoreElements()) {
key=(String)enum.nextElement();
out.println("<b>"+key+":</b>&nbsp;&quot;"+System.getProperty(key)+"&quot;<br><br>");
}
%>
<br><br>
<h1>Environment:</h1><br>
<%
Properties env = new Properties();
env.load(Runtime.getRuntime().exec("env").getInputStream());
enum=env.keys();

while(enum.hasMoreElements()) {
key=(String)enum.nextElement();
out.println("<b>"+key+":</b>&nbsp;&quot;"+env.get(key)+"&quot;<br><br>");
}
%>
</body>
</html>
