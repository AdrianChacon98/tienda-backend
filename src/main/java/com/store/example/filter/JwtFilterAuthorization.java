package com.store.example.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.example.jwt.JWTUtils;
import com.store.example.model.User;
import com.store.example.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtFilterAuthorization extends OncePerRequestFilter {


    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UserServiceImp userService;


    private Logger logger = LoggerFactory.getLogger(JwtFilterAuthorization.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        try{

            //get Authorization param from request.header
            String authorization = request.getHeader("Authorization");

            //compare is different of null and it starts with Bearer
            if(authorization != null && authorization.startsWith("Bearer")){

                //get JWT from Authorization
                String jwt = authorization.substring(7);

                //extract the id from token
                String subject = jwtUtils.getSubject(jwt);

                //verify that the subject is different of null and the context authentication is equal null
                if(subject!=null && SecurityContextHolder.getContext().getAuthentication()==null){

                    //get the user with the subject
                    UserDetails user =  userService.loadUserByUsername(subject);


                    //verify that the subject is equals that the user we got
                    if(jwtUtils.isValidateToken(jwt,user)){


                        List<GrantedAuthority> authorities = user.getAuthorities().stream().collect(Collectors.toList());

                        User userRole=(User) user;

                        authorities.add(new SimpleGrantedAuthority(userRole.getRole().getName()));




                        //create a UsernamePasswordAuthenticationToken
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user,"",authorities);

                        //set Details
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


                        //set security context holder
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                        filterChain.doFilter(request,response);

                    }
                }

            }else{
                SecurityContextHolder.getContext().setAuthentication(null);
                filterChain.doFilter(request,response);
            }

        }catch (Exception e){

            //logger.error("Error JwtFilterAuthorization---------->"+e.getMessage()+e.getCause());

            //ExceptionHandlerUnchecked.handlerException(e);

            response.setHeader("Error",e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());

            Map<String,String> error= new HashMap<>();
            error.put("error_message","Jwt expired");
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(),error);

        }

    }
}
