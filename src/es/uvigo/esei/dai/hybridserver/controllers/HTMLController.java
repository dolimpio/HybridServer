package es.uvigo.esei.dai.hybridserver.controllers;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


import es.uvigo.esei.dai.hybridserver.SQLConnectionException;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBHTML;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.HTMLDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
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
	public boolean validResource() {
		return request.getResourceChain().contains("html");
	}

    public String getUUID(String content) {
        UUID uuid = UUID.fromString(content);
		return uuid.toString();
	}

    public void getMethodHTML() throws SQLConnectionException {

        System.out.println("\n\nCONTENIDO DEL GET: " + request.toString() + "\n\n");
        
        String resourceChain = request.getResourceChain();
        System.out.println("\n\nCADENA DE RECURSO DEL GET: " + resourceChain + "\n\n");

        String contentRequest = request.getContent();
        System.out.println("\n\nCONTENIDO DEL GET: " + contentRequest + "\n\n");

        String resource = request.getResourceParameters().get("uuid");
        System.out.println("\n\nRECURSO DEL GET: " + resource + "\n\n");


        Map<String, String> resourcesMap = request.getResourceParameters();
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

            response.putParameter("Content-Type", "text/html");
        } else if (!validResource()) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S404);
        } else if (dao.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent(dao.get(resource));
            response.putParameter("Content-Type", "text/html");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
        System.out.println("\n\nRESPUESTA DEL GET: " + response.toString() + "\n\n");

    }


    public void postMethodHTML() throws SQLConnectionException {
        String uuid = "";

        System.out.println("\n\nCONTENIDO DEL POST: " + request.toString() + "\n\n");
        
        String resourceChain = request.getResourceChain();
        System.out.println("\n\nCADENA DE RECURSO DEL POST: " + resourceChain + "\n\n");

        String contentRequest = request.getContent();
        System.out.println("\n\nCONTENIDO DEL POST: " + contentRequest + "\n\n");

        String resource = request.getResourceParameters().get("uuid");
        System.out.println("\n\nRECURSO DEL POST: " + resource + "\n\n");

        if(resource == null){
            uuid = UUID.randomUUID().toString();
        }

        response.setVersion(request.getHttpVersion());
        if (!contentRequest.contains("html=")) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!dao.exists(uuid)) {
            String newContent = contentRequest.replace("html=", "");
            System.out.println("\n\nUUID DEL POST: " + uuid + "\n\n");
            System.out.println("\n\nNUEVO CONTENIDO DEL POST: " + newContent + "\n\n");
            dao.create(uuid, newContent);
            String uuidHyperlink = "<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>";
            response.setContent(uuidHyperlink);
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", "text/html");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }

        System.out.println("\n\nRESPUESTA DEL POST: " + response.toString() + "\n\n");

    }

    public void deleteMethodHTML() throws SQLConnectionException {
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
        System.out.println("\n\nRESPUESTA DEL DELETE: " + response.toString() + "\n\n");

    }

    public HTTPResponse getResponseHTML() {
        return response;
    }
}
