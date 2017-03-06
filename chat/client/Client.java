package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by sergei on 3/5/17.
 */
public class Client {

    /** connection which will be created */
    protected Connection connection;
    /** is there connection with user */
    private volatile boolean clientConnected;

    /** Method to start simple client messaging exchenge */
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    /** Method starts all application */
    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        try {
            synchronized (this) {
                this.wait();
            }

            if (clientConnected) {
                ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду ‘exit’.");
            } else {
                ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
            }

            while (true) {
                String text = ConsoleHelper.readString();
                if (text.equals("exit") || !clientConnected) {
                    break;
                }
                if (shouldSendTextFromConsole()) {
                    sendTextMessage(text);
                }
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("Error has happened!");
        }
    }

    /** */
    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Enter the address of the server");
        return ConsoleHelper.readString();
    }

    /** */
    protected int getServerPort() {
        ConsoleHelper.writeMessage("Enter the port of the server");
        return ConsoleHelper.readInt();
    }

    /** */
    protected String getUserName() {
        ConsoleHelper.writeMessage("Enter your username");
        return ConsoleHelper.readString();
    }

    /** */
    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    /** */
    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    /** */
    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("The message can't be sent.");
            clientConnected = false;
        }
    }

    /** Class-helper for exchange messages */
    public class SocketThread extends Thread {

        /** */
        @Override
        public void run() {
            try {
                String serverAddress = getServerAddress();
                int port = getServerPort();
                Socket socket = new Socket(serverAddress, port);
                Client.this.connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }

        /** */
        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                Message messageToSend = null;
                if (message.getType() == MessageType.NAME_REQUEST) {
                    String userName = getUserName();
                    messageToSend = new Message(MessageType.USER_NAME, userName);
                    connection.send(messageToSend);
                } else if (message.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    return;
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        /** */
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                String text = message.getData();
                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(text);
                } else if (message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(text);
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(text);
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        /** */
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        /** */
        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + " has joined to the chat.");
        }

        /** */
        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + " has been deleted from the chat.");
        }

        /** */
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }
    }
}
