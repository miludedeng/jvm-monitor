package cc.cafetime.controller;

import cc.cafetime.App;
import cc.cafetime.entity.ResponseData;
import cc.cafetime.info.OsInfo;
import cc.cafetime.info.VmJdbcInfo;
import cc.cafetime.info.VmListInfo;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by liujing on 2017/2/10.
 */
@Controller
public class VmSqlController {

    @ResponseBody
    @RequestMapping("/sql_stat/load_agent/{id}")
    public String loadAgent(@PathVariable("id") int id) {
        ResponseData data = new ResponseData();
        try {
            VmJdbcInfo.addAgent(id);
            data.setStatus(App.RESPONSE_STATUS_SUCCESS);
            return JSONObject.fromObject(data).toString();
        } catch (Exception e) {
            data.setStatus(App.RESPONSE_STATUS_FAILED);
            data.setMessage(e.getMessage());
            return JSONObject.fromObject(data).toString();
        }
    }

    @ResponseBody
    @RequestMapping("/sql_stat/is_load_agent/{id}")
    public String isLoadAgent(@PathVariable("id") int id) {
        ResponseData data = new ResponseData();
        try {
            data.setMessage(String.valueOf(VmJdbcInfo.isAddedAgent(id)));
            data.setStatus(App.RESPONSE_STATUS_SUCCESS);
            return JSONObject.fromObject(data).toString();
        } catch (Exception e) {
            data.setStatus(App.RESPONSE_STATUS_FAILED);
            data.setMessage(e.getMessage());
            return JSONObject.fromObject(data).toString();
        }
    }

    @ResponseBody
    @RequestMapping("/sql_stat/load_sql_list/{id}/{pgSize}/{pgCount}")
    public String loadSqlList(@PathVariable("id") int id, @PathVariable("pgSize") int pageSize, @PathVariable("pgCount") int pageCount) {
        ResponseData data = new ResponseData();
        try {
            data.setData(VmJdbcInfo.loadSqlList(id, pageSize, pageCount));
            data.setStatus(App.RESPONSE_STATUS_SUCCESS);
            return JSONObject.fromObject(data).toString();
        } catch (Exception e) {
            data.setStatus(App.RESPONSE_STATUS_FAILED);
            data.setMessage(e.getMessage());
            return JSONObject.fromObject(data).toString();
        }
    }

}
