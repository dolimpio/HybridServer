package es.uvigo.esei.dai.hybridserver;

import java.util.Set;

public interface DAO {

    public void create(String uuid, String content) throws SQLConnectionException;

    public void update(String uuid, String content) throws SQLConnectionException;

    public void delete(String uuid) throws SQLConnectionException;

    public String get(String uuid) throws SQLConnectionException;

    public Set<String> list() throws SQLConnectionException;

    public boolean exists(String uuid) throws SQLConnectionException;

}