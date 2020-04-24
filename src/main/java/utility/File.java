package utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class File implements Serializable {
    private long id; /** file id */
    private int version = 1; /** file version */
    private String title; /** file title */
    private String content = ""; /** file content */
    private List<String> owners = new ArrayList<>(); /** owners' usernames */

    public File (long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public File (String title, String content) {
        this.title = title;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getOwners() {
        return owners;
    }

    public void printInfo () {
        System.out.println("File ID: " + this.id + " Title: " + this.title + "   Version: " + this.version);
    }

    public void printContent() {
        System.out.println(content);
    }
}
