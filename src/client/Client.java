package client;

import server.Server;
import utility.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Scanner;

public class Client {
    private String host;
    private int port;
//    private AccountService accountService;
//    private FileService fileService;
//    private UpdateService updateService;
    private Account user;
    private Server server;

    public Client () {}

    public Client (String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws Exception {
        server = (Server) LocateRegistry.getRegistry(host, port).lookup("Server");
//        accountService = (AccountService) LocateRegistry.getRegistry(host, port).lookup("AccountService");
//        fileService = (FileService) LocateRegistry.getRegistry(host, port).lookup("FileService");
//        updateService = (UpdateService) LocateRegistry.getRegistry(host, port).lookup("UpdateService");
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
        System.out.println("Please enter whether you want to login or create a new account:");
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
                case "create":
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
                    System.out.print("wrong action, please input [create|login]");
                    break;
            }
        }

        System.out.println("Successfully logged. Here are your files: ");
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
                }
            }
        } catch (RemoteException e) { }


        while (true) {
            String command = scanner.nextLine();
            ClientMessage request = generateClientMessage(command);
            request.setAccount(client.user);
            if (request != null) {
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
            case "UPDATE":
                message.setAction(ClientMessage.Action.UPDATE);
                break;
            case "SHARE":
                message.setAction(ClientMessage.Action.SHARE);
                break;
        }
        return message;
    }

}
