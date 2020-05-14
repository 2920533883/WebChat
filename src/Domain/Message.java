package Domain;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: WebChat
 * @description: 消息类
 * @author: Mr.Zhang
 * @create: 2020-04-23 17:25
 **/

public class Message implements Serializable {
    private Object msg; // 消息内容
    private String time = new Date().toLocaleString(); // 消息时间
    private User sender; // 发送者IP

    public Object getMsg() {
        return msg;
    }

    public Message() {
    }

    public Message(String msg, User user) {
        this.msg = msg;
        sender = user;
    }
    public Message(File file, User user){
        this.msg = file;
        sender = user;
    }

    public String sendGroupMsg() {
        if (msg instanceof String) return time + " " + sender.getName() + " \n" + msg;
        else return time + " " + sender.getName() + " \n" + ((File) msg).getName() + " 已发送";
    }
    public String sendPrivateMsg(){
        return time + sender.getName() + "对你说: \n" + msg;
    }
    public String myMsg(String name){
        return time+" 你对"+ name + "说: \n" + msg;
    }
    public User getSender() {
        return sender;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public String getLoginMessage(String name){
        return time + " " + name + " 已登陆!\n";
    }
    public String getLogoutMessage(String name){
        return time + " " + name + " 已退出!\n";
    }
}
