package com.cslg.socket.listener;

import com.cslg.socket.common.ConnectionHolder;
import com.cslg.socket.service.AbstractService;
import com.cslg.socket.common.Pool;
import com.cslg.socket.common.Task;
import com.cslg.socket.dao.JDBC;
import com.cslg.socket.utils.CodeUtil;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author twilight
 */
@WebListener
public class SocketListener implements ServletContextListener {

    private ServerSocket serverSocket;

    public static AtomicInteger sum = new AtomicInteger(0);

    private static final Integer LISTEN_SOCKET_PORT = 10054;

    private static final Logger logger = LoggerFactory.getLogger(SocketListener.class);

    private static Map<String, String> signMap = new HashMap<>();

    public static ConcurrentMap<String, Task> clientSignMap = new ConcurrentHashMap<>();

    static {
        signMap.put("AA", "com.cslg.socket.service.InverterService");
        signMap.put("AB", "com.cslg.socket.service.LoadService");
        signMap.put("AC", "com.cslg.socket.service.InverterService");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean dealSign(String sign, Socket socket) {
        logger.info("初始化, 标记为: {}, 工作的线程数为: {}", sign, sum.incrementAndGet());
        Thread thread = Thread.currentThread();
        Task task = new Task(socket, thread);
        if(!thread.getName().contains(sign)) {
            String[] str = thread.getName().split(" ");
            String name = str.length > 1 ? str[2] : str[0];
            thread.setName("该客户端标识号: " + sign + "--工作线程名称为: " + name);
        }

        //TODO
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private AbstractService dealSign(Socket socket) {
        byte[] bytes = new byte[1];
        try {
            socket.getInputStream().read(bytes);
            String sign = CodeUtil.encode(bytes);
            if(signMap.get(sign) == null) {
                return null;
            }
            Class<?> cls = Class.forName(signMap.get(sign));
            //getConstructor返回访问权限是public的构造器，getDeclaredConstructor返回所有权限的构造器
            Object object = cls.getConstructor(String.class).newInstance(sign);
            if(object instanceof AbstractService) {
                return (AbstractService) object;
            }
        } catch (SocketTimeoutException e) {
            logger.info("read()超时");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleSocket(AbstractService service, Socket socket) {
        try {
            service.setInputStream(socket.getInputStream());
            service.setOutputStream(socket.getOutputStream());
            if(dealSign(service.getSign(), socket)) {
                return;
            }
            while(true) {
                if(Pool.threadPool.isShutdown()) {
                    break;
                }
                if(service.writeData()) {
                    break;
                }
                //暂停2分钟在获取
                Thread.sleep(120000);
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

    private void start(Socket socket) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("成功连接到ip为: {}的客户端, time: {}", socket.getInetAddress(), df.format(new Date()));
        AbstractService service = dealSign(socket);
        if(service != null) {
            handleSocket(service, socket);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void socketReceive() {
        try {
            while(true) {
                Socket socket = serverSocket.accept();
                // 设置read()阻塞超时时间
                socket.setSoTimeout(180000);
                Pool.threadPool.execute(() -> start(socket));
            }
        } catch (IOException e) {
            System.out.println("手动停止线程");
            e.printStackTrace();
            logger.info("线程中断", e);
        }
    }

    private void start() {
        try {
            serverSocket = new ServerSocket(LISTEN_SOCKET_PORT);
            socketReceive();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("创建ServerSocket出错", e);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Pool.singleThreadPool.execute(this::start);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Pool.singleThreadPool.shutdown();
        Pool.threadPool.shutdown();
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
