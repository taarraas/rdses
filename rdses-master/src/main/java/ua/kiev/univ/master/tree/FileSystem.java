package ua.kiev.univ.master.tree;

import ua.kiev.univ.slave.FileService;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class FileSystem {

    private FileService fileService;
    private Directory[] drivesCache;

    public FileSystem(FileService fileService) {
        this.fileService = fileService;
    }

    public Directory[] listDrives() {
        if (drivesCache == null) {
            String[] drives = fileService.listDrives();
            drivesCache = new Directory[drives.length];
            int i = 0;
            for (String drive: drives) {
                drivesCache[i++] = new Directory(drive, this);
            }
        }
        return drivesCache;
    }

    public FileService getFileService() {
        return fileService;
    }

    @Override
    public String toString() {
        return "Remote FileSystem";
    }
}
