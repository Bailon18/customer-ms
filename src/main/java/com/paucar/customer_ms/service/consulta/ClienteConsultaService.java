package com.paucar.customer_ms.service.consulta;

import com.paucar.customer_ms.dto.ClienteDTO;

import java.util.List;
import java.util.Optional;

public interface ClienteConsultaService {
    List<ClienteDTO> obtenerTodosLosClientes();
    Optional<ClienteDTO> buscarClientePorId(Long clienteId);
}
