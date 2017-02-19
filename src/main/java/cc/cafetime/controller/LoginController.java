package cc.cafetime.controller;

import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by steven on 2017/2/19.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/do_login",method = RequestMethod.POST)
    public String login(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("user","user");
        return "redirect:/index";
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return "redirect:/login";
    }
}
