package com.paucar.customer_ms.validacion;

import com.paucar.customer_ms.exception.DniYaRegistradoException;
import com.paucar.customer_ms.exception.EmailYaRegistradoException;
import com.paucar.customer_ms.model.Cliente;
import com.paucar.customer_ms.repository.ClienteRepository;
import com.paucar.customer_ms.service.validacion.ClienteValidacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteValidacionServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(ClienteValidacionServiceImplTest.class);

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteValidacionServiceImpl clienteValidacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validarCliente_CuandoEmailYaRegistrado_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarCliente_CuandoEmailYaRegistrado_DeberiaLanzarExcepcion");

        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Luis")
                .apellido("Gonzales")
                .dni("87654321")
                .email("luis.gonzales@gmail.com")
                .build();

        Cliente clienteExistente = Cliente.builder().id(2L).email("luis.gonzales@gmail.com").build();
        when(clienteRepository.findByEmail("luis.gonzales@gmail.com")).thenReturn(Optional.of(clienteExistente));

        assertThrows(EmailYaRegistradoException.class, () -> clienteValidacionService.validarCliente(cliente, 1L));

        log.info("Resultado: se lanzó la excepción EmailYaRegistradoException correctamente");
        verify(clienteRepository, times(1)).findByEmail("luis.gonzales@gmail.com");
    }

    @Test
    void validarCliente_CuandoDniYaRegistrado_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarCliente_CuandoDniYaRegistrado_DeberiaLanzarExcepcion");

        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("María")
                .apellido("Rojas")
                .dni("65432178")
                .email("maria.rojas@hotmail.com")
                .build();

        Cliente clienteExistente = Cliente.builder().id(2L).dni("65432178").build();
        when(clienteRepository.findByDni("65432178")).thenReturn(Optional.of(clienteExistente));

        assertThrows(DniYaRegistradoException.class, () -> clienteValidacionService.validarCliente(cliente, 1L));

        log.info("Resultado: se lanzó la excepción DniYaRegistradoException correctamente");
        verify(clienteRepository, times(1)).findByDni("65432178");
    }

    @Test
    void validarCliente_CuandoEmailYdDniNoRegistrados_NoDeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarCliente_CuandoEmailYdDniNoRegistrados_NoDeberiaLanzarExcepcion");

        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Quispe")
                .dni("78965432")
                .email("juan.quispe@outlook.com")
                .build();

        when(clienteRepository.findByEmail("juan.quispe@outlook.com")).thenReturn(Optional.empty());
        when(clienteRepository.findByDni("78965432")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> clienteValidacionService.validarCliente(cliente, 1L));

        log.info("Resultado: la validación se completó sin excepciones");
        verify(clienteRepository, times(1)).findByEmail("juan.quispe@outlook.com");
        verify(clienteRepository, times(1)).findByDni("78965432");
    }

    @Test
    void validarCliente_CuandoEsElMismoCliente_NoDeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarCliente_CuandoEsElMismoCliente_NoDeberiaLanzarExcepcion");

        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Ana")
                .apellido("Flores")
                .dni("76543219")
                .email("ana.flores@peru.com")
                .build();

        Cliente clienteExistente = Cliente.builder()
                .id(1L)
                .dni("76543219")
                .email("ana.flores@peru.com")
                .build();

        when(clienteRepository.findByEmail("ana.flores@peru.com")).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.findByDni("76543219")).thenReturn(Optional.of(clienteExistente));

        assertDoesNotThrow(() -> clienteValidacionService.validarCliente(cliente, 1L));

        log.info("Resultado: la validación del cliente se completó sin excepciones");
        verify(clienteRepository, times(1)).findByEmail("ana.flores@peru.com");
        verify(clienteRepository, times(1)).findByDni("76543219");
    }
}
