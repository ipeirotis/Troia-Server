package com.datascience.datastoring.backends.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author: konrad
 */
public class SQLCommandOperatorFactory {

	protected String insertReplacingSQL;
	protected boolean utf8Modification;

	public SQLCommandOperatorFactory(String insertReplacingSQL, boolean utf8Modification){
		this.insertReplacingSQL = insertReplacingSQL;
		this.utf8Modification = utf8Modification;
	}

	public SQLCommandOperator create(Connection connection) throws SQLException {
		return new SQLCommandOperator(connection, insertReplacingSQL, utf8Modification);
	}

}
