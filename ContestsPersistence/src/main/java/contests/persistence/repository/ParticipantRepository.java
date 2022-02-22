package contests.persistence.repository;

import contests.model.Participant;
import contests.model.dtos.ParticipantDTO;

import java.util.List;

public interface ParticipantRepository extends ICrudRepository<Long, Participant> {
    List<Participant> filterByActivity(Long activityId);
    List<Participant> filterByCategory(Long categoryId);
    Long getIdByParticipant(Participant participant);
}
