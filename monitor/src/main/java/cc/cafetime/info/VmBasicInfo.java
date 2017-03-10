package cc.cafetime.info;

import com.jvmtop.monitor.VMInfo;
import com.jvmtop.monitor.VMInfoState;

import java.util.*;

/**
 * Created by steven on 2017/2/9.
 */
public class VmBasicInfo {

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

        Map<String, String> properties = vmInfo.getSystemProperties();
        map.put("PID",vmInfo.getId());
        map.put("User",vmInfo.getOSUser());
        map.put("SystemProperties", properties);
        String command = properties.get("sun.java.command");
        if (command != null) {
            String[] commandArray = command.split(" ");
            List<String> commandList = Arrays.asList(commandArray);
            commandList = commandList.subList(1, commandList.size());
            map.put("Args",commandList);
        } else {
            map.put("Args","[UNKNOWN]");
        }
        map.put("VmArgs",vmInfo.getRuntimeMXBean().getInputArguments());
        map.put("Vendor",properties.get("java.vendor"));
        map.put("VmName",properties.get("java.vm.name"));
        map.put("VmVersion", properties.get("java.version"));
        return map;
    }

}
