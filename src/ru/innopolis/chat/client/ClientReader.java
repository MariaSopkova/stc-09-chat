package ru.innopolis.chat.client;

import ru.innopolis.chat.service.PortReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientReader extends Thread {
    private ServerSocket serverSocket;
    private boolean work;
    private String stopReaderWord;

    public ClientReader(int port) throws IOException {
        work = true;
        stopReaderWord = "stopReader" + port;
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (work) {
            try {
                Socket socket = serverSocket.accept();
                String messageFromServer = PortReader.readString(socket);
                if (messageFromServer.equals(stopReaderWord)) {
                    work = false;
                } else {
                    System.out.println(messageFromServer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
