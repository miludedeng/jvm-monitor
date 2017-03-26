package cc.cafetime.sqlstat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by liujing on 2017/3/6.
 */
public class BroadCastSender {

    /**
     * 将接收到的信息通过广播报文的方式发送给监控端
     * @param msg
     * @throws IOException
     */
    public void sender(String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        InetAddress inetAddr = InetAddress.getByName("255.255.255.255");
        DatagramSocket client = new DatagramSocket();

        DatagramPacket sendPack = new DatagramPacket(bytes, bytes.length, inetAddr,
                34876);

        client.send(sendPack);
        client.close();
    }
}
