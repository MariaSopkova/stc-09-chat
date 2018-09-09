package ru.innopolis.chat.client;

import ru.innopolis.chat.server.ServerPortsKeeper;
import ru.innopolis.chat.service.PortReader;
import ru.innopolis.chat.service.PortWriter;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    private final String stopChatWord = "!!stop";
    private int port;
    private String clientName;
    private Scanner scanner;
    private Socket socket;
    private ClientReader reader;

    public Client() throws IOException {
        askClientName();
        addClientToChat();
        initReader();
    }

    private void askClientName() {
        scanner = new Scanner(System.in);
        System.out.println("Please, enter your name:");
        clientName = scanner.nextLine();
        System.out.println("Enter " + stopChatWord + " to left chat");
    }

    private void addClientToChat() throws IOException {
        String result = sendMessageToServeicePort("addUser " + clientName);
        port = Integer.valueOf(result);
    }

    private String sendMessageToServeicePort(String message) throws IOException {
        Socket socket = sendMessageToServeidePort(message);
        return PortReader.readString(socket);
    }

    private void initReader() throws IOException {
        reader = new ClientReader(port);
        reader.start();
    }

    @Override
    public void run() {
        String message = scanner.nextLine();
        while (!message.isEmpty() && !message.equals(stopChatWord)) {
            try {
                sendMessageToUsers(message);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            message = scanner.nextLine();
        }
        try {
            sendMessageToServeidePort("leftUser " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("exit");
    }

    private void sendMessageToUsers(String message) throws IOException {
        String messageToServer = "msgToAll " + port + " " + message;
        sendMessageToServeidePort(messageToServer);
    }

    private Socket sendMessageToServeidePort(String message) throws IOException {
        Socket socket = new Socket("127.0.0.1", ServerPortsKeeper.servicePort);
        PortWriter.sendMessage(socket, message);
        return socket;
    }
}
