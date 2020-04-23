package utility;

import java.util.List;

public class File {
    private long id; /** file id */
    private int version; /** file version */
    private String title; /** file title */
    private String content; /** file content */
    private List<String> owners; /** owners' usernames */

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
        System.out.println(this.title + "   v: " + this.version);
    }
}
