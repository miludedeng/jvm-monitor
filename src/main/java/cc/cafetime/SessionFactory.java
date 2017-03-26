package cc.cafetime;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujing on 2017/3/26.
 */
public class SessionFactory {

    private static Map<String, HttpSession> map = new HashMap<String, HttpSession>();

    public static HttpSession getSession(String token) {
        return (map.get(token));
    }

    public static void storeSession(String token, HttpSession session) {
        map.put(token, session);
    }

    public static void removeSession(String token) {
        map.remove(token);
    }
}
