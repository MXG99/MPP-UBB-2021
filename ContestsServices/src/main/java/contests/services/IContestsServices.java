package contests.services;

import contests.model.*;
import contests.model.dtos.ParticipantDTO;

import java.util.List;

public interface IContestsServices {

    Organizer login(OrganizerCredentials credentials, IContestsObserver client) throws ContestsException;

    void addParticipant(Participant participant) throws ContestsException;

    List<ParticipantDTO> getParticipants() throws ContestsException;

    List<Activity> getAllActivities() throws ContestsException;

    List<Category> getAllCategories() throws ContestsException;

    Organizer getLoggedOrganizer() throws ContestsException;

    void logOut(Organizer organizer, IContestsObserver client) throws ContestsException;
}
