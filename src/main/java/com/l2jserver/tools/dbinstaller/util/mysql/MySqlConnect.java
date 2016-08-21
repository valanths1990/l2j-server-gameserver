/*
 * Copyright (C) 2004-2016 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.tools.dbinstaller.util.mysql;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;
import java.util.Properties;
import java.util.TimeZone;

import javax.swing.JOptionPane;

/**
 * @author mrTJO
 */
public class MySqlConnect
{
	private Connection con = null;
	
	public MySqlConnect(String host, String port, String user, String password, String db, boolean console)
	{
		try (Formatter form = new Formatter())
		{
			String url = form.format("jdbc:mysql://%s:%s", host, port).toString();
			Driver driver = DriverManager.getDriver(url);
			Properties info = new Properties();
			info.put("user", user);
			info.put("password", password);
			info.put("useSSL", "false");
			info.put("serverTimezone", TimeZone.getDefault().getID());
			con = driver.connect(url, info);
		}
		catch (Throwable t)
		{
			if (console)
			{
				t.printStackTrace();
			}
			else
			{
				StringWriter writer = new StringWriter();
				try (PrintWriter pw = new PrintWriter(writer))
				{
					t.printStackTrace(pw);
					JOptionPane.showMessageDialog(null, "Failed to establish mysql connection!\n\n" + writer.toString(), "MySql Connection", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			return;
		}
		
		try (Statement s = con.createStatement())
		{
			s.execute("CREATE DATABASE IF NOT EXISTS `" + db + "`");
			s.execute("USE `" + db + "`");
		}
		catch (Throwable t)
		{
			if (console)
			{
				t.printStackTrace();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Failed to ensure mysql database " + db + "!\n\n" + t.toString(), "MySql Connection", JOptionPane.ERROR_MESSAGE);
			}
			
			return;
		}
	}
	
	public Connection getConnection()
	{
		return con;
	}
	
	public Statement getStatement()
	{
		try
		{
			return con.createStatement();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("Statement Null");
			return null;
		}
	}
}
