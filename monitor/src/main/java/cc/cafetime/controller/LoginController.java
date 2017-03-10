package cc.cafetime.controller;

import cc.cafetime.App;
import cc.cafetime.util.UserUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

/**
 * Created by steven on 2017/2/19.
 */
@Controller
public class LoginController {

    @Value("${server.servlet-path}")
    private String servletPath;

    @RequestMapping(value = "/do_login", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest request) {
        String authcode = request.getParameter("authcode");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        Object authcodeTime = session.getAttribute("authcodeTime");
        if (authcodeTime == null || new Date().getTime() - ((Date) authcodeTime).getTime() > 60 * 60 * 1000) { // 验证码超过一个小时
            return App.RESPONSE_STATUS_AUTHCODE_OVERTIME;
        }
        if (authcode == null || !authcode.equals(session.getAttribute("authcode"))) {
            return App.RESPONSE_STATUS_FAILED;
        }
        if (UserUtil.checkUser(username, password)) {
            session.setAttribute("username", username);
            return App.RESPONSE_STATUS_SUCCESS;
        } else {
            return App.RESPONSE_STATUS_FAILED;
        }
    }

    @RequestMapping(value = "/authcode_verify")
    @ResponseBody
    public String authcodeVerify(HttpServletRequest request) {
        String authcode = request.getParameter("authcode");
        HttpSession session = request.getSession();
        Date authcodeTime = (Date) session.getAttribute("authcodeTime");
        if (authcodeTime == null || new Date().getTime() - authcodeTime.getTime() > 60 * 60 * 1000) { // 验证码超过一个小时
            return App.RESPONSE_STATUS_AUTHCODE_OVERTIME;
        }
        return authcode == null || !authcode.equals(session.getAttribute("authcode")) ? App.RESPONSE_STATUS_FAILED : App.RESPONSE_STATUS_SUCCESS;
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return "redirect:" + servletPath + "/login";
    }

    @RequestMapping(value = "/authcode")
    public void getAuthCode(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        int width = 88;
        int height = 37;
        Random random = new Random();
        //设置response头信息
        //禁止缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        //生成缓冲区image类
        BufferedImage image = new BufferedImage(width, height, 1);
        //产生image类的Graphics用于绘制操作
        Graphics g = image.getGraphics();
        //Graphics类的样式
        g.setColor(this.getRandColor(200, 250));
        g.setFont(new Font("Times New Roman", 0, 28));
        g.fillRect(0, 0, width, height);
        //绘制干扰线
        for (int i = 0; i < 40; i++) {
            g.setColor(this.getRandColor(130, 200));
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int x1 = random.nextInt(12);
            int y1 = random.nextInt(12);
            g.drawLine(x, y, x + x1, y + y1);
        }

        //绘制字符
        String strCode = "";
        for (int i = 0; i < 6; i++) {
            String rand = String.valueOf(random.nextInt(10));
            strCode = strCode + rand;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 28);
        }
        //将字符保存到session中用于前端的验证
        session.setAttribute("authcode", strCode);
        session.setAttribute("authcodeTime", new Date());
        g.dispose();

        ImageIO.write(image, "JPEG", response.getOutputStream());
        response.getOutputStream().flush();

    }

    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
