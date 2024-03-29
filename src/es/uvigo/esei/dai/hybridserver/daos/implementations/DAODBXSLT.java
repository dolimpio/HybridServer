package es.uvigo.esei.dai.hybridserver.daos.implementations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import es.uvigo.esei.dai.hybridserver.DAO;
import es.uvigo.esei.dai.hybridserver.SQLConnectionException;

public class DAODBXSLT implements DAO {
    private final String DB_URL = "jdbc:mysql://localhost:3306/hstestdb";
    private final String DB_USER = "hsdb";
    private final String DB_PASSWORD = "hsdbpass";

    // Trabajar no es malo, lo malo es tener que trabajar (Don Ramón)

    public void createXSLT(String uuid, String content, String uuidXSD) throws SQLConnectionException {
        try (Connection connection = DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO XSLT (UUID, CONTENT, XSD) VALUES (?, ?, ?)")) {
                statement.setString(1, uuid);
                statement.setString(2, content);
                statement.setString(3, uuidXSD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new SQLConnectionException(e);
        }

    }

    public void update(String uuid, String content, String uuidXSD) throws SQLConnectionException {
        try (Connection connection = DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE XSLT SET UUID=?, CONTENT=?, XSD=?")) {
                statement.setString(1, uuid);
                statement.setString(2, content);
                statement.setString(3, uuidXSD);
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
                DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM XSLT WHERE UUID=?")) {
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
                DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT content FROM XSLT WHERE UUID=?")) {
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
                DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT UUID FROM XSLT")) {

                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        uuid.add(result.getString("UUID"));
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
                DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT UUID FROM XSLT")) {

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

    @Override
    public void create(String uuid, String content) throws SQLConnectionException {
        // TODO Auto-generated method stub
    }

    @Override
    public void update(String uuid, String content) throws SQLConnectionException {
        // TODO Auto-generated method stub

    }

}
