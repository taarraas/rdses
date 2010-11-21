package ua.kiev.univ.master.tree;

import ua.kiev.univ.master.SlaveConfig;
import ua.kiev.univ.slave.SearchService;
import ua.kiev.univ.slave.to.SearchStatus;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class MonitoringThread extends Thread {

    private SlaveConfig slaveConfig;

    public MonitoringThread(SlaveConfig slaveConfig) {
        this.slaveConfig = slaveConfig;
    }

    @Override
    public void run() {
        SearchService searchService = slaveConfig.getSearchService();
        while (searchService.getStatus() == SearchStatus.RUNNING) {
            updateInformation();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        updateInformation();
    }

    private void updateInformation() {
        SearchService service = slaveConfig.getSearchService();
        slaveConfig.setProgress(service.getProgress());
        slaveConfig.setStatus(service.getStatus());
    }
}
