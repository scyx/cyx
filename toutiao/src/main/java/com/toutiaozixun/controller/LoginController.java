package com.toutiaozixun.controller;

import com.toutiaozixun.async.EventModel;
import com.toutiaozixun.async.EventProducer;
import com.toutiaozixun.async.EventType;
import com.toutiaozixun.dao.LoginTicketDAO;
import com.nowcoder.model.*;
import com.toutiaozixun.model.HostHolder;
import com.toutiaozixun.service.UserService;
import com.toutiaozixun.util.ToutiaoUtil;
import com.toutiaozixun.model.LoginTicket;
import com.toutiaozixun.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    LoginTicketDAO loginTicketDAO;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value="rember", defaultValue = "0") int rememberme,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                String ticket = map.get("ticket").toString();
                LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
                User user = userService.selectByid(loginTicket.getUserId());
                int userId= user.getId();
                if(user!=null){
                    eventProducer.fireEvent(new EventModel(EventType.REGEIST).setActorId(userId));
                }
                return ToutiaoUtil.getJSONString(0, "注册成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }
    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value="rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(username, password);
            logger.info("LoginController.login");
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "登录成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            logger.error("登录异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "登录异常");
        }
    }

    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

}
