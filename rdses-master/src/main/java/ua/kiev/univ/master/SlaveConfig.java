package ua.kiev.univ.master;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import ua.kiev.univ.master.tree.Directory;
import ua.kiev.univ.master.tree.MonitoringThread;
import ua.kiev.univ.master.tree.SelectionModel;
import ua.kiev.univ.slave.FileService;
import ua.kiev.univ.slave.SearchService;
import ua.kiev.univ.slave.to.SearchConfig;
import ua.kiev.univ.slave.to.SearchProgress;
import ua.kiev.univ.slave.to.SearchStatus;

import javax.swing.*;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class SlaveConfig extends Observable {

    private String ip;
    private FileService fileService;
    private SearchService searchService;
    private SelectionModel directoriesSelectionModel;
    private Set<String> selectedExtension;
    private SearchStatus status;
    private SearchProgress progress;
    private MonitoringThread monitoringThread;

    public SlaveConfig(String ip) {
        this.ip = ip;
        fileService = getService("rmi://localhost:8080/" + ip + "-FileService", FileService.class);
        searchService = getService("rmi://localhost:8080/" + ip + "-SearchService", SearchService.class);
        directoriesSelectionModel = new SelectionModel();
        selectedExtension = new HashSet<String>();
    }

    public String getIp() {
        return ip;
    }

    public FileService getFileService() {
        return fileService;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public SelectionModel getSelectedDirectoriesModel() {
        return directoriesSelectionModel;
    }

    public Set<String> getSelectedExtension() {
        return selectedExtension;
    }

    @Override
    public String toString() {
        return ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SlaveConfig that = (SlaveConfig) o;

        if (!ip.equals(that.ip)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ip.hashCode();
    }

    public void startSearch() {
        if (selectedExtension.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You need to select at least one extension to search for");
            return;
        }
        if (directoriesSelectionModel.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You need to select at least one path to search in");
            return;
        }
        String exts = getExtensionPattern();
        SearchConfig.SearchConfigBuilder builder = new SearchConfig.SearchConfigBuilder();
        for (Directory directory : directoriesSelectionModel.getSelectedDirectories()) {
            builder.add(directory.getAbsolutePath(), exts);
        }
        SearchConfig config = builder.build();
        searchService.start(config);
        monitoringThread = new MonitoringThread(this);
        monitoringThread.start();
    }

    private String getExtensionPattern() {
        StringBuilder extensions = new StringBuilder();
        for (String extension : getSelectedExtension()) {
            extensions.append(extension);
            extensions.append(",");
        }
        extensions.deleteCharAt(extensions.length() - 1);
        return extensions.toString();
    }

    public void stopSearch() {
        monitoringThread.interrupt();
        searchService.stop();
    }

    private <T> T getService(String url, Class<T> type) {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceUrl(url);
        rmiProxyFactoryBean.setServiceInterface(type);
        rmiProxyFactoryBean.afterPropertiesSet();
        return (T) rmiProxyFactoryBean.getObject();
    }

    public void setStatus(SearchStatus status) {
        if (status == SearchStatus.TERMINATED) {
            setChanged();
            notifyObservers("Work completed with " + progress.getNumberOfSubmittedFiles() + " files submitted");
        }
    }

    public void setProgress(SearchProgress progress) {
        this.progress = progress;
        setChanged();
        notifyObservers("Submitted " + progress.getNumberOfSubmittedFiles() +
                " files of " + progress.getNumberOfScannedFiles() + " scanned");
    }
}
