package contests.network.rpcprotocol;

import contests.model.*;
import contests.model.dtos.ParticipantDTO;
import contests.network.dto.DTOUtils;
import contests.network.dto.OrganizerDTO;
import contests.services.ContestsException;
import contests.services.IContestsObserver;
import contests.services.IContestsServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ContestServicesRPCProxy implements IContestsServices {

    private String host;
    private int port;

    private IContestsObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;

    private volatile boolean finished;

    private static final Logger logger = LogManager.getLogger();

    public ContestServicesRPCProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    private void initializeConnection() throws ContestsException {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }


    @Override
    public Organizer login(OrganizerCredentials credentials, IContestsObserver client) throws ContestsException {
        initializeConnection();
        Request req = new Request.Builder().type(RequestType.LOGIN).data(credentials).build();
        logger.trace(String.format("Request %s", req));
        sendRequest(req);
        Response response = readResponse();
        logger.trace(String.format("Response %s", response));
        if (response.type() == ResponseType.OK) {
            this.client = client;
            OrganizerDTO organizerDTO = (OrganizerDTO) response.data();
            logger.trace(String.format("OrganizerDTO %s", organizerDTO));
            return DTOUtils.getFromDTO(organizerDTO);
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new ContestsException(err);
        }
        return null;
    }

    @Override
    public void logOut(Organizer organizer, IContestsObserver client) throws ContestsException {
        logger.trace(String.format("In logOut - proxy %s", organizer.toString()));
        OrganizerDTO oDTO = DTOUtils.getDTO(organizer);
        logger.trace(String.format("In logOut - proxy %s", oDTO.toString()));
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(oDTO).build();
        logger.trace(String.format("Request %s", request));
        sendRequest(request);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ContestsException(err);
        }
    }

    @Override
    public List<Activity> getAllActivities() throws ContestsException {
        logger.info("In getAllActivities - proxy");
        Request request = new Request.Builder().type(RequestType.GET_ACTIVITIES).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ContestsException(err);
        }
        return (List<Activity>) response.data();
    }

    @Override
    public void addParticipant(Participant participant) throws ContestsException {
        logger.info("In addParticipant - proxy");
        Request request = new Request.Builder().type(RequestType.ADD_PARTICIPANT).data(participant).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ContestsException(err);
        }
    }

    @Override
    public List<Category> getAllCategories() throws ContestsException {
        logger.info("In getAllCategories - proxy");
        Request request = new Request.Builder().type(RequestType.GET_CATEGORIES).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ContestsException(err);
        }
        return (List<Category>) response.data();
    }


    @Override
    public List<ParticipantDTO> getParticipants() throws ContestsException {
        logger.info("In getParticipants - proxy");
        Request request = new Request.Builder().type(RequestType.GET_PARTICIPANTS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ContestsException(err);
        }
        return (List<ParticipantDTO>) response.data();
    }

    private void sendRequest(Request request) throws ContestsException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new ContestsException("Error sending object " + e);
        }
    }

    private Response readResponse() throws ContestsException {
        Response response = null;
        try {
            logger.trace(String.format("QResponses %s", qresponses.size()));
            response = qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }


    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.PARTICIPANT_ADDED) {
            ParticipantDTO participant = (ParticipantDTO) response.data();
            logger.trace(String.format("Participant added %s", participant.toString()));
            try {
                client.participantAdded(participant);
            } catch (ContestsException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.PARTICIPANT_ADDED;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {

                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error " + e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }

    @Override
    public Organizer getLoggedOrganizer() throws ContestsException {
        return null;
    }
}
