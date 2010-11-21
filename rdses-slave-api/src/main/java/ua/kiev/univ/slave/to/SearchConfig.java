package ua.kiev.univ.slave.to;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.*;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class SearchConfig implements Serializable {

    private Map<String, SerializableFileFilter> config;

    private SearchConfig(Map<String, SerializableFileFilter> config) {
        this.config = config;
    }

    public Set<Map.Entry<String, SerializableFileFilter>> getConfigurations() {
        return config.entrySet();
    }

    public static class SearchConfigBuilder implements Serializable {

        private Map<String, SerializableFileFilter> config = new HashMap<String, SerializableFileFilter>();

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

        private SerializableFileFilter toFileFilter(String mask) {
            final List<String> extensions = Arrays.asList(mask.split("[,]"));
            return new SerializableFileFilter() {
                
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
