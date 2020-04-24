package client;

import server.Server;
import utility.*;
import utility.Message;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import java.util.*;
import java.util.Scanner;

public class Client {
    final static String instruction = "Please type your request...\n" +
            "LIST: list your files\n" +
            "OPEN <file ID>: open your file to view the content\n" +
            "CREATE <file title> <file content>: create a file with a title and content\n" +
            "SHARE <file ID> <username>: share a file with a user\n" +
            "UPDATE <file ID> <new content>: update content for a file\n";

    private String host;
    private int port;
    private Account user;
    private Server server;
//    private Set<Long> receiving = new HashSet<>();

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
                        if (response.getResult() == Message.Result.FAIL || response.getAccount() == null) {
                            Logger.warnLog("Not a valid username! Please check your username and try again.");
                        } else {
                            Logger.infoLog("Successfully logged in.");
                            client.user = response.getAccount();
                            Thread t = new Thread(new Receiver(client.user.getUsername()));
                            t.start();
//                            client.receiving.add(response.getFileID());
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
                            System.out.println("here is account for " + response.getAccount().getUsername());
                            client.user = response.getAccount();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("wrong action, please input [login|signup]");
                    break;
            }
        }

        System.out.println("Retrieving your files... ");
        ClientMessage clientMessage = new ClientMessage();
        clientMessage.setAction(ClientMessage.Action.LIST);
        clientMessage.setAccount(client.user);

        try {
            ClientMessage response = client.send(clientMessage);
            if (response.getResult() == Message.Result.FAIL || response.getFiles().size() == 0) {
                System.out.println("There is no file under your account");
            } else {
                for (File file : response.getFiles()) {
                    if (file == null) {
                        System.out.println("null");
                    }else {
                        file.printInfo();
//                        Thread t = new Thread(new Receiver("file" + file.getId()));
//                        t.start();
//                        client.receiving.add(file.getId());
                    }
                }
            }
        } catch (RemoteException e) {
            Logger.warnLog(e.getMessage());
        }

        System.out.println(instruction);

        //Start to take request from client
        while (true) {
            String command = scanner.nextLine();
            ClientMessage request = generateClientMessage(command);
            //invalid command
            if (request == null) {
                continue;
            }

            if (request.getAction() != ClientMessage.Action.ACCEPT) {
                request.setAccount(client.user);
                try {
                    Logger.infoLog("Sending request: " + command);
                    ClientMessage response = client.send(request);
                    response.printMessage();
                    if (response.getResult() != Message.Result.FAIL) {
                        if (response.getAction() == ClientMessage.Action.LIST) {
                            if (response.getFiles().size() == 0) {
                                System.out.println("There is no file under your account");
                            } else {
                                for (File file : response.getFiles()) {
                                    if (file == null) {
                                        System.out.println("null");
                                    }else {
                                        file.printInfo();
                                    }
                                }
                            }
                        } else if (response.getAction() == ClientMessage.Action.CREATE) {
                            client.user = response.getAccount();
//                            Thread t = new Thread(new Receiver("file" + response.getFileID()));
//                            t.start();
//                            client.receiving.add(response.getFileID());
                        } else if (response.getAction() == ClientMessage.Action.OPEN) {
                            response.getFile().printContent();
                        } else {
                            Logger.infoLog(response.getErrorMessage());
                        }
                    }
                } catch (RemoteException e) {
                    Logger.warnLog("Client send request error!\n" + e.getMessage());
                }
//            } else {
//                if (!client.receiving.contains(request.getFileID())) {
//                    Thread t = new Thread(new Receiver("file" + request.getFileID()));
//                    t.start();
//                    client.receiving.add(request.getFileID());
//                }
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
            case "OPEN":
                message.setAction(ClientMessage.Action.OPEN);
                message.setFileID(Long.valueOf(elements[1]));
                break;
            case "CREATE":
                if (elements.length < 3) {
                    Logger.warnLog("Please enter a valid command!");
                    System.out.println("CREATE <file title> <file content>");
                    return null;
                }
                message.setAction(ClientMessage.Action.CREATE);
                File newFile = new File(elements[1], elements[2]);
                message.setFile(newFile);
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
            default:
                Logger.warnLog("Please enter a valid command!");
                System.out.println(instruction);
                return null;
        }
        return message;
    }
}