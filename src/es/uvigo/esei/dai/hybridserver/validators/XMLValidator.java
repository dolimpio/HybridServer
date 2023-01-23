package es.uvigo.esei.dai.hybridserver.validators;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ContentHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import es.uvigo.esei.dai.hybridserver.configurations.SimpleErrorHandler;

public class XMLValidator {

    public XMLValidator(){

    }

/*     public boolean validation(String xml, String xsd) throws SAXException {
        // Creamos un SchemaFactory capaz de entender los esquemas de W3C.
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        //Cargamos el esquema (xsd)
        StringReader xsdReader = new StringReader(xsd);
        InputSource inputXSD = new InputSource(xsdReader);
        Schema schema = factory.newSchema(new StreamSource(inputXSD.getCharacterStream()));

        // Create a Validator object, which can be used to validate an instance document.
        Validator validator = schema.newValidator();

        // Validate the XML document against the schema.
        try {
            StringReader xmlReader = new StringReader(xml);
            InputSource inputXML = new InputSource(xmlReader);

            validator.validate(new StreamSource(inputXML.getCharacterStream()));
            System.out.println("XML document is valid");

        } catch (SAXException e) {
            System.out.println("XML document is not valid reason:" + e);
            return false;
        } catch (IOException e) {
            System.out.println("IO exception occured: " + e);
        }
        return true;
    } */

    public void validation(String xmlContent, String schemaContent) throws ParserConfigurationException, SAXException, IOException{
        //Convertimos el schema puro a source
        Source schemaSource = new StreamSource(new ByteArrayInputStream(schemaContent.getBytes()));
        
    
        // Construcción del schema
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaSource);
    
        // Construcción del parser del documento.
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(false);
        parserFactory.setNamespaceAware(true);
        parserFactory.setSchema(schema);
    
        // Se añade el manejador de errores
        SAXParser parser = parserFactory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(new DefaultHandler());
        xmlReader.setErrorHandler(new SimpleErrorHandler());
    
        // Parsing
        try (StringReader stringReader = new StringReader(xmlContent)) {
            xmlReader.parse(new InputSource(stringReader));
        }
    }

    public String transformWithXSLT(String xml, String xslt)throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslt));
		StringWriter writer = new StringWriter();
		transformer.transform( new StreamSource(xml), new StreamResult(writer) );
		return writer.toString();

	}
}