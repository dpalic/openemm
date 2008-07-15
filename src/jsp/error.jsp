<%@ page isErrorPage="true"%>
		
<html>
<head><title>Error</title></head>
<h3>Ein Fehler ist aufgetreten</h3>
<table>
<tr valign="top"><td align="top"><b>Ursache:</b></td><td><%= exception %></td></tr>
<% exception.printStackTrace(System.out); %>
</table>
</body>
</html>

