package ua.kiev.univ.master;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author sshyiko@luxoft.com
 * @version Nov 27, 2010
 */
public class Main {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-master-module.xml");
        new MainForm(context).show();
    }
}
