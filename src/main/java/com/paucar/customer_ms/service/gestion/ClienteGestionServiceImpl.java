package com.paucar.customer_ms.service.gestion;

import com.paucar.customer_ms.client.CuentaFeign;
import com.paucar.customer_ms.dto.ClienteDTO;
import com.paucar.customer_ms.exception.ClienteConCuentasActivasException;
import com.paucar.customer_ms.exception.ClienteNoEncontradoException;
import com.paucar.customer_ms.mapper.ClienteMapper;
import com.paucar.customer_ms.client.dto.Cuenta;
import com.paucar.customer_ms.model.Cliente;
import com.paucar.customer_ms.repository.ClienteRepository;
import com.paucar.customer_ms.service.validacion.ClienteValidacionService;
import com.paucar.customer_ms.util.ApiResponse;
import com.paucar.customer_ms.util.EstadoCuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteGestionServiceImpl implements ClienteGestionService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final CuentaFeign cuentaFeign;
    private final ClienteValidacionService clienteValidacionService;

    @Override
    public ClienteDTO guardarCliente(ClienteDTO clienteDTO) {
        Cliente cliente = clienteMapper.convertirAEntidad(clienteDTO);
        clienteValidacionService.validarCliente(cliente, null);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return clienteMapper.convertiADTO(clienteGuardado);
    }

    @Override
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con el ID: " + id));

        Cliente clienteActualizado = clienteMapper.convertirAEntidad(clienteDTO);
        clienteValidacionService.validarCliente(clienteActualizado, id);

        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setApellido(clienteActualizado.getApellido());
        clienteExistente.setDni(clienteActualizado.getDni());
        clienteExistente.setEmail(clienteActualizado.getEmail());

        Cliente clienteGuardado = clienteRepository.save(clienteExistente);
        return clienteMapper.convertiADTO(clienteGuardado);
    }

    @Override
    public void eliminarClientePorId(Long id) {
        if (!clienteRepository.existsById(id)) {
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
}
