package es.uvigo.esei.dai.hybridserver.daos.implementations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import es.uvigo.esei.dai.hybridserver.SQLConnectionException;
import es.uvigo.esei.dai.hybridserver.daos.interfaces.XSDDAO;

public class DAODBXSD implements XSDDAO {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private int service_port = 0;

    public DAODBXSD(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    // Trabajar no es malo, lo malo es tener que trabajar (Don Ramón)
    public void setPort(int port) {
        this.service_port = port;
    }

    @Override
    public void create(String uuid, String content) throws SQLConnectionException {
        try (Connection connection = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO XSD (uuid, content) VALUES (?, ?)")) {
                statement.setString(1, uuid);
                statement.setString(2, content);
                int result = statement.executeUpdate();

                if (result != 1)
                    throw new SQLException("Error creando content");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new SQLConnectionException(e);
        }

    }

    @Override
    public void update(String uuid, String content) throws SQLConnectionException {
        try (Connection connection = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE XSD SET uuid=?, content=?")) {
                statement.setString(1, uuid);
                statement.setString(2, content);
                int result = statement.executeUpdate();

                if (result != 1)
                    throw new SQLException("Error actualizando content");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new SQLConnectionException(e);
        }
    }

    @Override
    public void delete(String uuid) throws SQLConnectionException {
        try (Connection connection = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM XSD WHERE uuid=?")) {
                statement.setString(1, uuid);
                int result = statement.executeUpdate();

                if (result != 1)
                    throw new SQLException("Error eliminando content");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new SQLConnectionException(e);
        }
    }

    @Override
    public String get(String uuid) throws SQLConnectionException {
        String uuidConsulta = uuid;
        String ret = "";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT content FROM XSD WHERE uuid=?")) {
                statement.setString(1, uuidConsulta);

                try (ResultSet result = statement.executeQuery()) {
                    result.next();
                    ret = result.getString("content");

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new SQLConnectionException(e);
        }
        return ret;
    }

    @Override
    public Set<String> list() throws SQLConnectionException {
        Set<String> uuid = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT uuid FROM XSD")) {

                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        uuid.add("<a href=http://localhost:" + service_port + "/xsd?uuid=" + result.getString("uuid")
                                + ">" + result.getString("uuid") + "</a><br>");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new SQLConnectionException(e);
        }
        return uuid;
    }

    @Override
    public boolean exists(String uuidValue) throws SQLConnectionException {
        String uuidCheck = uuidValue;
        Set<String> uuid = new HashSet<>();
        boolean existe = false;
        try (Connection connection = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT uuid FROM XSD")) {

                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        uuid.add(result.getString("uuid"));
                    }

                    existe = uuid.contains(uuidCheck);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new SQLConnectionException(e);
        }
        return existe;
    }

}
