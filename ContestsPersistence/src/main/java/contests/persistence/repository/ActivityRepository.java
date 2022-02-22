package contests.persistence.repository;

import contests.model.Activity;

public interface ActivityRepository extends ICrudRepository<Long, Activity> {
    Activity filterByName(String name);
    String getNameById(Long id);
}
