package Client.UI;
import Domain.Warning;
import DataBase.Dao;
import Client.Web.Client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.MatteBorder;

/**
 * @program: WebChat
 * @description: 登录窗口
 * @author: Mr.Zhang
 * @create: 2020-04-17 15:49
 **/

public class LoginWindow extends JFrame{
    Dao dao = new Dao();
    String ip = null;
    public void initUI() {
        Scanner s = new Scanner(System.in);
        ip = s.next();
        // 在initUI方法中，实例化JFrame类的对象。
        JFrame frame = new JFrame();
        frame.setTitle("登录"); // 设置窗体的标题
        frame.setSize(450, 300); // 设置窗体的大小，单位是像素
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 设置窗体的关闭操作；
        frame.setLocationRelativeTo(null); // 设置窗体相对于另一个组件的居中位置，参数null表示窗体相对于屏幕的中央位置
        frame.setResizable(false); // 设置禁止调整窗体大小

        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        frame.add(mainPanel);
        //设置标签大小
        Dimension dim1 = new Dimension(200,25);
        Dimension dim2 = new Dimension(100,35);
        Dimension dim3 = new Dimension(300, 30);
        // TextFild样式
        MatteBorder border = new MatteBorder(0, 0, 1, 0, Color.BLACK);
        // 姓名
        JPanel namePanel = new JPanel();
        namePanel.setSize(dim3);
        JLabel nameLable = new JLabel("姓名：");
        nameLable.setFont(new Font("黑体", Font.PLAIN, 13));
        namePanel.add(nameLable);
        JTextField nameText = new JTextField();
        nameText.setOpaque(false);
        nameText.setBorder(border);
        nameText.setPreferredSize(dim1);
        namePanel.add(nameText);
        mainPanel.add(namePanel);
        // 专业班级
        JPanel classPanel = new JPanel();
        classPanel.setSize(dim3);
        JLabel classLable = new JLabel("班级：");
        classLable.setFont(new Font("黑体", Font.PLAIN, 13));
        classPanel.add(classLable);
        JTextField classText = new JTextField();
        classText.setOpaque(false);
        classText.setBorder(border);
        classText.setPreferredSize(dim1);
        classPanel.add(classText);
        mainPanel.add(classPanel);
        // 密码
        JPanel passwordPanel = new JPanel();
        passwordPanel.setSize(dim3);
        JLabel passwordLable= new JLabel("密码：");
        passwordLable.setFont(new Font("黑体", Font.PLAIN, 13));
        passwordPanel.add(passwordLable);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setOpaque(false);
        passwordField.setBorder(border);
        passwordField.setPreferredSize(dim1);
        passwordPanel.add(passwordField);
        mainPanel.add(passwordPanel);

        //实例化JButton组件
        JPanel buttonPanel = new JPanel(new GridLayout(1,2,35,5));
        buttonPanel.setSize(dim3);
        MyButton loginButton = new MyButton("登录");
        MyButton registerButton = new MyButton("注册");
        //设置按钮的大小
        loginButton.setFont(new Font("华文行楷", Font.PLAIN, 16));
        registerButton.setFont(new Font("华文行楷", Font.PLAIN, 16));
        loginButton.setPreferredSize(dim2);
        registerButton.setPreferredSize(dim2);
        loginButton.setFocusPainted(false);
        registerButton.setFocusPainted(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel);
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