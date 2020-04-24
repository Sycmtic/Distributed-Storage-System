package service;

import utility.*;
import java.util.List;

public class ShareService {
    protected AccountDB accountDB;
    protected FileDB fileDB;
    protected Long fileID; /** file id of the shared file */
    protected String newUsername; /** username of the added user */

    public ShareService(AccountDB accountDB, FileDB fileDB, String newUsername, Long fileID) {
        this.accountDB = accountDB;
        this.fileDB = fileDB;
        this.fileID = fileID;
        this.newUsername = newUsername;
    }

    /*
     * share files with another client
     */
    public ClientMessage process(ClientMessage message) {
        Account newUserAccount = accountDB.getAccount(newUsername);
        if (newUserAccount == null) {
            message.setResult(Message.Result.FAIL);
        }else {
            List<Long> files = newUserAccount.getFiles();
            if (files == null) {
                message.setResult(Message.Result.FAIL);
            } else {
                files.add(fileID);
            }
            message.setAccountDB(accountDB);
        }
        return message;
    }
}
