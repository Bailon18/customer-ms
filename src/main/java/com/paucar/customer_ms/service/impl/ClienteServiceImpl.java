package com.paucar.customer_ms.service.impl;

import com.paucar.customer_ms.client.CuentaFeign;
import com.paucar.customer_ms.dto.ClienteDTO;
import com.paucar.customer_ms.exception.ClienteConCuentasActivasException;
import com.paucar.customer_ms.exception.ClienteNoEncontradoException;
import com.paucar.customer_ms.exception.DniYaRegistradoException;
import com.paucar.customer_ms.exception.EmailYaRegistradoException;
import com.paucar.customer_ms.mapper.ClienteMapper;
import com.paucar.customer_ms.model.Cuenta;
import com.paucar.customer_ms.model.Cliente;
import com.paucar.customer_ms.repository.ClienteRepository;
import com.paucar.customer_ms.service.ClienteService;
import com.paucar.customer_ms.util.ApiResponse;
import com.paucar.customer_ms.util.EstadoCuenta;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final CuentaFeign cuentaFeign;
    private final ClienteMapper clienteMapper;

    public ClienteServiceImpl(ClienteRepository clienteRepository,
                              CuentaFeign cuentaFeign,
                              ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.cuentaFeign = cuentaFeign;
        this.clienteMapper = clienteMapper;
    }

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

    @Override
    public ClienteDTO guardarCliente(ClienteDTO clienteDTO) {
        Cliente cliente = clienteMapper.convertirAEntidad(clienteDTO);
        validarCliente(cliente, null);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return clienteMapper.convertiADTO(clienteGuardado);
    }

    @Override
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con el ID: " + id));

        Cliente clienteActualizado = clienteMapper.convertirAEntidad(clienteDTO);
        validarCliente(clienteActualizado, id);

        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setApellido(clienteActualizado.getApellido());
        clienteExistente.setDni(clienteActualizado.getDni());
        clienteExistente.setEmail(clienteActualizado.getEmail());

        Cliente clienteGuardado = clienteRepository.save(clienteExistente);
        return clienteMapper.convertiADTO(clienteGuardado);
    }

    @Override
    public void eliminarClientePorId(Long id) {

        boolean clienteExiste = clienteRepository.existsById(id);
        if (!clienteExiste) {
            throw new ClienteNoEncontradoException("Cliente no encontrado con el ID: " + id);
        }

        ResponseEntity<ApiResponse<List<Cuenta>>> response = cuentaFeign.obtenerCuentasPorClienteId(id);

        ApiResponse<List<Cuenta>> accountData = response.getBody();
        if (accountData == null || accountData.getDatos() == null) {
            throw new IllegalStateException("No se pudo obtener información de las cuentas del cliente.");
        }

        boolean tieneCuentasActivas = accountData.getDatos().stream()
                .anyMatch(cuenta -> cuenta.getEstado() == EstadoCuenta.ACTIVO);

        if (tieneCuentasActivas) {
            throw new ClienteConCuentasActivasException("No se puede eliminar el cliente con ID: " + id + " porque tiene cuentas activas.");
        }

        clienteRepository.deleteById(id);
    }



    private void validarCliente(Cliente cliente, Long id) {
        Optional<Cliente> clienteOptionalEmail = clienteRepository.findByEmail(cliente.getEmail());
        if (clienteOptionalEmail.isPresent() && !clienteOptionalEmail.get().getId().equals(id)) {
            throw new EmailYaRegistradoException("El correo electrónico ya está en uso: " + cliente.getEmail());
        }

        Optional<Cliente> clienteOptionalDni = clienteRepository.findByDni(cliente.getDni());
        if (clienteOptionalDni.isPresent() && !clienteOptionalDni.get().getId().equals(id)) {
            throw new DniYaRegistradoException("El DNI ya está en uso: " + cliente.getDni());
        }
    }

}
