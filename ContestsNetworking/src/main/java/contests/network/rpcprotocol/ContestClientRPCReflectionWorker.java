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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;

public class ContestClientRPCReflectionWorker implements Runnable, IContestsObserver {

    private IContestsServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    private static final Logger logger = LogManager.getLogger();

    public ContestClientRPCReflectionWorker(IContestsServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
//            try {
//                Thread.sleep(0);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    @Override
    public void participantAdded(ParticipantDTO participant) throws ContestsException {
        Response response = new Response.Builder().type(ResponseType.PARTICIPANT_ADDED).data(participant).build();
        logger.trace(String.format("Pariticipant added %s", participant.toString()));
        try {
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("Sending response " + response);
        output.writeObject(response);
        output.flush();
    }

    private Response handleLOGIN(Request request) {
        System.out.println("Login request..." + request.type());
        OrganizerCredentials credentials = (OrganizerCredentials) request.data();
        try {
            Organizer organizer = server.login(credentials, this);
            logger.trace(String.format("In handleLOGIN organizer {%s}", organizer.toString()));
            OrganizerDTO organizerDTO = DTOUtils.getDTO(organizer);
            logger.trace(String.format("In handleLOGIN organizerDTO {%s}", organizerDTO.toString()));
            return new Response.Builder().type(ResponseType.OK).data(organizerDTO).build();
        } catch (ContestsException e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request) {
        logger.info("Logout request");
        OrganizerDTO oDTO = (OrganizerDTO) request.data();
        Organizer organizer = DTOUtils.getFromDTO(oDTO);
        try {
            server.logOut(organizer, this);
            connected = false;
            return okResponse;
        } catch (ContestsException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_ACTIVITIES(Request request) {
        logger.info("Get activities request");
        try {
            List<Activity> activities = server.getAllActivities();
            return new Response.Builder().type(ResponseType.OK).data(activities).build();
        } catch (ContestsException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_CATEGORIES(Request request) {
        logger.info("Get categories request");
        try {
            List<Category> categories = server.getAllCategories();
            return new Response.Builder().type(ResponseType.OK).data(categories).build();
        } catch (ContestsException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_PARTICIPANTS(Request request) {
        logger.info("Get participants request");
        try {
            List<ParticipantDTO> participants = server.getParticipants();
            return new Response.Builder().type(ResponseType.OK).data(participants).build();
        } catch (ContestsException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_PARTICIPANT(Request request) {
        logger.info("Add participant request");
        try {
            Participant participant = (Participant) request.data();
            server.addParticipant(participant);
            return okResponse;
        } catch (ContestsException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
}
