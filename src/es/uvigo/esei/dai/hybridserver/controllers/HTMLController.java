package es.uvigo.esei.dai.hybridserver.controllers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Iterator;

import es.uvigo.esei.dai.hybridserver.SQLConnectionException;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBHTML;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.HTMLDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HTMLController {
    private HTTPRequest request;
    private HTMLDAO dao;
    private HTTPResponse response;

    public HTMLController(HTTPRequest request) {
        this.request = request;
        this.dao = new DAODBHTML();
        response = new HTTPResponse();
    }

    public void getMethodHTML() throws SQLConnectionException {
        Map<String, String> resourcesMap = request.getResourceParameters();
        String resource = request.getResourceParameters().get("uuid");
        response.setVersion(request.getHttpVersion());
        if (request.getResourceName().isEmpty()) {
            response.setStatus(HTTPResponseStatus.S200);
            String pageContent = "Hybrid Server => Mirandios Carou Laiño, David Olímpico Silva";
            String welcomePage = "<html><head> <title>Root Page</title> </head>"
                    + "<body>" + "<h1>" + pageContent + "</h1>" + "</body></html>";
            response.setContent(welcomePage);
        } else if (request.getResourceName().equals("html") && !request.getResourceChain().contains("uuid")) {
            System.out.println("esta resource name no vale baby" + request.getResourceName());
            System.out.println("esta resource chain no vale baby" + request.getResourceParameters());
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

            response.putParameter("Content-Type:", "text/html; charset=ISO-8859-1");
        } else if (!request.validResource()) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S404);
        } else if (dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent(dao.get(resource));
            response.putParameter("Content-Type:", "text/html; charset=ISO-8859-1");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
    }

    public void postMethodHTML() throws SQLConnectionException {
        String resource = request.getResourceParameters().get("uuid");
        String contentRequest = request.getContent();
        response.setVersion(request.getHttpVersion());
        if (!request.validResource() || !contentRequest.contains("html=")) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!dao.exists(resource)) {
            String newContent = contentRequest.replace("html=", "");
            String uuid = UUID.randomUUID().toString();
            dao.create(uuid, newContent);
            String uuidHyperlink = "<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>";
            response.setContent(uuidHyperlink);
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type:", "text/html; charset=ISO-8859-1");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
    }

    public void deleteMethodHTML() throws SQLConnectionException {
        String resource = request.getResourceParameters().get("uuid");
        response.setVersion(request.getHttpVersion());
        if (dao.exists(resource)) {
            dao.delete(resource);
            response.setStatus(HTTPResponseStatus.S200);
        } else if (!dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S404);
        } else if (!request.validResource()) {
            response.setStatus(HTTPResponseStatus.S400);
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
    }

    public HTTPResponse getResponseHTML() {
        return response;
    }
}
