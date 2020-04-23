package utility;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String username;
    private List<Long> files = new ArrayList<>(); /** list of file IDs owned by user */

    public Account() {}

    public Account (String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public List<Long> getFiles() {
        return this.files;
    }
}
