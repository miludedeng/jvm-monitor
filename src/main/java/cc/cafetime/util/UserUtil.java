package cc.cafetime.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by liujing on 2017/2/19.
 */
public class UserUtil {

    private static Map<String, String> userMap = new HashMap<String, String>();
    private static final String USERNAME_PREFIX = "user";
    private static final String PASSWORD_PREFIX = "password";

    static {
        Properties prop = new Properties();
        try {
            prop.load(UserUtil.class.getResourceAsStream("/user.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Object obj : prop.keySet()) {
            String key = (String) obj;
            String value = (String) prop.get(key);
            if (key.startsWith(USERNAME_PREFIX)){
                String username = value;
                String passwdKey = PASSWORD_PREFIX + key.substring(USERNAME_PREFIX.length());
                String password = prop.getProperty(passwdKey);
                userMap.put(username,password);
            }
        }
    }

    public static boolean checkUser(String username, String password){
        String passwd = userMap.get(username);
        if(passwd!=null&&passwd.equals(password)){
            return true;
        }
        return false;
    }

}
