/*
 * Created on Jan 26, 2005
 */
package com.pcyang.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.pcyang.common.Utilities.Pair;


/**
 * @author slc
 */
public class DerbyDatabase
{
	private static Connection conn = null;
	private static Statement stmt = null;

	public static Connection getConnInstance()
	{
		if (conn == null)
		{
			conn = createConnection(Config.DBUSERNAME,Config.DBPASSWORD);
		}
		return conn;
	}

	private static Connection createConnection(String userName, String password)
	{
		try
		{
			Class.forName(Config.DRIVERNAME);
			Properties dbProps = new Properties();
			dbProps.put("user", userName);
			dbProps.put("password", password);
			conn = DriverManager.getConnection(Config.DBURL + Config.DBLOCATION, dbProps);
			System.out.println("Connection successful!");
		}
		catch (Exception except)
		{
			System.out.print("Could not connect to the database with username: " + userName);
			System.out.println(" password " + password);
			System.out.println("Check that the Derby Network Server is running on localhost.");
			except.printStackTrace();
		}
		return conn;
	}
	public static int executeUpdate(Connection conn, String sql)
	{
		// the number of rows affected by the update or insert
		int numRows = 0;

		try
		{
			stmt = conn.createStatement();
			numRows = stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (SQLException sqlExcept)
		{
			sqlExcept.printStackTrace();
		}
		return numRows;
	}

	public static String[] runQuery(Connection conn, String sql)
	{
		List list = Collections.synchronizedList(new ArrayList(10));
		try
		{
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = results.getMetaData();
			int numberCols = rsmd.getColumnCount();

			while(results.next())
			{
				StringBuffer sbuf = new StringBuffer(200);
				for (int i = 1; i <= numberCols; i++)
				{
					sbuf.append(results.getString(i));
					sbuf.append(", ");
				}
				list.add(sbuf.toString());
			}
			results.close();
			stmt.close();
		}
		catch (SQLException sqlExcept)
		{
			sqlExcept.printStackTrace();
		}
		return (String[])list.toArray(new String[list.size()]);
	}

	public static List<Map<String,String>> getProjects(Connection conn)
	{
		return getProjects(conn, null);
	}
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	public static List<Map<String,String>> getProjects(Connection conn, Map<String,String[]> filter)
	{
		StringBuilder query =  new StringBuilder("SELECT tracking_id, description, first_name, last_name, estimited_hour, status_id, subteam_id, team_id, group_id, project_id FROM ((app.projects NATURAL JOIN app.users) NATURAL JOIN app.subteams) NATURAl JOIN app.teams");
		if(filter!= null && filter.size() > 0)
		{
			query.append(" WHERE ");
			String prefix = "";
			for(String key : filter.keySet()){
				query.append(prefix);
				prefix = " AND ";
				if(isNumeric(filter.get(key)[0]))
					query.append(key).append("=").append(filter.get(key)[0]);
				else
					query.append(key).append("='").append(filter.get(key)[0]).append("'");
			}
		}
		List<Map<String,String>> resultList = new ArrayList<Map<String, String>>();
		try
		{
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(query.toString());

			while(results.next())
			{
				HashMap<String,String> tempMap = new HashMap<String,String>();
				tempMap.put("_tracking_id", results.getString("tracking_id"));
				tempMap.put("_id", results.getString("project_id"));
				tempMap.put("_name", results.getString("first_name") + " " +  results.getString("last_name"));
				String desc = results.getString("description");
				tempMap.put("_description", desc.substring(0, Math.min(25, desc.length())));
				tempMap.put("estimited_hour", results.getString("estimited_hour"));
				tempMap.put("_column", results.getString("status_id"));
				tempMap.put("_order", "_id, name, description");
				resultList.add(tempMap);
			}
			results.close();
			stmt.close();
		}
		catch (SQLException sqlExcept)
		{
			sqlExcept.printStackTrace();
		}

		return resultList;
	}

	public static List<Map<Integer,String>> getSubteamStatusID(Connection conn, String subteam_id)
	{
		StringBuilder query =  new StringBuilder("SELECT status_id, status_name FROM app.status");
		query.append(" WHERE subteam_id=").append(subteam_id).append("ORDER BY status_order");

		List<Map<Integer,String>> resultList = new ArrayList<Map<Integer,String>>();
		try
		{
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(query.toString());

			while(results.next())
			{
				HashMap<Integer,String> tempMap = new HashMap<Integer,String>();
				tempMap.put(results.getInt("status_id"), results.getString("status_name"));
				resultList.add(tempMap);
			}
			results.close();
			stmt.close();
		}
		catch (SQLException sqlExcept)
		{
			sqlExcept.printStackTrace();
		}
		return resultList;
	}
	
	public static List<Pair<String,String>> getSubteamsUsingTeam(Connection conn, String team_id)
	{
		StringBuilder query =  new StringBuilder("SELECT subteam_id,subteam_name FROM app.subteams");
		query.append(" WHERE team_id=").append(team_id);

		List<Pair<String, String>> resultList = new ArrayList<Pair<String,String>>();
		try
		{
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(query.toString());

			while(results.next())
			{
				Pair<String, String> temp = new Pair<String,String>(results.getString("subteam_id"),results.getString("subteam_name"));
				resultList.add(temp);
			}
			results.close();
			stmt.close();
		}
		catch (SQLException sqlExcept)
		{
			sqlExcept.printStackTrace();
		}
		return resultList;
	}
	
	public static List<Pair<String,String>> getSubteamsUsingGroup(Connection conn, String group_id)
	{
		StringBuilder query =  new StringBuilder("SELECT subteam_id,subteam_name,team_name FROM app.subteams NATURAL JOIN app.teams");
		query.append(" WHERE group_id=").append(group_id);

		List<Pair<String, String>> resultList = new ArrayList<Pair<String,String>>();
		try
		{
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(query.toString());

			while(results.next())
			{
				Pair<String, String> temp = new Pair<String,String>(results.getString("subteam_id"),results.getString("team_name")+": "+results.getString("subteam_name"));
				resultList.add(temp);
			}
			results.close();
			stmt.close();
		}
		catch (SQLException sqlExcept)
		{
			sqlExcept.printStackTrace();
		}
		return resultList;
	}
	
	public static boolean updateProjectStatus(Connection conn, String project_id, String new_status)
	{
		StringBuilder query =  new StringBuilder("UPDATE app.projects");
		query.append(" SET status_id=").append(new_status);
		query.append(" WHERE project_id=").append(project_id).append("");
		try
		{
			stmt = conn.createStatement();
			stmt.executeUpdate(query.toString());
			stmt.close();
		}
		catch (SQLException sqlExcept)
		{
			sqlExcept.printStackTrace();
			return false;
		}
		return true;
	}    
	
//    public static int getLabelID(Connection conn, String label)
//    {
//    	StringBuilder query =  new StringBuilder("SELECT label_id FROM app.labels");
//        query.append(" WHERE label='").append(label).append("'");
//        int label_id = -1;
//        try
//        {
//			stmt = conn.createStatement();
//			ResultSet results = stmt.executeQuery(query.toString());
// 
//            while(results.next())
//            {
//            	label_id = results.getInt("label_id");
//            	break;
//            }
//            results.close();
//            stmt.close();
//        }
//        catch (SQLException sqlExcept)
//        {
//            sqlExcept.printStackTrace();
//        }
//        return label_id;
//    }
}

