package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nowcoder on 2016/7/14.
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        System.out.println("Liked");
//        Message message = new Message();
//        User user = userService.getUser(model.getActorId());
//        message.setToId(model.getEntityOwnerId());
//        //message.setToId(model.getActorId());
//        message.setContent("用户" + user.getName() +
//                " 赞了你的资讯,http://127.0.0.1:8080/news/"
//                + String.valueOf(model.getEntityId()));
//        // SYSTEM ACCOUNT
//        message.setFromId(3);
//        message.setCreatedDate(new Date());
//        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
