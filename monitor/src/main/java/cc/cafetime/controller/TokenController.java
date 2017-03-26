package cc.cafetime.controller;

import cc.cafetime.App;
import cc.cafetime.SessionFactory;
import cc.cafetime.entity.ResponseData;
import cc.cafetime.info.VmBasicInfo;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by liujing on 2017/3/26.
 */
@Controller
public class TokenController {

    @RequestMapping(value = "/get_token")
    @ResponseBody
    public String getToken(HttpServletRequest request) {
        ResponseData data = new ResponseData();
        String token = UUID.randomUUID().toString();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token",token);
        data.setData(map);
        data.setStatus(App.RESPONSE_STATUS_SUCCESS);
        SessionFactory.storeSession(token, request.getSession());
        return JSONObject.fromObject(data).toString();
    }
}
