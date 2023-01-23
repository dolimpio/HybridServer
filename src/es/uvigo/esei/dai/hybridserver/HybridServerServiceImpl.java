package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.daos.interfaces.HTMLDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XMLDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XSDDAO;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XSLTDAO;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HybridServerService")
public class HybridServerServiceImpl implements HybridServerService{

        public HTMLDAO daoHTML;
        public XMLDAO daoXML;
        public XSLTDAO daoXSLT;
        public XSDDAO daoXSD;

    public HybridServerServiceImpl(HTMLDAO daoHTML, XMLDAO daoXML, XSLTDAO daoXSLT, XSDDAO daoXSD){
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
        return daoHTML.get(uuid);
    }

    @Override
    public String getXML(String uuid) throws SQLConnectionException {
        return daoXML.get(uuid);
    }

    @Override
    public String getXSD(String uuid) throws SQLConnectionException {
        return daoXSD.get(uuid);
    }

    @Override
    public String getXSLT(String uuid) throws SQLConnectionException {
        return daoXSLT.get(uuid);
    }

    @Override
    public String getXSDwithXSLT(String uuid) throws SQLConnectionException {
        String xsdUUID = daoXSLT.getXSD(uuid);
        return daoXSD.get(xsdUUID);
    }
    
}
