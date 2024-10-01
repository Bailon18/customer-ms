package com.paucar.customer_ms.mapper;


import com.paucar.customer_ms.dto.CustomerDTO;
import com.paucar.customer_ms.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDTO aDTO(Customer customer);
    Customer aEntidad(CustomerDTO customerDTO);

}
