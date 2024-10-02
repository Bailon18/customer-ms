package com.paucar.customer_ms.model;

import com.paucar.customer_ms.util.EstadoCuenta;
import com.paucar.customer_ms.util.TipoCuenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cuenta {

    private Long id;
    private String numeroCuenta;
    private Double saldo;
    private Long clienteId;

    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;

    @Enumerated(EnumType.STRING)
    private EstadoCuenta estado;
}
