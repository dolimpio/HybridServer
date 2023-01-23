package es.uvigo.esei.dai.hybridserver.configurations;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public class GeneralContentHandler implements ContentHandler{

    boolean httpOK;
    boolean webServiceURIOK;
    boolean numClientsOK;
    boolean userOK;
    boolean passwordStringOK;
    boolean urlOK;
    Configuration myConfiguration;
    List<ServerConfiguration> servers;
    ServerConfiguration myServerConfiguration;
    
    public GeneralContentHandler(){
        httpOK =  false;
        webServiceURIOK = false;
        numClientsOK = false;
        userOK = false;
        passwordStringOK = false;
        urlOK = false;
    }
    @Override
    public void setDocumentLocator(Locator locator) {
        // Queda vaciito
        
    }

    @Override
    public void startDocument() throws SAXException {
        myConfiguration = new Configuration();
        servers = new ArrayList<>();
        
    }

    @Override
    public void endDocument() throws SAXException {  
        List<ServerConfiguration> aux = new ArrayList<>();  
        for (ServerConfiguration serverConfiguration : servers) {
            //Quitamos los servidores que tienen el nombre "Down Server" porque no funcionan y asi ya no se conecta"
            if(!(serverConfiguration.getName().equals("Down Server"))){
                aux.add(serverConfiguration);
            }
        }
        this.myConfiguration.setServers(aux);
        
    }


    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {}
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {}

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch (localName) {
            case "http":
                httpOK =  true;
            break;
            case "webservice":
                webServiceURIOK = true;
            break;
            case "numClients":
                numClientsOK = true;            
            break;
            case "user":
                userOK = true;
            break;
            case "password":
                passwordStringOK = true;
            break;
            case "url":
                urlOK = true;
            break;
            case "server":
                myServerConfiguration = new ServerConfiguration(atts.getValue("name"),atts.getValue("wsdl"),atts.getValue("namespace"),atts.getValue("service"),atts.getValue("httpAddress"));
                servers.add(myServerConfiguration);
            break;
            default:
                break;
        }
        
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.myConfiguration.setServers(servers);
    }
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String txt = new String(ch, start, length);

        if(httpOK) {
            this.myConfiguration.setHttpPort(Integer.parseInt(txt));
            httpOK=false;
        }else if(webServiceURIOK) {
            this.myConfiguration.setWebServiceURL(txt);
            webServiceURIOK=false;
        }else if(numClientsOK) {
            this.myConfiguration.setNumClients(Integer.parseInt(txt));
            numClientsOK=false;
        }else if(userOK) {
            this.myConfiguration.setDbUser(txt);
            userOK=false;
        }else if(passwordStringOK) {
            this.myConfiguration.setDbPassword(txt);
            passwordStringOK=false;
        }else if(urlOK) {
            this.myConfiguration.setDbURL(txt);
            urlOK=false;
        }
        
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
    @Override
    public void processingInstruction(String target, String data) throws SAXException {}
    @Override
    public void skippedEntity(String name) throws SAXException {}

    public Configuration getConfiguration(){
        return this.myConfiguration;
    }
}
