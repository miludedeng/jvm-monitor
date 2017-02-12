package cc.cafetime.controller;

import cc.cafetime.info.VmListInfo;
import cc.cafetime.info.OsInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by liujing on 2017/2/10.
 */
@Controller
public class VmListController {

    @ResponseBody
    @RequestMapping("/os_info")
    public String osInfo() {
        OsInfo osInfo = OsInfo.getInstance();
        return JSONObject.fromObject(osInfo.getInfo()).toString();
    }

    @ResponseBody
    @RequestMapping("/vm_list")
    public String vmList() {
        VmListInfo vmList = VmListInfo.getInstance();
        return JSONArray.fromObject(vmList.getList()).toString();
    }

}
