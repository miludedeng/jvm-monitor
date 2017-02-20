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

    public List<Map<String, Object>> getInfo(int vmId, int numberOfDisplayedThreads) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

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
            throw new RuntimeException("Could not fetch telemetries -Process terminated?");
        }
        if (vmInfo.getState() != VMInfoState.ATTACHED) {
            throw new RuntimeException("Could not attach to process.");
        }
        if (!vmInfo.getThreadMXBean().isThreadCpuTimeSupported()) {
            throw new RuntimeException("Thread CPU telemetries are not available on the monitored jvm/platform");
        }
        Map<Long, Long> newThreadCPUMillis = new HashMap<Long, Long>();
        Map<Long, Long> cpuTimeMap = new TreeMap<Long, Long>();
        Map<Long, ThreadInfo> threadInfoMap = new HashMap<Long, ThreadInfo>();
        for (ThreadInfo info : vmInfo.getThreadMXBean().dumpAllThreads(true, true)) {
            long tid = info.getThreadId();
            long threadCpuTime = vmInfo.getThreadMXBean().getThreadCpuTime(tid);
            long deltaThreadCpuTime = 0;
            if (previousThreadCPUMillis.containsKey(tid)) {
                deltaThreadCpuTime = threadCpuTime - previousThreadCPUMillis.get(tid);

                cpuTimeMap.put(tid, deltaThreadCpuTime);
            }
            newThreadCPUMillis.put(tid, threadCpuTime);
            threadInfoMap.put(tid,info);
        }

        cpuTimeMap = CommonUtil.sortByValue(cpuTimeMap, true);

        int displayedThreads = 0;
        for (Long tid : cpuTimeMap.keySet()) {
            ThreadInfo info = threadInfoMap.get(tid);
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
                map.put("Detail",info.getStackTrace());
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
