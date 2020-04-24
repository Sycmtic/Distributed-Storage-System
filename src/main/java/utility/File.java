package utility;

import java.util.ArrayList;
import java.util.List;

public class File {
    private long id; /** file id */
    private int version = 1; /** file version */
    private String title; /** file title */
    private String content = ""; /** file content */
    private List<String> owners = new ArrayList<>(); /** owners' usernames */

    public File (long id, String title) {
        this.id = id;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getOwners() {
        return owners;
    }

    public void printInfo () {
        System.out.println("File ID: " + this.id + " Title: " + this.title + "   Version: " + this.version);
    }
}
