package contests.server;

import contests.model.*;
import contests.model.dtos.ParticipantDTO;
import contests.persistence.repository.ActivityRepository;
import contests.persistence.repository.CategoryRepository;
import contests.persistence.repository.OrganizerRepository;
import contests.persistence.repository.ParticipantRepository;
import contests.services.ContestsException;
import contests.services.IContestsObserver;
import contests.services.IContestsServices;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ContestsServices implements IContestsServices {


    private OrganizerRepository organizerRepository;
    private ParticipantRepository participantRepository;
    private ActivityRepository activityRepository;
    private CategoryRepository categoryRepository;
    private Map<Long, IContestsObserver> loggedOrganizers;

    public ContestsServices(OrganizerRepository organizerRepository,
                            ParticipantRepository participantRepository,
                            ActivityRepository activityRepository,
                            CategoryRepository categoryRepository) {
        this.organizerRepository = organizerRepository;
        this.participantRepository = participantRepository;
        this.activityRepository = activityRepository;
        this.categoryRepository = categoryRepository;
        loggedOrganizers = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Organizer login(OrganizerCredentials credentials, IContestsObserver client) throws ContestsException {
        System.out.println("Intializing login sequence");
        Organizer organizer = organizerRepository.checkLogin(credentials.getEmail(), credentials.getPassword());
        System.out.println(organizer.toString());
        if (organizer != null) {
            if (loggedOrganizers.get(organizer.getId()) != null)
                throw new ContestsException("Organizer allready logged in");
            loggedOrganizers.put(organizer.getId(), client);
        } else
            throw new ContestsException("Authentification failed");
        return organizer;
    }

    @Override
    public void addParticipant(Participant participant) throws ContestsException {
        Participant newParticipant = new Participant(participant.getFirstActivityId(),
                participant.getSecondActivityId(),
                categoryRepository.getCategoryIdByAge(participant.getAge()),
                participant.getFirstName(),
                participant.getLastName());
        participantRepository.add(newParticipant);
        notifyParticipantAdded(newParticipant);
    }

    private final int defaultThreadNo = 5;

    private void notifyParticipantAdded(Participant participant) {
        Iterable<Organizer> organizers = organizerRepository.findAll();
        Long participantId = participantRepository.getIdByParticipant(participant);
        String id = participantId.toString();
        String firstName = participant.getFirstName();
        String lastName = participant.getLastName();
        String firstActivity = activityRepository.getNameById(participant.getFirstActivityId());
        String secondActivity = activityRepository.getNameById(participant.getSecondActivityId());
        String category = categoryRepository.getCategoryById(participant.getCategory());

        ParticipantDTO participantDTO = new ParticipantDTO(
                id,
                firstName,
                lastName,
                firstActivity,
                secondActivity,
                category
        );

        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadNo);
        for (Organizer organizer : organizers) {
            IContestsObserver contestClient = loggedOrganizers.get(organizer.getId());
            if (contestClient != null) {
                executor.execute(() -> {
                    try {
                        System.out.println("Notifying [" + organizer.getId() + "] participant [" + participant.getId()
                                + "] added");
                        contestClient.participantAdded(participantDTO);
                    } catch (ContestsException | RemoteException e) {
                        System.err.println("Error notifying organizer " + e);
                    }
                });
            }
        }
        executor.shutdown();
    }

    @Override
    public List<ParticipantDTO> getParticipants() throws ContestsException {
        List<ParticipantDTO> participantDTOList = new ArrayList<>();
        List<Participant> participantList = StreamSupport
                .stream(participantRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        participantList.forEach(participant -> {
            String id = participant.getId().toString();
            String firstName = participant.getFirstName();
            String lastName = participant.getLastName();
            String firstActivity = activityRepository.getNameById(participant.getFirstActivityId());
            String secondActivity = activityRepository.getNameById(participant.getSecondActivityId());
            String category = categoryRepository.getCategoryById(participant.getCategory());
            ParticipantDTO participantDTO = new ParticipantDTO(
                    id,
                    firstName,
                    lastName,
                    firstActivity,
                    secondActivity,
                    category
            );
            participantDTOList.add(participantDTO);
        });
        return participantDTOList;
    }

    @Override
    public List<Activity> getAllActivities() throws ContestsException {
        Iterable<Activity> activityIterable = activityRepository.findAll();
        return StreamSupport.stream(activityIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> getAllCategories() throws ContestsException {
        Iterable<Category> categoryIterable = categoryRepository.findAll();
        return StreamSupport.stream(categoryIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Organizer getLoggedOrganizer() throws ContestsException {
        return null;
    }

    @Override
    public synchronized void logOut(Organizer organizer, IContestsObserver client) throws ContestsException {
        IContestsObserver localClient = loggedOrganizers.remove(organizer.getId());
        if (localClient == null) {
            throw new ContestsException("Organizer " + organizer + " is not logged in");
        }
    }
}
