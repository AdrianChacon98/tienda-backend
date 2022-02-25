package com.store.example.service.interfaces;

import com.store.example.model.Address;

import java.util.List;

public interface AddressService {


    public List<Address> getAll(Long id);
    public Address getOne(Long id,Long userId);
    public String create(Address address);
    public String update(Address address,Long id);
    public String delete(Long id);

}
