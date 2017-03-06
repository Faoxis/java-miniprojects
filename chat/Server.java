package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sergei on 3/5/17.
 */
public class Server {

    /** Mapping username to connection */
    private static Map<String, Connection> connectionMap =
            new ConcurrentHashMap<>();

    /** Method to start the server */
    public static void main(String[] args) throws IOException {
        ConsoleHelper.writeMessage("Enter port number");
        int portNumber = ConsoleHelper.readInt();

        ServerSocket serverSocket = new ServerSocket(portNumber);
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (Exception e) {
            serverSocket.close();
            ConsoleHelper.writeMessage("Server socket has been closed!");
        }
    }

    /** Method to send text to everyone */
    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> nameConnection : connectionMap.entrySet()) {
            try {
                nameConnection.getValue().send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Something has happened wtih "
                        + nameConnection.getKey());
            }
        }
    }


    /** This class is Handling input data */
    private static class Handler extends Thread {
        private Socket socket;

        /** Constructor */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /** Thread method to handle request */
        @Override
        public void run() {
            String userName = null;
            try (Connection connection = new Connection(socket)) {
                ConsoleHelper.writeMessage("A new connection with "
                        + socket.getRemoteSocketAddress() + " has got.");

                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);

            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("The error has happened with remote connection "
                        + socket.getRemoteSocketAddress());
            } finally {
                if (userName != null) {
                    connectionMap.remove(userName);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED,
                            userName));
                }
                ConsoleHelper.writeMessage("Connection with " + socket.getRemoteSocketAddress() + " has been closed");
            }
        }

        /** Handshake with user */
        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();
                if (message.getType() == MessageType.USER_NAME) {
                    String name = message.getData();
                    if (!name.isEmpty() && !connectionMap.containsKey(name)) {
                        connection.send(new Message(MessageType.NAME_ACCEPTED));
                        connectionMap.put(name, connection);
                        return name;
                    }
                }
            }
        }

        /** sending message to full user list */
        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                String eachName = entry.getKey();

                if (!eachName.equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, eachName));
                }
            }
        }

        /** the main method to exchange messages */
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    String stringToSend = userName + ": " + message.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, stringToSend));
                } else {
                    ConsoleHelper.writeMessage("Wrong type has been got!");
                }
            }
        }
    }


}
