package ua.kiev.univ.slave;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.kiev.univ.common.Network;
import ua.kiev.univ.master.SlaveRegistry;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class Main {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-slave-module.xml");
        SlaveRegistry slaveRegistry = context.getBean("slaveRegistry", SlaveRegistry.class);
        slaveRegistry.register(Network.getPublicIP());
    }
}
