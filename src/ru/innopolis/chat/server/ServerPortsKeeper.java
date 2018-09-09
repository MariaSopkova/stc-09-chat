package ru.innopolis.chat.server;

public class ServerPortsKeeper {
    public static final int servicePort = 5001;
    private int freePort = 5003;

    public int GetFreePort() {
        return freePort++;
    }
}
