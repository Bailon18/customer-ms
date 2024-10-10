package com.paucar.customer_ms.service.validacion;

import com.paucar.customer_ms.exception.DniYaRegistradoException;
import com.paucar.customer_ms.exception.EmailYaRegistradoException;
import com.paucar.customer_ms.model.Cliente;
import com.paucar.customer_ms.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteValidacionServiceImpl implements ClienteValidacionService {

    private final ClienteRepository clienteRepository;

    @Override
    public void validarCliente(Cliente cliente, Long id) {
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
