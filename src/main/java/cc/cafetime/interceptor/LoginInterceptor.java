package cc.cafetime.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by steven on 2017/2/19.
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    public static String LOGIN_PATH = "/login";
    public static String DO_LOGIN_PATH = "/do_login";
    public static String AUTH_CODE = "/authcode";
    public static String AUTH_CODE_VERIFY = "/authcode_verify";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String path = request.getRequestURI();
        if (!AUTH_CODE_VERIFY.equals(path) && !AUTH_CODE.equals(path) && !LOGIN_PATH.equals(path) && !DO_LOGIN_PATH.equals(path) && session.getAttribute("username") == null) {
            response.sendRedirect(LOGIN_PATH);
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
    }
}
