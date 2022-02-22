package contests.network.dto;

import contests.model.Organizer;
import contests.model.Participant;
import contests.model.dtos.ParticipantDTO;

public class DTOUtils {
    public static Organizer getFromDTO(OrganizerDTO organizerDTO) {
        Long id = Long.parseLong(organizerDTO.getId());
        String firstName = organizerDTO.getFirstName();
        String lastName = organizerDTO.getLastName();
        String email = organizerDTO.getEmail();
        String password = organizerDTO.getPassword();
        return new Organizer(id, firstName, lastName, email, password);
    }

    public static OrganizerDTO getDTO(Organizer organizer) {
        String id = String.valueOf(organizer.getId());
        String firstName = organizer.getFirstName();
        String lastName = organizer.getLastName();
        String email = organizer.getEmail();
        String password = organizer.getPassword();
        return new OrganizerDTO(id, firstName, lastName, email, password);
    }
}
