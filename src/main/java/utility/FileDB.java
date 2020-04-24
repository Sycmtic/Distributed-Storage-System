package utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileDB implements Serializable {
    private long lastID = 100003;
    private Map<Long, File> files; /** key is file ID */

    public FileDB () {
        files = new HashMap<>();
        files.put ((long)100001, new File((long)100001, "title1", "content1"));
        files.put ((long)100002, new File((long)100002, "title2", "content2"));
        files.put ((long)100003, new File((long)100003, "title3", "content3"));
    }

    public FileDB(FileDB fileDB) {
        files = new HashMap<>();
        files.putAll(fileDB.getFiles());
        lastID = fileDB.getLastID();
    }

    public long getLastID() {
        return lastID;
    }

    public Map<Long, File> getFiles() {
        return files;
    }

    /**
     * get files by list of file ids
     * @param fileIds
     * @return list of files
     */
    public List<File> getFileByIds(List<Long> fileIds) {
        List<File> list = new ArrayList<>();
        for (long id : fileIds) {
            // -- TO DO handle the case if there is no specific file id in files
            System.out.println(id);
            list.add(files.get(id));
            System.out.println(id);
            if (files.get(id) == null) {
                System.out.println("Got a null file!");
            }
        }
        return list;
    }

    public void addFile(long id, File file) {
        file.setId(id);
        files.put(id, file);
        lastID = id;
    }

    public File getFileById(long id) {
        return files.get(id);
    }
}
