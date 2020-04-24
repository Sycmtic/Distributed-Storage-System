package utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {
    private String username;
    private List<Long> files; /** list of file IDs owned by user */

    public Account() {
        files = new ArrayList<>();
    }

    public Account(String username) {
        this.username = username;
        files = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<Long> getFiles() {
        return files;
    }

    public void addFile(long fileId) {
        if(files == null) {
            files = new ArrayList<>();
        }
        files.add(fileId);
    }
}
