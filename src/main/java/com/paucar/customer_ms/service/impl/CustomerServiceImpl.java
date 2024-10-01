package com.paucar.customer_ms.service.impl;

import com.paucar.customer_ms.client.AccountClient;
import com.paucar.customer_ms.dto.CustomerDTO;
import com.paucar.customer_ms.exception.CustomerHasActiveAccountsException;
import com.paucar.customer_ms.exception.CustomerNotFoundException;
import com.paucar.customer_ms.exception.DniAlreadyExistsException;
import com.paucar.customer_ms.exception.EmailAlreadyExistsException;
import com.paucar.customer_ms.mapper.CustomerMapper;
import com.paucar.customer_ms.model.Account;
import com.paucar.customer_ms.model.Customer;
import com.paucar.customer_ms.repository.CustomerRepository;
import com.paucar.customer_ms.service.CustomerService;
import com.paucar.customer_ms.util.ApiResponse;
import com.paucar.customer_ms.util.EstadoCuenta;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountClient accountClient;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               AccountClient accountClient,
                               CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.accountClient = accountClient;
        this.customerMapper = customerMapper;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .sorted(Comparator.comparing(Customer::getId).reversed())
                .map(customerMapper::aDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> findCustomerById(Long customerId) {

        // orElseThrow devuelve valor del optional si esta presente de lo contrario lanza exception
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado con el ID: " + customerId));
        return Optional.of(customerMapper.aDTO(customer));
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.aEntidad(customerDTO);
        validateCustomerDetails(customer, null);
        return customerMapper.aDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado con el ID: " + id));

        //  Convertir el CustomerDTO a una entidad Customer
        Customer customerToUpdate = customerMapper.aEntidad(customerDTO);

        // 3. Validar los detalles del nuevo Customer antes de actualizar
        validateCustomerDetails(customerToUpdate, id);

        existingCustomer.setName(customerToUpdate.getName());
        existingCustomer.setLastname(customerToUpdate.getLastname());
        existingCustomer.setDni(customerToUpdate.getDni());
        existingCustomer.setEmail(customerToUpdate.getEmail());

        Customer updatedCustomer = customerRepository.save(existingCustomer);

        return customerMapper.aDTO(updatedCustomer);
    }


    @Override
    public void deleteCustomerById(Long id) {

        boolean customerExists = customerRepository.existsById(id);
        if (!customerExists) {
            throw new CustomerNotFoundException("Cliente no encontrado con el ID: " + id);
        }

        // 2. Obtener las cuentas asociadas al cliente utilizando el AccountClient
        ResponseEntity<ApiResponse<List<Account>>> response = accountClient.getAccountsByCustomerId(id);

        // 3. Verificar si la respuesta del cliente contiene datos de cuentas
        ApiResponse<List<Account>> accountData = response.getBody();
        if (accountData == null) {
            throw new IllegalStateException("No se pudo obtener información de las cuentas del cliente.");
        }

        // Revisar si alguna de las cuentas está en estado ACTIVO
        boolean hasActiveAccounts = accountData.getData().stream()
                .anyMatch(account -> account.getEstado() == EstadoCuenta.ACTIVO);

        if (hasActiveAccounts) {
            throw new CustomerHasActiveAccountsException("No se puede eliminar el cliente con ID: " + id + " porque tiene cuentas activas.");
        }

        // Eliminar el cliente si no tiene cuentas activas
        customerRepository.deleteById(id);
    }


    private void validateCustomerDetails(Customer customer, Long id) {
        Long customerId = (id != null) ? id : -1L;

        if (customerRepository.existsByEmailAndIdNot(customer.getEmail(), customerId)) {
            throw new EmailAlreadyExistsException("El correo electrónico ya está en uso: " + customer.getEmail());
        }

        if (customerRepository.existsByDniAndIdNot(customer.getDni(), customerId)) {
            throw new DniAlreadyExistsException("El DNI ya está registrado: " + customer.getDni());
        }
    }
}
