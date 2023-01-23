package es.uvigo.esei.dai.hybridserver.controllers;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.SQLConnectionException;
import es.uvigo.esei.dai.hybridserver.ServerConnection;
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
    private List<HybridServerService> services;
    private ServerConnection connection;


    public XMLController(DAODBXML daoXML, DAODBXSLT daoXSLT, DAODBXSD daoXSD, List<ServerConfiguration> servers, String webService){
        this.daoXML = daoXML;
        this.daoXSLT = daoXSLT;
        this.daoXSD = daoXSD;
        this.response = new HTTPResponse();
        this.servers = servers;
        this.services = new ArrayList<HybridServerService>();
        this.connection = new ServerConnection(this.servers, webService);
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
    public void getMethodXML() throws SQLConnectionException, MalformedURLException {

        System.out.println("\n\nCONTENIDO DEL GET: " + request.toString() + "\n\n");
        
        String resourceChain = request.getResourceChain();
        System.out.println("\n\nCADENA DE RECURSO DEL GET xml: " + resourceChain + "\n\n");

        String contentRequest = request.getContent();
        System.out.println("\n\nCONTENIDO DEL GET: " + contentRequest + "\n\n");

        String resource = request.getResourceParameters().get("uuid");
        System.out.println("\n\nRECURSO DEL GET: " + resource + "\n\n");

        
        String resourceXSLT = request.getResourceParameters().get("xslt");
        System.out.println("\n\nRECURSO DEL GET CUANDO SE PIDE un xslt -----: " + resourceXSLT + "\n\n");
            
        if (request.getResourceName().equals("xml") && !request.getResourceChain().contains("uuid")) {
            response.setStatus(HTTPResponseStatus.S200);
            String htmlPage = listadoXML();
            response.setContent(htmlPage);
            response.putParameter("Content-Type", "text/html");

        }else if(request.getResourceChain().contains("uuid") && request.getResourceChain().contains("xslt")){
            System.out.println("ENTRAAAAAAAAA");
           String xml = "";
           String xslt = "";
            String xsd = "";
            String xsdUUID = "";
            
            String recursosXML = listadoXML();            
            String recursosXSLT = listadoXSLT();
            String recursosXSD = listadoXSD();
            
            if(recursosXML.contains(resource) && recursosXSLT.contains(resourceXSLT)) {
            	if(daoXML.exists(resource)) {
                    xml = daoXML.get(resource);
            	}else{
            		xml = getRemotoXML(resource);
            	}
            	
            	
            	
            	if(daoXSLT.exists(resourceXSLT)) {
            		xsdUUID = daoXSLT.getXSD(resourceXSLT);	
            	}else {
            		xsdUUID = getRemotoXSDUUIDconXSLT(resourceXSLT);
            	}
            	
            	
            	if(recursosXSD.contains(xsdUUID)) {
                	if(daoXSD.exists(xsdUUID)) {
                		xsd = daoXSD.get(xsdUUID);
                	}else {
                		xsd = getRemotoXSD(xsdUUID);
                	}
            	}else {
                    response.setStatus(HTTPResponseStatus.S400);

            	}

                XMLValidatorAndTransformer validator = new XMLValidatorAndTransformer();
                try {

                    validator.validation(xml, xsd);

                    if(daoXSLT.exists(resourceXSLT)) {
                    	xslt = daoXSLT.get(resourceXSLT);
                    }else {
                    	xslt = getRemotoXSLT(resourceXSLT);
                    }
                    
                    
                    
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
                response.setStatus(HTTPResponseStatus.S404);
            }

            /*if(daoXML.exists(resource) && daoXSLT.exists(resourceXSLT)) {
                String xml = daoXML.get(resource);
                String xslt = daoXSLT.get(resourceXSLT);
                String xsdUUID = daoXSLT.getXSD(resourceXSLT);
                
                if(daoXSD.exists(xsdUUID)) {
                    String xsd = daoXSD.get(xsdUUID);
                    XMLValidatorAndTransformer validator = new XMLValidatorAndTransformer();
                    try {

                        validator.validation(xml, xsd);

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
                }else if(recursosXSD.contains(xsdUUID)) {
                	String xsd = "";
                	
                    if(servers != null) {
                        services = connection.connectToServers();
                        for (HybridServerService serverIt : services) {
                        	String currentContent = serverIt.getXSDwithXSLT(resourceXSLT);
                        	if(!currentContent.isEmpty()) {
                        		xsd = currentContent;
                        	}
                        }
                    }
                    
                    
                    XMLValidatorAndTransformer validator = new XMLValidatorAndTransformer();
                    try {

                        validator.validation(xml, xsd);

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
                
            }else if(recursosXML.contains(resource) && recursosXSLT.contains(recursosXSLT)) {
            	String xml = "";
            	String xslt = "";
            	
                if(servers != null) {
                    services = connection.connectToServers();
                    for (HybridServerService serverIt : services) {
                    	String currentXML = serverIt.getXML(resource);
                    	String currentXSLT = serverIt.getXSLT(resourceXSLT);
                    	if(!currentXML.isEmpty()) {
                    		xml = currentXML;
                    	}
                    	if(!currentXSLT.isEmpty()) {
                    		xslt = currentXSLT;
                    	}
                    }
                }
                
            	
            }else {
                response.setStatus(HTTPResponseStatus.S404);
            }
            
            
            
            
            if(daoXML.exists(resource) && daoXSLT.exists(resourceXSLT)) {
                String xml = daoXML.get(resource);
                String xslt = daoXSLT.get(resourceXSLT);

                String xsdUUID = daoXSLT.getXSD(resourceXSLT);
                
                if(daoXSD.exists(xsdUUID)) {
                    String xsd = daoXSD.get(xsdUUID);
                    XMLValidatorAndTransformer validator = new XMLValidatorAndTransformer();
                    try {

                        validator.validation(xml, xsd);

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

            }*/

        } else if (!validResource() ) {
            response.setStatus(HTTPResponseStatus.S400);
        }  else if (daoXML.exists(resource)) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent(daoXML.get(resource));
            response.putParameter("Content-Type", "application/xml");
        }else if (!daoXML.exists(resource)) {
        	String todosRecursos = listadoXML();
        	if(todosRecursos.contains(resource)) {
                if(servers != null) {
                    services = connection.connectToServers();
                    for (HybridServerService serverIt : services) {
                    	String currentContent = serverIt.getXML(resource);
                    	if(!currentContent.isEmpty()) {
                            response.setContent(currentContent);
                            response.setStatus(HTTPResponseStatus.S200);
                            response.putParameter("Content-Type", "application/xml");
                    	}
                    }
                }    
        	}else {
                response.setStatus(HTTPResponseStatus.S404);
        	}
        } else {
            response.setStatus(HTTPResponseStatus.S500);
        }
        System.out.println("\n\nRESPUESTA DEL GET: " + response.toString() + "\n\n");

    }
    public String getRemotoXML(String uuid) throws MalformedURLException, SQLConnectionException {
        String xml = "";
    	if(servers != null) {
            services = connection.connectToServers();
            for (HybridServerService serverIt : services) {
            	String currentContent = serverIt.getXML(uuid);
            	if(!currentContent.isEmpty()) {
            		xml = currentContent;
            	}
            }
    	}
    	return xml;
    	
    }
    
    public String getRemotoXSD(String uuid) throws MalformedURLException, SQLConnectionException {
    	String xsd = "";
    	
        if(servers != null) {
            services = connection.connectToServers();
            for (HybridServerService serverIt : services) {
            	String currentContent = serverIt.getXSD(uuid);
            	if(!currentContent.isEmpty()) {
            		xsd = currentContent;
            	}
            }
        }
    	return xsd;
    	
    }
   
    public String getRemotoXSLT(String uuid) throws MalformedURLException, SQLConnectionException {
    	String xslt = "";
    	
        if(servers != null) {
            services = connection.connectToServers();
            for (HybridServerService serverIt : services) {
            	String currentContent = serverIt.getXSLT(uuid);
            	if(!currentContent.isEmpty()) {
            		xslt = currentContent;
            	}
            }
        }
    	return xslt;
    	
    }
    
    
    public String getRemotoXSDUUIDconXSLT(String uuidXSLT) throws MalformedURLException, SQLConnectionException {
    	
    	String xsdUUID = "";
    	
        if(servers != null) {
            services = connection.connectToServers();
            for (HybridServerService serverIt : services) {
            	String currentContent = serverIt.getXSDwithXSLT(uuidXSLT);
            	if(!currentContent.isEmpty()) {
            		xsdUUID = currentContent;
            	}
            }
        }
    	return xsdUUID;
    	
    }
    public String listadoXML() throws MalformedURLException, SQLConnectionException {
        Set<String> set = daoXML.list();
        Iterator<String> it = set.iterator();
        String listaContent = "";
        while (it.hasNext()) {
            String uuidProximo = it.next().toString();
            //listaContent += "<li>" + uuidProximo + "</li>";
            listaContent +=  uuidProximo + " //"; 
        }
        if(servers != null) {
       services = connection.connectToServers();
       for (HybridServerService serverIt : services) {
 	    listaContent += serverIt.getListXML() + " // ";
       }
        }
    	return listaContent;
    }
    
    public String listadoXSLT() throws MalformedURLException, SQLConnectionException {
        Set<String> set = daoXSLT.list();
        Iterator<String> it = set.iterator();
        String listaContent = "";
        while (it.hasNext()) {
            String uuidProximo = it.next().toString();
            //listaContent += "<li>" + uuidProximo + "</li>";
            listaContent +=  uuidProximo + " //"; 
        }
        if(servers != null) {
       services = connection.connectToServers();
       for (HybridServerService serverIt : services) {
 	    listaContent += serverIt.getListXSLT() + " // ";
       }
        }
    	return listaContent;
    }
    
    public String listadoXSD() throws MalformedURLException, SQLConnectionException {
        Set<String> set = daoXSD.list();
        Iterator<String> it = set.iterator();
        String listaContent = "";
        while (it.hasNext()) {
            String uuidProximo = it.next().toString();
            //listaContent += "<li>" + uuidProximo + "</li>";
            listaContent +=  uuidProximo + " //"; 
        }
        if(servers != null) {
       services = connection.connectToServers();
       for (HybridServerService serverIt : services) {
 	    listaContent += serverIt.getListXSD() + " // ";
       }
        }
    	return listaContent;
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

