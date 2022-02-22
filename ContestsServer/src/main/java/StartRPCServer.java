import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartRPCServer {

    public static void main(String[] args) {
//        Properties serverProps = new Properties();
//        try {
//            serverProps.load(StartRPCServer.class.getResourceAsStream("/contestsserver.properties"));
//            System.out.println("Server properties set: ");
//            serverProps.list(System.out);
//        } catch (IOException e) {
//            System.err.println("Cannot find contestsserver.properties " + e);
//            return;
//        }
//        OrganizerRepository organizerRepository = new OrganizerRepositoryJDBC(serverProps);
//        ParticipantRepository participantRepository = new ParticipantRepositoryJDBC(serverProps);
//        ActivityRepository activityRepository = new ActivityRepositoryJDBC(serverProps);
//        CategoryRepository categoryRepository = new CategoryRepositoryJDBC(serverProps);
//        IContestsServices contestsServices = new ContestsServices(
//                organizerRepository,
//                participantRepository,
//                activityRepository,
//                categoryRepository
//        );
//        int contestsServerPort = defaultPort;
//        try {
//            contestsServerPort = Integer.parseInt(serverProps.getProperty("contests.server.port"));
//        } catch (NumberFormatException e) {
//            System.err.println("Wrong port number " + e.getMessage());
//            System.err.println("Using default port " + defaultPort);
//        }
//        System.out.println("Starting server on port: " + contestsServerPort);
//        AbstractServer server = new ContestRPCConcurrentServer(contestsServerPort, contestsServices);
//        try {
//            server.start();
//        } catch (ServerException e) {
//            System.err.println("Error starting the server " + e.getMessage());
//        } finally {
//            try {
//                server.stop();
//            } catch (ServerException e) {
//                System.err.println("Error stopping server " + e.getMessage());
//            }
//        }
//        System.setProperty("java.rmi.server.hostname", "89.136.249.167");
        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-server.xml");
    }

}

