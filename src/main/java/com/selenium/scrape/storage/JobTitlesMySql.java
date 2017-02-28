package com.selenium.scrape.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JobTitlesMySql {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	private static Logger LOG = Logger.getLogger(JobTitlesMySql.class.getName());

	public static final String MYSQL_HOST = "localhost:3306";
	public static final String DATABASE_NAME = "fb_audience";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "facetuan";

	public static final String TABLENAME = "facebook_audience_crawl";
	int count = 0;

	private static JobTitlesMySql mysql = null;

	private JobTitlesMySql() throws ClassNotFoundException, SQLException {
		// Register JDBC driver
		Class.forName("com.mysql.jdbc.Driver");

		// Open a connection
		connect = DriverManager.getConnection("jdbc:mysql://" + MYSQL_HOST + "/" + DATABASE_NAME + "?" + "user="
				+ USERNAME + "&password=" + PASSWORD
				+ "&noAccessToProcedureBodies=true&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&useSSL=false");
		statement = connect.createStatement();
		LOG.info("Connecting to mysql...");
	}

	public static JobTitlesMySql getInstance() throws ClassNotFoundException, SQLException {
		if (mysql == null)
			mysql = new JobTitlesMySql();

		return mysql;
	}

	public void truncate() throws SQLException {
		String query = "TRUNCATE TABLE " + TABLENAME;
		execute(query);
		LOG.info("Table " + TABLENAME + " truncated!");
	}

	public void insert(long auId, String parentName, String auName, String label, long numUsers) throws SQLException {

		String query = "INSERT INTO " + TABLENAME
				+ " (auId, parentName, auName, label, numUsers) VALUES (?, ?, ?, ?, ?)"
				+ " ON DUPLICATE KEY UPDATE auId=values(auId),parentName=values(parentName), auName=values(auName),label=values(label),numUsers=values(numUsers)";

		if (preparedStatement == null)
			preparedStatement = connect.prepareStatement(query);

		preparedStatement.setLong(1, auId);
		preparedStatement.setString(2, parentName);
		preparedStatement.setString(3, auName);
		preparedStatement.setString(4, label);
		preparedStatement.setLong(5, numUsers);

		preparedStatement.execute();
		LOG.info("Value inserted !");

	}

	public void insertBatch(long auId, String parentName, String auName, String label, long numUsers)
			throws SQLException {

		String query = "INSERT INTO " + TABLENAME
				+ " (auId, parentName, auName, label, numUsers) VALUES (?, ?, ?, ?, ?)"
				+ " ON DUPLICATE KEY UPDATE auId=values(auId),parentName=values(parentName), auName=values(auName),label=values(label),numUsers=values(numUsers)";

		if (preparedStatement == null)
			preparedStatement = connect.prepareStatement(query);

		preparedStatement.setLong(1, auId);
		preparedStatement.setString(2, parentName);
		preparedStatement.setString(3, auName);
		preparedStatement.setString(4, label);
		preparedStatement.setLong(5, numUsers);
		preparedStatement.addBatch();

		count++;

		if (count >= 50) {
			count = 0;
			preparedStatement.executeBatch();
			LOG.info("Inserting batch to mysql...");
		}

	}

	public String takeAudience(long auId) throws SQLException {
		String query = "SELECT * FROM " + TABLENAME + " WHERE auId=?";

		preparedStatement = connect.prepareStatement(query);
		preparedStatement.setLong(1, auId);

		String result = "Audience[Id=";

		resultSet = preparedStatement.executeQuery();
		preparedStatement = null;

		while (resultSet.next()) {

			String parentName = resultSet.getString("parentName");
			String auName = resultSet.getString("auName");
			String label = resultSet.getString("label");
			long numUsers = resultSet.getLong("numUsers");

			result = result + auId + ", Parent=" + parentName + ", AudienceName=" + auName + ", Label=" + label
					+ ", NumberUsers=" + numUsers + "]";
		}

		return result;
	}

	public long takeNumUsers(long auId) throws SQLException {
		String query = "SELECT * FROM " + TABLENAME + " WHERE auId=?";

		preparedStatement = connect.prepareStatement(query);
		preparedStatement.setLong(1, auId);

		long result = 0l;

		resultSet = preparedStatement.executeQuery();
		preparedStatement = null;

		while (resultSet.next()) {

			result = resultSet.getLong("numUsers");
		}

		return result;
	}

	public List<String> takeAll() throws SQLException {
		List<String> list = new ArrayList<>();

		String query = "SELECT * FROM " + TABLENAME;

		preparedStatement = connect.prepareStatement(query);

		resultSet = preparedStatement.executeQuery();
		preparedStatement = null;

		while (resultSet.next()) {
			long auId = resultSet.getLong("auId");
			String parentName = resultSet.getString("parentName");
			String auName = resultSet.getString("auName");
			String label = resultSet.getString("label");
			long numUsers = resultSet.getLong("numUsers");

			String result = "Audience[Id=" + auId + ", Parent=" + parentName + ", AudienceName=" + auName + ", Label="
					+ label + ", NumberUsers=" + numUsers + "]";

			list.add(result);
		}

		return list;
	}

	public String getSize() throws SQLException {
		String query = "SELECT count(*) FROM " + TABLENAME;
		ResultSet result = statement.executeQuery(query);
		result.next();
		return result.getString("count(*)");
	}

	public ResultSet executeQuery(String query) throws SQLException {
		return statement.executeQuery(query);
	}

	public int executeUpdate(String update) throws SQLException {
		return statement.executeUpdate(update);
	}

	public void execute(String s) throws SQLException {
		statement.execute(s);
	}

	public void close() throws SQLException {
		if (preparedStatement != null) {
			preparedStatement.executeBatch();
		}
		LOG.info("Closing mysql connection!");

		preparedStatement.close();
		connect.close();
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		JobTitlesMySql mysql = JobTitlesMySql.getInstance();
		long auId = 14332551414l;
		String parentName = "parent name";
		String auName = "audience name";
		String label = "label";
		long numUsers = 1343443l;

		mysql.insert(auId, parentName, auName, label, numUsers);
		mysql.close();
	}

}
