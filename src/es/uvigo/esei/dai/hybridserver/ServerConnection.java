package es.uvigo.esei.dai.hybridserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;

public class ServerConnection {
	/*
	 * String name,
	 * String wsdl,
	 * String namespace,
	 * String service,
	 * String httpAddress
	 */
	List<ServerConfiguration> serverConfiguration;
	List<HybridServerService> services;

	public ServerConnection(List<ServerConfiguration> servers) {
		System.out.println("constructor de servicios");
		this.serverConfiguration = servers;
		this.services = new ArrayList<>();
	}

	public List<HybridServerService> connectToServers() throws MalformedURLException {
		System.out.println("antes for");

		for (ServerConfiguration conf : serverConfiguration) {
			System.out.println("Configuracion para el server: " + conf.getName());
			System.out.println("El wsld del server es: " + conf.getWsdl());
			System.out.println("El namespace del server es: " + conf.getNamespace());
			System.out.println("El service del server es: " + conf.getService());
			System.out.println("El httpAddress del server es: " + conf.getHttpAddress());
			URL url = new URL(conf.getWsdl());
			String theService = conf.getService();
			theService += "ImplService";
			System.out.println("CPRUEBA SERVICE NAME" + theService);
			QName name = new QName(conf.getNamespace(), theService);
			System.out.println("EL NOMBRE SERVICE ES: " + conf.getService());
			System.out.println("EL NOMBRE ES: " + conf.getName());
			Service service = Service.create(url, name);
			HybridServerService hybridServ = service.getPort(HybridServerService.class);
			services.add(hybridServ);
			System.out.println("nos metemos en el for para hacer la lista de servicios");

		}
		return services;
	}

	public void print() {

		for (ServerConfiguration server : serverConfiguration) {
			System.out.println("Configuracion para el server: " + server.getName());
			System.out.println("El wsld del server es: " + server.getWsdl());
			System.out.println("El namespace del server es: " + server.getNamespace());
			System.out.println("El service del server es: " + server.getService());
			System.out.println("El httpAddress del server es: " + server.getHttpAddress());

		}
	}

}
