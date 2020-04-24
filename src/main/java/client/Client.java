package client;

import server.Server;
import utility.*;
import utility.Message;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
<<<<<<< HEAD:src/main/java/client/Client.java
import java.util.*;

import static utility.ClientMessage.Action.ACCEPT;
=======
import java.util.Scanner;
>>>>>>> master:src/client/Client.java

public class Client {
    final static String instruction = "Please type your request...\n" +
            "LIST: list your files\n" +
            "CREATE <file title> <file content>: create a file with a title and content\n" +
            "SHARE <file ID> <username>: share a file with a user\n" +
            "ACCEPT <file ID>: accept a file share invitation \n" +
            "UPDATE <file ID> <new content>: update content for a file\n";

    private String host;
    private int port;
    private Account user;
    private Server server;
    private Set<Long> receiving = new HashSet<>();

    public Client () {}

    public Client (String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws Exception {
        server = (Server) LocateRegistry.getRegistry(host, port).lookup("Server");
    }

    public LogInMessage send (LogInMessage logInMessage) throws RemoteException {
        return server.process(logInMessage);
    }

    public ClientMessage send (ClientMessage clientMessage) throws RemoteException {
        return server.process (clientMessage);
    }

    public static void main (String[] args) {
        if (args.length < 2) {
            Logger.warnLog("Pleas input both sever hostname/IP and port number.");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        Client client = new Client(host, port);
        try{
            client.connect();
        } catch (Exception e) {
            Logger.warnLog("Client connection to " + client.host + " " + client.port + " start error!\n"
                    + e.getMessage());
        }

        System.out.println("Welcome!");
        System.out.println("Please enter \"login\" or \"signup\":");
        Scanner scanner = new Scanner (System.in);
        while (client.user == null) {
            String action = scanner.nextLine();
            switch (action) {
                case "login":
                    System.out.println("Please enter your username to log in: ");
                    String username = scanner.nextLine();
                    LogInMessage logInMessage = new LogInMessage(username);
                    logInMessage.setAction(LogInMessage.Action.LOGIN);
                    try {
                        LogInMessage response = client.send(logInMessage);
                        if (response.getResult() == Message.Result.FAIL) {
                            Logger.warnLog("Not a valid username! Please check your username and try again.");
                        } else {
                            client.user = response.getAccount();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case "signup":
                    System.out.println("Please enter your new account username: ");
                    String newUsername = scanner.nextLine();
                    LogInMessage createMessage = new LogInMessage(newUsername);
                    createMessage.setAction(LogInMessage.Action.CREATE);
                    try {
                        LogInMessage response = client.send(createMessage);
                        if (response.getResult() == Message.Result.FAIL) {
                            Logger.warnLog("Not a valid username! Please check your username and try again.");
                        } else {
                            System.out.println("here");
                            client.user = response.getAccount();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
<<<<<<< HEAD:src/main/java/client/Client.java
                    System.out.print("wrong action, please input [signup|login]");
=======
                    System.out.print("wrong action, please input [longin|signup]");
>>>>>>> master:src/client/Client.java
                    break;
            }
        }

        System.out.println("Successfully logged. Retrieving your files... ");
        ClientMessage clientMessage = new ClientMessage();
        clientMessage.setAction(ClientMessage.Action.LIST);
        clientMessage.setAccount(client.user);

        try {
            ClientMessage response = client.send(clientMessage);
            if (response.getResult() == Message.Result.FAIL) {
                System.out.println("There is no file under your account");
            } else {
                for (File file : response.getFiles()) {
                    file.printInfo();
                    Thread t = new Thread(new Receiver("file" + file.getId()));
                    t.start();
                    client.receiving.add(file.getId());
                }
            }
        } catch (RemoteException e) { }

<<<<<<< HEAD:src/main/java/client/Client.java
        System.out.println("Please type your request...\n" +
                "LIST: list your files\n" +
                "SHARE <file ID> <username>: share a file with a user\n" +
                "ACCEPT <file ID>: accept a file share invitation \n" +
                "UPDATE <file ID> <new title>: update a title for a file\n");

        //need change the instruction
        //add an accept share action
=======
        System.out.println(instruction);
>>>>>>> master:src/client/Client.java

        while (true) {
            String command = scanner.nextLine();
            ClientMessage request = generateClientMessage(command);
<<<<<<< HEAD:src/main/java/client/Client.java

            if (request.getAction() != ACCEPT) {
=======
            if (request == null) {
                continue;
            }
            if (request.getAction() != ClientMessage.Action.ACCEPT) {
>>>>>>> master:src/client/Client.java
                request.setAccount(client.user);
                try {
                    Logger.infoLog("Sending request: " + command);
                    ClientMessage response = client.send(request);
                    if (response.getAction() == ClientMessage.Action.LIST) {
                        for (File file : response.getFiles()) {
                            file.printInfo();
                        }
                    }else {
                        Logger.infoLog(response.getErrorMessage());
                    }
                } catch (RemoteException e) {
                    Logger.warnLog("Client send request error!\n" + e.getMessage());
                }
            } else {
                if (!client.receiving.contains(request.getFileID())) {
                    Thread t = new Thread(new Receiver("file" + request.getFileID()));
                    t.start();
                    client.receiving.add(request.getFileID());
                }
            }
        }
    }

    private static ClientMessage generateClientMessage(String command) {
        String[] elements = command.split(" ");
        String action = elements[0].toUpperCase();
        ClientMessage message = new ClientMessage ();

        switch (action) {
            case "LIST":
                message.setAction(ClientMessage.Action.LIST);
                break;
            case "CREATE":
                if (elements.length < 3) {
                    Logger.warnLog("Please enter a valid command!");
                    System.out.println("CREATE <file title> <file content>");
                    return null;
                }
                message.setAction(ClientMessage.Action.CREATE);
                File newFile = new File(elements[1], elements[2]);
                break;
            case "UPDATE":
                if (elements.length < 3) {
                    Logger.warnLog("Please enter a valid command!");
                    System.out.println("UPDATE <file ID> <new content>: update content for a file");
                    return null;
                }
                message.setAction(ClientMessage.Action.UPDATE);
                message.setFileID(Long.valueOf(elements[1]));
                message.setNewContent(elements[2]);
                break;
            case "SHARE":
                if (elements.length < 3) {
                    Logger.warnLog("Please enter a valid command!");
                    System.out.println("SHARE <file ID> <username>: share a file with a user");
                    return null;
                }
                message.setAction(ClientMessage.Action.SHARE);
                message.setFileID(Long.valueOf(elements[1]));
                message.setUsername(elements[2]);
                break;
            case "ACCEPT":
                if (elements.length < 2) {
                    Logger.warnLog("Please enter a valid command!");
                    System.out.println("ACCEPT <file ID>: accept a file share invitation");
                    return null;
                }
                message.setAction (ClientMessage.Action.ACCEPT);
                message.setFileID(Long.valueOf(elements[1]));
                // more....
                break;
<<<<<<< HEAD:src/main/java/client/Client.java
            case "ACCEPT":
                message.setAction (ClientMessage.Action.ACCEPT);
                message.setFileID(Long.valueOf(elements[1]));
                break;
=======
            default:
                Logger.warnLog("Please enter a valid command!");
                System.out.println(instruction);
                return null;
>>>>>>> master:src/client/Client.java
        }
        return message;
    }
}