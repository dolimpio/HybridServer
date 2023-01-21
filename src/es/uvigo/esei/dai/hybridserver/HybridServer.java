/**
 *  HybridServer
 *  Copyright (C) 2022 Miguel Reboiro-Jato
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBHTML;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXML;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXSD;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXSLT;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.HTMLDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XMLDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XSDDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XSLTDAO;

public class HybridServer {
	private String webService;
	private int service_port;
	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	private int numHilos;
	private ExecutorService threadPool;
	private Thread serverThread;
	private boolean stop;
	private DAODBHTML daoHTML;
	private DAODBXML daoXML;
	private DAODBXSLT daoXSLT;
	private DAODBXSD daoXSD;
	private List<ServerConfiguration> moreServers; 

	Properties propertiesHybrid;
	Endpoint endpoint;

	public HybridServer() {

		daoHTML = new DAODBHTML();
		daoXML = new DAODBXML();
		daoXSLT = new DAODBXSLT();
		daoXSD = new DAODBXSD();

		dbPassword = "hsdbpass";
		dbUser = "hsdb";
		dbUrl = "jdbc:mysql://localhost:3306/hstestdb";
		numHilos = 50;
		service_port = 8888;

	}

	public HybridServer(Configuration conf) {
		daoHTML = new DAODBHTML();
		daoXML = new DAODBXML();
		daoXSLT = new DAODBXSLT();
		daoXSD = new DAODBXSD();

		webService = conf.getWebServiceURL();
		service_port = conf.getHttpPort();
		dbUrl = conf.getDbURL();
		dbUser = conf.getDbUser();
		dbPassword = conf.getDbPassword();
		numHilos = conf.getNumClients();

		moreServers = conf.getServers();

	}

	public HybridServer(Properties properties) {
		daoHTML = new DAODBHTML();
		daoXML = new DAODBXML();
		daoXSLT = new DAODBXSLT();
		daoXSD = new DAODBXSD();
		propertiesHybrid = properties;

		propertiesHybrid.setProperty("numClients", "50");
 		propertiesHybrid.setProperty("port", "8888");
		propertiesHybrid.setProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		propertiesHybrid.setProperty("db.user", "hsdb");
		propertiesHybrid.setProperty("db.password", "hsdbpass");

		dbUrl = propertiesHybrid.getProperty("db.url");
		dbUser = propertiesHybrid.getProperty("db.user");
		dbPassword = propertiesHybrid.getProperty("db.password");
		
		numHilos = Integer.parseInt(propertiesHybrid.getProperty("numClients"));
		service_port = Integer.parseInt(propertiesHybrid.getProperty("port")); 
		numHilos = 50;
		service_port = 8888;

	}

	public int getPort() {
		return service_port;
	}

	public void start() {

		if(webService != null){
			endpoint = Endpoint.publish(
				webService,
				new HybridServerServiceImpl(daoHTML, daoXML, daoXSLT, daoXSD));
		}

		this.serverThread = new Thread() {
			@Override
			public void run() {

				try (ServerSocket serverSocket = new ServerSocket(service_port)) {
					threadPool = Executors.newFixedThreadPool(numHilos);
					while (true) {
						Socket clientSocket = serverSocket.accept();
						if (stop)
							break;
						threadPool.execute(new ServiceThread(clientSocket, daoHTML, daoXML, daoXSLT, daoXSD));

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", service_port)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		threadPool.shutdownNow();

		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.serverThread = null;
	}
}
