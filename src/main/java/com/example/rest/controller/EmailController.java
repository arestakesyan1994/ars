package com.example.rest.controller;

import com.example.rest.model.User;
import com.example.rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmailController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token,
                         @RequestParam("username") String username){
        User allByUsername = userRepository.findAllByUsername(username);
        if(allByUsername!=null){
            if(allByUsername.getToken()!=null && allByUsername.getToken().equals(token)){
                allByUsername.setToken(null);
                allByUsername.setVerify(true);
                userRepository.save(allByUsername);
            }
        }
        return "index";
    }
}
