package ua.kiev.univ.slave;

import ua.kiev.univ.slave.model.SearchConfig;
import ua.kiev.univ.slave.model.SearchProgress;
import ua.kiev.univ.slave.model.SearchStatus;

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
