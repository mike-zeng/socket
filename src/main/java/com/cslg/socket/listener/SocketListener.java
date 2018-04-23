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
            logger.info("成功连接到ip为: {}的客户端, time: {}", socket.getInetAddress(), df.format(new Date()));
            Thread.currentThread().setName(socket.getInetAddress().toString() + " Thread-" +sum.incrementAndGet());
            logger.info("工作的线程数为: {}", sum.get());
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
            logger.error("关闭serverSocket出错", e);
        }
    }

    private void handleSocket(Socket socket) {
        try {
            HandleData handleData = new HandleData();
            handleData.setInputStream(socket.getInputStream());
            handleData.setOutputStream(socket.getOutputStream());
            byte[] bytes = new byte[16];
            handleData.getInputStream().read(bytes);
            logger.info("初始化: {}", handleData.encode(bytes));
            while(true) {
                if(threadPool.isShutdown()) {
                    break;
                }
                //暂停1分钟在获取
                Thread.sleep(60000);
                if(handleData.writeData()) {
                    break;
                }
            }
            //断开和数据库的连接
            ConnectionHolder.remove();
            //关闭socket
            socket.close();
            logger.info("剩余的线程数为: {}", sum.decrementAndGet());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            logger.error("系统出错", e);
        }
    }

    private void socketReceive() {
        try {
            while(true) {
                if(singleThreadPool.isShutdown()) {
                    break;
                }
                threadPool.execute(new Handler(serverSocket.accept()));
                System.out.println("------=================================");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("连接客户端失败", e);
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
                    e.printStackTrace();
                    logger.error("创建ServerSocket出错", e);
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
