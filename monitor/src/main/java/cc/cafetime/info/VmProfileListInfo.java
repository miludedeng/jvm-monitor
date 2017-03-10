package cc.cafetime.info;

import com.jvmtop.monitor.VMInfo;
import com.jvmtop.monitor.VMInfoState;
import com.jvmtop.profiler.CPUSampler;
import com.jvmtop.profiler.MethodStats;

import java.util.*;

/**
 * Created by liujing on 2017/2/13.
 */
public class VmProfileListInfo {

    private static Map<Integer, CPUSampler> cpuSamplerMap = new HashMap<Integer, CPUSampler>();
    private static Map<Integer, Date> timeMap = new HashMap<Integer, Date>();

    public static List<Map<String, Object>> getInfo(int vmId, int count) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        clearOverlayCpuSampler();
        VMInfo vmInfo = VmListInfo.vmInfoMap.get(vmId);
        if (vmInfo == null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("error", "no such vm");
            list.add(map);
            return list;
        }
        try {
            vmInfo.update();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("error", "vm update failed");
            list.add(map);
            return list;
        } if (vmInfo.getState() == VMInfoState.ATTACHED_UPDATE_ERROR) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("error", "Could not fetch telemetries - Process terminated?");
            list.add(map);
            return list;
        }
        if (vmInfo.getState() != VMInfoState.ATTACHED) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("error", "Could not attach to process.");
            list.add(map);
            return list;
        }

        CPUSampler cpuSampler = getCPUSample(vmInfo);
        try {
            cpuSampler.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Iterator<MethodStats> iterator = cpuSampler.getTop(count).iterator(); iterator.hasNext(); ) {
            MethodStats stats = iterator.next();
            double wallRatio = (double) stats.getHits().get() / cpuSampler.getTotal() * 100;
            if (!Double.isNaN(wallRatio)) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("Method", stats.getClassName() + "." + stats.getMethodName());
                map.put("Precent", wallRatio);
                map.put("Time", wallRatio / 100d * cpuSampler.getUpdateCount() * 0.1d);
                list.add(map);
            }
        }
        return list;
    }


    private static void clearOverlayCpuSampler() {
        List<Integer> list = new ArrayList<Integer>();
        for (int vmId : timeMap.keySet()) {
            Date time = timeMap.get(vmId);
            if (System.currentTimeMillis() - time.getTime() > 30 * 60 * 1000) {
                list.add(vmId);
            }
        }
        for (int vmId : list) {
            timeMap.remove(vmId);
            cpuSamplerMap.remove(vmId);
        }
    }

    private static CPUSampler getCPUSample(VMInfo vmInfo) {
        if (cpuSamplerMap.get(vmInfo.getId()) == null) {
            try {
                CPUSampler cpuSampler = new CPUSampler(vmInfo);
                timeMap.put(vmInfo.getId(), new Date());
                cpuSamplerMap.put(vmInfo.getId(), cpuSampler);
                return cpuSampler;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return cpuSamplerMap.get(vmInfo.getId());
        }
    }
}
