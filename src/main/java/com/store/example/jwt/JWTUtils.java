package com.store.example.jwt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.example.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class JWTUtils implements Serializable {


    private static final long serialVersionUID = -56203940029416013L;


    private final Logger logger=LoggerFactory.getLogger(JWTUtils.class);


    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.tokenTimeMs}")
    private String timeMS;

    private final Logger LOGGER = LoggerFactory.getLogger(JWTUtils.class);

    @Async
    public Map<String, Object> generateJWT(User user){

        String access_token = "";
        String refresh_token="";
        Map<String,Object> tokens = new HashMap<>();


        try{

            //Algorithm hash
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            //Time token
            long nowMillis=System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //get the bites from the secret key parse as a  binary method
            byte [] secretKeyBites= DatatypeConverter.parseBase64Binary(secretKey);


            //create the signingKey to sign the token
            Key signing = new SecretKeySpec(secretKeyBites,signatureAlgorithm.getJcaName());



            //Get authorities
            List<GrantedAuthority> grantedAuthorityList = user.getAuthorities()
                    .stream()
                    .map(authority->new SimpleGrantedAuthority(authority.getAuthority()))
                    .collect(Collectors.toList());



            //create Claims and serializable with jackson
            Map<String,Object> claims = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);


            //becareful with null values because it could throw exception claims can not seralization
            claims.put("id",user.getId());
            claims.put("username",user.getUsername());
            claims.put("Role",user.getRole().getName());
            claims.put("Authorities",grantedAuthorityList.stream().map(authority->authority.getAuthority()).collect(Collectors.toList()));


            //create jwt
            if(!timeMS.isEmpty()) {
                //set time expiration to the token
                long expireMs = nowMillis + Long.parseLong(timeMS);
                Date expirationAt = new Date(expireMs);



                access_token = Jwts.builder()
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setClaims(claims)
                        .setSubject(user.getEmail())
                        .setExpiration(expirationAt)
                        .signWith(signatureAlgorithm, signing).compact();



                //create refresh_token
                refresh_token = Jwts.builder()
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setSubject(user.getEmail())
                        .setExpiration(new Date(System.currentTimeMillis() + (Long.parseLong((timeMS)) * 2) * 24))//set 1 day
                        .signWith(signatureAlgorithm, signing).compact();


            }









        }catch(Exception e){
            logger.info(e.getMessage());
            logger.info(e.getCause().toString());
        }

        logger.info(access_token);
        logger.info(refresh_token);
        tokens.put("access_token",access_token);
        tokens.put("refresh_token",refresh_token);
        return tokens;


    }

    @Async
    public Map<String,Object> refresToken(Claims claims,String subject){
        JwtBuilder access_token = null;
        JwtBuilder refresh_token = null;
        Map<String,Object> tokens=new HashMap<>();

        try {

            //Algorithm hash
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            //Time token
            long nowMillis=System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //get the bites from the secret key parse as a  binary method
            byte [] secretKeyBites= DatatypeConverter.parseBase64Binary(secretKey);


            //create the signingKey to sign the token
            Key signing = new SecretKeySpec(secretKeyBites,signatureAlgorithm.getJcaName());


            //set time expiration to the token
            long expireMs=nowMillis+Long.parseLong(timeMS);
            Date expirationAt = new Date(expireMs);

            //create jwt
            access_token = Jwts.builder()
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setSubject(subject)
                    .setClaims(claims)
                    .setExpiration(expirationAt)
                    .signWith(signatureAlgorithm,signing);

            //create refresh_token
            refresh_token=Jwts.builder()
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setSubject(claims.getSubject())
                    .setExpiration(new Date(System.currentTimeMillis()+(Long.parseLong((timeMS))*2)*24))//set 1 day
                    .signWith(signatureAlgorithm,signing);

            tokens.put("access_token",access_token.compact());
            tokens.put("refresh_token",refresh_token.compact());

        }catch (Exception e){



        }

        return tokens;
    }


    public Claims getBodyClaims(String token){
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                    .parseClaimsJws(token).getBody();
    }

    public String getSubject(String token){
        Claims claims = getBodyClaims(token);



        return claims.getSubject();
    }


    public Long getId(String token){
        Claims claims = getBodyClaims(token);
        return Long.parseLong(claims.getId());
    }


    //if true is not expired
    public boolean isTokenExpired(String token){
        Claims claims = getBodyClaims(token);
        return claims.getExpiration().before(new Date());
    }


    public boolean isValidateToken(String token, UserDetails user){
        return user.getUsername().equals(getBodyClaims(token).get("username")) && !isTokenExpired(token);
    }

    public boolean isValidateRefreshToken(String access_token, String refresh_token){
        return getSubject(refresh_token).equals(getSubject(access_token)) && !isTokenExpired(refresh_token);
    }





}
