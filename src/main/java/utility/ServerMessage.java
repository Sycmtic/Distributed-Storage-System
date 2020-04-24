package utility;

import java.util.List;

public class ServerMessage extends Message {
    /* Define status type of the message */
    public enum Status {
        PREPARED,
        PROMISE,
        ACCEPTED
    }

    private List<File> files;
    int sender;
    long vote = -1;
    Status status;
    FileDB fileDB;
    AccountDB accountDB;

    public ServerMessage() { super(); }

    public ServerMessage(ServerMessage serverMessage) {
        super();
        this.files = serverMessage.files;
        this.sender = serverMessage.sender;
        this.vote = serverMessage.vote;
        this.status = serverMessage.status;
        this.fileDB = serverMessage.fileDB;
        this.accountDB = serverMessage.accountDB;
    }

    public ServerMessage deepCopy() {
        return new ServerMessage(this);
    }

    public List<File> getFiles() {
        return files;
    }

    public String getMessage() {
        return "";
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public long getVote() {
        return vote;
    }

    public void setVote(long vote) {
        this.vote = vote;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public FileDB getFileDB() {
        return fileDB;
    }

    public void setFileDB(FileDB fileDB) {
        this.fileDB = fileDB;
    }

    public AccountDB getAccountDB() {
        return accountDB;
    }

    public void setAccountDB(AccountDB accountDB) {
        this.accountDB = accountDB;
    }
}
