package com.cslg.socket.service;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author twilight
 */
public abstract class AbstractService<T> {

    private T object;

    private OutputStream outputStream;

    private InputStream inputStream;

    private String sign;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public abstract boolean readData(String ... str);

    public abstract boolean writeData();

    public abstract void handleMessage();

}
