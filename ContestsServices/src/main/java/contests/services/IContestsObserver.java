package contests.services;

import contests.model.Organizer;
import contests.model.Participant;
import contests.model.dtos.ParticipantDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IContestsObserver extends Remote {
    void participantAdded(ParticipantDTO participant) throws ContestsException, RemoteException;
}
