package cc.cafetime.controller;

import cc.cafetime.App;
import cc.cafetime.entity.ResponseData;
import cc.cafetime.info.OsInfo;
import cc.cafetime.info.VmListInfo;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by liujing on 2017/2/10.
 */
@Controller
public class VmListController {

    @ResponseBody
    @RequestMapping("/os_info")
    public String osInfo() {
        ResponseData data = new ResponseData();
        OsInfo osInfo = OsInfo.getInstance();
        data.setData(osInfo.getInfo());
        data.setStatus(App.RESPONSE_STATUS_SUCCESS);
        return JSONObject.fromObject(data).toString();
    }

    @ResponseBody
    @RequestMapping("/vm_list")
    public String vmList() {
        ResponseData data = new ResponseData();
        VmListInfo vmList = VmListInfo.getInstance();
        try {
            List list = vmList.getList();
            data.setData(list);
            data.setStatus(App.RESPONSE_STATUS_SUCCESS);
            return JSONObject.fromObject(data).toString();
        } catch (Exception e) {
            data.setStatus(App.RESPONSE_STATUS_FAILED);
            data.setMessage(e.getMessage());
            return JSONObject.fromObject(data).toString();
        }
    }

}
