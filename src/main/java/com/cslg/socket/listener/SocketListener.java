package com.cslg.socket.listener;

import com.cslg.socket.common.ConnectionHolder;
import com.cslg.socket.common.HandleData;
import com.cslg.socket.dao.JDBC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SocketListener implements ServletContextListener {

    private ServerSocket serverSocket;

    private static AtomicInteger sum = new AtomicInteger(0);

    private static final Integer LISTEN_SOCKET_PORT = 10054;

    private static final Logger logger = LoggerFactory.getLogger(SocketListener.class);

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    private static ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    class Handler implements Runnable {

        private Socket socket;

        private Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("成功连接到ip为: " + socket.getInetAddress() + "的客户端, time: " + df.format(new Date()));
            //记录工作的线程数
            System.out.println("工作的线程数为: " + sum.incrementAndGet());
            ConnectionHolder.add(JDBC.getConnect());
            handleSocket(socket);
        }
    }

    private void closeSocket() {
        try {
            if(!serverSocket.isClosed()) {
                //中断阻塞serverSocket.accept(); 也可以直接用singleThreadPool.shutDownNow()
                //serverSocket.setSoTimeout(1);
                serverSocket = new ServerSocket(10055);
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSocket(Socket socket) {
        try {
            HandleData handleData = new HandleData();
            handleData.setInputStream(socket.getInputStream());
            handleData.setOutputStream(socket.getOutputStream());
            byte[] bytes = new byte[16];
            handleData.getInputStream().read(bytes);
            System.out.println("初始化: " + handleData.encode(bytes));
            while(true) {
                if(threadPool.isShutdown()) {
                    System.out.println("剩余的线程数为: " + sum.decrementAndGet());
                    //断开和数据库的连接
                    ConnectionHolder.remove();
                    socket.close();
                    //closeSocket();
                    break;
                }
                //暂停1分钟在获取
                Thread.sleep(60000);
                if(handleData.writeData()) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void socketReceive() {
        try {
            while(true) {
                if(singleThreadPool.isShutdown()) {
                    break;
                }
                threadPool.execute(new Handler(serverSocket.accept()));
            }
        } catch (IOException e) {
            logger.error("连接客户端失败", e);
            e.printStackTrace();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(LISTEN_SOCKET_PORT);
                    socketReceive();
                } catch (IOException e) {
                    logger.error("创建ServerSocket出错", e);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        singleThreadPool.shutdown();
        threadPool.shutdown();
        while(true) {
            //是否还有正在工作的线程
            if(sum.get() == 0) {
                closeSocket();
                Thread.yield();
                break;
            }
        }
    }
}
