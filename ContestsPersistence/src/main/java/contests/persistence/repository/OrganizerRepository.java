package contests.persistence.repository;

import contests.model.Organizer;

import java.util.Optional;

public interface OrganizerRepository extends ICrudRepository<Long, Organizer> {
    Organizer filterByEmail(String email);
    Organizer checkLogin(String email, String password);
}
