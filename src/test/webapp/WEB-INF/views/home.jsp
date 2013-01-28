<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
	<head>
		<title>Cookie Flash Sample App</title>
	</head>
	<body>
		<ul>
			<li>Flash Attribute : ${flashAttr}</li>
			<li><a href="<c:url value="/setFlashAttr" />">Set Flash Attribute</a></li>
			<li><a href="<c:url value="/" />">Refresh</a></li>
		</ul>			
	</body>
</html>