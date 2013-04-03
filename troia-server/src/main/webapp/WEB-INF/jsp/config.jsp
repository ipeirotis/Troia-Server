<%@ page language="java" contentType="text/html"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>Troia config</title>
</head>
<body>
	<form method="post" action="config">
		<table>
			<thead>
				<tr>
					<th>
						Option
					</th>
					<th>
						Value
					</th>
				</tr>	
			</thead>
			<tbody>
			    <c:forEach var="item" items="${it.items}">
			        <tr>
			            <td>${item.name}</td>
			            <td><input type="text" name="${item.name}" value="${item.value}" /></td>
                    </tr>
                </c:forEach>
			</tbody>
		</table>
		<input type="submit" value="Submit">
	</form>
</body>
</html>