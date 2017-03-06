package cc.cafetime.info;

import cc.cafetime.LimitQueue;
import cc.cafetime.entity.SqlData;
import com.sun.tools.attach.VirtualMachine;

import java.util.*;

/**
 * Created by liujing on 2017/3/6.
 */
public class VmJdbcInfo {

    public final static String SQL_STAT_AGENT_LOADED = "cc.cafetime.sqlstat.loaded";
    private static Map<Integer, String> tokenMap = new HashMap<Integer, String>();
    public static Queue<SqlData> sqlDataQueue = new LimitQueue<SqlData>(20000);

    /**
     * 加载jar报到指定的应用
     * @param vmId
     * @param listenAddr
     * @throws Exception
     */
    public static void addAgent(int vmId, String listenAddr) throws Exception {
        VirtualMachine vm = VirtualMachine.attach(String.valueOf(vmId));
        Properties prop = vm.getSystemProperties();
        boolean isLoaded = (Boolean) prop.get(SQL_STAT_AGENT_LOADED);
        if (isLoaded) {
            throw new RuntimeException("Don't loaded agent more than once!");
        }
        String token = UUID.randomUUID().toString();
        vm.loadAgent("sqlstat.jar", listenAddr + "," + token);
    }
}
