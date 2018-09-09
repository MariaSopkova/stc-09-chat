package ru.innopolis.chat.server;

import ru.innopolis.chat.service.PortReader;
import ru.innopolis.chat.service.PortWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Server extends Thread {
    private ServerPortsKeeper portsKeeper;
    private Map<Integer, String> portsAndUsers;
    private boolean stopServer;

    public Server() {
        portsAndUsers = new TreeMap<>();
        portsKeeper = new ServerPortsKeeper();
        stopServer = false;
    }

    @Override
    public void run() {
        System.out.println("server started");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(ServerPortsKeeper.servicePort);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (!stopServer) {
            try {
                Socket socket = serverSocket.accept();
                processingMessageFromClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("server stopped");
    }

    private void processingMessageFromClient(Socket socket) throws IOException {
        List<String> commandParts = Arrays.asList(PortReader.readString(socket).split(" ", 2));
        if (commandParts.size() < 1) {
            return;
        } else {
            switch (commandParts.get(0)) {
                case "addUser": {
                    addUser(commandParts.get(1), socket);
                    break;
                }
                case "leftUser": {
                    deleteUser(commandParts.get(1));
                    break;
                }
                case "msgToAll": {
                    sendMessageToUsers(commandParts.get(1));
                    break;
                }
                default: {
                    doOnDefaultCommand(commandParts.get(0));
                    break;
                }
            }
        }
    }

    private boolean addUser(String userName, Socket socket) throws IOException {
        int freePort = portsKeeper.GetFreePort();
        try {
            PortWriter.sendMessage(socket, new Integer(freePort).toString());
            runNewPort(freePort, userName);
            sendMessageToAll(userName + " joined!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean runNewPort(int port, String userName) throws IOException {
        portsAndUsers.put(port, userName);
        System.out.println(userName + " entered. Port " + port);
        return true;
    }

    private boolean sendMessageToUsers(String command) throws IOException {
        List<String> commandParts = Arrays.asList(command.split(" ", 2));
        Integer fromPort = Integer.valueOf(commandParts.get(0));
        String message = portsAndUsers.get(fromPort) + ": " + commandParts.get(1);
        System.out.println(message);
        sendMessageToAllExceptOne(fromPort, message);
        return true;
    }

    private boolean deleteUser(String portAsString) throws IOException {
        int deletePort = Integer.valueOf(portAsString);
        String messageToAll = portsAndUsers.get(deletePort) + " left chat";
        sendMessageToPort(deletePort, "stopReader" + deletePort);
        portsAndUsers.remove(deletePort);
        if (portsAndUsers.isEmpty()) {
            stopServer = true;
        } else {
            System.out.println(messageToAll);
            sendMessageToAll(messageToAll);
        }
        return true;
    }

    private void doOnDefaultCommand(String command) {
        System.out.println("Command " + command + " not supported");
    }

    private void sendMessageToAll(String message) throws IOException {
        for (Map.Entry<Integer, String> userAndPort : portsAndUsers.entrySet()) {
            sendMessageToPort(userAndPort.getKey(), message);
        }
    }

    private void sendMessageToAllExceptOne(Integer exceptPort, String message) throws IOException {
        for (Map.Entry<Integer, String> userAndPort : portsAndUsers.entrySet()) {
            if (!userAndPort.getKey().equals(exceptPort)) {
                sendMessageToPort(userAndPort.getKey(), message);
            }
        }
    }

    private void sendMessageToPort(Integer port, String message) throws IOException {
        Socket socket = new Socket("127.0.0.1", port);
        PortWriter.sendMessage(socket, message);
    }
}
