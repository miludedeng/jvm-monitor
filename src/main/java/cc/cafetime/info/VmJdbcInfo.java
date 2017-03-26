package cc.cafetime.info;

import cc.cafetime.LimitQueue;
import cc.cafetime.entity.SqlData;
import cc.cafetime.sqlstat.BroadCastSender;
import cc.cafetime.sqlstat.JdbcClassVisitor;
import cc.cafetime.sqlstat.Main;
import cc.cafetime.sqlstat.SQLReciver;
import com.jvmtop.monitor.VMInfo;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Created by liujing on 2017/3/6.
 */
public class VmJdbcInfo {

    private static final String SQL_DATA_SPERA = "__JVM_MONITOR__";
    public final static String SQL_STAT_AGENT_LOADED = "cc.cafetime.sqlstat.loaded";
    public static Map<String, Queue<SqlData>> sqlDataQueueMap = new HashMap<String, Queue<SqlData>>();
    public static Map<String, Object[]> sqlDataArrayMap = new HashMap<String, Object[]>();
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
        vm.loadAgent(createJar().getAbsolutePath());
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
        VmJdbcInfo.sqlDataQueueMap.put(vmId, q);
    }

    public static boolean isAddedAgent(int vmId) throws IOException, AttachNotSupportedException {
        VirtualMachine vm = VirtualMachine.attach(String.valueOf(vmId));
        Properties prop = vm.getSystemProperties();
        if (prop.get(SQL_STAT_AGENT_LOADED) != null && Boolean.parseBoolean((String) prop.get(SQL_STAT_AGENT_LOADED))) {
            return true;
        } else {
            return false;
        }
    }

    public static Map<String, Object> loadSqlList(int vmId, int pageSize, int pageIndex, int isRefresh, int min, int max) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<SqlData> list = new ArrayList<SqlData>();
        Queue queue = sqlDataQueueMap.get(vmId + "");
        if (queue.size() <= 0) {
            map.put("List", list);
            map.put("Total", 0);
            return map;
        }
        Object[] objArr = sqlDataArrayMap.get(vmId + "");
        if (objArr == null || 0 != isRefresh) { // 参数isRefresh值0表示不刷新，1表示刷新
            objArr = queue.toArray();
            sqlDataArrayMap.put(vmId + "", objArr);
        }
        Object[] tempArr = null;
        if (min > 0 || max > 0) {
            List<SqlData> tempList = new ArrayList<SqlData>();
            for (Object obj : objArr) {
                SqlData sqlData = (SqlData) obj;
                if (min != 0 && min > sqlData.getCost()) {
                    continue;
                }
                if (max != 0 && max < sqlData.getCost()) {
                    continue;
                }
                tempList.add(sqlData);
            }
            tempArr = tempList.toArray();
        }
        if (tempArr != null) {
            objArr = tempArr;
        }
        int j = 0;
        for (int i = objArr.length - 1; i > 0; i--) {
            if (pageSize * pageIndex > j++ && pageSize * (pageIndex - 1) < j) {
                SqlData sqlData = (SqlData) objArr[i];
                list.add(sqlData);
            }
        }
        map.put("List", list);
        map.put("Total", objArr.length);
        return map;
    }

    public static File createJar() throws IOException {
        InputStream mainInputStream = Main.class.getResourceAsStream('/' + Main.class.getName().replace('.', '/') + ".class");
        InputStream broadCastSenderInputStream = BroadCastSender.class.getResourceAsStream('/' + BroadCastSender.class.getName().replace('.', '/') + ".class");
        InputStream jdbcClassVisitorInputStream = JdbcClassVisitor.class.getResourceAsStream('/' + JdbcClassVisitor.class.getName().replace('.', '/') + ".class");
        InputStream sqlReciverInputStream = SQLReciver.class.getResourceAsStream('/' + SQLReciver.class.getName().replace('.', '/') + ".class");
        try {
            File agentJar = File.createTempFile("sqlAgent", ".jar");
            agentJar.deleteOnExit();
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(new Attributes.Name("Agent-Class"), Main.class.getName());
            manifest.getMainAttributes().put(new Attributes.Name("Can-Redefine-Classes"), Boolean.TRUE.toString());
            manifest.getMainAttributes().put(new Attributes.Name("Can-Retransform-Classes"), Boolean.TRUE.toString());
            manifest.getMainAttributes().put(new Attributes.Name("Can-Set-Native-Method-Prefix"), Boolean.TRUE.toString());
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(agentJar), manifest);

            try {
                jarOutputStream.putNextEntry(new JarEntry(Main.class.getName().replace('.', '/') + ".class"));
                byte[] buffer = new byte[1024];
                int index;
                while ((index = mainInputStream.read(buffer)) != -1) {
                    jarOutputStream.write(buffer, 0, index);
                }
                jarOutputStream.putNextEntry(new JarEntry(BroadCastSender.class.getName().replace('.', '/') + ".class"));
                index = 0;
                while ((index = broadCastSenderInputStream.read(buffer)) != -1) {
                    jarOutputStream.write(buffer, 0, index);
                }
                jarOutputStream.putNextEntry(new JarEntry(JdbcClassVisitor.class.getName().replace('.', '/') + ".class"));
                index = 0;
                while ((index = jdbcClassVisitorInputStream.read(buffer)) != -1) {
                    jarOutputStream.write(buffer, 0, index);
                }
                jarOutputStream.putNextEntry(new JarEntry(SQLReciver.class.getName().replace('.', '/') + ".class"));
                index = 0;
                while ((index = sqlReciverInputStream.read(buffer)) != -1) {
                    jarOutputStream.write(buffer, 0, index);
                }

                jarOutputStream.closeEntry();
                return agentJar;
            } finally {
                jarOutputStream.close();
            }
        } finally {
            mainInputStream.close();
            broadCastSenderInputStream.close();
            jdbcClassVisitorInputStream.close();
            sqlReciverInputStream.close();
        }
    }
}
