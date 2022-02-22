package contests.persistence.repository.jdbc;

import contests.model.Participant;
import contests.persistence.repository.ParticipantRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ParticipantRepositoryJDBC implements ParticipantRepository {

    private JdbcUtils jdbcUtils;

    private static final Logger logger = LogManager.getLogger();

    public ParticipantRepositoryJDBC(Properties props) {
        jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public void add(Participant entity) {
        logger.traceEntry("Saving task {}", entity);
        String sql = "insert into Participants (firstName, lastName, categoryId, firstActivityId, secondActivityId) values (?,?,?,?,?)";
        try (Connection con = jdbcUtils.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setLong(3, entity.getCategory());
            preparedStatement.setLong(4, entity.getFirstActivityId());
            preparedStatement.setLong(5, entity.getSecondActivityId());
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
    public void delete(Participant entity) {
        Long id = entity.getId();
        String sql = "delete from Participants where  id = ?";
        try (Connection con = jdbcUtils.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
    }

    @Override
    public void update(Long id, Participant entity) {
        String sql = "update Participants set " +
                "firstName = ? , " +
                "lastName = ? , " +
                "categoryId = ? , " +
                "firstActivityId = ?, " +
                "secondActivityId = ? " +
                "where id = ?";
        try (Connection con = jdbcUtils.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setLong(3, entity.getCategory());
            preparedStatement.setLong(4, entity.getFirstActivityId());
            preparedStatement.setLong(5, entity.getSecondActivityId());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Participant findById(Long id) {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        Participant participant = null;
        String sql = "select * from Participants where id = " + id;
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    Long categoryId = resultSet.getLong("categoryId");
                    Long firstActivityId = resultSet.getLong("firstActivityId");
                    Long secondActivityId = resultSet.getLong("secondActivityId");
                    participant = new Participant(firstActivityId, secondActivityId, categoryId, firstName, lastName);
                    participant.setId(id);
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

        logger.traceExit(participant);
        return participant;
    }

    @Override
    public Iterable<Participant> findAll() {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        List<Participant> participants = new ArrayList<>();
        String sql = "select * from Participants";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    Long categoryId = resultSet.getLong("categoryId");
                    Long firstActivityId = resultSet.getLong("firstActivityId");
                    Long secondActivityId = resultSet.getLong("secondActivityId");
                    Participant participant = new Participant(firstActivityId, secondActivityId, categoryId, firstName, lastName);
                    participant.setId(id);
                    participants.add(participant);
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

        logger.traceExit(participants);
        return participants;
    }

    @Override
    public Collection<Participant> getAll() {
        return null;
    }

    @Override
    public List<Participant> filterByActivity(Long activityId) {
        return null;
    }

    @Override
    public List<Participant> filterByCategory(Long categoryId) {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        List<Participant> participantList = new ArrayList<>();
        Participant participant = null;
        String sql = "select * from Participants where " +
                "categoryId = " + categoryId;
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    Long firstActivityId = resultSet.getLong("firstActivityId");
                    Long secondActivityId = resultSet.getLong("secondActivityId");
                    participant = new Participant(firstActivityId, secondActivityId, categoryId, firstName, lastName);
                    participantList.add(participant);
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

        logger.traceExit(participantList);
        return participantList;
    }

    @Override
    public Long getIdByParticipant(Participant participant) {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        String sql = "select * from Participants where firstName = ? and " +
                "lastName = ? and " +
                "categoryId = ? and " +
                "firstActivityId = ? and " +
                "secondActivityId = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, participant.getFirstName());
            preparedStatement.setString(2, participant.getLastName());
            preparedStatement.setLong(3, participant.getCategoryId());
            preparedStatement.setLong(4, participant.getFirstActivityId());
            preparedStatement.setLong(5, participant.getSecondActivityId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    return id;
                }
            }
        } catch (SQLException ignored) {

        }
        return null;
    }
}
