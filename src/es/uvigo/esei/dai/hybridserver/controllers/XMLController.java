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
    private String resource;

    public XMLController(HTTPRequest request){
        this.request = request;
        this.dao = new DAODBXML();
        this.response = new HTTPResponse();
        this.resource = request.getResourceParameters().get("uuid");
    }
    private HTTPResponse get() throws SQLConnectionException{
        Map<String, String> resourcesMap = request.getResourceParameters();
        response.setVersion(request.getHttpVersion());

        if (!resourcesMap.containsKey("uuid")) {
            response.setStatus(HTTPResponseStatus.S200);
            String welcomePage ="<?xml version=1.0 ?>"+
                                    "<proyecto>"+
                                        "<title>Root Page</title>"+
                                        "<integrante1>Mirandios Carou Lainho</integrante1>"+
                                        "<integrante2>David Olimpico Silva</integrante1>"+
                                    "</proyecto>";
            response.setContent(welcomePage);

        } else if (!request.getResourceChain().contains("uuid") && request.getResourceName().equals("xml")) {

            response.setStatus(HTTPResponseStatus.S200);
            Set<String> set = dao.list();
            Iterator<String> it = set.iterator();
            String listaContent = "";

            while (it.hasNext()) {
                String uuidProximo = it.next().toString();
                listaContent += "<li>" + uuidProximo + "</li>";
            }

            String htmlPage = "<?xml version=1.0 ?>"+
                                "<proyecto>"+
                                "<title>Root Page</title>"+
                                listaContent+    
                                "</proyecto>";
            response.setContent(htmlPage);
            response.putParameter("Content-Type:", "text/xml; charset=ISO-8859-1");

        } else if (!request.validResource()) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S404);
        } else if (dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent(dao.get(resource));
            response.putParameter("Content-Type:", "text/xml; charset=ISO-8859-1");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
        return response;
    }


    private HTTPResponse post() throws SQLConnectionException {
        String contentRequest = request.getContent();
        response.setVersion(request.getHttpVersion());

        if (!request.validResource() || !contentRequest.contains("xml=")) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!dao.exists(resource)) {

            String newContent = contentRequest.replace("xml=", "");
            String uuid = UUID.randomUUID().toString();
            dao.create(uuid, newContent);
            String uuidHyperlink = "<a href=\"xml?uuid=" + uuid + "\">" + uuid + "</a>";
            response.setContent(uuidHyperlink);
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type:", "text/xml; charset=ISO-8859-1");

        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
        return response;
    }

    private HTTPResponse delete() throws SQLConnectionException {
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
        return response;
    }
}

