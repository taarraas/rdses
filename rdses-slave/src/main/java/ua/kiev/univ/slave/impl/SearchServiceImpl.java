package ua.kiev.univ.slave.impl;

import ua.kiev.univ.slave.model.SearchConfig;
import ua.kiev.univ.slave.model.SearchProgress;
import ua.kiev.univ.slave.SearchService;
import ua.kiev.univ.slave.model.SearchStatus;
import ua.kiev.univ.slave.model.SearchTask;

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
            if (searchThread.isAlive())
                throw new IllegalStateException("Search already in progress...");
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
