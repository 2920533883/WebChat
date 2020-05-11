package Client.Web;


import Domain.Warning;

import java.net.Socket;


/**
 * 客户端
 */
public class Client{
    public static void start(String ip) {
        Socket socket = null;
        try {
            socket = new Socket(ip, 8880);
        } catch (Exception e) {
            Warning.CLIENTCONTSTART();
        }
    }

}