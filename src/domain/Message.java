package domain;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * @program: WebChat
 * @description: 消息类
 * @author: Mr.Zhang
 * @create: 2020-04-23 17:25
 **/

public class Message implements Serializable {
    private Object msg; // 消息内容
    private String time = new Date().toLocaleString();; // 消息时间
    private String sender; // 发送者

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Message() {
    }

    public Message(String msg, String sender) {
        this.msg = msg;
        this.sender = sender;
    }
    public Message(File file, String sender){
        this.msg = file;
        this.sender = sender;
    }
    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        if (msg instanceof String) return time + " " + sender + " \n" + msg;
        else return time + " " + sender + " \n" + ((File) msg).getName();
    }

    public String getLoginMessage(String name){
        return time + " " + name + " 已登陆!\n";
    }
    public String getLogoutMessage(String name){
        return time + " " + name + " 已退出!\n";
    }
}
