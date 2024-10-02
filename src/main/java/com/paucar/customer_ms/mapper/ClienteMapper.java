package com.paucar.customer_ms.mapper;


import com.paucar.customer_ms.dto.ClienteDTO;
import com.paucar.customer_ms.model.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO convertiADTO(Cliente customer);
    Cliente convertirAEntidad(ClienteDTO customerDTO);

}
