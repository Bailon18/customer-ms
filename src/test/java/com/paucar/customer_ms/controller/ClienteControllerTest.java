package com.paucar.customer_ms.controller;

import com.paucar.customer_ms.dto.ClienteDTO;
import com.paucar.customer_ms.service.consulta.ClienteConsultaService;
import com.paucar.customer_ms.service.gestion.ClienteGestionService;
import com.paucar.customer_ms.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ClienteControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ClienteControllerTest.class);

    @Mock
    private ClienteConsultaService clienteConsultaService;

    @Mock
    private ClienteGestionService clienteGestionService;

    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        log.info("Inicializando pruebas para ClienteController...");
    }

    @Test
    void obtenerTodosLosClientes() {
        log.info("Prueba: obtenerTodosLosClientes");

        List<ClienteDTO> listaClientes = Arrays.asList(new ClienteDTO(), new ClienteDTO());
        when(clienteConsultaService.obtenerTodosLosClientes()).thenReturn(listaClientes);

        ResponseEntity<ApiResponse<List<ClienteDTO>>> response = clienteController.obtenerTodosLosClientes();

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Lista de clientes recuperada exitosamente", response.getBody().getMensaje());
        verify(clienteConsultaService, times(1)).obtenerTodosLosClientes();
    }

    @Test
    void buscarClientePorId_CuandoExiste() {
        log.info("Prueba: buscarClientePorId_CuandoExiste");

        ClienteDTO clienteDTO = new ClienteDTO();
        Long id = 1L;
        when(clienteConsultaService.buscarClientePorId(id)).thenReturn(Optional.of(clienteDTO));

        ResponseEntity<ApiResponse<ClienteDTO>> response = clienteController.buscarClientePorId(id);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cliente encontrado con éxito", response.getBody().getMensaje());
        verify(clienteConsultaService, times(1)).buscarClientePorId(id);
    }

    @Test
    void buscarClientePorId_CuandoNoExiste() {
        log.info("Prueba: buscarClientePorId_CuandoNoExiste");

        Long id = 1L;
        when(clienteConsultaService.buscarClientePorId(id)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<ClienteDTO>> response = clienteController.buscarClientePorId(id);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cliente no encontrado con el ID: " + id, response.getBody().getMensaje());
        verify(clienteConsultaService, times(1)).buscarClientePorId(id);
    }

    @Test
    void crearCliente() {
        log.info("Prueba: crearCliente");

        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNombre("Juan");
        when(clienteGestionService.guardarCliente(clienteDTO)).thenReturn(clienteDTO);

        ResponseEntity<ApiResponse<ClienteDTO>> response = clienteController.crearCliente(clienteDTO);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Cliente creado exitosamente", response.getBody().getMensaje());
        verify(clienteGestionService, times(1)).guardarCliente(clienteDTO);
    }

    @Test
    void actualizarCliente() {
        log.info("Prueba: actualizarCliente");

        Long id = 1L;
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNombre("Juan Actualizado");
        when(clienteGestionService.actualizarCliente(id, clienteDTO)).thenReturn(clienteDTO);

        ResponseEntity<ApiResponse<ClienteDTO>> response = clienteController.actualizarCliente(id, clienteDTO);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cliente actualizado exitosamente", response.getBody().getMensaje());
        verify(clienteGestionService, times(1)).actualizarCliente(id, clienteDTO);
    }

    @Test
    void eliminarCliente() {
        log.info("Prueba: eliminarCliente");

        Long id = 1L;
        doNothing().when(clienteGestionService).eliminarClientePorId(id);

        ResponseEntity<ApiResponse<Void>> response = clienteController.eliminarCliente(id);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cliente eliminado exitosamente", response.getBody().getMensaje());
        verify(clienteGestionService, times(1)).eliminarClientePorId(id);
    }
}
