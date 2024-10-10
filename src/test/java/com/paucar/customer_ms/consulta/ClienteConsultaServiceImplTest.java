package com.paucar.customer_ms.consulta;

import com.paucar.customer_ms.dto.ClienteDTO;
import com.paucar.customer_ms.exception.ClienteNoEncontradoException;
import com.paucar.customer_ms.mapper.ClienteMapper;
import com.paucar.customer_ms.model.Cliente;
import com.paucar.customer_ms.repository.ClienteRepository;
import com.paucar.customer_ms.service.consulta.ClienteConsultaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteConsultaServiceImplTest {

    // Definición del Logger utilizando LoggerFactory
    private static final Logger log = LoggerFactory.getLogger(ClienteConsultaServiceImplTest.class);

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteConsultaServiceImpl clienteConsultaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerTodosLosClientes_DeberiaRetornarListaDeClientes() {
        log.info("Iniciando prueba: obtenerTodosLosClientes_DeberiaRetornarListaDeClientes");

        // Simular una lista de clientes en el repositorio usando el patrón Builder
        List<Cliente> clientes = Arrays.asList(
                Cliente.builder()
                        .id(1L)
                        .nombre("Juan")
                        .apellido("Perez")
                        .dni("12345678")
                        .email("juan@gmail.com")
                        .build(),
                Cliente.builder()
                        .id(2L)
                        .nombre("Maria")
                        .apellido("Lopez")
                        .dni("87654321")
                        .email("maria@gmail.com")
                        .build()
        );
        when(clienteRepository.findAll()).thenReturn(clientes);

        // Simular el mapeo de entidades a DTO usando el patrón Builder
        when(clienteMapper.convertiADTO(clientes.get(0)))
                .thenReturn(ClienteDTO.builder()
                        .id(1L)
                        .nombre("Juan")
                        .apellido("Perez")
                        .dni("12345678")
                        .email("juan@gmail.com")
                        .build());
        when(clienteMapper.convertiADTO(clientes.get(1)))
                .thenReturn(ClienteDTO.builder()
                        .id(2L)
                        .nombre("Maria")
                        .apellido("Lopez")
                        .dni("87654321")
                        .email("maria@gmail.com")
                        .build());

        // Ejecutar el método a probar
        List<ClienteDTO> resultado = clienteConsultaService.obtenerTodosLosClientes();

        // Log del resultado mejorado
        resultado.forEach(cliente -> log.info("Cliente encontrado: {}", cliente.toString()));

        // Validar el resultado
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    void buscarClientePorId_CuandoClienteExiste_DeberiaRetornarCliente() {
        log.info("Iniciando prueba: buscarClientePorId_CuandoClienteExiste_DeberiaRetornarCliente");

        // Simular un cliente en el repositorio usando el patrón Builder
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .email("juan@gmail.com")
                .build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Simular el mapeo de la entidad a DTO usando el patrón Builder
        ClienteDTO clienteDTO = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .email("juan@gmail.com")
                .build();
        when(clienteMapper.convertiADTO(cliente)).thenReturn(clienteDTO);

        // Ejecutar el método a probar
        Optional<ClienteDTO> resultado = clienteConsultaService.buscarClientePorId(1L);

        // Log del resultado mejorado
        resultado.ifPresent(clienteEncontrado -> log.info("Cliente encontrado por ID: {}", clienteEncontrado.toString()));

        // Validar el resultado
        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    void buscarClientePorId_CuandoClienteNoExiste_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: buscarClientePorId_CuandoClienteNoExiste_DeberiaLanzarExcepcion");

        // Simular que el cliente no existe en el repositorio
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Validar que se lanza la excepción y capturar el mensaje de error
        ClienteNoEncontradoException exception = assertThrows(ClienteNoEncontradoException.class, () -> clienteConsultaService.buscarClientePorId(99L));

        // Log de la excepción con mensaje detallado
        log.error("Excepción lanzada: Cliente con ID {} no encontrado. Mensaje: {}", 99L, exception.getMessage());
    }
}
