package utility;

import java.util.List;

public class ClientMessage extends Message {
    /* Define operation type of the message */
    public enum Action {
        LIST,
        CREATE,
        UPDATE,
        SHARE
    }

    private Account account;
    private Action action;
    private List<File> files;
    // used for create action
    private File file;

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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

