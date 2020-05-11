package DataBase;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * @program: WebChat
 * @description: JDBC工具类
 * @author: Mr.Zhang
 * @create: 2020-04-17 15:09
 **/


public class JDBCUtil {
    private static String user = null;
    private static String password = null;
    private static String url = null;
    static {
        Properties pro = new Properties();
        try {
            // 获取properties资源文件
            pro.load(new FileReader(JDBCUtil.class.getResource("jdbc.properties").getFile()));
            // 获取键值
            url = pro.getProperty("url");
            user = pro.getProperty("user");
            password = pro.getProperty("password");
            String driver = pro.getProperty("driver");
            // 加载驱动
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 连接数据库
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // 关闭资源
    public static void close(Statement stmt, Connection con, ResultSet resultSet, PreparedStatement pstmt){
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (con != null) con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (resultSet != null) resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (pstmt != null)
            pstmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

