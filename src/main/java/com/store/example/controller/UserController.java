package com.store.example.controller;


import com.store.example.model.User;
import com.store.example.service.UserServiceImp;
import com.store.example.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {


    private Logger logger = LoggerFactory.getLogger(UserController.class);



    @Autowired
    private UserService userService;





    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam("name")String name, @RequestParam("lastname")String lastname, @RequestParam("email") String email, @RequestParam("password") String password){
        try{


            String response = userService.register(name,lastname,email,password);

            if(response.equals("The email doesnt match with the rules")){
                return new ResponseEntity<String>(response,HttpStatus.OK); //200
            }else if(response.equals("The user already exist")){
                return  new ResponseEntity<String>(response,HttpStatus.OK); //200
            }else if(response.equals("Error internal server")){
                return new ResponseEntity<String>("it was a error on the server",HttpStatus.INTERNAL_SERVER_ERROR); //500
            }

            return new ResponseEntity<String>(response, HttpStatus.CREATED); //201

        }catch(Exception e)
        {

            return new ResponseEntity<String>("it was a error on the server",HttpStatus.INTERNAL_SERVER_ERROR); //500
        }

    }


    @PostMapping(value = "/login",produces="application/json")
    public ResponseEntity<Map<String,Object>> login(@RequestParam("email")String email,@RequestParam("password")String password){
        Map<String,Object> response = null;
        try{


           response=userService.login(email,password);




           for(String key : response.keySet()){

               switch (response.get(key).toString()){
                   case "The email it doesnt matching with the rules":
                   case "The user was not found":
                   case "The email or password is not correct please check them and try again":
                       return new ResponseEntity<>(response,HttpStatus.OK);


               }

           }


        }catch (Exception e) {

            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR); //500
        }

        return new ResponseEntity<>(response,HttpStatus.CREATED);


    }


    @GetMapping("/refresh")
    public ResponseEntity<Map<String,Object>> refreshToken(@RequestParam("access_token")String access,@RequestParam("refresh_token") String refresh){

        try{

            Map<String,Object> tokens = userService.refreshToken(access,refresh);

            if(!tokens.isEmpty() && tokens.size()>1){
                return new ResponseEntity<Map<String,Object>>(tokens,HttpStatus.CREATED);
            }else{
                return new ResponseEntity<Map<String,Object>>(tokens,HttpStatus.BAD_REQUEST);
            }

        }catch (Exception e){

            return new ResponseEntity<Map<String,Object>>(new HashMap<>(),HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirmToken(@RequestParam("token") String token){

        try{

            String response = userService.confirmToken(token);

            switch (response){
                case "the user was not confirmed at time, please create a new user":
                    return new ResponseEntity<String>(response,HttpStatus.BAD_REQUEST);
                case "The user was not found":
                    return new ResponseEntity<String>(response,HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<String>(response,HttpStatus.CREATED);

        }catch (Exception e){
            return new ResponseEntity<String>("it was a error on the server",HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(path = "/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable("id") Integer id){
        User user=null;

        try {

            user = userService.getUserDetails(id);

            if(user==null){
                return new ResponseEntity<User>(user,HttpStatus.NOT_FOUND);
            }

            return  new ResponseEntity<User>(user,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<User>(user,HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }



}
