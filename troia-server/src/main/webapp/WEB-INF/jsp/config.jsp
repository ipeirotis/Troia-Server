<%@ page language="java" contentType="text/html"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>Troia config</title>
</head>
<body>
	<form method="post" action="config" id="form">
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
			            <td><input type="text" name="${item.name}" value="${item.value}"
			                <c:if test="${it.freezed}">disabled</c:if>
                        /></td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="2"><input type="checkbox" name="freezed"
                        <c:if test="${it.freezed}">checked disabled</c:if>
                    >settings freezed?</td>
                </tr>
			</tbody>
		</table>
		<c:if test="${!it.freezed}"><input type="submit" value="Submit"></c:if>
	</form>
	<div id="error"></div>
	<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="http://malsup.github.com/jquery.form.js"></script>
    <script>
        $(document).ready(function() {
            $("#form").ajaxForm({
                success: function() {
                    alert("Settings successfully changed!");
                    window.location.replace(location.href);
                },
                error: function(res) {
                    $("#error").text(res.responseText);
                }
            });
        });
    </script>
</body>
</html>