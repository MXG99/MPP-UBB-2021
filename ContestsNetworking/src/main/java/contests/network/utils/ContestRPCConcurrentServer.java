package contests.network.utils;

import contests.network.rpcprotocol.ContestClientRPCReflectionWorker;
import contests.services.IContestsServices;

import java.net.Socket;

public class ContestRPCConcurrentServer extends AbstractConcurrentServer {
    private IContestsServices contestsServices;

    public ContestRPCConcurrentServer(int port, IContestsServices contestsServices) {
        super(port);
        this.contestsServices = contestsServices;
        System.out.println("Contest - ContestRPCConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ContestClientRPCReflectionWorker worker = new ContestClientRPCReflectionWorker(contestsServices, client);
        return new Thread(worker);
    }

    @Override
    public void stop() {
        System.out.println("Stopping services ...");
    }
}
