package ua.kiev.univ.slave.impl;

import ua.kiev.univ.slave.FileService;

import java.io.File;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class FileServiceImpl implements FileService {

    @Override
    public String[] listDirectories(String root) {
        return new File(root).list();
    }

    @Override
    public String[] listDrives() {
        File[] files = File.listRoots();
        String[] result = new String[files.length];
        for (int i = 0; i < files.length; i++)
            result[i] = files[i].getName();
        return result;
    }
}
