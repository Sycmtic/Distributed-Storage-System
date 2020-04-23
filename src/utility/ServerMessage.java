package utility;

import java.util.List;

public class ServerMessage extends Message {
    private List<File> files;
    private Action action;

    public List<File> getFiles() {
        return files;
    }

    public Action getAction() {
        return action;
    }

    public String getMessage() {
        return "";
    }
}
