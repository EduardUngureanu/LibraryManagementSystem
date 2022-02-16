package com.libmgrsys;

import java.sql.*;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Helper class for handling operations with the MySQL Database.
 */
public class DatabaseHelper
{
	/**
	 * Generates and returns a new connection to the database.
	 * @return MySQL Connection
	 */
	public static Connection connect()
	{
		try
		{
			String connectionUrl = "jdbc:mysql://localhost:3306/mysql?serverTimezone=UTC";
	        
	        Connection con = DriverManager.getConnection(connectionUrl,"root","Password1234");
	        System.out.println("Connected to MySQL");
	        
	        return con;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Generates and returns a result set based using an existing MySQL Connection and a query string to the database.
	 * @return MySQL ResultSet
	 */
	public static ResultSet createResultSet(Connection connection, String sql)
	{
		try
        {
        	Statement stmt = connection.createStatement();
        	stmt.executeUpdate("USE LIBRARY_DB");

			return stmt.executeQuery(sql);
        }
        catch (Exception ex)
        {
             ex.printStackTrace();
        }
		
		return null;
	}

	/**
	 * Returns a vector containing the names of the columns from a ResultSet.
	 * @param resultSet ResultSet
	 * @return Vector with the column names
	 */
	public static Vector<String> getColumnNames(ResultSet resultSet)
	{
		try
        {
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnCount = rsmd.getColumnCount();
			
			Vector<String> columnNames = new Vector<>();
			
			for (int column = 0; column < columnCount; column++)
			{
				columnNames.addElement(rsmd.getColumnLabel(column + 1));
			}
			
			return columnNames;
        }
        catch (Exception ex)
        {
             ex.printStackTrace();
        }

		return null;
	}

	/**
	 * Returns a matrix containing all the data in a ResultSet.
	 * @param resultSet ResultSet
	 * @return double Vector
	 */
	public static Vector<Vector<Object>> getRows(ResultSet resultSet)
	{
		try
        {
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnCount = rsmd.getColumnCount();
			
			Vector<Vector<Object>> rows = new Vector<>();
			
			while (resultSet.next())
			{
				Vector<Object> newRow = new Vector<>();
				
				for (int i = 1; i <= columnCount; i++)
				{
				    newRow.addElement(resultSet.getObject(i));
				}

				rows.addElement(newRow);
			}
			
			return rows;
        }
        catch (Exception ex)
        {
             ex.printStackTrace();
        }
		
		return null;
	}

	/**
	 * Generates and returns a TableModel from a ResultSet.
	 * @param resultSet ResultSet
	 * @return TableModel containing the data from the ResultSet
	 */
	public static TableModel resultSetToTableModelWithID(ResultSet resultSet)
	{
		try
		{
		    ResultSetMetaData metaData = resultSet.getMetaData();
		    int numberOfColumns = metaData.getColumnCount();
		    Vector<String> columnNames = new Vector<>();

		    // Get the column names
		    for (int column = 0; column < numberOfColumns; column++)
		    {
		    	columnNames.addElement(metaData.getColumnLabel(column + 1));
		    }

		    // Get all rows.
		    Vector<Vector<Object>> rows = new Vector<>();

		    while (resultSet.next())
		    {
				Vector<Object> newRow = new Vector<>();
				
//				newRow.addElement(resultSet.getObject(1));
				for (int i = 2; i <= numberOfColumns; i++)
				{
				    newRow.addElement(resultSet.getObject(i));
				}
	
				rows.addElement(newRow);
		    }

		    return new DefaultTableModel(rows, columnNames);
		}
		catch (Exception e)
		{
		    e.printStackTrace();

		    return null;
		}
	}
}
