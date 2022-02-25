package com.store.example.service;

import com.store.example.email.EmailSender;
import com.store.example.enums.Roles;
import com.store.example.jwt.JWTUtils;
import com.store.example.model.Authorities;
import com.store.example.model.Role;
import com.store.example.model.User;
import com.store.example.repository.AuthoritiesRepository;
import com.store.example.repository.UserRepository;
import com.store.example.service.interfaces.UserService;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImp implements UserDetailsService, UserService {

    private final String USER_NOT_FOUND="user not found";

    private Logger logger = LoggerFactory.getLogger(UserServiceImp.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;



    @Autowired
    private EmailSender emailSender;



    @Autowired
    private JWTUtils jwtUtils;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException(USER_NOT_FOUND));
    }



    @Override
    @Transactional
    @Async
    public String register(String name,String lastname,String email,String password){
        try{

            //verify the email with regex
            boolean isEmail = isValid(email);
            //verify if the user exist
            boolean isUserExist=false;
            User user=null;



            if(isEmail) {

                isUserExist = userRepository.findByEmail(email).isPresent();

            }else{
                return "The email doesnt match with the rules";
            }



            //if the user not exist create a new user otherwise the user exist
            if(!isUserExist){



                //generate UIDD to send to the email
                UUID uuid = UUID.randomUUID();


                //El Error posible es que no estamos insertando el role que corresponde
                //create role and authorities
                Role role = new Role();
                role.setId(1l);
                role.setName(Roles.ROLE_USER.name());



                //create the user
                User newUser=new User(name,lastname,email,generateHash(password),true,false, LocalDateTime.now(),LocalDateTime.now().plusMinutes(15),uuid.toString(),role);

                userRepository.save(newUser);



                List<Authorities> authorities = new ArrayList<>();

                Roles.ROLE_USER.getAuthorities().stream().forEach(authority->{
                    authorities.add(new Authorities(newUser,authority.name()));
                });


                //save authorities
                authoritiesRepository.saveAll(authorities);



                //send the email
                String endpoint="http://localhost:8080/api/v1/user/confirm?token="+uuid;

                emailSender.send(newUser.getEmail(),buildEmail(newUser.getUsername(),endpoint));



                return "User was created succefully";

            }else{
                return "The user already exist";
            }


        }catch(Exception e){

            logger.info(e.getCause().toString());

            return "Error internal server";

        }

    }


    @Override
    public String confirmToken(String token){

        try{

            Optional<User> user = userRepository.findByVerificationCode(token);
            logger.info("USER IS->"+user.toString());
            //if user is not empty
            if(!user.isEmpty()){

                //verify if the expiredAt propertie from the user not pass of 15 minutes of created the user
                if(user.get().getExpiredAt().isBefore(LocalDateTime.now())){

                    return "the user was not confirmed at time, please create a new user"; //mejorar la opcion y hacer que se genere nuevo token para verificar y evitar que se pierda la cuenta
                }else{
                    userRepository.enabledTrue(token);
                }

            }else{
                return "The user was not found";
            }


        }catch(Exception e){

        }

        return "The user was enabled now you can log in";
    }


    @Override
    public Map<String, Object> login(String email, String password){

        Map<String,Object> tokens=null;

        try{

            //verify the email with regex
            boolean isEmail = isValid(email);


            //if isEmail is not true that mean the email not match with the rules
            if(!isEmail){
                Map<String,Object> errorEmail = new HashMap<>();
                errorEmail.put("ErrorEmail","The email it doesnt matching with the rules");
                return errorEmail;
            }

            //find the user with email
            Optional<User> user = userRepository.findByEmail(email);

            //ask if the user is empty if yes then user was not found
            if(user.isEmpty()){
                Map<String,Object> userNotFound = new HashMap<>();
                userNotFound.put("UserNotFound","The user was not found");
                return userNotFound;
            }


            //compare if the user email is equals than the email user input and the password isEquals with
            if(user.get().getEmail().equals(email) && isPasswordEquals(user.get().getPassword(),password)){

                tokens = jwtUtils.generateJWT(user.get());

            }else{

                Map<String,Object> userIsNotCorrect = new HashMap<>();
                userIsNotCorrect.put("UserOrPasswordIsNotCorrect","The email or password is not correct please check them and try again");
                return userIsNotCorrect;
            }




        }catch (Exception e){
            Map<String,Object> serverError = new HashMap<>();
            logger.info(e.getCause().toString());

            serverError.put("Server Error","Error Login");
            return serverError;

        }




        return tokens;

    }

    @Override
    public User getUserDetails(Integer id){
        try{


            Optional<User> user = userRepository.findById(id);

            return user.get();



        }catch (Exception e){

            return new User();

        }
    }



    @Override
    public Map<String,Object> refreshToken(String access,String refresh){

        Map<String,Object> tokens=null;

        try{

            //verify that the token is correct with the secret key
            boolean isValid= jwtUtils.isValidateRefreshToken(access,refresh);

            //extract the claims from the access_token
            if(isValid){

                Claims claims = jwtUtils.getBodyClaims(access);

                tokens = jwtUtils.refresToken(claims,refresh);


            }else{
                tokens.put("Error","refresh_token is expired log in again");
            }

        }catch (Exception e){


        }


        return tokens;
    }






    //these can be move it to other class
    private String generateHash(String password){

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2d);

        return argon2.hash(5,1024*1,2, password);


    }

    private boolean isPasswordEquals(String userPassword,String password){
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2d);
        return argon2.verify(userPassword,password);
    }

    private boolean isValid(String email){
        try {
            Pattern pattern = Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@[a-z0-9-]+(.[a-z0-9-]+)*(.[a-z]{2,4})$");

            Matcher matcher = pattern.matcher(email);

            if(matcher.find())
                return true; // is correct



        }catch (Exception e){
            logger.info(e.getMessage());

        }

        return false;


    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}
