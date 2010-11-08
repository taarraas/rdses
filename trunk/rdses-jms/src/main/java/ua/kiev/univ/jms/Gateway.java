package ua.kiev.univ.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.JmsUtils;

import javax.jms.*;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public abstract class Gateway<T extends Document> {

    private JmsTemplate jmsTemplate;

    private Destination destination;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    /**
     * @param document document to send
     * @throws org.springframework.jms.JmsException in case of any errors
     */
    public void push(final T document) {
        jmsTemplate.send(destination,
                new MessageCreator() {
                    public Message createMessage(Session session) throws JMSException {
                        ObjectMessage message = session.createObjectMessage();
                        message.setObject(document);
                        return message;
                    }
                }
        );
    }

    /**
     * @return document from InputQueue
     * @throws org.springframework.jms.JmsException in case of any errors
     */
    public T pull() {
        ObjectMessage message = (ObjectMessage) jmsTemplate.receive(destination);
        try {
            return (T) message.getObject();
        } catch (JMSException e) {
            throw JmsUtils.convertJmsAccessException(e);
        }
    }
}
