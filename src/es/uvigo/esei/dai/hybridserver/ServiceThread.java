package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private DAO dao;

    public ServiceThread(Socket clientSocket, DAO dao) throws IOException {
        this.socket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream());
        this.dao = dao;
    }

    @Override
    public void run() {

        HTTPRequest request;
        HTTPRequestMethod method;
        try {
            request = new HTTPRequest(reader);
            method = request.getMethod();
            HTTPResponse response = new HTTPResponse();
            Map<String, String> resourcesMap = request.getResourceParameters();

            String resource = request.getResourceParameters().get("uuid");

            try {
                switch (method.toString()) {

                    // La vida es dura. Después de todo, te mata (Katherine Hepburn)

                    case "GET":
                        response.setVersion(request.getHttpVersion());
                        if (!resourcesMap.containsKey("uuid") && !request.getResourceChain().contains("html")) {
                            response.setStatus(HTTPResponseStatus.S200);
                            String pageContent = "Hybrid Server => Mirandios Carou Laiño, David Olímpico Silva";
                            String welcomePage = "<html><head> <title>Root Page</title> </head>"
                                    + "<body>" + "<h1>" + pageContent + "</h1>" + "</body></html>";
                            response.setContent(welcomePage);
                        } else if (!request.getResourceChain().contains("uuid")
                                && request.getResourceName().equals("html")) {
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
                        break;

                    // No te tomes la vida demasiado en serio. No saldrás de ella con vida (Elbert
                    // Hubbard)

                    case "POST":
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
                        break;

                    // La confianza es 10% trabajo y 90% delirio (Tina Fey)

                    case "DELETE":
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HTTPParseException e) {
            e.printStackTrace();
        }
    }

}