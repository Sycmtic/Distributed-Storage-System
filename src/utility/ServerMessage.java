package utility;

import java.util.List;

public class ServerMessage extends Message {
    private List<File> files;

    public List<File> getFiles() {
        return files;
    }

    public String getMessage() {
        return "";
    }
}
