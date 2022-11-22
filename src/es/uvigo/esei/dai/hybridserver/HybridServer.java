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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HybridServer {
	private int SERVICE_PORT;
	private int numHilos;
	private ExecutorService threadPool;
	private Thread serverThread;
	private boolean stop;
	private DAO dao;
	Properties propertiesHybrid;

	public HybridServer() {

		propertiesHybrid = new Properties();
		propertiesHybrid.setProperty("numClients", "50");
		propertiesHybrid.setProperty("port", "8888");
		propertiesHybrid.setProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
		propertiesHybrid.setProperty("db.user", "hsdb");
		propertiesHybrid.setProperty("db.password", "hsdbpass");		
		SERVICE_PORT = Integer.parseInt(propertiesHybrid.getProperty("port"));
		numHilos= Integer.parseInt(propertiesHybrid.getProperty("numClients"));
		this.dao = new DAODB(propertiesHybrid);



	}

	public HybridServer(Map<String, String> pages) {
	    SERVICE_PORT = 8888;
		numHilos = 50;
		this.dao = new DAOMap(pages);
	}

	public HybridServer(Properties properties) {
		propertiesHybrid = properties;
		this.dao = new DAODB(propertiesHybrid);
		numHilos = Integer.parseInt(propertiesHybrid.getProperty("numClients"));
		SERVICE_PORT = Integer.parseInt(propertiesHybrid.getProperty("port"));


	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {

				try (ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					threadPool = Executors.newFixedThreadPool(numHilos);
					while (true) {
						Socket clientSocket = serverSocket.accept();
						if (stop)
							break;
						threadPool.execute(new ServiceThread(clientSocket, dao));

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

		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
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
