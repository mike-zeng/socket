package com.cslg.socket;

import com.cslg.socket.common.ConnectionHolder;
import com.cslg.socket.dao.JDBC;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SocketTest {

    private ServerSocket serverSocket;

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    boolean flag = true;


/*
    @Test
    public void test3() {
        try {
            serverSocket = new ServerSocket(10056);
        } catch (IOException e) {
            e.printStackTrace();
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println("等待连接");
                        System.out.println("连接上了");
                        flag = false;
                        break;
                    }
                    System.out.println("asdf");
                } catch (IOException e) {
                    System.out.println("线程挂了");
                    e.printStackTrace();
                }
            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        try {
//            serverSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //threadPool.shutdownNow();
        //System.out.println(threadPool.isShutdown());
        try {
            serverSocket = new ServerSocket(10057);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("serverSocket关闭了");
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/

    @Test
    public void test2() {
        long startTime = System.currentTimeMillis();
        PreparedStatement preparedStatement = null;
        Connection connection = JDBC.getConnect();
        int k = 1;
        try {
            for(int i = 1; i <= 11187; i++) {
                System.out.println(i);
                //String searchSql = "SELECT * FROM tb_inverter WHERE id = " + i;
                //String updateSql = "UPDATE tb_inverter SET daily_output = ?, total_output = ? WHERE id = " + i;
                String updateSql = "UPDATE tb_inverter SET local = ? WHERE id = " + i;
                //preparedStatement = connection.prepareStatement(searchSql);
                //ResultSet resultSet = preparedStatement.executeQuery();
                //if(resultSet.next()) {
                    //double daily = resultSet.getDouble(5);
                    //double total = resultSet.getDouble(6);
                    preparedStatement = connection.prepareStatement(updateSql);
                    //System.out.println(daily + "-" +  total);
                    /*if(daily > 100000) {
                        preparedStatement.setDouble(1, daily / 10);
                        preparedStatement.setDouble(2, total);
                        preparedStatement.executeUpdate();
                    }*/
                    String local = "长沙理工大学云塘校区";
                    if(k == 1) {
                        local = "长沙理工大学云塘校区";
                        k = 2;
                    } else if(k == 2) {
                        local = "株洲市";
                        k = 3;
                    } else if(k == 3) {
                        local = "车溪村";
                        k = 1;
                    }
                    preparedStatement.setString(1, local);
                preparedStatement.executeUpdate();
                //}
            }
            long endTime = System.currentTimeMillis();
            System.out.println(startTime - endTime);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test() {
        // socketClient();
        List<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    socketClient();
                }
            });
            threadList.add(thread);
        }
        for(Thread thread : threadList) {
            thread.start();
        }
        for(Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void socketClient() {
        Socket socket = null;
        try {
            System.out.println(Thread.currentThread().getName());
            //socket = new Socket(URL, PORT);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            int i = 0;
            while (true) {
                //Thread.sleep(1000);
                bufferedWriter.write(Thread.currentThread().getName() + ": " + i++ + "\n");
                bufferedWriter.flush();
                Thread.sleep(300);
            }
            //bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
