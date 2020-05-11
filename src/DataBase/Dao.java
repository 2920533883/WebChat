package DataBase;

import Domain.Warning;
import Domain.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * @program: WebChat
 * @description: 数据库操作
 * @author: Mr.Zhang
 * @create: 2020-04-26 17:55
 **/
public class Dao {
    PreparedStatement pstmt = null;
    Statement stmt = null;
    Connection con = null;
    public Dao() {
        try {
            con = JDBCUtil.getConnection();
        } catch (SQLException throwables) {
            Warning.DBFAIL();
        }
    }

    // 登陆操作
    public boolean login(String name, String password, String _class, String ip){
        try {
            pstmt = con.prepareStatement("select * from login where name = ? and password = ? and class = ?");
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            pstmt.setString(3, _class);
            if (pstmt.executeQuery().next()) {
                // 插入在线名单
                pstmt = con.prepareStatement("insert into online value (? ,?, ?)");
                pstmt.setString(1, name);
                pstmt.setString(2, _class);
                pstmt.setString(3, ip);
                pstmt.execute();
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // 注册操作
    public boolean register(String name, String password, String _class) throws SQLException {
        pstmt = con.prepareStatement("insert into login value (? ,?, ?)");
        pstmt.setString(1, name);
        pstmt.setString(2, _class);
        pstmt.setString(3, password);
        int flag = pstmt.executeUpdate();
        return flag > 0;
    }

    // 获取在线学生信息
    public ArrayList<String> getOnline(){
        ArrayList<String> onlineList = new ArrayList<>();
        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("select * from online");
            while (resultSet.next()){
                User user = new User(resultSet.getString("name"), resultSet.getString("class"), resultSet.getString("ip"));
                onlineList.add(user.get_class()+" "+user.getName());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return onlineList;
    }

    // 查询在线学生信息
    public User queryOnline(String userIp){
        User user = null;
        try {
            pstmt = con.prepareStatement("select * from online where ip = ?");
            pstmt.setString(1, userIp);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();
            user = new User(resultSet.getString("name"), resultSet.getString("class"), userIp);
            return user;
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
            return null;
        }
    }

    // 删除退出学生
    public void deletLogout(String userIp){
        try {
            pstmt = con.prepareStatement("delete from online where ip = ?");
            pstmt.setString(1, userIp);
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    // 清空在线学生表
    public void clearOnlineTable(){
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("truncate table online");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // 保存聊天信息
    public void insertChatMsg(String msg){
        String[] str = msg.split(" ");
        str[1] = str[0]+str[1];
        try {
            pstmt = con.prepareStatement("insert into chatmsg value (?, ?, ?)");
            for (int i = 1; i <= 3; ++i){
                pstmt.setString(i, str[i]);
            }
            pstmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // 获取聊天信息
    public String getChatMsg(){
        StringBuilder chatMsg = new StringBuilder();
        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("select * from chatmsg");
            while (resultSet.next()){
                String name = resultSet.getString("name");
                String time = resultSet.getString("time");
                String msg = resultSet.getString("msg");
                chatMsg.append(time).append(" ").append(name).append("说：").append(msg).append("\n");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return String.valueOf(chatMsg);
    }

    // 清空聊天信息表
    public void clearChatMsgTable(){
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("truncate table chatmsg");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 关闭数据库连接文件
    public void close(){
        JDBCUtil.close(stmt, con, null, pstmt);
    }
}
