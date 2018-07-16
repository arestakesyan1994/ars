package com.example.rest.security;


import com.example.rest.model.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CurrentUser(User user) {
        super(user.getUsername(), user.getPassword(), user.isVerify(), true,
                true, true, AuthorityUtils.createAuthorityList("user"));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
