package Client.UI;
import Client.Web.Warning;
import DataBase.Dao;
import DataBase.JDBCUtil;
import Client.Web.Client;
import domain.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Scanner;

import javax.swing.*;

/**
 * @program: WebChat
 * @description: 登录窗口
 * @author: Mr.Zhang
 * @create: 2020-04-17 15:49
 **/

public class LoginWindow{
    Dao dao = new Dao();
    String ip = null;
    public void initUI() {
        Scanner s = new Scanner(System.in);
        ip = s.next();
        // 在initUI方法中，实例化JFrame类的对象。
        JFrame frame = new JFrame();
        frame.setTitle("Login"); // 设置窗体的标题
        frame.setSize(400, 300); // 设置窗体的大小，单位是像素
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 设置窗体的关闭操作；
        frame.setLocationRelativeTo(null); // 设置窗体相对于另一个组件的居中位置，参数null表示窗体相对于屏幕的中央位置
        frame.setResizable(false); // 设置禁止调整窗体大小

        // 实例化流式布局类的对象
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        //设置标签大小
        Dimension dim1 = new Dimension(300,30);
        Dimension dim2 = new Dimension(90,30);
        // 姓名
        JLabel nameLable = new JLabel("姓名：");
        frame.add(nameLable);
        JTextField nameText = new JTextField();
        nameText.setPreferredSize(dim1);
        frame.add(nameText);
        // 专业班级
        JLabel classLable = new JLabel("班级：");
        frame.add(classLable);
        JTextField classText = new JTextField();
        classText.setPreferredSize(dim1);
        frame.add(classText);

        // 密码
        JLabel passwordLable= new JLabel("密码：");
        frame.add(passwordLable);
        JPasswordField passwordField=new JPasswordField();
        passwordField.setPreferredSize(dim1);
        frame.add(passwordField);

        //实例化JButton组件
        JButton loginButton = new JButton();
        JButton registerButton = new JButton();
        //设置按钮的显示内容
        loginButton.setText("登录");
        registerButton.setText("注册");
        //设置按钮的大小
        loginButton.setPreferredSize(dim2);
        registerButton.setPreferredSize(dim2);
        frame.add(loginButton);
        frame.add(registerButton);
        frame.setVisible(true);// 设置窗体为可见
        // 登录监视
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String password = passwordField.getText();
                String name = nameText.getText();
                String _class = classText.getText();
                if (Check.check(name, password)){
                    if (dao.login(name, password, _class, ip))
                    {
                        Warning.LOGINSUCESS();
                        frame.dispose();
                        Client.start(ip);
                    }
                    else Warning.LOGINFAIL();
                }
            }
        });
        // 注册监视
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String name = nameText.getText();
                    String _class = classText.getText();
                    String password = passwordField.getText();
                    if (Check.check(name, password)){
                        int goToReg = Warning.GOTOREG(name, password);
                        if (goToReg == 0){
                            if (dao.register(name, password, _class)){
                                Warning.REGSUCESS();
                            }
                            else Warning.REGFAIL();
                        }
                    }
                } catch (SQLException e) {
                    Warning.REGFAIL();
                }
            }
        });
    }

    public static void main(String[] args) {
        new LoginWindow().initUI();
    }
}


// 检查合格性
class Check{
    public static boolean check(String user, String password){
        if (user.equalsIgnoreCase("") || user.contains(" ")){
            Warning.USERNAMENULL();
            return false;
        }
        else if (password.equalsIgnoreCase("") || password.contains(" ")){
            Warning.PASSWORDNULL();
            return false;
        }
        return true;
    }
}