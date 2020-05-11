package Client.UI;

import DataBase.Dao;
import domain.Message;
import domain.User;


import java.awt.*;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import static java.lang.Thread.sleep;

public class ChatWindow extends JFrame {
    private static DatagramSocket datagramSocket = null;
    private ArrayList<String> onlineList = null;
    private final Dao dao = new Dao(); // 数据库操作类
    private JList onlineJList = null; // 在线人数
    private Socket userSocket;
    private User user;
    private JPanel mainPanel; // 主面板
    private JTextPane printor;   // 聊天面板
    private JTextPane editor = null;    // 编辑信息面板
    private JSplitPane splitPane;    // 动态分割线
    private static final int SERVERPORT = 8880;

    public ChatWindow(Socket userSocket, User user) {
        this.userSocket = userSocket;
        this.user = user;
        try {
            datagramSocket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTitle("聊天窗口");
        setEnabled(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 650, 600);
        setLocationRelativeTo(null);
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BorderLayout(0, 0));
        setContentPane(mainPanel);

        //将界面分成左右两部分
        splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.6);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(8);
        splitPane.setBorder(new LineBorder(Color.BLACK, 0));
        mainPanel.add(splitPane, BorderLayout.CENTER);

        //左边部分
        JSplitPane chatSplitPanel = new JSplitPane();
        chatSplitPanel.setResizeWeight(0.7);
        chatSplitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        chatSplitPanel.setContinuousLayout(true);
        chatSplitPanel.setDividerSize(8);
        chatSplitPanel.setBorder(new LineBorder(Color.BLACK, 0));
        splitPane.setLeftComponent(chatSplitPanel);

        // 左上部分
        JPanel topPanel = new JPanel();
        chatSplitPanel.setLeftComponent(topPanel);
        topPanel.setLayout(new BorderLayout(0, 0));

        // 聊天显示区的下拉
        JScrollPane printorScrollPanel = new JScrollPane();
        printorScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        topPanel.add(printorScrollPanel, BorderLayout.CENTER);

        // 聊天信息显示区域
        printor = new JTextPane();
        printor.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        printor.setEditable(false);
        printorScrollPanel.setViewportView(printor);

        // 左下部分
        JPanel downPanel = new JPanel();
        chatSplitPanel.setRightComponent(downPanel);
        downPanel.setLayout(new BorderLayout(0, 0));

        // 按钮面板
        JPanel sendPanel = new JPanel();
        downPanel.add(sendPanel, BorderLayout.SOUTH);
        sendPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        // 发送信息按钮
        JButton btnSend = new JButton("发送");
        btnSend.setMnemonic(KeyEvent.VK_ENTER); // 设置快捷键 ALT+ENTER
        sendPanel.add(btnSend);
        // 发送按钮事件监听
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!editor.getText().equals("")) {
                    try {
                        sendMessage(editor.getText());
                        editor.setText("");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        // 发送文件按钮
        JButton sendFileButton = new JButton("发送文件");
        sendPanel.add(sendFileButton);
        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(); // 文件选择器
                int flag = fileChooser.showOpenDialog(mainPanel); // 是否发送
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                File selectedFile = fileChooser.getSelectedFile();
                if (flag == JFileChooser.APPROVE_OPTION) sendMessage(selectedFile); // 如果flag=JFileChooser.APPROVE_OPTION，就发送
            }
        });
        // 在线列表的下拉
        JScrollPane onlineListScrollPanel = new JScrollPane();
        splitPane.setRightComponent(onlineListScrollPanel);
        // 动态监听在线同学列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    onlineList = dao.getOnline();
                    onlineJList = new JList<Object>(onlineList.toArray());
                    onlineJList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
                    onlineJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    onlineListScrollPanel.setViewportView(onlineJList);
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // 当窗口大小改变时，保持在线学生面板宽度不变
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent arg0) {
                splitPane.setDividerLocation(splitPane.getWidth() - 167);
            }
        });

        // 信息编辑区
        editor = new JTextPane();
        // 信息编辑区的下拉
        JScrollPane editorScrollPane = new JScrollPane();
        editorScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        downPanel.add(editorScrollPane, BorderLayout.CENTER);
        editorScrollPane.setViewportView(editor);


        // 监听顶层窗口
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {

            }
            // 如果窗口关闭，则socket关闭
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                try {
                    userSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });
        setVisible(true);
    }

    /**
     * 发送函数
     * @param content
     */
    private void sendMessage(Object content) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oops = null;
        try {
            Message msg = null;
            // 继承看是String类还是File类
            if (content instanceof String) {
                msg = new Message((String)content, user.getName());
            }
            else if (content instanceof File){
                msg = new Message((File) content, user.getName());
            }
            // 发送
            oops = new ObjectOutputStream(baos);
            oops.writeObject(msg); // 序列化
            oops.flush();
            byte[] buff = baos.toByteArray();   //转化为字节数组
            DatagramPacket datagram = new DatagramPacket(buff, buff.length, InetAddress.getByName(user.getUserPort()), 8881); // 打包
            datagramSocket.send(datagram);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oops != null) {
                    oops.close();
                }
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 追加到显示区
     * @param text
     */
    public void appendMyText(String text) {
        printor.setText(printor.getText() + text + '\n');
    }


    public Socket getSocket(){
        return userSocket;
    }
    public String getUserport(){
        return user.getUserPort();
    }

}