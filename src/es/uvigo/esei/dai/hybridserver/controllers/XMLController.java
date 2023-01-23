package es.uvigo.esei.dai.hybridserver.controllers;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.SQLConnectionException;
import es.uvigo.esei.dai.hybridserver.configurations.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.configurations.XMLConfigurationLoader;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXML;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXSD;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXSLT;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XMLDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.xmlUtil.XMLValidatorAndTransformer;

public class XMLController {
    private HTTPRequest request;
    private HTTPResponse response;
    private DAODBXML daoXML;
    private DAODBXSLT daoXSLT;
    private DAODBXSD daoXSD; 
    private List<ServerConfiguration> servers;

    public XMLController(DAODBXML daoXML,List<ServerConfiguration> servers){
        this.daoXML = daoXML;
        daoXSLT = new DAODBXSLT();
        daoXSD = new DAODBXSD();  
        this.response = new HTTPResponse();
        this.servers = servers;
    }
    public void setRequest(HTTPRequest request){
        this.request = request;
        this.response.setVersion(request.getHttpVersion());

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

        
        String resourceXSLT = request.getResourceParameters().get("xslt");
        System.out.println("\n\nRECURSO DEL GET CUANDO SE PIDE un xslt -----: " + resourceXSLT + "\n\n");
            
        if (request.getResourceName().isEmpty()) {
            response.setStatus(HTTPResponseStatus.S200);
            String pageContent = "Hybrid Server => Mirandios Carou Laiño, David Olímpico Silva";
            String welcomePage = "<htmL><head> <title>Root Page</title> </head>"
                    + "<body>" + "<h1>" + pageContent + "</h1>" + "</body></html>";
            response.setContent(welcomePage);
        } else if (request.getResourceName().equals("xml") && !request.getResourceChain().contains("uuid")) {
            response.setStatus(HTTPResponseStatus.S200);
            Set<String> set = daoXML.list();
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

        }else if(request.getResourceChain().contains("uuid") && request.getResourceChain().contains("xslt")){
            System.out.println("ENTRAAAAAAAAA");

            if(daoXML.exists(resource) && daoXSLT.exists(resourceXSLT)) {
                String xml = daoXML.get(resource);
                String xsdUUID = daoXSLT.getXSD(resourceXSLT);
                
                if(daoXSD.exists(xsdUUID)) {
                    String xsd = daoXSD.get(xsdUUID);
                    XMLValidatorAndTransformer validator = new XMLValidatorAndTransformer();
                    try {

                        validator.validation(xml, xsd);

                        String xslt = daoXSLT.get(resourceXSLT);
                        try {
                            String html = validator.transformWithXSLT(xml, xslt);
                            response.setContent(html);
    
                        } catch (TransformerException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        response.setStatus(HTTPResponseStatus.S200);
                        response.putParameter("Content-Type", "text/html");      

                        
                    } catch (Exception e) {
                        response.setStatus(HTTPResponseStatus.S400);
                        e.printStackTrace();
                    }
                }else {
                    response.setStatus(HTTPResponseStatus.S400);
                }
            	
            }else {
                response.setStatus(HTTPResponseStatus.S404);

            }

        } else if (!validResource() ) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!daoXML.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S404);
        } else if (daoXML.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent(daoXML.get(resource));
            response.putParameter("Content-Type", "application/xml");
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

        if (!contentRequest.contains("xml=")) {
            response.setStatus(HTTPResponseStatus.S400);
        } else if (!daoXML.exists(uuid)) {
            String newContent = contentRequest.replace("xml=", "");
            daoXML.create(uuid, newContent);
            String uuidHyperlink = "<a href=\"xml?uuid=" + uuid + "\">" + uuid + "</a>";
            response.setContent(uuidHyperlink);
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", "application/xml");
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
    }

    public void deleteMethodXML() throws SQLConnectionException {
        String resource = request.getResourceParameters().get("uuid");

        if (daoXML.exists(resource)) {
            daoXML.delete(resource);
            response.setStatus(HTTPResponseStatus.S200);
        } else if (!daoXML.exists(resource)) {
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

