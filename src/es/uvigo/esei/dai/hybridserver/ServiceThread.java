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

import es.uvigo.esei.dai.hybridserver.controllers.HTMLController;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBHTML;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXML;
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

            try {
                String resourceTypeDoc = request.getResourceName();
                HTMLController htmlController = new HTMLController(request);

                switch (method.toString()) {

                    // La vida es dura. Después de todo, te mata (Katherine Hepburn)

                    case "GET":
                        htmlController.getMethodHTML();
                        response = htmlController.getResponseHTML();
                        break;

                    // No te tomes la vida demasiado en serio. No saldrás de ella con vida (Elbert
                    // Hubbard)

                    case "POST":
                        htmlController.postMethodHTML();
                        response = htmlController.getResponseHTML();
                        break;

                    // La confianza es 10% trabajo y 90% delirio (Tina Fey)

                    case "DELETE":
                        htmlController.postMethodHTML();
                        response = htmlController.getResponseHTML();
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