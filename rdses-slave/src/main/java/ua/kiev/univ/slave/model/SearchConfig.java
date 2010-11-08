package ua.kiev.univ.slave.model;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.*;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class SearchConfig implements Serializable {

    private Map<String, FileFilter> config;

    private SearchConfig(Map<String, FileFilter> config) {
        this.config = config;
    }

    public Set<Map.Entry<String, FileFilter>> getConfigurations() {
        return config.entrySet();
    }

    public static class SearchConfigBuilder {

        private Map<String, FileFilter> config = new HashMap<String, FileFilter>();

        /**
         * @param path directory location
         * @param filter comma-separated list of extensions
         */
        public void add(String path, String filter) {
            config.put(path, toFileFilter(filter));
        }

        public SearchConfig build() {
            return new SearchConfig(Collections.unmodifiableMap(config));
        }

        private FileFilter toFileFilter(String mask) {
            final List<String> extensions = Arrays.asList(mask.split("[,]"));
            return new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isDirectory())
                        return true;
                    String filename = file.getName();
                    int dotPosition = filename.lastIndexOf(".");
                    return dotPosition >= 0 && extensions.contains(filename.substring(dotPosition + 1));
                }
            };
        }
    }
}
