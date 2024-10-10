package com.paucar.customer_ms.service.gestion;

import com.paucar.customer_ms.dto.ClienteDTO;

public interface ClienteGestionService {
    ClienteDTO guardarCliente(ClienteDTO clienteDTO);
    ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO);
    void eliminarClientePorId(Long id);
}
