package cc.cafetime.info;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steven on 2017/2/9.
 */
public class OsInfo {
    private OperatingSystemMXBean osMxBean;
    private static OsInfo osInfo;

    private int processerNum;
    private String arch;
    private String osVersion;

    private OsInfo() {
        this.osMxBean = ManagementFactory.getOperatingSystemMXBean();
        this.processerNum = this.osMxBean.getAvailableProcessors();
        this.arch = this.osMxBean.getArch();
        this.osVersion = this.osMxBean.getName() + " " + this.osMxBean.getVersion();
    }

    public static OsInfo getInstance() {
        if (osInfo == null) {
            osInfo = new OsInfo();
        }
        return osInfo;
    }

    public Map<String, Object> getInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ProcesserNum", processerNum);
        map.put("Arch", arch);
        map.put("OsVersion", osVersion);
        map.put("LoadAvg", osMxBean.getSystemLoadAverage());
        return map;
    }
}
