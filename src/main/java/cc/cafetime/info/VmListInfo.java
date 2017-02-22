package cc.cafetime.info;

import cc.cafetime.type.MainClassApplicationType;
import cc.cafetime.type.MainClassApplicationTypeFactory;
import com.jvmtop.monitor.VMInfo;
import com.jvmtop.monitor.VMInfoState;
import com.jvmtop.openjdk.tools.ConnectionState;
import com.jvmtop.openjdk.tools.LocalVirtualMachine;

import java.io.IOException;
import java.util.*;


/**
 * Created by liujing on 2017/2/10.
 */
public class VmListInfo {
    private List<VMInfo> vmInfoList = new ArrayList<VMInfo>();

    private Map<Integer, LocalVirtualMachine> vmMap = new HashMap<Integer, LocalVirtualMachine>();

    public static VmListInfo vmList;
    public static Map<Integer, VMInfo> vmInfoMap = new HashMap<Integer, VMInfo>();

    private VmListInfo() {
    }

    public static VmListInfo getInstance() {
        if (vmList == null) {
            vmList = new VmListInfo();
        }
        return vmList;
    }

    public List<Object> getList() throws Exception {
        scanForNewVMs();
        try {
            updateVMs(vmInfoList);
        } catch (Exception e) {
            throw new RuntimeException("update failed");
        }
        Collections.sort(vmInfoList, VMInfo.VM_ID_COMPARATOR);
        List<Object> vmDataList = new ArrayList<Object>();
        for (VMInfo vmInfo : vmInfoList) {
            int vmId = vmInfo.getId();
            String entryPointClass = getEntryPointClass(vmInfo.getDisplayName());
            Map<String, Object> vmInfoMap = new HashMap<String, Object>();
            MainClassApplicationType applicationType = null;
            if (entryPointClass != null && entryPointClass.length() > 0) {
                applicationType = new MainClassApplicationTypeFactory().getApplicationType(entryPointClass);
            }
            vmInfoMap.put("Name", applicationType != null ? applicationType.getName() : entryPointClass);
            vmInfoMap.put("Icon", applicationType != null ? applicationType.getIcon() : "application.png");
            vmInfoMap.put("VmID", vmId);
            if (vmInfo.getState() == VMInfoState.ATTACHED) {
                String deadlockState = "";
                if (vmInfo.hasDeadlockThreads()) {
                    deadlockState = "!D";
                }
                vmInfoMap.put("DeadLockState", deadlockState);
            } else if (vmInfo.getState() == VMInfoState.ATTACHED_UPDATE_ERROR) {
                vmInfoMap.put("Error", "[ERROR: Could not fetch telemetries (Process DEAD?)] ");
            } else if (vmInfo.getState() == VMInfoState.ERROR_DURING_ATTACH) {
                vmInfoMap.put("Error", "[ERROR: Could not attach to VM]");
            } else if (vmInfo.getState() == VMInfoState.CONNECTION_REFUSED) {
                vmInfoMap.put("Error", "[ERROR: Connection refused/access denied]");
            }
            vmDataList.add(vmInfoMap);

        }
        return vmDataList;
    }

    private void updateVMs(List<VMInfo> vmList) throws Exception {
        List<VMInfo> delList = new ArrayList<VMInfo>();
        for (VMInfo vmInfo : vmList) {
            vmInfo.update();
            if (ConnectionState.DISCONNECTED.equals(vmInfo.getState())
                    || VMInfoState.CONNECTION_REFUSED.equals(vmInfo.getState())
                    || VMInfoState.ERROR_DURING_ATTACH.equals(vmInfo.getState())
                    || VMInfoState.DETACHED.equals(vmInfo.getState())) {
                delList.add(vmInfo);
            }
        }
        for (VMInfo vmInfo : delList) {
            vmList.remove(vmInfo);
        }
    }

    private void scanForNewVMs() {
        Map<Integer, LocalVirtualMachine> machines = LocalVirtualMachine.getNewVirtualMachines(vmMap);
        Set<Map.Entry<Integer, LocalVirtualMachine>> set = machines.entrySet();

        for (Map.Entry<Integer, LocalVirtualMachine> entry : set) {
            LocalVirtualMachine localvm = entry.getValue();
            int vmid = localvm.vmid();

            if (!vmMap.containsKey(vmid)) {
                VMInfo vmInfo = VMInfo.processNewVM(localvm, vmid);
                if (ConnectionState.DISCONNECTED.equals(vmInfo.getState())
                        && VMInfoState.CONNECTION_REFUSED.equals(vmInfo.getState())
                        && VMInfoState.ERROR_DURING_ATTACH.equals(vmInfo.getState())) {
                    continue;
                }
                vmInfoList.add(vmInfo);
                vmInfoMap.put(vmid, vmInfo);
            }
        }
        vmMap = machines;
    }

    private String getEntryPointClass(String name) {
        if (name.indexOf(' ') > 0) {
            name = name.substring(0, name.indexOf(' '));
        }
        return name;
    }

}
