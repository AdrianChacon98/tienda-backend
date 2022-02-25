package com.store.example.repository;

import com.store.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User,Long>{


    @Query(value= "Select * From User u Where u.email=:email",nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE User u Set u.enabled=true WHERE u.verification_code=:token limit 1",nativeQuery = true)
    int enabledTrue(String token);

    @Query(value = "Select * From User u where u.verification_code=:verificationCode",nativeQuery = true)
    Optional<User> findByVerificationCode(String verificationCode);

    @Query(value="Select * From User u Where u.id=:id", nativeQuery = true)
    Optional<User> findById(Integer id);


}
