package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/1/26 0026.
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUsers() {
        return users.get();
    }

    public void setUsers(User user) {
        users.set(user);
    }

    public void  clear(){
        users.remove();
    }
}
