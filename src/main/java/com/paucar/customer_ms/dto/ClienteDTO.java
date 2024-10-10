package com.paucar.customer_ms.dto;


import lombok.*;

@Builder
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;

}
