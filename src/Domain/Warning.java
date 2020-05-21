package Domain;

import javax.swing.*;

/**
 * @program: WebChat
 * @description: 弹出框
 * @author: Mr.Zhang
 * @create: 2020-04-17 17:56
 **/

public class Warning {
    public static void CLIENTCONTSTART(){
        JOptionPane.showMessageDialog(null, "客户端启动失败！", "警告",JOptionPane.WARNING_MESSAGE);
    }
    public static void SERVERCONTSTART(){
        JOptionPane.showMessageDialog(null, "服务端启动失败！", "警告",JOptionPane.WARNING_MESSAGE);
    }
    public static void DBFAIL(){
        JOptionPane.showMessageDialog(null, "数据库连接失败！", "警告",JOptionPane.WARNING_MESSAGE);
    }
    public static void USERNAMENULL(){
        JOptionPane.showMessageDialog(null, "账号不能为空！", "警告",JOptionPane.WARNING_MESSAGE);
    }
    public static void PASSWORDNULL(){
        JOptionPane.showMessageDialog(null, "密码不能为空！", "警告",JOptionPane.WARNING_MESSAGE);
    }
    public static void LOGINSUCESS(){
        JOptionPane.showMessageDialog(null, "登陆成功！");
    }
    public static void LOGINFAIL(){
        JOptionPane.showMessageDialog(null, "登陆失败！密码错误或该用户已登陆!");
    }
    public static void REGSUCESS(){
        JOptionPane.showMessageDialog(null, "注册成功！");
    }
    public static void REGFAIL(){
        JOptionPane.showMessageDialog(null, "注册失败！账号重复！");
    }
    public static int GOTOREG(String username, String password){
        String msg = "账号：" + username + "\n" + "密码：" + password;
        return JOptionPane.showConfirmDialog(null, msg, "注册",JOptionPane.YES_NO_OPTION); //返回值为0或1
    }
    public static void SAVESUCCESS(){
        JOptionPane.showMessageDialog(null, "保存成功！");
    }
    public static void SAVEFAIL(){
        JOptionPane.showMessageDialog(null, "保存失败！");
    }
    public static void FORMATERORR(){
        JOptionPane.showMessageDialog(null, "格式不符！", "警告",JOptionPane.WARNING_MESSAGE);
    }
    public static void FINDFAIL(){
        JOptionPane.showMessageDialog(null, "未找到该用户！", "警告",JOptionPane.WARNING_MESSAGE);
    }
}
