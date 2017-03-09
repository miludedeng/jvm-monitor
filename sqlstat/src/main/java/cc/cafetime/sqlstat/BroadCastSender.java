package cc.cafetime.sqlstat;

import java.io.IOException;
import java.net.*;

/**
 * Created by liujing on 2017/3/6.
 */
public class BroadCastSender {

    public void sender(String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        /*
         * 在Java UDP中单播与广播的代码是相同的,要实现具有广播功能的程序只需要使用广播地址即可, 例如：这里使用了本地的广播地址
         */
        InetAddress inetAddr = InetAddress.getByName("255.255.255.255");
        DatagramSocket client = new DatagramSocket();

        DatagramPacket sendPack = new DatagramPacket(bytes, bytes.length, inetAddr,
                34876);

        client.send(sendPack);
        client.close();
    }
}
