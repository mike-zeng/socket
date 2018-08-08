package com.cslg.socket.common;

import java.net.Socket;

public class Task {

    private Socket socket;

    private Thread thread;

    public Task(Socket socket, Thread thread) {
        this.socket = socket;
        this.thread = thread;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
