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
                if (accountDB.getAccounts().containsKey(logInMessage.getUsername())) {
                    logInMessage.setResult(Message.Result.FAIL);
                }else {
                    Account newAccount = accountDB.createAccount(logInMessage.getUsername());
                    logInMessage.setAccount(newAccount);
                }
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
