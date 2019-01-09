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
     * @throws TimeoutException
     * @throws IOException
     */
    public AMQPCommunication(final String host, final int port) throws IOException, TimeoutException {
        LOGGER.info("Setting up RabbitMQ connection to '{}:{}'", host, port);
        this.factory = new ConnectionFactory();
        this.factory.setHost(host);
        this.factory.setPort(port);
        this.connection = this.factory.newConnection();
        this.channel = this.connection.createChannel();
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
            channel.basicPublish(exchange, key, null, message.getBytes());
            LOGGER.info("Message being sent.");
            return true;
        } catch (IOException e) {
            LOGGER.error("An error occured when trying to produce the message.\nError: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Closes an AMQP connection.
     */
    public final void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                LOGGER.error("Failed to close connection.\nError: {}", e.getMessage());
            }
        }
    }

    /**
     * Set username and password to use when connecting to the broker
     *
     * @param username
     *            the username to use
     * @param password
     *            the password to use
     */
    public void setCredentials(final String username, final String password) {
        this.factory.setUsername(username);
        this.factory.setPassword(password);
    }
}