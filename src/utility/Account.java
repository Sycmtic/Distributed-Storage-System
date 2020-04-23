package utility;

import java.util.List;

public class Account {
    private String username;
    private List<Long> files; /** list of file IDs owned by user */

    public String getUsername() {
        return this.username;
    }

    public List<Long> getFiles() {
        return this.files;
    }
}
