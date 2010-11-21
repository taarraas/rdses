package ua.kiev.univ.master;

import java.util.Observable;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class SlaveRegistryImpl extends Observable implements SlaveRegistry {

    @Override
    public void register(String ip) {
        setChanged();
        notifyObservers(ip);
    }
}
