package com.ericsson.ei.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Basic AMQP communication class.
 */
public class AMQPCommunication {
    ConnectionFactory factory;
    Connection connection;
    Channel channel;

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPCommunication.class);

    /**
     * AMQPCommunication constructor.
     *
     * @param host
     *            host name
     * @param port
     *            port number
     * @param username
     *            the username to use
     * @param password
     *            the password to use
     * @throws IOException
     * @throws TimeoutException
     */
    public AMQPCommunication(final String host, final int port, final String username, final String password)
            throws IOException, TimeoutException {
        LOGGER.info("Setting up RabbitMQ connection to '{}:{}'", host, port);
        this.factory = new ConnectionFactory();
        this.factory.setHost(host);
        this.factory.setPort(port);
        this.factory.setUsername(username);
        this.factory.setPassword(password);
        this.connection = this.factory.newConnection();
        this.channel = connection.createChannel();
    }

    /**
     * Send a message to specified message bus.
     *
     * @param message
     *            content to send
     * @param exchange
     *            exchange to receive content
     * @param key
     *            routing key
     * @return true if message was sent, false otherwise
     */
    public boolean produceMessage(final String message, final String exchange, final String key) {
        LOGGER.info("Preparing to produce message -> Host: {}, Exchange: {}, RoutingKey: {}\nMessage: {}",
                factory.getHost() + ":" + factory.getPort(), exchange, key, message);
        try {
            this.channel.basicPublish(exchange, key, null, message.getBytes());
            LOGGER.info("Message being sent.");
            return true;
        } catch (IOException e) {
            LOGGER.error("An error occured when trying to produce the message.", e);
        }
        return false;
    }

    /**
     * Closes an AMQP connection.
     */
    public final void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (IOException e) {
                LOGGER.error("Failed to close connection.", e);
            }
        }
    }
}