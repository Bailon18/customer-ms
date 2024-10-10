package com.paucar.customer_ms.service.consulta;

import com.paucar.customer_ms.dto.ClienteDTO;
import com.paucar.customer_ms.exception.ClienteNoEncontradoException;
import com.paucar.customer_ms.mapper.ClienteMapper;
import com.paucar.customer_ms.model.Cliente;
import com.paucar.customer_ms.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteConsultaServiceImpl implements ClienteConsultaService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public List<ClienteDTO> obtenerTodosLosClientes() {
        return clienteRepository.findAll().stream()
                .sorted(Comparator.comparing(Cliente::getId).reversed())
                .map(clienteMapper::convertiADTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ClienteDTO> buscarClientePorId(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con el ID: " + clienteId));
        return Optional.of(clienteMapper.convertiADTO(cliente));
    }
}
