package ua.kiev.univ.slave;

import ua.kiev.univ.slave.to.SearchConfig;
import ua.kiev.univ.slave.to.SearchProgress;
import ua.kiev.univ.slave.to.SearchStatus;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class SearchServiceImpl implements SearchService {

    private SearchTask searchTask;
    private Thread searchThread;

    public void setSearchTask(SearchTask searchTask) {
        this.searchTask = searchTask;
    }

    @Override
    public void start(SearchConfig searchConfig) {
        synchronized (this) {
            if (searchThread != null && searchThread.isAlive())
                throw new IllegalStateException("Search already in progress...");
            searchTask.setSearchConfig(searchConfig);
            searchThread = new Thread(searchTask);
            searchThread.start();
        }
    }

    @Override
    public SearchStatus getStatus() {
        return searchTask.getStatus();
    }

    @Override
    public SearchProgress getProgress() {
        return searchTask.getProgress();
    }

    @Override
    public void stop() {
        synchronized (this) {
            searchThread.interrupt();
        }
    }
}
