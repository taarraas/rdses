package ua.kiev.univ.slave;

import ua.kiev.univ.slave.to.SearchConfig;
import ua.kiev.univ.slave.to.SearchProgress;
import ua.kiev.univ.slave.to.SearchStatus;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public interface SearchService {

    void start(SearchConfig searchConfig);
    SearchStatus getStatus();
    SearchProgress getProgress();
    void stop();
}
