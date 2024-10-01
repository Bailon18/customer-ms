package com.paucar.customer_ms.dto;


import lombok.*;

@Setter
@Getter
public class CustomerDTO {

    private long id;
    private String name;
    private String lastname;
    private String dni;
    private String email;

}
