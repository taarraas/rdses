package ua.kiev.univ.slave;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class FileServiceImpl implements FileService {

    @Override
    public String[] listDirectories(String root) {
        String[] result = new File(root).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });
        if (result == null)
            result = new String[0];
        return result;
    }

    @Override
    public String[] listDrives() {
        File[] files = File.listRoots();
        String[] result = new String[files.length];
        for (int i = 0; i < files.length; i++)
            result[i] = files[i].getAbsolutePath();
        return result;
    }
}
