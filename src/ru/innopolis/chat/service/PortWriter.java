package ru.innopolis.chat.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PortWriter {
    public static void sendMessage(Socket socket, String msg) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter portWriter = new BufferedWriter(outputStreamWriter);
        portWriter.write(msg);
        portWriter.newLine();
        portWriter.flush();
    }
}
