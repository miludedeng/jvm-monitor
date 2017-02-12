package cc.cafetime.controller;

import cc.cafetime.info.VmBasicInfo;
import cc.cafetime.info.VmMonitorInfo;
import com.jvmtop.monitor.JstatInfo;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by liujing on 2017/2/12.
 */
@Controller
public class VmInfoController {

    @ResponseBody
    @RequestMapping("/vm_basic_info/{id}")
    public String vmInfo(@PathVariable("id") int id) {
        Map<String, Object> map = VmBasicInfo.getInfo(id);
        return JSONObject.fromObject(map).toString();
    }

    @ResponseBody
    @RequestMapping("/vm_mon_info/{id}")
    public String osMonitorInfo(@PathVariable("id") int id) {
        Map<String, Object> map = VmMonitorInfo.getInfo(id);
        JstatInfo jstatInfo = new JstatInfo(id);
        VmMonitorInfo.addHeapInfo(jstatInfo,map);
        return JSONObject.fromObject(map).toString();
    }
}