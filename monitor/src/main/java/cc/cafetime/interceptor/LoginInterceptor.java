package cc.cafetime.interceptor;

import cc.cafetime.SessionFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * Created by steven on 2017/2/19.
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    public static String LOGIN_PATH = "/login";
    public static String DO_LOGIN_PATH = "/do_login";
    public static String AUTH_CODE = "/authcode";
    public static String AUTH_CODE_VERIFY = "/authcode_verify";
    public static String GET_TOKEN_PATH = "/get_token";
    @Value("${server.servlet-path}")
    private String servletPath;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter("token");
        }
        HttpSession session = request.getSession();
        if (StringUtils.isNotEmpty(token)) {
            session.setAttribute("token", token);
            HttpSession tokenSession = SessionFactory.getSession(token);
            if (tokenSession != null) {
                Enumeration enumeration = tokenSession.getAttributeNames();
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement().toString();//获取session中的键值
                    if ("token".equals(key)){
                        continue;
                    }
                    Object value = tokenSession.getAttribute(key);//根据键值取出session中的值
                    session.setAttribute(key, value);
                }
            }
        }
        String path = request.getRequestURI();
        if (!path.endsWith(AUTH_CODE_VERIFY) && !path.endsWith(GET_TOKEN_PATH) && !path.endsWith(AUTH_CODE) && !path.endsWith(LOGIN_PATH) && !path.endsWith(DO_LOGIN_PATH) && session.getAttribute("username") == null) {
            response.sendRedirect(servletPath + LOGIN_PATH);
        } else {
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("token");
        if (StringUtils.isNotEmpty(token)) {
            HttpSession tokenSession = SessionFactory.getSession(token);
            if (tokenSession == null) {
                return;
            }
            Enumeration enumeration = session.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement().toString();//获取session中的键值
                Object value = session.getAttribute(key);//根据键值取出session中的值
                tokenSession.setAttribute(key, value);
            }
        }
    }
}
