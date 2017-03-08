package cc.cafetime.info;

import cc.cafetime.LimitQueue;
import cc.cafetime.entity.SqlData;
import com.jvmtop.monitor.VMInfo;
import com.sun.tools.attach.VirtualMachine;

import java.util.*;

/**
 * Created by liujing on 2017/3/6.
 */
public class VmJdbcInfo {

    private static final String SQL_DATA_SPERA = "__JVM_MONITOR__";
    public final static String SQL_STAT_AGENT_LOADED = "cc.cafetime.sqlstat.loaded";
    public static Map<String, Queue<SqlData>> sqlDataQueueMap = new HashMap<String, Queue<SqlData>>();
    public static int QUEUE_LENGTH = 20000;

    /**
     * 加载jar报到指定的应用
     *
     * @param vmId
     * @throws Exception
     */
    public static void addAgent(int vmId) throws Exception {
        VirtualMachine vm = VirtualMachine.attach(String.valueOf(vmId));
        Properties prop = vm.getSystemProperties();
        if (prop.get(SQL_STAT_AGENT_LOADED) != null && Boolean.parseBoolean((String) prop.get(SQL_STAT_AGENT_LOADED))) {
            throw new RuntimeException("Don't loaded agent more than once!");
        }
        vm.loadAgent(VmJdbcInfo.class.getResource("/sqlstat.jar").getFile());
    }

    /**
     * 存储接收的sql信息
     */
    public static void saveSqlData(String recvData) {
        String[] datas = recvData.split(SQL_DATA_SPERA);
        if (datas.length < 4) {
            return;
        }
        String vmId = datas[0];
        if (!VmListInfo.vmInfoMap.keySet().contains(Integer.parseInt(vmId))) {
            return;
        }
        SqlData sqlData = new SqlData();
        sqlData.setCost(Long.parseLong(datas[1]));
        sqlData.setSql(datas[2]);
        Date date = new Date();
        date.setTime(Long.parseLong(datas[3]));
        sqlData.setDate(date);
        Queue<SqlData> q = VmJdbcInfo.sqlDataQueueMap.get(vmId);
        if (q == null) {
            q = new LimitQueue<SqlData>(VmJdbcInfo.QUEUE_LENGTH);
        }
        q.offer(sqlData);
        System.out.println("VmID: " + vmId + " Time: " + sqlData.getCost() + " SQL: " + sqlData.getSql());
        VmJdbcInfo.sqlDataQueueMap.put(vmId, q);
    }
}
