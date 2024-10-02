package com.paucar.customer_ms.service;

import com.paucar.customer_ms.dto.ClienteDTO;

import java.util.List;
import java.util.Optional;

public interface ClienteService {

    List<ClienteDTO> obtenerTodosLosClientes();
    Optional<ClienteDTO> buscarClientePorId(Long clienteId);
    ClienteDTO guardarCliente(ClienteDTO clienteDTO);
    ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO);
    void eliminarClientePorId(Long id);
}
