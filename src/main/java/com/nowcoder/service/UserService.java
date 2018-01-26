package com.nowcoder.service;

import com.nowcoder.Util.ToutiaoUtil;
import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)){
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }
        User user = userDAO.selectByname(username);
        if (user != null) {
            map.put("msgname", "用户名已经被注册");
            return map;
        }
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);
        //map.put("msgname", "注册成功");
        return map;
    }

    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)){
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }
        User user = userDAO.selectByname(username);
        if (user == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }
        if (!user.getPassword().equals(ToutiaoUtil.MD5(password + user.getSalt()))){
            map.put("msgpwd", "密码不正确");
        }
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        //map.put("msgname", "注册成功");
        return map;
    }

    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addLoginTicket(ticket);
        return ticket.getTicket();
    }

}
