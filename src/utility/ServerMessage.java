package utility;

import java.util.List;

public class ServerMessage extends Message {
    private List<File> files;
    private Operation operation;

    public List<File> getFiles() {
        return files;
    }

    public Operation getAction() {
        return operation;
    }

    public String getMessage() {
        return "";
    }
}
