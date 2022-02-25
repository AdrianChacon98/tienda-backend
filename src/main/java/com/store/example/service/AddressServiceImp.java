package com.store.example.service;

import com.store.example.exceptionHandler.ExceptionHandlerUnchecked;
import com.store.example.model.Address;
import com.store.example.repository.AddressRespository;
import com.store.example.service.interfaces.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressServiceImp implements AddressService {

    private final Logger logger = LoggerFactory.getLogger(AddressServiceImp.class);


    @Autowired
    private AddressRespository addressRespository;






    @Override
    public List<Address> getAll(Long id) {

        try{

            List<Address> addresses = addressRespository.findAllAddressByUserId(id);

            return addresses;

        }catch (Exception e){

            ExceptionHandlerUnchecked.handlerException(e);


            return new ArrayList<>();
        }


    }

    @Override
    public Address getOne(Long id,Long userId) {

        try{

            Optional<Address> address= addressRespository.findOneWithAddressIdAndUserId(id,userId);

            if(!address.isEmpty()){
                return address.get();
            }


            return null;


        }catch (Exception e){
            ExceptionHandlerUnchecked.handlerException(e);
            return null;
        }


    }



    @Override
    public String create(Address address) {
        return null;
    }

    @Override
    public String update(Address address, Long id) {
        return null;
    }

    @Override
    public String delete(Long id) {
        return null;
    }
}
