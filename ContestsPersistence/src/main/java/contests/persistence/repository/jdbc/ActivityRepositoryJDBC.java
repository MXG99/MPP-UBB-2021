package contests.persistence.repository.jdbc;

import contests.model.Activity;
import contests.persistence.repository.ActivityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ActivityRepositoryJDBC implements ActivityRepository {

    private JdbcUtils jdbcUtils;

    private static final Logger logger = LogManager.getLogger();

    public ActivityRepositoryJDBC(Properties props) {
        jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public Activity filterByName(String name) {
        return null;
    }

    @Override
    public String getNameById(Long id) {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        String name = null;
        String sql = "select * from Activities where id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    name = resultSet.getString("name");
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

        logger.traceExit(name);
        return name;
    }

    @Override
    public void add(Activity entity) {
        logger.traceEntry("Saving task {}", entity);
        String sql = "insert into Activities (name) values (?)";
        try (Connection con = jdbcUtils.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getName());
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
    public void delete(Activity entity) {

    }

    @Override
    public void update(Long aLong, Activity entity) {

    }

    @Override
    public Activity findById(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Activity> findAll() {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        List<Activity> activities = new ArrayList<>();
        String sql = "select * from Activities";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    Activity activity = new Activity(name);
                    activity.setId(id);
                    activities.add(activity);
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

        logger.traceExit(activities);
        return activities;
    }

    @Override
    public Collection<Activity> getAll() {
        return null;
    }
}
