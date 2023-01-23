package es.uvigo.esei.dai.hybridserver.xmlUtil;

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
import java.io.ByteArrayOutputStream;
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

public class XMLValidatorAndTransformer {

    public XMLValidatorAndTransformer(){

    }

     public void validation(String xml, String xsd) throws SAXException, IOException {
        // Creamos un SchemaFactory capaz de entender los esquemas de W3C.
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        //Cargamos el XSD
        StringReader xsdReader = new StringReader(xsd);
        InputSource inputXSD = new InputSource(xsdReader);
        Schema schema = factory.newSchema(new StreamSource(inputXSD.getCharacterStream()));

        // Creamos el objeto validador
        Validator validator = schema.newValidator();

        // Validamos el XML con el XSD.

            StringReader xmlReader = new StringReader(xml);
            InputSource inputXML = new InputSource(xmlReader);

            validator.validate(new StreamSource(inputXML.getCharacterStream()));
            System.out.println("XML document is valid");

    } 


    public String transformWithXSLT(String xml, String xslt)throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(new ByteArrayInputStream(xslt.getBytes())));
		StringWriter writer = new StringWriter();
		transformer.transform( new StreamSource(new ByteArrayInputStream(xml.getBytes())), new StreamResult(writer) );
		
		return writer.toString();

	}
    
}