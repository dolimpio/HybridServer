package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface HybridServerService {
    //Usamos Strings para evitar problemas...
    @WebMethod
    public String getListHTML() throws SQLConnectionException;
    
    @WebMethod
    public String getListXML() throws SQLConnectionException;
    
    @WebMethod
    public String getListXSD() throws SQLConnectionException;
    
    @WebMethod
    public String getListXSLT() throws SQLConnectionException;

    @WebMethod
    public String getHTML(String uuid) throws SQLConnectionException;
    
    @WebMethod
    public String getXML(String uuid) throws SQLConnectionException;
    
    @WebMethod
    public String getXSD(String uuid) throws SQLConnectionException;
    
    @WebMethod
    public String getXSLT(String uuid) throws SQLConnectionException;

    public String getXSDwithXSLT(String uuid) throws SQLConnectionException;

}
