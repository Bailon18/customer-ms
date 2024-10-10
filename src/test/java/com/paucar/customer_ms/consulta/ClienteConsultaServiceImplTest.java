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

        List<ClienteDTO> resultado = clienteConsultaService.obtenerTodosLosClientes();

        resultado.forEach(cliente -> log.info("Cliente encontrado: {}", cliente.toString()));

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    void buscarClientePorId_CuandoClienteExiste_DeberiaRetornarCliente() {
        log.info("Iniciando prueba: buscarClientePorId_CuandoClienteExiste_DeberiaRetornarCliente");

        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .email("juan@gmail.com")
                .build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteDTO clienteDTO = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .email("juan@gmail.com")
                .build();
        when(clienteMapper.convertiADTO(cliente)).thenReturn(clienteDTO);

        Optional<ClienteDTO> resultado = clienteConsultaService.buscarClientePorId(1L);

        resultado.ifPresent(clienteEncontrado -> log.info("Cliente encontrado por ID: {}", clienteEncontrado.toString()));

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    void buscarClientePorId_CuandoClienteNoExiste_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: buscarClientePorId_CuandoClienteNoExiste_DeberiaLanzarExcepcion");

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        ClienteNoEncontradoException exception = assertThrows(ClienteNoEncontradoException.class, () -> clienteConsultaService.buscarClientePorId(99L));

        log.error("Excepción lanzada: Cliente con ID {} no encontrado. Mensaje: {}", 99L, exception.getMessage());
    }
}
