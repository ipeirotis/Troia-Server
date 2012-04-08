<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Form One</title>
<script type="text/javascript">
	function clearfield(field) {
		var toclear = document.getElementById(field);
		toclear.value="";
	}
</script>
</head>
<body>
	<form method="post" action="formOne" name="formOne" enctype="multipart/form-data">
		<table align="center" width="400px;">
			<thead>
				<tr>
					<th>
						Categories
					</th>
					<th>
						Iterations
					</th>
				</tr>	
			</thead>
			<tbody>
				<tr>
					<td>Line format</td>
					<td>Line format</td>
				</tr>
				<tr>
					<td>assignedlabel[tab]probability</td>
					<td>number of iterations</td>
				</tr>
				<tr>
					<td>
						<textarea style="resize: none;" rows="2" cols="40" name="categories" id="categories">${it.categories}</textarea>
					</td>
					<td>
						<input type="text" name="iterations" id="iterations" value="${it.iterations}"/>
					</td>
				</tr>
				<tr>
					<td>
						<input type="file" accept="text/plain" name="cmuds_categories" width=40px/>
						<input type="submit" value="..." name="btn_categories"/>
						<input type="button" value="X" onclick="clearfield('categories');"/>
					</td>
					<td>
						<input type="button" value="X" onclick="clearfield('iterations');"/>
					</td>
				</tr>
			</tbody>
		</table>
		<table align="center" width="600px;">
			<thead>
				<tr>
					<th>
						Unlabeled Examples
					</th>
					<th>
						"Gold" Answers
					</th>
					<th>
						Classification Costs
					</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Line format</td>
					<td>Line format</td>
					<td>Line format</td>
				</tr>
				<tr>
					<td>workerid[tab]objectid[tab]assignedlabel</td>
					<td>objectid[tab]correctlabel</td>
					<td>correct_class[tab]classified_into[tab]cost</td>
				</tr>
				<tr>
					<td>
						<textarea style="resize: none;" rows="10" cols="40" name="unlabeled" id="unlabeled">${it.unlabeled}</textarea>
					</td>
					<td>
						<textarea style="resize: none;" rows="10" cols="40" name="gold" id="gold">${it.gold}</textarea>
					</td>
					<td>
						<textarea style="resize: none;" rows="10" cols="40" name="costs" id="costs">${it.costs}</textarea>
					</td>
				</tr>
				<tr>
					<td>
						<input type="file" accept="text/plain" name="cmuds_unlabeled" width="40px"/>
						<input type="submit" value="..." name="btn_unlabeled"/>
						<input type="button" value="X" onclick="clearfield('unlabeled');"/>
					</td>
					<td>
						<input type="file" accept="text/plain" name="cmuds_gold" width="40px"/>
						<input type="submit" value="..." name="btn_gold"/>
						<input type="button" value="X" onclick="clearfield('gold');"/>
					</td>
					<td>
						<input type="file" accept="text/plain" name="cmuds_costs" width="40px"/>
						<input type="submit" value="..." name="btn_costs"/>
						<input type="button" value="X" onclick="clearfield('costs');"/>
					</td>
				</tr>
			</tbody>
		</table>
		<br/>
		<div align="center"><button type="submit">Compute</button></div>
		<br/>
		<div align="center">
			<textarea style="resize: none; width:1000px; height:200px;" readonly="readonly">${it.results}</textarea>
		</div>
	</form>
</body>
</html>