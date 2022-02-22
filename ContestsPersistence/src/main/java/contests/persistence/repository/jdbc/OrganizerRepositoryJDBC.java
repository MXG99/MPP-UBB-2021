package contests.persistence.repository.jdbc;

import contests.model.Organizer;
import contests.persistence.repository.OrganizerRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OrganizerRepositoryJDBC implements OrganizerRepository {
    private JdbcUtils jdbcUtils;

    private static final Logger logger = LogManager.getLogger();

    public OrganizerRepositoryJDBC(Properties props) {
        jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public void add(Organizer entity) {
        logger.traceEntry("Saving task {}", entity);
        String sql = "insert into Organizers (firstName, lastName, email, password) values (?,?,?,?)";
        try (Connection con = jdbcUtils.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getEmail());
            preparedStatement.setString(4, entity.getPassword());
            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        } finally {
            logger.traceExit();
            //Ignored
        }
    }

    @Override
    public void delete(Organizer entity) {
        Long id = entity.getId();
        String sql = "delete from Organizers where  id = ?";
        try (Connection con = jdbcUtils.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
    }

    @Override
    public void update(Long id, Organizer entity) {
        String sql = "update Organizers set " +
                "firstName = ? , " +
                "lastName = ? , " +
                "email = ? , " +
                "password = ? " +
                "where id = ?";
        try (Connection con = jdbcUtils.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getEmail());
            preparedStatement.setString(4, entity.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Organizer findById(Long id) {
        logger.traceEntry();
        Organizer organizer = null;
        String sql = "select * from Organizers where id = " + id;
        try (Connection con = jdbcUtils.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    organizer = new Organizer(firstName, lastName, email, password);
                    organizer.setId(id);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error db" + ex);
        }
        logger.traceExit(organizer);
        return organizer;
    }

    @Override
    public Iterable<Organizer> findAll() {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        List<Organizer> organizers = new ArrayList<>();
        String sql = "select * from Organizers";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    Organizer organizer = new Organizer(firstName, lastName, email, password);
                    organizer.setId(id);
                    organizers.add(organizer);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error db" + ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ignored) {
                //Ignored
            }
        }

        logger.traceExit(organizers);
        return organizers;
    }

    @Override
    public Collection<Organizer> getAll() {
        return null;
    }

    @Override
    public Organizer filterByEmail(String email) {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        Organizer organizer = null;
        String sql = "select * from Organizers where " +
                "email = '" + email + "'";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String password = resultSet.getString("password");
                    organizer = new Organizer(firstName, lastName, email, password);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error db" + ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ignored) {
                //Ignored
            }
        }
        logger.traceExit(organizer);
        return organizer;
    }

    @Override
    public Organizer checkLogin(String email, String password) {
        logger.traceEntry();
        Organizer oraganizer = null;
        String sql = "SELECT * FROM Organizers WHERE email = ?  AND password = ?";
        try (Connection con = jdbcUtils.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                oraganizer = new Organizer(resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("password"));
                oraganizer.setId(resultSet.getLong("id"));
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        return oraganizer;
    }
}
