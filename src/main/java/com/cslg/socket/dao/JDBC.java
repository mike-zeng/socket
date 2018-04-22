package com.cslg.socket.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {

    private static final String URL = "jdbc:mysql://rm-bp1p625j8640m9ug7uo.mysql.rds.aliyuncs.com:3306/gfjkpt?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8";

    private static final String DRIVER = "com.mysql.jdbc.Driver";

    private static final String USERNAME = "root";

    private static final String PASSWORD = "Caa123456";

    public static Connection getConnect() {
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void close(Connection connection) {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.getErrorCode();
        }
    }
}
