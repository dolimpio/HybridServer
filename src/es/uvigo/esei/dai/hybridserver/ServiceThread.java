package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import es.uvigo.esei.dai.hybridserver.controllers.HTMLController;
import es.uvigo.esei.dai.hybridserver.controllers.XMLController;
import es.uvigo.esei.dai.hybridserver.controllers.XSDController;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public ServiceThread(Socket clientSocket, DAO dao) throws IOException {
        this.socket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run() {

        HTTPRequest request;
        HTTPRequestMethod method;
        try {
            request = new HTTPRequest(reader);
            method = request.getMethod();
            HTTPResponse response = new HTTPResponse();

            try {
                String resourceTypeDoc = request.getResourceName();
                System.out.println("AQUI EMPEZAMOS LOS PRINTS");
                System.out.println("TIPO DEL RECURSO SOLICITADO: " + resourceTypeDoc);

                HTMLController htmlController = new HTMLController();
                XMLController xmlController = new XMLController();
                XSDController xsdController = new XSDController();

                System.out.println("AQUI instanciamos LOS controllers");

                if(resourceTypeDoc.equals("html")){
                    System.out.println("AQUI VAMOS A SETEAR");

                   htmlController.setRequest(request);
                   System.out.println("AQUI HEMOS SERTEADO");

                }else if(resourceTypeDoc.equals("xml")){


                    xmlController.setRequest(request);
                }else if (resourceTypeDoc.equals("xsd")){
                    xsdController.setRequest(request);
                }
                
                switch (method.toString()) {
                    // La vida es dura. Después de todo, te mata (Katherine Hepburn)
                    case "GET":
                        if(resourceTypeDoc.equals("html")){
                            htmlController.getMethodHTML();
                            response = htmlController.getResponseHTML();
                        }else if(resourceTypeDoc.equals("xml")){
                            xmlController.getMethodXML();
                            response = xmlController.getResponseXML();
                        }else if(resourceTypeDoc.equals("xsd")){
                            xsdController.getMethodXSD();
                            response = xsdController.getResponseXSD();
                        }
                        
                        break;

                    // No te tomes la vida demasiado en serio. No saldrás de ella con vida (Elbert
                    // Hubbard)

                    case "POST":
                        if(resourceTypeDoc.equals("html")){
                            htmlController.postMethodHTML();
                            response = htmlController.getResponseHTML();
                        }else if(resourceTypeDoc.equals("xml")){
                            xmlController.postMethodXML();
                            response = xmlController.getResponseXML();
                        }else if(resourceTypeDoc.equals("xsd")){
                            xsdController.postMethodXSD();
                            response = xsdController.getResponseXSD();
                        }
                        
                        break;

                    // La confianza es 10% trabajo y 90% delirio (Tina Fey)

                    case "DELETE":
                        if(resourceTypeDoc.equals("html")){
                            htmlController.deleteMethodHTML();
                            response = htmlController.getResponseHTML();
                        }else if(resourceTypeDoc.equals("xml")){
                            xmlController.deleteMethodXML();
                            response = xmlController.getResponseXML();
                        }else if(resourceTypeDoc.equals("xsd")){
                            xsdController.deleteMethodXSD();
                            response = xsdController.getResponseXSD();
                        }
                        break;

                    default:
                        break;
                }
            } catch (SQLConnectionException e) {
                response.setVersion(request.getHttpVersion());
                response.setStatus(HTTPResponseStatus.S500);
            }
            // Mucho animo Miguel, ya queda poco!!
            writer.println(response.toString());
            writer.flush();
            socket.close();
        } catch (

        IOException e) {
            e.printStackTrace();
        } catch (HTTPParseException e) {
            e.printStackTrace();
        }
    }

}