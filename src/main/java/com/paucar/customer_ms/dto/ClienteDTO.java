package com.paucar.customer_ms.dto;


import lombok.*;

@Setter
@Getter
public class ClienteDTO {

    private long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;

}
