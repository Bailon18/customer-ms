package com.paucar.customer_ms.service.validacion;

import com.paucar.customer_ms.model.Cliente;

public interface ClienteValidacionService {
    void validarCliente(Cliente cliente, Long id);
}
