package cc.cafetime;

import cc.cafetime.entity.SqlData;
import cc.cafetime.info.VmJdbcInfo;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by liujing on 2017/3/6.
 */
public class BroadcastReceive {

    private static final String SQL_DATA_SPERA = "__JVM_MONITOR__";

    public static void exec() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramPacket receive = new DatagramPacket(new byte[1024], 1024);
                DatagramSocket server = null;
                try {
                    server = new DatagramSocket(34876);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                System.out.println("---------------------------------");
                System.out.println("Board Cast start......");
                System.out.println("---------------------------------");

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
                    String[] datas = recvData.split(SQL_DATA_SPERA);
                    if (datas.length < 3) {
                        continue;
                    }
                    SqlData sqlData = new SqlData();
                    sqlData.setCost(Long.parseLong(datas[0]));
                    sqlData.setSql(datas[1]);
                    Date date = new Date();
                    date.setTime(Long.parseLong(datas[2]));
                    sqlData.setDate(date);
                    VmJdbcInfo.sqlDataQueue.offer(sqlData);
                }
            }
        }).start();
    }
}
