package com.paucar.customer_ms.controller;

import com.paucar.customer_ms.dto.ClienteDTO;
import com.paucar.customer_ms.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.paucar.customer_ms.service.consulta.ClienteConsultaService;
import com.paucar.customer_ms.service.gestion.ClienteGestionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cliente")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteConsultaService clienteConsultaService;
    private final ClienteGestionService clienteGestionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> obtenerTodosLosClientes() {
        List<ClienteDTO> clientes = clienteConsultaService.obtenerTodosLosClientes();
        ApiResponse<List<ClienteDTO>> respuesta = ApiResponse.<List<ClienteDTO>>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Lista de clientes recuperada exitosamente")
                .datos(clientes)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> buscarClientePorId(@PathVariable long id) {
        Optional<ClienteDTO> cliente = clienteConsultaService.buscarClientePorId(id);
        return cliente.map(valor -> {
            ApiResponse<ClienteDTO> respuesta = ApiResponse.<ClienteDTO>builder()
                    .estado(HttpStatus.OK.value())
                    .mensaje("Cliente encontrado con éxito")
                    .datos(valor)
                    .build();
            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        }).orElseGet(() -> {
            ApiResponse<ClienteDTO> respuesta = ApiResponse.<ClienteDTO>builder()
                    .estado(HttpStatus.NOT_FOUND.value())
                    .mensaje("Cliente no encontrado con el ID: " + id)
                    .build();
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        });
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteDTO>> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO nuevoCliente = clienteGestionService.guardarCliente(clienteDTO);
        ApiResponse<ClienteDTO> respuesta = ApiResponse.<ClienteDTO>builder()
                .estado(HttpStatus.CREATED.value())
                .mensaje("Cliente creado exitosamente")
                .datos(nuevoCliente)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteActualizado = clienteGestionService.actualizarCliente(id, clienteDTO);
        ApiResponse<ClienteDTO> respuesta = ApiResponse.<ClienteDTO>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Cliente actualizado exitosamente")
                .datos(clienteActualizado)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCliente(@PathVariable Long id) {
        clienteGestionService.eliminarClientePorId(id);
        ApiResponse<Void> respuesta = ApiResponse.<Void>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Cliente eliminado exitosamente")
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}
