package com.store.example.repository;

import com.store.example.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AddressRespository extends JpaRepository<Address,Long> {


    @Query(value = "Select * From Address a Where a.id_user_address=:id",nativeQuery = true)
    public List<Address> findAllAddressByUserId(Long id);

    @Query(value="Select * from Address a Where a.id=:id AND a.id_user_address=:userId",nativeQuery = true)
    public Optional<Address> findOneWithAddressIdAndUserId(Long id,Long userId);



}
