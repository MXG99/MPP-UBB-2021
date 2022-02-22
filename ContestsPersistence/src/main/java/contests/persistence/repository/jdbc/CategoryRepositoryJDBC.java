package contests.persistence.repository.jdbc;

import contests.model.Category;
import contests.persistence.repository.CategoryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CategoryRepositoryJDBC implements CategoryRepository {

    private JdbcUtils jdbcUtils;

    private static final Logger logger = LogManager.getLogger();

    public CategoryRepositoryJDBC(Properties props) {
        jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public String getCategoryById(Long id) {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        String category = null;
        String sql = "select * from Categories where id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int lower_bound = resultSet.getInt("lower_bound");
                    int upper_bound = resultSet.getInt("upper_bound");
                    category = lower_bound + "-" + upper_bound + " Years";
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

        logger.traceExit(category);
        return category;
    }

    @Override
    public Long getCategoryIdByAge(Integer age) {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        Long categoryId = null;
        String sql = "select * from Categories where lower_bound <= ? AND upper_bound >= ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setLong(1, age);
            preparedStatement.setLong(2, age);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    categoryId = resultSet.getLong("id");
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

        logger.traceExit(categoryId);
        return categoryId;
    }

    @Override
    public void add(Category entity) {
        logger.traceEntry("Saving task {}", entity);
        String sql = "insert into Categories (lower_bound, upper_bound) values (?,?)";
        try (Connection con = jdbcUtils.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, entity.getLowerBound());
            preparedStatement.setInt(2, entity.getUpperBound());
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
    public void delete(Category entity) {

    }

    @Override
    public void update(Long aLong, Category entity) {

    }

    @Override
    public Category findById(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Category> findAll() {
        logger.traceEntry();
        Connection con = jdbcUtils.getConnection();
        List<Category> categories = new ArrayList<>();
        String sql = "select * from Categories";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    Integer lower_bound = resultSet.getInt("lower_bound");
                    Integer upper_bound = resultSet.getInt("upper_bound");
                    Category category = new Category(lower_bound, upper_bound);
                    category.setId(id);
                    categories.add(category);
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

        logger.traceExit(categories);
        return categories;
    }

    @Override
    public Collection<Category> getAll() {
        return null;
    }
}
