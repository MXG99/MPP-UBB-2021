package contests.persistence.repository;

import contests.model.Category;

public interface CategoryRepository extends ICrudRepository<Long, Category> {
    String getCategoryById(Long id);
    Long getCategoryIdByAge(Integer age);
}
