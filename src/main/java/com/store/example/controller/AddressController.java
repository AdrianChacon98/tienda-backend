package com.store.example.controller;


import com.store.example.model.Address;
import com.store.example.service.interfaces.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {


    @Autowired
    private AddressService addressService;

    private Logger logger = LoggerFactory.getLogger(AddressController.class);



    @GetMapping("/get/{id}/{userId}")
    public ResponseEntity<Address> getOne(@PathVariable("id") Long id,@PathVariable("userId") Long userId){

        Address response=null;
        try{

            logger.info("El id es:"+id+"El user ID es"+userId );

            response=addressService.getOne(id,userId);



            if(response==null){
                Address address = null;
                return new ResponseEntity<Address>(address,HttpStatus.NOT_FOUND);
            }


        }catch (Exception e){

            return new ResponseEntity<Address>(new Address(), HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return new ResponseEntity<Address>(response,HttpStatus.OK);

    }


    @GetMapping("/all/{id}")
    public ResponseEntity<List<Address>> getAll(@PathVariable("id") Long id){

        List<Address> addresses=null;

        try{

            addresses=addressService.getAll(id);

            if(addresses.isEmpty()){
               List<Address> addressesEmpty =null;
               return new ResponseEntity<>(addressesEmpty,HttpStatus.NOT_FOUND);
            }


        }catch (Exception e){

            List<Address> error=null;
            return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return new ResponseEntity<>(addresses,HttpStatus.OK);

    }

    /*

    public ResponseEntity<String> create(@RequestBody Address address){

        try{


            return new ResponseEntity<String>("Address created sussefully",HttpStatus.CREATED);


        }catch (Exception e){

            return new ResponseEntity<String>("Error creating a new address",HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }




    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer id){

        try{



        }catch (Exception e){




        }


    }


    @PutMapping("/update/{id}")
    public ResponseEntity update(@RequestBody Address address,@PathVariable("id") Integer id){
        try {



            return new ResponseEntity(HttpStatus.CREATED);


        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    */




}
