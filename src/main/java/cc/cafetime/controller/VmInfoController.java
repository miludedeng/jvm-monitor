package cc.cafetime.controller;

import cc.cafetime.App;
import cc.cafetime.entity.ResponseData;
import cc.cafetime.info.VmBasicInfo;
import cc.cafetime.info.VmMonitorInfo;
import cc.cafetime.info.VmProfileListInfo;
import cc.cafetime.info.VmThreadListInfo;
import com.jvmtop.monitor.JstatInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by liujing on 2017/2/12.
 */
@Controller
public class VmInfoController {

    @ResponseBody
    @RequestMapping("/vm_basic_info/{id}")
    public String vmInfo(@PathVariable("id") int id) {
        ResponseData data = new ResponseData();
        try {
            Map<String, Object> map = VmBasicInfo.getInfo(id);
            data.setData(map);
            data.setStatus(App.RESPONSE_STATUS_SUCCESS);
            return JSONObject.fromObject(data).toString();
        } catch (Exception e) {
            data.setMessage(e.getMessage());
            data.setStatus(App.RESPONSE_STATUS_FAILED);
            return JSONObject.fromObject(data).toString();
        }

    }

    @ResponseBody
    @RequestMapping("/vm_mon_info/{id}")
    public String vmMonitorInfo(@PathVariable("id") int id) {
        ResponseData data = new ResponseData();
        try {
            Map<String, Object> map = VmMonitorInfo.getInfo(id);
            JstatInfo jstatInfo = new JstatInfo(id);
            VmMonitorInfo.addHeapInfo(jstatInfo, map);
            data.setData(map);
            data.setStatus(App.RESPONSE_STATUS_SUCCESS);
            return JSONObject.fromObject(data).toString();
        } catch (Exception e) {
            data.setMessage(e.getMessage());
            data.setStatus(App.RESPONSE_STATUS_FAILED);
            return JSONObject.fromObject(data).toString();
        }

    }

    @ResponseBody
    @RequestMapping("/vm_thread_list/{id}/{count}")
    public String vmThreadList(@PathVariable("id") int id, @PathVariable("count") int count) {
        ResponseData data = new ResponseData();
        try {
            VmThreadListInfo info = VmThreadListInfo.getInstance();
            List<Map<String, Object>> list = info.getInfo(id, count);
            data.setData(list);
            data.setStatus(App.RESPONSE_STATUS_SUCCESS);
            return JSONObject.fromObject(data).toString();
        } catch (Exception e) {
            data.setMessage(e.getMessage());
            data.setStatus(App.RESPONSE_STATUS_FAILED);
            return JSONObject.fromObject(data).toString();
        }
    }

    @ResponseBody
    @RequestMapping("/vm_thread_count/{id}")
    public String vmThreadCount(@PathVariable("id") int id) {
        ResponseData data = new ResponseData();
        try {
            Map<String, Object> map = VmMonitorInfo.getThreadCount(id);
            data.setData(map);
            data.setStatus(App.RESPONSE_STATUS_SUCCESS);
            return JSONObject.fromObject(data).toString();
        } catch (Exception e) {
            data.setMessage(e.getMessage());
            data.setStatus(App.RESPONSE_STATUS_FAILED);
            return JSONObject.fromObject(data).toString();
        }
    }

    @ResponseBody
    @RequestMapping("/vm_profile_list/{id}/{count}")
    public String vmProfileList(@PathVariable("id") int id, @PathVariable("count") int count) {
        ResponseData data = new ResponseData();
        try {
            List<Map<String, Object>> list = VmProfileListInfo.getInfo(id, count);
            data.setData(list);
            data.setStatus(App.RESPONSE_STATUS_SUCCESS);
            return JSONObject.fromObject(data).toString();
        } catch (Exception e) {
            data.setMessage(e.getMessage());
            data.setStatus(App.RESPONSE_STATUS_FAILED);
            return JSONObject.fromObject(data).toString();
        }
    }
}
