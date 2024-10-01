package com.paucar.customer_ms.service;

import com.paucar.customer_ms.dto.CustomerDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();
    Optional<CustomerDTO>  findCustomerById(Long customerId);
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);
    void deleteCustomerById(Long id);

}
