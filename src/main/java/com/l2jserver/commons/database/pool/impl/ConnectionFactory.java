/*
 * Copyright (C) 2004-2018 L2J Server
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
package com.l2jserver.commons.database.pool.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.Config;
import com.l2jserver.commons.database.pool.IConnectionFactory;

/**
 * Connection Factory implementation.
 * @author Zoey76
 */
public class ConnectionFactory
{
	private ConnectionFactory()
	{
		// Hide constructor.
	}
	
	public static IConnectionFactory getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);
		
		protected static final IConnectionFactory INSTANCE;
		
		static
		{
			switch (Config.DATABASE_CONNECTION_POOL)
			{
				default:
				case "HikariCP":
				{
					INSTANCE = HikariCPConnectionFactory.INSTANCE;
					break;
				}
				case "C3P0":
				{
					INSTANCE = C3P0ConnectionFactory.INSTANCE;
					break;
				}
				case "BoneCP":
				{
					INSTANCE = BoneCPConnectionFactory.INSTANCE;
					break;
				}
			}
			LOG.info("Using {} connection pool.", INSTANCE.getClass().getSimpleName().replace("ConnectionFactory", ""));
		}
	}
}
