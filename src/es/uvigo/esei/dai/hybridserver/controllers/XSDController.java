package es.uvigo.esei.dai.hybridserver.controllers;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.SQLConnectionException;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.ServerConnection;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXSD;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class XSDController {
    private HTTPRequest request;
    private DAODBXSD daoXSD;
    private HTTPResponse response;
    private List<ServerConfiguration> servers;
    private List<HybridServerService> services;
    private ServerConnection connection;

    public XSDController(DAODBXSD daoXSD, List<ServerConfiguration> servers) {
        this.daoXSD = daoXSD;
        this.response = new HTTPResponse();
        this.servers = servers;
        response = new HTTPResponse();
        this.services = new ArrayList<HybridServerService>();
        this.connection = new ServerConnection(this.servers);
    }

    public void setRequest(HTTPRequest request) {
        this.request = request;
        this.response.setVersion(request.getHttpVersion());
    }

    public boolean validResource() {
        return request.getResourceChain().contains("xsd");
    }

    public String getUUID(String content) {
        UUID uuid = UUID.fromString(content);
        return uuid.toString();
    }

    public void getMethodXSD() throws SQLConnectionException, MalformedURLException {

        System.out.println("\n\nCONTENIDO DEL GET: " + request.toString() + "\n\n");

        String resourceChain = request.getResourceChain();
        System.out.println("\n\nCADENA DE RECURSO DEL GET: " + resourceChain + "\n\n");

        String contentRequest = request.getContent();
        System.out.println("\n\nCONTENIDO DEL GET: " + contentRequest + "\n\n");

        String resource = request.getResourceParameters().get("uuid");
        System.out.println("\n\nRECURSO DEL GET: " + resource + "\n\n");

        if (request.getResourceName().equals("xsd") && !request.getResourceChain().contains("uuid")) {
            System.out.println("esta resource name no vale baby" + request.getResourceName());
            System.out.println("esta resource chain no vale baby" + request.getResourceParameters());
            String listaContent = listado();

            String htmlPage = "<html><head> <title>List Page</title> </head>"
                    + "<body>" + "<span>" + listaContent + "</span>" + "</body></html>";

            response.setContent(htmlPage);
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", "text/html");

        } else if (!validResource()) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (daoXSD.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent(daoXSD.get(resource));
            response.putParameter("Content-Type", "application/xml");
        } else if (!daoXSD.exists(resource)) {

            String todosRecursos = listado();
            if (todosRecursos.contains(resource)) {
                if (servers != null) {
                    services = connection.connectToServers();
                    for (HybridServerService serverIt : services) {
                        String currentContent = serverIt.getXSD(resource);
                        if (!currentContent.isEmpty()) {
                            response.setContent(currentContent);
                            response.setStatus(HTTPResponseStatus.S200);
                            response.putParameter("Content-Type", "application/xml");
                        }
                    }
                }
            } else {
                response.setStatus(HTTPResponseStatus.S404);
            }

        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
        System.out.println("\n\nRESPUESTA DEL GET: " + response.toString() + "\n\n");

    }

    public String listado() throws MalformedURLException, SQLConnectionException {
        Set<String> set = daoXSD.list();
        Iterator<String> it = set.iterator();
        String listaContent = "";
        while (it.hasNext()) {
            String uuidProximo = it.next().toString();
            // listaContent += "<li>" + uuidProximo + "</li>";
            listaContent += uuidProximo + " //";
        }
        if (servers != null) {
            services = connection.connectToServers();
            for (HybridServerService serverIt : services) {
                listaContent += serverIt.getListXSD() + " // ";
            }
        }

        return listaContent;
    }

    public void postMethodXSD() throws SQLConnectionException {
        String uuid = "";

        System.out.println("\n\nCONTENIDO DEL POST: " + request.toString() + "\n\n");

        String resourceChain = request.getResourceChain();
        System.out.println("\n\nCADENA DE RECURSO DEL POST: " + resourceChain + "\n\n");

        String contentRequest = request.getContent();
        System.out.println("\n\nCONTENIDO DEL POST: " + contentRequest + "\n\n");

        String resource = request.getResourceParameters().get("uuid");
        System.out.println("\n\nRECURSO DEL POST: " + resource + "\n\n");

        if (resource == null) {
            uuid = UUID.randomUUID().toString();
        }

        if (!contentRequest.contains("xsd=")) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!daoXSD.exists(uuid)) {
            String newContent = contentRequest.replace("xsd=", "");
            System.out.println("\n\nUUID DEL POST: " + uuid + "\n\n");
            System.out.println("\n\nNUEVO CONTENIDO DEL POST: " + newContent + "\n\n");
            daoXSD.create(uuid, newContent);
            String uuidHyperlink = "<a href=\"xsd?uuid=" + uuid + "\">" + uuid + "</a>";
            response.setContent(uuidHyperlink);
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", "application/xml");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }

        System.out.println("\n\nRESPUESTA DEL POST: " + response.toString() + "\n\n");

    }

    public void deleteMethodXSD() throws SQLConnectionException {
        String resource = request.getResourceParameters().get("uuid");
        if (daoXSD.exists(resource)) {
            daoXSD.delete(resource);
            response.setStatus(HTTPResponseStatus.S200);
        } else if (!daoXSD.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S404);
        } else if (!validResource()) {
            response.setStatus(HTTPResponseStatus.S400);
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
        System.out.println("\n\nRESPUESTA DEL DELETE: " + response.toString() + "\n\n");

    }

    public HTTPResponse getResponseXSD() {
        return response;
    }
}
