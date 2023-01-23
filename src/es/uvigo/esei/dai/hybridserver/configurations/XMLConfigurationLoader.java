/**
 *  HybridServer
 *  Copyright (C) 2022 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver.configurations;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLConfigurationLoader {

	//Constructor vacio
	public XMLConfigurationLoader(){
	}

	public Configuration load(File xmlFile) throws Exception {
		Configuration myconfi = new Configuration();
		try {
			System.out.println("PRE PARSE AND");
			myconfi = parseAndValidateWithExternalXSD(xmlFile, "./configuration.xsd", new GeneralContentHandler());
		} catch (Exception e) {
			throw new Exception("Error al cargar el configuration");
		}
		System.out.println(myconfi.toString());
		return myconfi;
	}
	public static String transformWithXSLT(File xml, File xslt)throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslt));
		StringWriter writer = new StringWriter();
		transformer.transform( new StreamSource(xml), new StreamResult(writer) );
		return writer.toString();
	}
	public static Configuration parseAndValidateWithExternalXSD( File xmlPath, String schemaPath, GeneralContentHandler handler) throws ParserConfigurationException, SAXException, IOException {

		handler = new GeneralContentHandler();
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new File(schemaPath));
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(false);
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema);
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.setErrorHandler(new SimpleErrorHandler());

		try (FileReader fileReader = new FileReader(xmlPath)) {
			xmlReader.parse(new InputSource(fileReader));
		}
		return handler.getConfiguration();
	}
}