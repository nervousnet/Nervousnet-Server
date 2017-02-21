/*******************************************************************************
 *     NervousnetCoreServer - A Core Server template which is part of the Nervousnet project
 *     sensor data, text messages and more.
 *
 *     Copyright (C) 2015 ETH Zürich, COSS
 *
 *     This file is part of Nervousnet.
 *
 *     Nervousnet is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Nervousnet is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Nervousnet. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * 	*******************************************************************************/
package ch.ethz.coss.nervousnet.core.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import ch.ethz.coss.nervousnet.core.utils.Log;

public final class SqlConnection {

	// Server properties
	private String username;
	private String password;
	private String hostname;
	private int port;
	private String database;

	private DataSource source;

	public Connection getConnection() {
		try {
			return source.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.getInstance().append(Log.FLAG_ERROR, "Can't get a connection from the data source");
			return null;
		}
	}

	/**
	 * Connection will be set up this way: [jdbc:mysql://" + hostname + ":
	 * " + port + "/" + database]
	 * 
	 * @param hostname
	 * @param username
	 * @param password
	 * @param port
	 * @param database
	 */
	public SqlConnection(String hostname, String username, String password, int port, String database) {
		this.username = username;
		this.password = password;
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		print();
		source = setup();

	}

	private DataSource setup() {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			Log.getInstance().append(Log.FLAG_ERROR, "Error loading the SQL driver");
			return null;
		}

		ConnectionFactory cf = null;
		try {
			cf = new DriverManagerConnectionFactory("jdbc:mysql://" + hostname + ":" + port + "/" + database, username,
					password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, null);

		ObjectPool<PoolableConnection> connPool = new GenericObjectPool<PoolableConnection>(pcf);

		pcf.setPool(connPool);
		PoolingDataSource<PoolableConnection> dataSource = new PoolingDataSource<PoolableConnection>(connPool);
		return dataSource;
	}

	private void print() {

	}

}