package com.paucar.customer_ms.model;


import com.paucar.customer_ms.util.EstadoCuenta;
import com.paucar.customer_ms.util.TipoCuenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    private Long id;
    private String numeroCuenta;
    private Double saldo;

    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;

    private Long clienteId;

    @Enumerated(EnumType.STRING)
    private EstadoCuenta estado;
}
