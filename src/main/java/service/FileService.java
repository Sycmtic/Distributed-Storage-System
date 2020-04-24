package service;

import server.Server;
import utility.*;

import java.rmi.registry.LocateRegistry;

import java.util.*;

public class FileService {
    List<Integer> ports;
    int port;
    ServerMessage previousVote;
    Map<Integer, Server> servers;
    Set<Integer> failedServers;
    FileDB fileDB;
    AccountDB accountDB;

    public FileService(int port, List<Integer> ports, ServerMessage previousVote,
                       Map<Integer, Server> servers, Set<Integer> failedServers,
                       FileDB fileDB, AccountDB accountDB) {
        this.port = port;
        this.ports = ports;
        this.previousVote = previousVote;
        this.servers = servers;
        this.fileDB = fileDB;
        this.accountDB = accountDB;
        this.failedServers = failedServers;
    }

    public ClientMessage process(ClientMessage clientMessage) {
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
            if (msg.getStatus() == ServerMessage.Status.PREPARED && msg.getAccountDB() != null && msg.getVote() > proposal.getVote()) {
                proposal.setFileDB(msg.getFileDB());
                proposal.setAccountDB(msg.getAccountDB());
                proposal.setVote(msg.getVote());
            }
        }
        proposal.setVote(vote);
        if (clientMessage.getAction() == ClientMessage.Action.CREATE) {
//            long fileId = vote;
            long fileId = fileDB.getLastID() + 1;
            clientMessage.setFileID(fileId);
            proposal.getFileDB().addFile(fileId, clientMessage.getFile());
            proposal.getAccountDB().getAccount(clientMessage.getAccount().getUsername()).addFile(fileId);
        } else {
            // -- TO-DO: Add other action handler
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
        clientMessage.setResult(Message.Result.SUCCESS);
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
}
