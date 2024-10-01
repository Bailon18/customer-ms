package com.paucar.customer_ms.controller;

import com.paucar.customer_ms.dto.CustomerDTO;
import com.paucar.customer_ms.service.CustomerService;
import com.paucar.customer_ms.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "*") 
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        ApiResponse<List<CustomerDTO>> response = ApiResponse.<List<CustomerDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lista de clientes recuperada exitosamente")
                .data(customers)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerById(@PathVariable Long id) {
        Optional<CustomerDTO> customer = customerService.findCustomerById(id);
        return customer.map(value -> {
            ApiResponse<CustomerDTO> response = ApiResponse.<CustomerDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Cliente encontrado con éxito")
                    .data(value)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }).orElseGet(() -> {
            ApiResponse<CustomerDTO> response = ApiResponse.<CustomerDTO>builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Cliente no encontrado con el ID: " + id)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        });
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO newCustomer = customerService.saveCustomer(customerDTO);
        ApiResponse<CustomerDTO> response = ApiResponse.<CustomerDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Cliente creado exitosamente")
                .data(newCustomer)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
        ApiResponse<CustomerDTO> response = ApiResponse.<CustomerDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Cliente actualizado exitosamente")
                .data(updatedCustomer)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomerById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Cliente eliminado exitosamente")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
