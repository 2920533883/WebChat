package Server.Web;


import Client.UI.ChatWindow;
import DataBase.Dao;
import Domain.Message;
import Domain.User;
import Domain.Warning;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * 服务器（教师端）
 */
public class Server extends JFrame{
    ArrayList<ChatWindow> onlineList = new ArrayList<>(); // 存储在线同学的聊天窗口
    public final int SOCKETPORT = 8880; // TCP端口
    public final int DATAGRAMPORT = 8881; // UDP端口
    private DatagramSocket datagramSocket = null; // 数据报
    private final Dao dao = new Dao(); // 数据库操作类
    private final Message msg = new Message(); // 记录登录，退出消息
    private int onlineNum = 0; // 在线人数
    private ServerSocket serverSocket = null;
    private JTextArea textArea = null; // 显示登陆，推出消息
    private final JLabel onlineShowLabel; // 显示在线人数
    public Server(){

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BorderLayout(0, 0));
        setContentPane(mainPanel);
        // 顶部
        JPanel panel = new JPanel();
        mainPanel.add(panel, BorderLayout.NORTH);
        panel.setLayout(new BorderLayout(0, 0));

        // 服务器名称
        JLabel nameLabel = new JLabel();
        nameLabel.setFont(new Font("华文行楷", Font.BOLD, 20));
        nameLabel.setText("服务器");
        panel.add(nameLabel, BorderLayout.WEST);

        // 服务器状态
        JLabel statusLabel = new JLabel();
        statusLabel.setIcon(new ImageIcon(Server.class.getResource("../Image/started.gif")));
        panel.add(statusLabel, BorderLayout.EAST);

