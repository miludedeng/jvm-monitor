package cc.cafetime.info;

import cc.cafetime.util.CommonUtil;
import com.jvmtop.monitor.JstatInfo;
import com.jvmtop.monitor.VMInfo;
import com.jvmtop.monitor.VMInfoState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steven on 2017/2/9.
 */
public class VmMonitorInfo {

    public static Map<String, Object> getInfo(int vmId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        VMInfo vmInfo = VmListInfo.vmInfoMap.get(vmId);
        if (vmInfo == null) {
            throw new RuntimeException("no such vm");
        }
        try {
            vmInfo.update();
        } catch (Exception e) {
            throw new RuntimeException("vm update failed");
        }
        if (vmInfo.getState() == VMInfoState.ATTACHED_UPDATE_ERROR) {
            throw new RuntimeException("Could not fetch telemetries - Process terminated?");
        }
        if (vmInfo.getState() != VMInfoState.ATTACHED) {
            throw new RuntimeException("Could not attach to process.");
        }
        map.put("CPUPrecent", vmInfo.getCpuLoad() * 100);
        map.put("HeapPrecent", (vmInfo.getHeapUsed() * 1d / vmInfo.getHeapMax()) * 100);
        map.put("HeapUsed", CommonUtil.toMB(vmInfo.getHeapUsed()));
        map.put("HeapMax", CommonUtil.toMB(vmInfo.getHeapMax()));
        map.put("NonHeapUsed", CommonUtil.toMB(vmInfo.getNonHeapUsed()));
        map.put("NonHeapMax", CommonUtil.toMB(vmInfo.getNonHeapMax()));
        map.put("UpTime", CommonUtil.toHHMM(vmInfo.getRuntimeMXBean().getUptime()));
        map.put("ThreadCount", vmInfo.getThreadCount());
        map.put("ThreadPeak", vmInfo.getThreadMXBean().getPeakThreadCount());
        map.put("ThreadCreated", vmInfo.getThreadMXBean().getTotalStartedThreadCount());
        map.put("TotalLoadedClasses", vmInfo.getTotalLoadedClassCount());
        map.put("GCTime", CommonUtil.toHHMM(vmInfo.getGcTime()));
        map.put("GCRuns", vmInfo.getGcCount());
        return map;
    }

    public static Map<String, Object> getThreadCount(int vmId) {
        Map<String, Object> map = new HashMap<String, Object>();
        VMInfo vmInfo = VmListInfo.vmInfoMap.get(vmId);
        if (vmInfo == null) {
            map.put("error", "no such vm");
            return map;
        }
        try {
            vmInfo.update();
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "vm update failed");
            return map;
        }
        if (vmInfo.getState() == VMInfoState.ATTACHED_UPDATE_ERROR) {
            map.put("error", "Could not fetch telemetries - Process terminated?");
            return map;
        }
        if (vmInfo.getState() != VMInfoState.ATTACHED) {
            map.put("error", "Could not attach to process.");
            return map;
        }
        map.put("ThreadCount", vmInfo.getThreadCount());
        return map;
    }

    public static void addHeapInfo(JstatInfo jstatInfo, Map<String, Object> map) {
        jstatInfo.update();
        map.put("OldSpaceUsed", CommonUtil.toMB(Long.parseLong(jstatInfo.getOldUsed())));
        map.put("OldSpaceCap", CommonUtil.toMB(Long.parseLong(jstatInfo.getOldCapacity())));
        map.put("OldSpaceMax", CommonUtil.toMB(Long.parseLong(jstatInfo.getOldMaxCapacity())));
        map.put("OldSpacePrecent", Long.parseLong(jstatInfo.getOldUsed()) * 1d / Long.parseLong(jstatInfo.getOldCapacity()));
        map.put("EdenSpaceUsed", CommonUtil.toMB(Long.parseLong(jstatInfo.getEdenUsed())));
        map.put("EdenSpaceCap", CommonUtil.toMB(Long.parseLong(jstatInfo.getEdenCapacity())));
        map.put("EdenSpaceMax", CommonUtil.toMB(Long.parseLong(jstatInfo.getEdenMaxCapacity())));
        map.put("EdenSpacePrecent", Long.parseLong(jstatInfo.getEdenUsed()) * 1d / Long.parseLong(jstatInfo.getEdenCapacity()));
        map.put("Sur1SpaceUsed", CommonUtil.toMB(Long.parseLong(jstatInfo.getSurvival1Used())));
        map.put("Sur1SpaceCap", CommonUtil.toMB(Long.parseLong(jstatInfo.getSurvival1Capacity())));
        map.put("Sur1SpaceMax", CommonUtil.toMB(Long.parseLong(jstatInfo.getSurvival1MaxCapacity())));
        map.put("Sur1SpacePrecent", Long.parseLong(jstatInfo.getSurvival1Used()) * 1d / Long.parseLong(jstatInfo.getSurvival1Capacity()));
        map.put("Sur2SpaceUsed", CommonUtil.toMB(Long.parseLong(jstatInfo.getSurvival2Used())));
        map.put("Sur2SpaceCap", CommonUtil.toMB(Long.parseLong(jstatInfo.getSurvival2Capacity())));
        map.put("Sur2SpaceMax", CommonUtil.toMB(Long.parseLong(jstatInfo.getSurvival2MaxCapacity())));
        map.put("Sur2SpacePrecent", Long.parseLong(jstatInfo.getSurvival2Used()) * 1d / Long.parseLong(jstatInfo.getSurvival2Capacity()));
    }
}
