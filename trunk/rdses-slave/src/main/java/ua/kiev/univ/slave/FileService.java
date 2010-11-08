package ua.kiev.univ.slave;

import java.util.List;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public interface FileService {

    String[] listDirectories(String root);
    String[] listDrives();
}
