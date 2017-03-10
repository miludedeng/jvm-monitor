package cc.cafetime;

import cc.cafetime.entity.SqlData;
import cc.cafetime.info.VmJdbcInfo;
import cc.cafetime.info.VmListInfo;
import cc.cafetime.util.NetUtil;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.Queue;

/**
 * Created by liujing on 2017/3/6.
 */
public class BroadcastReceive {
    private static int PORT = 34876;
    private static boolean lock = false;


    public static void exec() {
        if (lock) {
            throw new RuntimeException("do not run broad cast more than once");
        }
        lock = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (NetUtil.isLoclePortUsing(PORT)) {
                    PORT = PORT + 1;
                    if (PORT > 65536) {
                        throw new RuntimeException("no port can be used!");
                    }
                }
                DatagramPacket receive = new DatagramPacket(new byte[1024], 1024);
                DatagramSocket server = null;
                try {
                    server = new DatagramSocket(34876);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                System.out.println("BoardCast Listener start......");

                while (true) {
                    try {
                        server.receive(receive);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String recvData = new String(Arrays.copyOfRange(receive.getData(), 0, receive.getLength()));

                    if (StringUtils.isEmpty(recvData)) {
                        continue;
                    }
                    VmJdbcInfo.saveSqlData(recvData);
                }
            }
        }).start();
    }


    public static int getPORT() {
        return PORT;
    }
}
