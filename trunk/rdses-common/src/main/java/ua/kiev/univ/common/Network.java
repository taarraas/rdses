package ua.kiev.univ.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author jamanal
 * @version 2010-11-09
 */
public class Network {

    public static String getPublicIP() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return address.getHostAddress();
    }
}
