package server;

import utility.*;

import java.rmi.RemoteException;
import java.util.*;
import service.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerNode implements Server {
    // server port number
    int port = 8080;
    // port numbers of the whole distributed system
    List<Integer> ports;
    // port number of failed servers
    protected Set<Integer> failedServers;
    // map of running servers
    protected Map<Integer, Server> servers;

    // account database
    protected AccountDB accountDB;
    // file database
    protected FileDB fileDB;
    // previous promised vote
    protected ServerMessage previousVote;

    // micro service to handle different request
    // service to get account information
    AccountService accountService;
    // service to add a file, change a file content (title) and notify
    FileService fileService;
    // service to notify subscribers
    NotificationService notificationService;
    // service to share a file
    ShareService shareService;

    public ServerNode(int port, List<Integer> ports) {
        this.port = port;
        this.ports = ports;
        previousVote = new ServerMessage();

        failedServers = new HashSet<>();
        servers = new HashMap<>();
        fileDB = new FileDB();
        accountDB = new AccountDB();

        accountService = new AccountService(accountDB, fileDB);
        fileService = new FileService(port, ports, previousVote, servers, failedServers, fileDB, accountDB);
    }


    /**
     * process the request from the client
     */
    @Override
    public ClientMessage process(ClientMessage message) throws RemoteException {
        switch (message.getAction()) {
            // -- TO DO: call corresponding service to handle different request
            case LIST:
                return accountService.process(message);
            case CREATE:
                return fileService.process(message);
            default:
                break;
        }
        return message;
    }

    /**
     * process login request
     * @param logInMessage
     * @return
     * @throws RemoteException
     */
    @Override
    public LogInMessage process(LogInMessage logInMessage) throws RemoteException {
        return accountService.process(logInMessage);
    }

    /**
     * process message from other server during paxos algorithm
     * @param serverMessage message from other servers
     * @return serverMessage
     * @throws RemoteException
     */
    @Override
    public ServerMessage process(ServerMessage serverMessage) throws RemoteException {
        serverMessage = serverMessage.deepCopy();
        switch (serverMessage.getStatus()) {
            case PREPARED:
                System.out.println("Prepared proposal requested from proposer on port:" + serverMessage.getSender());
                if (serverMessage.getVote() > previousVote.getVote()) {
                    serverMessage.setStatus(ServerMessage.Status.PROMISE);
                    serverMessage.setAccountDB(previousVote.getAccountDB());
                    serverMessage.setFileDB(previousVote.getFileDB());
                    previousVote.setVote(serverMessage.getVote());
                }
                break;
            case ACCEPTED:
                System.out.println("Accepted proposal requested from proposer on port:" + serverMessage.getSender());
                if (serverMessage.getVote() == previousVote.getVote()) {
                    accountDB = new AccountDB(serverMessage.getAccountDB());
                    fileDB = new FileDB(serverMessage.getFileDB());
                    previousVote.setAccountDB(accountDB);
                    previousVote.setFileDB(fileDB);
                }
            default:
                break;
        }
        return serverMessage;
    }

    private void start() {
        try {
            Server server = (Server) UnicastRemoteObject.exportObject(this, port);

            Registry registry = LocateRegistry.createRegistry(port);

            registry.bind("Server", server);
            System.out.println("server starts running at port: " + port);
        } catch (Exception e) {
            System.out.println(e.toString() + "unable to initialize the server");
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            Logger.warnLog("Please input port numbers of servers");
            System.exit(1);
        }

        int port = 0;
        List<Integer> ports = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            int p = Integer.parseInt(args[i]);
            if (i == 0) {
                port = p;
            } else {
                ports.add(p);
            }
        }

        ServerNode serverNode = new ServerNode(port, ports);
        serverNode.start();
    }
}
