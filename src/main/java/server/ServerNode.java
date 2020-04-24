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
    // service to notify subscribers
    NotificationService notificationService;

    public ServerNode(int port, List<Integer> ports) {
        this.port = port;
        this.ports = ports;
        previousVote = new ServerMessage();

        failedServers = new HashSet<>();
        servers = new HashMap<>();
        fileDB = new FileDB();
        accountDB = new AccountDB();
        notificationService = new NotificationService();
    }

    /**
     * process the request from the client
     */
    @Override
    public ClientMessage process(ClientMessage message) throws RemoteException {
        ClientMessage response = null;
        switch (message.getAction()) {
            // -- TO DO: call corresponding service to handle different request
            case LIST:
                return processList(message);
            case CREATE:
                return processFile(message);
            case UPDATE:
                response = processFile(message);
                for (String user : fileDB.getFileById(message.getFileID()).getOwners()) {
                    notificationService.sendNotification("File " + message.getFileID() + " is updated", user);
                }
                return response;
            case SHARE:
//                shareService = new ShareService(accountDB, fileDB, message.getUsername(), message.getFileID());
//                response = shareService.process(message);
                response = processFile(message);
                notificationService.sendNotification("File " + message.getFileID() + " is shared with you", message.getUsername());
                return response;
            case OPEN:
                return processOpen(message);
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
        return processAccount(logInMessage);
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
                Logger.infoLog("Prepared proposal requested from proposer on port:" + serverMessage.getSender());
                if (serverMessage.getVote() > previousVote.getVote()) {
                    serverMessage.setStatus(ServerMessage.Status.PROMISE);
                    serverMessage.setAccountDB(previousVote.getAccountDB());
                    serverMessage.setFileDB(previousVote.getFileDB());
                    previousVote.setVote(serverMessage.getVote());
                }
                break;
            case ACCEPTED:
                Logger.infoLog("Accepted proposal requested from proposer on port:" + serverMessage.getSender());
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

    // --------- Account Service ---------
    public LogInMessage processAccount(LogInMessage logInMessage) {
        switch (logInMessage.getAction()) {
            case LOGIN:
                Account account = accountDB.getAccount(logInMessage.getUsername());
                if (account == null) {
                    logInMessage.setResult(Message.Result.FAIL);
                } else {
                    logInMessage.setAccount(account);
                }
                break;
            case CREATE:
                // phase1: prepare
                // initialize server message to propose
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setSender(port);
                long vote = previousVote.getVote() + port % (servers.size() + 1) + 1;
                previousVote.setVote(vote);
                serverMessage.setVote(vote);
                serverMessage.setStatus(ServerMessage.Status.PREPARED);
                serverMessage.setFileDB(fileDB);
                serverMessage.setAccountDB(accountDB);
                List<ServerMessage> serverMessages = new ArrayList<>();
                // reconnect the recovered servers
                for (int port : ports) {
                    if (port == this.port) continue;
                    if (!servers.containsKey(port) || servers.get(port) == null) {
                        try {
                            Server server = connectServer(port);
                            if (!servers.containsKey(port)) {
                                servers.put(port, server);
                            }
                        } catch (Exception e) {
                            System.out.println(e.toString() + "\nThe server on port:" + port + " is down.");
                            failedServers.add(port);
                            servers.remove(port);
                        }
                    }
                }

                // total alive servers
                int total = 1;
                Set<Integer> ports = new HashSet<>(servers.keySet());
                for (int port : ports) {
                    if (port == this.port) continue;
                    Server server = servers.get(port);
                    try {
                        ServerMessage response = server.process(serverMessage);
                        serverMessages.add(response);
                        total++;
                    } catch (Exception e) {
                        System.out.println(e.toString() + "\nThe server on port:" + port + " is down.");
                        failedServers.add(port);
                        servers.remove(port);
                    }
                }

                // count how many acceptors promise this proposal
                int count = 1;
                for (ServerMessage msg : serverMessages) {
                    if (msg.getStatus() == ServerMessage.Status.PROMISE) {
                        count++;
                    }
                }

                // abort this proposal if less than half of accepters promise
                if (total != 0 && count <= total / 2) {
                    logInMessage.setResult(Message.Result.FAIL);
                    return logInMessage;
                }

                // phase2: propose with id and value
                ServerMessage proposal = new ServerMessage(serverMessage);
                proposal.setStatus(ServerMessage.Status.ACCEPTED);
                proposal.setVote(-1);
                // find the value in promising acceptors with greatest id
                for (ServerMessage msg : serverMessages) {
                    if (msg.getStatus() == ServerMessage.Status.PREPARED && msg.getAccountDB() != null && msg.getVote() > proposal.getVote()) {
                        proposal.setFileDB(msg.getFileDB());
                        proposal.setAccountDB(msg.getAccountDB());
                        proposal.setVote(msg.getVote());
                    }
                }
                proposal.setVote(vote);
                if (logInMessage.getAction() == LogInMessage.Action.CREATE) {
                    Account newAccount = proposal.getAccountDB().createAccount(logInMessage.getUsername());
                    logInMessage.setAccount(newAccount);
                }

                // multi-cast the proposal
                ports = new HashSet<>(servers.keySet());
                for (int port : ports) {
                    Server server = servers.get(port);
                    try {
                        server.process(proposal);
                    } catch (Exception e) {
                        System.out.println(e.toString() + "The server on port:" + port + " is down.");
                        failedServers.add(port);
                        servers.remove(port);
                    }
                }

                // update proposer
                accountDB = new AccountDB(proposal.getAccountDB());
                fileDB = new FileDB(proposal.getFileDB());
                previousVote.setAccountDB(proposal.getAccountDB());
                previousVote.setFileDB(proposal.getFileDB());
                logInMessage.setResult(Message.Result.SUCCESS);
                break;
            default:
                logInMessage.setResult(Message.Result.FAIL);
                break;
        }
        return logInMessage;
    }

    public ClientMessage processList(ClientMessage message) {
        Account account = accountDB.getAccount(message.getAccount().getUsername());
        List<File> files = fileDB.getFileByIds(account.getFiles());
        if (files == null) {
            message.setResult(Message.Result.FAIL);
        } else {
            message.setFiles(files);
        }
        return message;
    }

    // --------- File Service ---------
    public ClientMessage processOpen(ClientMessage clientMessage) {
        File file = fileDB.getFileById(clientMessage.getFileID());
        if (file == null) {
            clientMessage.setResult(Message.Result.FAIL);
        } else {
            clientMessage.setFile(file);
        }
        return clientMessage;
    }

    public ClientMessage processFile(ClientMessage clientMessage) {
        // phase1: prepare
        // initialize server message to propose
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setSender(port);
        long vote = previousVote.getVote() + port % (servers.size() + 1) + 1;
        previousVote.setVote(vote);
        serverMessage.setVote(vote);
        serverMessage.setStatus(ServerMessage.Status.PREPARED);
        serverMessage.setFileDB(fileDB);
        serverMessage.setAccountDB(accountDB);
        List<ServerMessage> serverMessages = new ArrayList<>();
        // reconnect the recovered servers
        for (int port : ports) {
            if (port == this.port) continue;
            if (!servers.containsKey(port) || servers.get(port) == null) {
                try {
                    Server server = connectServer(port);
                    if (!servers.containsKey(port)) {
                        servers.put(port, server);
                    }
                } catch (Exception e) {
                    System.out.println(e.toString() + "\nThe server on port:" + port + " is down.");
                    failedServers.add(port);
                    servers.remove(port);
                }
            }
        }

        // total alive servers
        int total = 1;
        Set<Integer> ports = new HashSet<>(servers.keySet());
        for (int port : ports) {
            if (port == this.port) continue;
            Server server = servers.get(port);
            try {
                ServerMessage response = server.process(serverMessage);
                serverMessages.add(response);
                total++;
            } catch (Exception e) {
                System.out.println(e.toString() + "\nThe server on port:" + port + " is down.");
                failedServers.add(port);
                servers.remove(port);
            }
        }

        // count how many acceptors promise this proposal
        int count = 1;
        for (ServerMessage msg : serverMessages) {
            if (msg.getStatus() == ServerMessage.Status.PROMISE) {
                count++;
            }
        }

        // abort this proposal if less than half of accepters promise
        if (total != 0 && count <= total / 2) {
            clientMessage.setResult(Message.Result.FAIL);
            return clientMessage;
        }

        // phase2: propose with id and value
        ServerMessage proposal = new ServerMessage(serverMessage);
        proposal.setStatus(ServerMessage.Status.ACCEPTED);
        proposal.setVote(-1);
        // find the value in promising acceptors with greatest id
        for (ServerMessage msg : serverMessages) {
            if (msg.getStatus() == ServerMessage.Status.PREPARED && msg.getVote() > proposal.getVote()) {
                proposal.setFileDB(msg.getFileDB());
                proposal.setAccountDB(msg.getAccountDB());
                proposal.setVote(msg.getVote());
            }
        }
        proposal.setVote(vote);
        if (clientMessage.getAction() == ClientMessage.Action.CREATE) {
            long fileId = fileDB.getLastID() + 1;
            clientMessage.getFile().getOwners().add(clientMessage.getAccount().getUsername());
            proposal.getFileDB().addFile(fileId, clientMessage.getFile());
            proposal.getAccountDB().getAccount(clientMessage.getAccount().getUsername()).addFile(fileId);
        } else if (clientMessage.getAction() == ClientMessage.Action.UPDATE) {
            File file = proposal.getFileDB().getFileById(clientMessage.getFileID());
            if (file == null) {
                clientMessage.setResult(Message.Result.FAIL);
            } else {
                file.setContent(clientMessage.getNewContent());
                file.setVersion(file.getVersion() + 1);
            }
        } else if (clientMessage.getAction() == ClientMessage.Action.SHARE) {
            proposal.getAccountDB().getAccount(clientMessage.getUsername()).addFile(clientMessage.getFileID());
            proposal.getFileDB().getFileById(clientMessage.getFileID()).getOwners().add(clientMessage.getUsername());
        }

        // multi-cast the proposal
        ports = new HashSet<>(servers.keySet());
        for (int port : ports) {
            Server server = servers.get(port);
            try {
                server.process(proposal);
            } catch (Exception e) {
                System.out.println(e.toString() + "The server on port:" + port + " is down.");
                failedServers.add(port);
                servers.remove(port);
            }
        }

        // update proposer
        accountDB = new AccountDB(proposal.getAccountDB());
        fileDB = new FileDB(proposal.getFileDB());
        previousVote.setFileDB(proposal.getFileDB());
        previousVote.setAccountDB(proposal.getAccountDB());
        clientMessage.setAccount(accountDB.getAccount(clientMessage.getAccount().getUsername()));
        return clientMessage;
    }

    /**
     * Connect to the specific server
     */
    private Server connectServer(int port) throws Exception {
        // Looking up the registry for the remote object
        Server server = (Server) LocateRegistry.getRegistry("localhost", port).lookup("Server");
        return server;
    }

    private void start() {
        try {
            Server server = (Server) UnicastRemoteObject.exportObject(this, port);

            Registry registry = LocateRegistry.createRegistry(port);

            registry.bind("Server", server);
            Logger.infoLog("server starts running at port: " + port);
        } catch (Exception e) {
            Logger.warnLog(e.toString() + "unable to initialize the server");
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
