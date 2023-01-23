package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBHTML;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXML;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXSD;
import es.uvigo.esei.dai.hybridserver.daos.implementations.DAODBXSLT;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.HTMLDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XMLDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XSDDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XSLTDAO;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HybridServerService")
public class HybridServerServiceImpl implements HybridServerService{

        public DAODBHTML daoHTML;
        public DAODBXML daoXML;
        public DAODBXSLT daoXSLT;
        public DAODBXSD daoXSD;

    public HybridServerServiceImpl(DAODBHTML daoHTML, DAODBXML daoXML, DAODBXSLT daoXSLT, DAODBXSD daoXSD){

        this.daoHTML = daoHTML;
        this.daoXML = daoXML;
        this.daoXSLT = daoXSLT;
        this.daoXSD = daoXSD;
    }

    @Override
    public String getListHTML() throws SQLConnectionException {

        String result = String.join(", ", new ArrayList<>(daoHTML.list()));
        return result;
    }

    @Override
    public String getListXML() throws SQLConnectionException {
        String result = String.join(", ", new ArrayList<>(daoXML.list()));
        return result; 
    }

    @Override
    public String getListXSD() throws SQLConnectionException {
        String result = String.join(", ", new ArrayList<>(daoXSD.list()));
        return result;   
    }

    @Override
    public String getListXSLT() throws SQLConnectionException {
        String result = String.join(", ", new ArrayList<>(daoXSLT.list()));
        return result;
    }

    @Override
    public String getHTML(String uuid) throws SQLConnectionException {
    	String toret = "";
    	if(daoHTML.exists(uuid)) {
    		toret = daoHTML.get(uuid);
    	}
        return toret;
    }

    @Override
    public String getXML(String uuid) throws SQLConnectionException {
    	String toret = "";
    	if(daoXML.exists(uuid)) {
    		toret = daoXML.get(uuid);
    	}
        return toret;
    }

    @Override
    public String getXSD(String uuid) throws SQLConnectionException {
    	String toret = "";
    	if(daoXSD.exists(uuid)) {
    		toret = daoXSD.get(uuid);
    	}
        return toret;
    }

    @Override
    public String getXSLT(String uuid) throws SQLConnectionException {
    	String toret = "";
    	if(daoXSLT.exists(uuid)) {
    		toret = daoXSLT.get(uuid);
    	}
        return toret;
    }

    @Override
    public String getXSDwithXSLT(String uuid) throws SQLConnectionException {
    	String toret = "";
    	if(daoXSLT.exists(uuid)) {
            toret = daoXSLT.getXSD(uuid);
            //if(daoXSD.exists(xsdUUID)) {
        		//toret = daoXSD.get(uuid);

            //}
    	}
 
        return toret;
    }
    
}
