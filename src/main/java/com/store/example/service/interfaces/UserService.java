package com.store.example.service.interfaces;

import com.store.example.model.User;

import java.util.Map;

public interface UserService {

    public String register(String name,String lastname,String email,String password);

    public String confirmToken(String token);

    public Map<String, Object> login(String email, String password);

    public User getUserDetails(Integer id);

    public Map<String,Object> refreshToken(String access,String refresh);



}
