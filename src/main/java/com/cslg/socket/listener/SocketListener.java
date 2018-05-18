package com.cslg.socket.listener;

import com.cslg.socket.common.ConnectionHolder;
import com.cslg.socket.common.HandleData;
import com.cslg.socket.common.Task;
import com.cslg.socket.dao.JDBC;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SocketListener implements ServletContextListener {

    private ServerSocket serverSocket;

    public static AtomicInteger sum = new AtomicInteger(0);

    private static final Integer LISTEN_SOCKET_PORT = 10054;

    private static final Logger logger = LoggerFactory.getLogger(SocketListener.class);

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    private static ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private static Set<String> signSet = new HashSet<>();

    public static ConcurrentMap<String, Task> clientSignMap = new ConcurrentHashMap<>();

    static {
        signSet.add("FE");
    }

    private boolean dealSign(HandleData handleData, Socket socket) {
        byte[] bytes = new byte[1];
        try {
            handleData.getInputStream().read(bytes);
        } catch (SocketTimeoutException e) {
            logger.info("read()超时");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
        String sign = handleData.encode(bytes);
        if(!signSet.contains(sign)) {
            return true;
        }
        logger.info("初始化, 标记为: {}", sign);
        logger.info("工作的线程数为: {}", sum.incrementAndGet());
        Thread thread = Thread.currentThread();
        Task task = new Task(socket, thread);
        if(!thread.getName().contains(sign)) {
            thread.setName("该客户端标识号: " + sign + "--工作线程名称为: " + thread.getName());
        }
        if(clientSignMap.containsKey(sign)) {
            try {
                clientSignMap.get(sign).getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clientSignMap.put(sign, task);
        ConnectionHolder.add(JDBC.getConnect());
        return false;
    }

    private void handleSocket(Socket socket) {
        try {
            HandleData handleData = new HandleData();
            handleData.setInputStream(socket.getInputStream());
            handleData.setOutputStream(socket.getOutputStream());
            if(dealSign(handleData, socket)) {
                return;
            }
            while(true) {
                if(threadPool.isShutdown()) {
                    break;
                }
                //暂停2分钟在获取
                Thread.sleep(120000);
                //Thread.sleep(3000);
                if(handleData.writeData()) {
                    break;
                }
            }
            //断开和数据库的连接
            ConnectionHolder.remove();
            //关闭socket
            socket.close();
            sum.decrementAndGet();
            logger.info("剩余的线程数为: {}", sum.get());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            logger.error("系统出错", e);
        }
    }

    private void socketReceive() {
        try {
            while(true) {
                Socket socket = serverSocket.accept();
                // 设置read()阻塞超时时间
                socket.setSoTimeout(180000);
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        logger.info("成功连接到ip为: {}的客户端, time: {}", socket.getInetAddress(), df.format(new Date()));
                        handleSocket(socket);
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("手动停止线程");
            e.printStackTrace();
            logger.info("线程中断", e);
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
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
