package ru.innopolis.chat.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PortReader {

    public static String readString(Socket socket) throws IOException {
        InputStreamReader portInputStream = new InputStreamReader(socket.getInputStream());
        BufferedReader portReader = new BufferedReader(portInputStream);
        String str = portReader.readLine();
        return str;
    }


}
