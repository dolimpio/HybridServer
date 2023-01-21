package es.uvigo.esei.dai.hybridserver.controllers;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.SQLConnectionException;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXML;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XMLDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class XMLController {
    private HTTPRequest request;
    private HTTPResponse response;
    private XMLDAO dao;

    public XMLController(){
        this.dao = new DAODBXML();
        this.response = new HTTPResponse();
    }
    public void setRequest(HTTPRequest request){
        this.request = request;
    }
    public boolean validResource() {
		return request.getResourceChain().contains("xml");
	}
    public String getUUID(String content) {
        UUID uuid = UUID.fromString(content);
		return uuid.toString();
	}
    public void getMethodXML() throws SQLConnectionException {

        System.out.println("\n\nCONTENIDO DEL GET: " + request.toString() + "\n\n");
        
        String resourceChain = request.getResourceChain();
        System.out.println("\n\nCADENA DE RECURSO DEL GET xml: " + resourceChain + "\n\n");

        String contentRequest = request.getContent();
        System.out.println("\n\nCONTENIDO DEL GET: " + contentRequest + "\n\n");

        String resource = request.getResourceParameters().get("uuid");
        System.out.println("\n\nRECURSO DEL GET: " + resource + "\n\n");
            
        response.setVersion(request.getHttpVersion());
        if (request.getResourceName().isEmpty()) {
            response.setStatus(HTTPResponseStatus.S200);
            String pageContent = "Hybrid Server => Mirandios Carou Laiño, David Olímpico Silva";
            String welcomePage = "<htmL><head> <title>Root Page</title> </head>"
                    + "<body>" + "<h1>" + pageContent + "</h1>" + "</body></html>";
            response.setContent(welcomePage);
        } else if (request.getResourceName().equals("xml") && !request.getResourceChain().contains("uuid")) {
            response.setStatus(HTTPResponseStatus.S200);
            Set<String> set = dao.list();
            Iterator<String> it = set.iterator();
            String listaContent = "";
            while (it.hasNext()) {
                String uuidProximo = it.next().toString();
                listaContent += "<li>" + uuidProximo + "</li>";
            }
            String htmlPage = "<html><head> <title>List</title> </head>"
                    + "<body>" + "<ul>" + listaContent + "</ul>" + "</body></html>";
            response.setContent(htmlPage);

            response.putParameter("Content-Type", "text/xml");
        } else if (!validResource()) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S404);
        } else if (dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent(dao.get(resource));
            response.putParameter("Content-Type", "text/xml");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
        System.out.println("\n\nRESPUESTA DEL GET: " + response.toString() + "\n\n");

    }


    public void postMethodXML() throws SQLConnectionException {
        String uuid = "";
        String resourceChain = request.getResourceChain();
        String contentRequest = request.getContent();
        String resource = request.getResourceParameters().get("uuid");
        if(resource == null){
            uuid = UUID.randomUUID().toString();
        }

        response.setVersion(request.getHttpVersion());
        if (!contentRequest.contains("xml=")) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!dao.exists(uuid)) {
            String newContent = contentRequest.replace("xml=", "");
            dao.create(uuid, newContent);
            String uuidHyperlink = "<a href=\"xml?uuid=" + uuid + "\">" + uuid + "</a>";
            response.setContent(uuidHyperlink);
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", "text/xml");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
    }

    public void deleteMethodXML() throws SQLConnectionException {
        String resource = request.getResourceParameters().get("uuid");
        response.setVersion(request.getHttpVersion());
        if (dao.exists(resource)) {
            dao.delete(resource);
            response.setStatus(HTTPResponseStatus.S200);
        } else if (!dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S404);
        } else if (!validResource()) {
            response.setStatus(HTTPResponseStatus.S400);
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }

    }
    public HTTPResponse getResponseXML() {
        return response;
    }
}

