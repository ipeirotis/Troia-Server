<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Compose DawidSkene object and store it to the database</title>
	<script type="text/javascript">
		function act(val) {
			document.forms["dsform"].action.value=val; 
			document.forms["dsform"].submit();
		}
	</script>
</head>
<body>
	<h3>Compose DawidSkene object and store it to the database</h3>
	<form method="post" action="composeDSForm" name="dsform" enctype="multipart/form-data">
		<input type="hidden" name="action" value="${it.action}"/>
		<table align="left" width="1000px;">
			<thead>
				<tr>
					<th width="150px;">Name</th>
					<th width="200px;">Fields</th>
					<th>Actions</th>
					<th>Result</th>
					<th width="100px;">Purpose</th>
				</tr>
				<tr>
					<th>(class name)</th>
					<th>(set of fields)</th>
					<th></th>
					<th>(json)</th>
					<th>(helps to test)</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Category</td>
					<td><div>name</div><div><input name="categoryName" value="${it.categoryName}"/></div></td>
					<td>
						<div><button type="button" onclick="act('c_json');">Generate JSON</button></div>
						<div><button type="button" onclick="act('c_store');" ${it.c_no_changes}>Put to the DB</button></div>
					</td>
					<td>
						<textarea style="resize: none;" readonly="readonly" rows="2" cols="30" name="categoryJson" id="categoryJson">${it.categoryJson}</textarea>
					</td>
					<td>
						---
					</td>
				</tr>
				<tr>
					<td>Set&lt;Category&gt;</td>
					<td><div>names (use ",")</div><div><input name="categoryNames" value="${it.categoryNames}"/></div></td>
					<td>
						<div><button type="button" onclick="act('cs_json');">Generate JSON</button></div>
						<div><button type="button" onclick="act('cs_store');" ${it.c_no_changes}>Put to the DB</button></div>
					</td>
					<td>
						<textarea style="resize: none;" readonly="readonly" rows="2" cols="30" name="categoriesJson" id="categoriesJson">${it.categoriesJson}</textarea>
					</td>
					<td>
						<a href="#" onclick="return false;" title="com.datascience.gal.service.Service.loadCategories.(String input, String idstr, String incremental)">loadCategories</a>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>