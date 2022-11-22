package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class DAODBXSD implements DAO {
    private final String DB_URL;
    private final String DB_USER;
    private final String DB_PASSWORD;

    private Properties propertiesDAO;

    // Trabajar no es malo, lo malo es tener que trabajar (Don Ramón)

    public DAODBXSD(Properties properties) {
        propertiesDAO = properties;
        DB_URL = propertiesDAO.getProperty("db.url");
        DB_USER = propertiesDAO.getProperty("db.user");
        DB_PASSWORD = propertiesDAO.getProperty("db.password");
    }

    @Override
    public void create(String uuid, String content) throws SQLConnectionException {
        try (Connection connection = DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO XSD (UUID, CONTENT) VALUES (?, ?)")) {
                statement.setString(1, uuid);
                statement.setString(2, content);
                int result = statement.executeUpdate();
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
                DB_URL, DB_USER, DB_PASSWORD)) {
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
                DB_URL, DB_USER, DB_PASSWORD)) {
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
                DB_URL, DB_USER, DB_PASSWORD)) {
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
                DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT UUID FROM XSD")) {

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
