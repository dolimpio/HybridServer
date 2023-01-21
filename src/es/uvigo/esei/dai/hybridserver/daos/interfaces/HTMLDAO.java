package es.uvigo.esei.dai.hybridserver.daos.interfaces;

import java.util.Set;
import es.uvigo.esei.dai.hybridserver.SQLConnectionException;

public interface HTMLDAO {

    public void create(String uuid, String content) throws SQLConnectionException;

    public void update(String uuid, String content) throws SQLConnectionException;

    public void delete(String uuid) throws SQLConnectionException;

    public String get(String uuid) throws SQLConnectionException;

    public Set<String> list() throws SQLConnectionException;

    public boolean exists(String uuid) throws SQLConnectionException;

}
