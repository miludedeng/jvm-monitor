package cc.cafetime.sqlstat;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.Date;

/**
 * Created by liujing on 2017/2/26.
 */
public class SQLReciver {

    private static final String SQL_DATA_SPERA = "__JVM_MONITOR__";

    /**
     * 接收从注入的代码中传出的参数，并传给广播方法发送给监控端
     * @param start
     * @param args
     */
    public static void recive(long start, Object[] args) {
        String sql = null;
        if (args[0] instanceof PreparedStatement) {
            PreparedStatement state = (PreparedStatement) args[0];
            sql = state.toString();
            if (sql.contains(":")) {
                sql = sql.substring(sql.indexOf(":") + 1).trim();
            }
        } else {
            sql = (String) args[1];
        }
        if (sql.startsWith("set") || sql.startsWith("SET") || sql.equals("commit") || sql.startsWith("/* mysql-connector-java-")) {
            return;
        }

        String msg = Main.pid + SQL_DATA_SPERA + (System.currentTimeMillis() - start) + SQL_DATA_SPERA + sql + SQL_DATA_SPERA + new Date().getTime();
        try {
            new BroadCastSender().sender(msg);
        } catch (IOException e) {
            System.out.println("send broadcast failed!");
        }
    }
}
