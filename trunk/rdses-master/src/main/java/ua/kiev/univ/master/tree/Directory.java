package ua.kiev.univ.master.tree;

import ua.kiev.univ.slave.FileService;

import java.io.File;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class Directory {

    private FileService fileService;
    private String name;
    private Directory parent;
    private Directory[] childrenCache;

    public Directory(String name, FileSystem fileSystem) {
        this.name = name;
        fileService = fileSystem.getFileService();
    }

    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
        this.fileService = parent.getFileService();
    }

    public Directory[] getChildren() {
        if (childrenCache == null) {
            String[] dirs = fileService.listDirectories(getAbsolutePath());
            childrenCache = new Directory[dirs.length];
            int i = 0;
            for (String dir: dirs) {
                childrenCache[i++] = new Directory(dir, this);
            }
        }
        return childrenCache;
    }

    public String getName() {
        return name;
    }

    public String getAbsolutePath() {
        if (parent == null)
            return name;
        String parentAbsolutePath = parent.getAbsolutePath();
        if (parentAbsolutePath.endsWith(File.separator))
            parentAbsolutePath = parentAbsolutePath.substring(0, parentAbsolutePath.length() - 1);    
        return parentAbsolutePath + File.separator + name;
    }

    public FileService getFileService() {
        return fileService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Directory directory = (Directory) o;

        if (!name.equals(directory.name)) return false;
        if (parent != null ? !parent.equals(directory.parent) : directory.parent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
