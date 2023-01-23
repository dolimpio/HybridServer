package es.uvigo.esei.dai.hybridserver.controllers;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.SQLConnectionException;
import es.uvigo.esei.dai.hybridserver.configurations.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXML;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXSD;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXSLT;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XMLDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XSDDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XSLTDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class XSLTController {
    private HTTPRequest request;
    private HTTPResponse response;
    private DAODBXSLT dao;
    private DAODBXSD daoXSD;
    private List<ServerConfiguration> servers;

    public XSLTController(DAODBXSLT dao, DAODBXSD daoXSD,List<ServerConfiguration> servers) {
        this.dao = dao;
        this.daoXSD = daoXSD;
        this.response = new HTTPResponse();
        this.servers = servers;
    }

    public void setRequest(HTTPRequest request) {
        this.request = request;
        this.response.setVersion(request.getHttpVersion());

    }

    public boolean validResource() {
        return request.getResourceChain().contains("xslt");
    }

    public String getUUID(String content) {
        UUID uuid = UUID.fromString(content);
        return uuid.toString();
    }

    public void getMethodXSLT() throws SQLConnectionException {

        System.out.println("\n\nCONTENIDO DEL GET: " + request.toString() + "\n\n");

        String resourceChain = request.getResourceChain();
        System.out.println("\n\nCADENA DE RECURSO DEL GET xslt: " + resourceChain + "\n\n");

        String contentRequest = request.getContent();
        System.out.println("\n\nCONTENIDO DEL GET: " + contentRequest + "\n\n");

        String resource = request.getResourceParameters().get("uuid");
        System.out.println("\n\nRECURSO DEL GET: " + resource + "\n\n");

        if (request.getResourceName().isEmpty()) {
            response.setStatus(HTTPResponseStatus.S200);
            String pageContent = "Hybrid Server => Mirandios Carou Laiño, David Olímpico Silva";
            String welcomePage = "<html><head> <title>Root Page</title> </head>"
                    + "<body>" + "<h1>" + pageContent + "</h1>" + "</body></html>";
            response.setContent(welcomePage);
        } else if (request.getResourceName().equals("xslt") && !request.getResourceChain().contains("uuid")) {
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

            response.putParameter("Content-Type", "application/xml");
        } else if (!validResource()) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S404);
        } else if (dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent(dao.get(resource));
            response.putParameter("Content-Type", "application/xml");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
        System.out.println("\n\nRESPUESTA DEL GET: " + response.toString() + "\n\n");

    }

    public void postMethodXSLT() throws SQLConnectionException {

        String uuid = "";
        String contentRequest = request.getContent();
        String resource = request.getResourceParameters().get("uuid");
        String xsd = request.getResourceParameters().get("xsd");

        System.out.println("\n\nCONTENIDO DEL POST: " + request.toString() + "\n\n");

        String resourceChain = request.getResourceChain();
        System.out.println("\n\nCADENA DE RECURSO DEL POST xslt: " + resourceChain + "\n\n");

        System.out.println("\n\nCONTENIDO DEL POST: " + contentRequest + "\n\n");

        System.out.println("\n\nRECURSO DEL POST: " + resource + "\n\n");

        System.out.println("\n\nXSD: " + xsd + " -----------\n\n");

        if (resource == null) {
            uuid = UUID.randomUUID().toString();
        }

/* 
        System.out.println("\n\nVALOR BANDERA XSD: " + xsdParameter + " -----------\n\n");
        System.out.println("\n\nVALOR BANDERA CONTENT: " + xsdParameter + " -----------\n\n"); */

        if(contentRequest == null || xsd == null){

            response.setStatus(HTTPResponseStatus.S400);

        }else if(!daoXSD.exists(xsd)){
            System.out.println("\n\nporque entras aqui?: " + xsd + " -----------\n\n");

            response.setStatus(HTTPResponseStatus.S404);

        }else if (!dao.exists(uuid) && daoXSD.exists(xsd)) {

            String[] splitContent = contentRequest.split("xslt=");
            String newContent = splitContent[1];
            System.out.println("\n\nUEVO CONTENIDO DEL POST: " + newContent + " --- PIROLA ---\n\n");

            dao.create(uuid, newContent, xsd);

            String uuidHyperlink = "<a href=\"xslt?uuid=" + uuid + "\">" + uuid + "</a>";
            response.setContent(uuidHyperlink);
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", "application/xml");

        } else{
            response.setStatus(HTTPResponseStatus.S500);
        }
    }

    public void deleteMethodXSLT() throws SQLConnectionException {
        String resource = request.getResourceParameters().get("uuid");
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

    public HTTPResponse getResponseXSLT() {
        return response;
    }
}
