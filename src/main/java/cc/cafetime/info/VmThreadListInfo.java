package cc.cafetime.info;

import cc.cafetime.util.CommonUtil;
import com.jvmtop.monitor.VMInfo;
import com.jvmtop.monitor.VMInfoState;

import java.lang.management.ThreadInfo;
import java.util.*;

/**
 * Created by steven on 2017/2/9.
 */
public class VmThreadListInfo {

    private boolean sortByTotalCPU_ = false;

    private boolean displayedThreadLimit = true;

    private static VmThreadListInfo listInfo = null;

    //TODO: refactor
    private Map<Long, Long> previousThreadCPUMillis = new HashMap<Long, Long>();

    private VmThreadListInfo(){}

    public static VmThreadListInfo getInstance(){
        if(listInfo==null){
            listInfo = new VmThreadListInfo();
        }
        return listInfo;
    }

    public List<Map<String, Object>> getInfo(int vmId, int numberOfDisplayedThreads) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

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
        }
        if (vmInfo.getState() == VMInfoState.ATTACHED_UPDATE_ERROR) {
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
        if (!vmInfo.getThreadMXBean().isThreadCpuTimeSupported()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("error", "Thread CPU telemetries are not available on the monitored jvm/platform");
            list.add(map);
            return list;
        }

        //TODO: move this into VMInfo?
        Map<Long, Long> newThreadCPUMillis = new HashMap<Long, Long>();

        Map<Long, Long> cpuTimeMap = new TreeMap<Long, Long>();

        for (Long tid : vmInfo.getThreadMXBean().getAllThreadIds()) {
            long threadCpuTime = vmInfo.getThreadMXBean().getThreadCpuTime(tid);
            long deltaThreadCpuTime = 0;
            if (previousThreadCPUMillis.containsKey(tid)) {
                deltaThreadCpuTime = threadCpuTime - previousThreadCPUMillis.get(tid);

                cpuTimeMap.put(tid, deltaThreadCpuTime);
            }
            newThreadCPUMillis.put(tid, threadCpuTime);
        }

        cpuTimeMap = CommonUtil.sortByValue(cpuTimeMap, true);

        int displayedThreads = 0;
        for (Long tid : cpuTimeMap.keySet()) {
            ThreadInfo info = vmInfo.getThreadMXBean().getThreadInfo(tid);
            displayedThreads++;
            if (displayedThreads > numberOfDisplayedThreads
                    && displayedThreadLimit) {
                break;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            if (info != null) {
                map.put("TID", tid);
                map.put("Name", info.getThreadName());
                map.put("State", info.getThreadState());
                map.put("CPU", getThreadCPUUtilization(cpuTimeMap.get(tid),
                        vmInfo.getDeltaUptime()));
                try {
                    map.put("TotalCPU", getThreadCPUUtilization(vmInfo.getThreadMXBean().getThreadCpuTime(tid), vmInfo.getProxyClient().getProcessCpuTime(), 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                map.put("BlockedBy", getBlockedThread(info));
            }
            list.add(map);
        }
        previousThreadCPUMillis = newThreadCPUMillis;
        return list;
    }

    private String getBlockedThread(ThreadInfo info) {
        if (info.getLockOwnerId() >= 0) {
            return "" + info.getLockOwnerId();
        } else {
            return "";
        }
    }

    private double getThreadCPUUtilization(long deltaThreadCpuTime, long totalTime) {
        return getThreadCPUUtilization(deltaThreadCpuTime, totalTime, 1000 * 1000);
    }

    private double getThreadCPUUtilization(long deltaThreadCpuTime, long totalTime, double factor) {
        if (totalTime == 0) {
            return 0;
        }
        return deltaThreadCpuTime / factor / totalTime * 100d;
    }
}
