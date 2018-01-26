package com.nowcoder;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.model.LoginTicket;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2018/1/26 0026.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class LoginTicketTest {
    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Test
    public void test(){
        //LoginTicket ticket = loginTicketDAO.selectByTicket("TICKET3");
        LoginTicket ticket = loginTicketDAO.selectById(3);
        loginTicketDAO.updateStatus(ticket.getTicket(), 0);
        Assert.assertEquals(0, loginTicketDAO.selectByTicket(ticket.getTicket()).getStatus());

    }



}
