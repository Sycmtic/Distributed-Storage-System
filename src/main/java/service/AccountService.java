package service;

import utility.*;

import java.util.List;

public class AccountService {
    protected AccountDB accountDB;
    protected FileDB fileDB;

    public AccountService(AccountDB accountDB, FileDB fileDb) {
        this.accountDB = accountDB;
        this.fileDB = fileDb;
    }

    /**
     * process login in request
     * @param logInMessage
     * @return
     */
    public LogInMessage process(LogInMessage logInMessage) {
        Account account = accountDB.getAccount(logInMessage.getUsername());
        if (account == null) {
            logInMessage.setResult(Message.Result.FAIL);
        } else {
            logInMessage.setAccount(account);
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
        List<File> files = fileDB.getFiles(account.getFiles());
        if (files == null || files.size() == 0) {
            message.setResult(Message.Result.FAIL);
        } else {
            message.setFiles(files);
        }
        return message;
    }
}
