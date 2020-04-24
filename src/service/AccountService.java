package service;

import server.Server;
import utility.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccountService {
    List<Integer> ports;
    int port;
    ServerMessage previousVote;
    Map<Integer, Server> servers;
    Set<Integer> failedServers;
    FileDB fileDB;
    AccountDB accountDB;

    public AccountService(int port, List<Integer> ports, ServerMessage previousVote,
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

    /**
     * process login in request
     * @param logInMessage
     * @return
     */
    public LogInMessage process(LogInMessage logInMessage) {
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
                Account newAccount = accountDB.createAccount(logInMessage.getUsername());
                logInMessage.setAccount(newAccount);
                break;
            default:
                logInMessage.setResult(Message.Result.FAIL);
                break;
        }
        return logInMessage;
    }

    /**
     * List files of current user
     * @param message
     * @return
     */
    public ClientMessage process(ClientMessage message) {
        Account account = message.getAccount();
        List<File> files = fileDB.getFileByIds(account.getFiles());
        if (files == null || files.size() == 0) {
            message.setResult(Message.Result.FAIL);
        } else {
            message.setFiles(files);
        }
        return message;
    }
}
