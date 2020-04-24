package utility;

import java.util.List;

public class ClientMessage extends Message {
    /* Define operation type of the message */
    public enum Action {
        LIST,
        OPEN,
        CREATE,
        UPDATE,
        SHARE,
        ACCEPT,
    }

    private Account account;
    private Action action;
    private List<File> files;
    private long fileID; /** used for receiving notification or share a file*/
    private File file;/** used for create action */
    private String username;/** used for user to share */
    private String newContent; /** used for update content */

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account user) {
        this.account = user;
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public List<File> getFiles() {
        return this.files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public long getFileID() {
        return fileID;
    }

    public void setFileID(long fileID) {
        this.fileID = fileID;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public void printMessage() {
        System.out.println(this.getResult() + " to " + this.getAction());
    }
}