        // 右部
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(3,1,30,30));
        JButton downLoadButton1 = new JButton("保存签到信息");
        downLoadButton1.setFocusPainted(false);
        JButton downLoadButton2 = new JButton("保存聊天信息");
        downLoadButton2.setFocusPainted(false);
        downLoadButton1.addActionListener(e -> {
            FileOutputStream fops = null;
            try {
                fops = new FileOutputStream("C:/Users/22972/Desktop/签到信息.txt");
                fops.write(textArea.getText().getBytes());
                Warning.SAVESUCCESS();
            } catch (IOException ioException) {
                Warning.SAVEFAIL();
                ioException.printStackTrace();
            }
        });
        downLoadButton2.addActionListener(e -> {
            FileOutputStream fops = null;
            try {
                fops = new FileOutputStream("C:/Users/22972/Desktop/聊天信息.txt");
                String chatMsg = dao.getChatMsg();
                fops.write(chatMsg.getBytes());
                Warning.SAVESUCCESS();
            } catch (IOException ioException) {
                Warning.SAVEFAIL();
                ioException.printStackTrace();
            }
        });
        rightPanel.add(downLoadButton1);
        rightPanel.add(downLoadButton2);
        onlineShowLabel = new JLabel();
        onlineShowLabel.setFont(new Font("华文行楷", Font.PLAIN, 17));
        onlineShowLabel.setText("在线人数：" + onlineNum);
        rightPanel.add(onlineShowLabel);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // 登录，退出信息显示
        textArea = new JTextArea();
        textArea.setFont(new Font("宋体", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane();
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(textArea);
        setVisible(true);
    }

    public void start(){
        try {
            serverSocket = new ServerSocket(SOCKETPORT);
            datagramSocket = new DatagramSocket(DATAGRAMPORT);
            // 监听程序退出，用于清空在线学生表和关闭资源
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                dao.clearOnlineTable();
                dao.clearChatMsgTable();
                dao.close();
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            // 线程
            new Thread(new LoginThread()).start();
            new Thread(new LogoutThread()).start();
            new Thread(new TransmitThread()).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Warning.SERVERCONTSTART();
            System.exit(0);
        }
    }

    // 转发
    class TransmitThread implements Runnable{
        final int MAX_LEN = 2000;
        byte[] buff = new byte[MAX_LEN];
        DatagramPacket packet = new DatagramPacket(buff, MAX_LEN);
        @Override
        synchronized public void run() {
            while (true){
                try {
                    datagramSocket.receive(packet);
                    ByteArrayInputStream bais = new ByteArrayInputStream(buff);
                    ObjectInputStream oips = new ObjectInputStream(bais);
                    Object msg = oips.readObject(); // 反序列化
                    Message message = (Message) msg;
                    Object msgContent = ((Message) msg).getMsg();
                    User sender = message.getSender();
                    if (msgContent instanceof String) {
                        String content = (String) msgContent;
                        if (content.charAt(0) == '@') {
                            if (content.contains(":")){
                                // 获取接收者姓名
                                String accpter = (content.substring(1, content.indexOf(":")));
                                // 裁剪信息
                                message.setMsg((content.substring(content.indexOf(":")+1)));
                                // 获取接收者IP
                                String accpterIP = dao.queryIP(accpter);
                                if (accpterIP == null) Warning.FINDFAIL();
                                else {
                                    InetAddress IP = InetAddress.getByName(accpterIP);
                                    for (ChatWindow chatWindow : onlineList) {
                                        if (chatWindow.getSocket().getLocalAddress().equals(IP)) {
                                            chatWindow.appendMyText(message.sendPrivateMsg());
                                        }
                                        if (chatWindow.getSocket().getLocalAddress().equals(InetAddress.getByName(sender.getUserPort()))) {
                                            chatWindow.appendMyText(message.myMsg(accpter));
                                        }
                                    }
                                }
                            }
                            else Warning.FORMATERORR();
                        }
                        else {
                            dao.insertChatMsg(message.sendGroupMsg());
                            for (ChatWindow chatWindow : onlineList) {
                                chatWindow.appendMyText(message.sendGroupMsg());
                            }
                        }
                    }
                    if (msgContent instanceof File){
                        File file = (File) message.getMsg();
                        FileInputStream fips = new FileInputStream(file);
                        FileSystemView fsv = FileSystemView.getFileSystemView(); // 获取桌面路径
                        String filePath = fsv.getHomeDirectory().getPath()+ "\\" + file.getName();
                        FileOutputStream fops = new FileOutputStream(filePath);
                        byte[] buffer = new byte[50];
                        int len = 0;
                        while ((len = fips.read(buffer)) != -1) {
                            fops.write(buffer, 0, len);
                        }
                        for (ChatWindow chatWindow : onlineList) {
                                chatWindow.appendMyText(message.sendGroupMsg());
                        }
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 登录
    class LoginThread implements Runnable{
        @Override
        public synchronized void run() {
            while (true){
                try {
                    Socket socket = serverSocket.accept();
                    String userIp = socket.getLocalAddress().toString().substring(1);
                    User user = dao.queryOnline(userIp);
                    ChatWindow chatWindow = new ChatWindow(socket, user);
                    onlineList.add(chatWindow);
                    textArea.append(msg.getLoginMessage(user.toString()));
                    onlineNum++;
                    onlineShowLabel.setText("在线人数：" + onlineNum);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 退出
    class LogoutThread implements Runnable{
        @Override
        public synchronized void run() {
            while (true){
                Iterator<ChatWindow> chatWindowIterator = onlineList.iterator();
                while (chatWindowIterator.hasNext()){
                    ChatWindow chatWindow = chatWindowIterator.next();
                    if (chatWindow.getSocket().isClosed()) {
                        User user = dao.queryOnline(chatWindow.getUserport());
                        textArea.append(msg.getLogoutMessage(user.toString()));
                        dao.deletLogout(chatWindow.getUserport());
                        chatWindowIterator.remove();
                        onlineNum--;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onlineShowLabel.setText("在线人数：" + onlineNum);
            }
        }
    }
    public static void main(String[] args) {
        new Server().start();
    }
}