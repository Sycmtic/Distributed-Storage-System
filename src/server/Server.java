package server;

import utility.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import service.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Remote {
    // server port number
    int port = 8080;
    // port numbers of the whole distributed system
    List<Integer> ports;
    // port number of failed servers
    protected Set<Integer> failedServers = new HashSet<>();

    // account database
    protected AccountDB accountDB;
    // file database
    protected FileDB fileDB;
    // previous promised vote
    protected Message previousVote = new Message();

    // micro service to handle different request
    FileService fileService;
    NotificationService notificationService;
    ShareService shareService;

    public Server(int port, List<Integer> ports) {
        this.port = port;
        this.ports = ports;
    }

    /*
     * process the request from the client
     */
    public Message process(Message message) throws RemoteException {
        switch(message.getType()) {
            // -- TO DO: call corresponding service to handle different request
        }
        return message;
    }

    private void start() {
        try {
            Server server = (Server) UnicastRemoteObject.exportObject(this, port);
            System.out.println("remote object exported");

            Registry registry = LocateRegistry.createRegistry(port);
            System.out.println("rmi registry created");

            registry.bind("Server", server);
            System.out.println("server ready in port: " + port);
        } catch (Exception e) {
            System.out.println(e.toString() + "unable to initialize the server");
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please input port numbers of servers");
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

        Server server = new Server(port, ports);
        server.start();
    }
}
