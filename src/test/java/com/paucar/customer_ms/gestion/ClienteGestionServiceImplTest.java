package com.paucar.customer_ms.gestion;

import com.paucar.customer_ms.client.CuentaFeign;
import com.paucar.customer_ms.dto.ClienteDTO;
import com.paucar.customer_ms.exception.ClienteConCuentasActivasException;
import com.paucar.customer_ms.exception.ClienteNoEncontradoException;
import com.paucar.customer_ms.mapper.ClienteMapper;
import com.paucar.customer_ms.model.Cliente;
import com.paucar.customer_ms.client.dto.Cuenta;
import com.paucar.customer_ms.repository.ClienteRepository;
import com.paucar.customer_ms.service.gestion.ClienteGestionServiceImpl;
import com.paucar.customer_ms.service.validacion.ClienteValidacionService;
import com.paucar.customer_ms.util.ApiResponse;
import com.paucar.customer_ms.util.EstadoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteGestionServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(ClienteGestionServiceImplTest.class);

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private CuentaFeign cuentaFeign;

    @Mock
    private ClienteValidacionService clienteValidacionService;

    @InjectMocks
    private ClienteGestionServiceImpl clienteGestionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardarCliente_CuandoDatosCorrectos_DeberiaGuardarExitosamente() {
        log.info("Iniciando prueba: guardarCliente_CuandoDatosCorrectos_DeberiaGuardarExitosamente");

        ClienteDTO clienteDTO = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .email("juan@gmail.com")
                .build();

        Cliente clienteEntidad = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .email("juan@gmail.com")
                .build();

        when(clienteMapper.convertirAEntidad(clienteDTO)).thenReturn(clienteEntidad);
        when(clienteRepository.save(clienteEntidad)).thenReturn(clienteEntidad);
        when(clienteMapper.convertiADTO(clienteEntidad)).thenReturn(clienteDTO);

        ClienteDTO resultado = clienteGestionService.guardarCliente(clienteDTO);

        log.info("Resultado de guardar cliente: {}", resultado);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(clienteRepository, times(1)).save(clienteEntidad);
    }

    @Test
    void actualizarCliente_CuandoClienteExiste_DeberiaActualizarCorrectamente() {
        log.info("Iniciando prueba: actualizarCliente_CuandoClienteExiste_DeberiaActualizarCorrectamente");

        Cliente clienteExistente = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .email("juan@gmail.com")
                .build();

        ClienteDTO clienteDTO = ClienteDTO.builder()
                .id(1L)
                .nombre("Juanito")
                .apellido("Perez")
                .dni("12345678")
                .email("juanito@gmail.com")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteMapper.convertirAEntidad(clienteDTO)).thenReturn(clienteExistente);
        when(clienteRepository.save(clienteExistente)).thenReturn(clienteExistente);
        when(clienteMapper.convertiADTO(clienteExistente)).thenReturn(clienteDTO);

        ClienteDTO resultado = clienteGestionService.actualizarCliente(1L, clienteDTO);

        log.info("Resultado de actualización de cliente: {}", resultado);

        assertNotNull(resultado);
        assertEquals("Juanito", resultado.getNombre());
        verify(clienteRepository, times(1)).save(clienteExistente);
    }

    @Test
    void eliminarClientePorId_CuandoClienteTieneCuentasActivas_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: eliminarClientePorId_CuandoClienteTieneCuentasActivas_DeberiaLanzarExcepcion");

        when(clienteRepository.existsById(1L)).thenReturn(true);

        Cuenta cuentaActiva = Cuenta.builder().estado(EstadoCuenta.ACTIVO).build();
        ApiResponse<List<Cuenta>> apiResponse = ApiResponse.<List<Cuenta>>builder()
                .datos(Arrays.asList(cuentaActiva))
                .build();

        when(cuentaFeign.obtenerCuentasPorClienteId(1L)).thenReturn(ResponseEntity.ok(apiResponse));

        assertThrows(ClienteConCuentasActivasException.class, () -> clienteGestionService.eliminarClientePorId(1L));
        verify(clienteRepository, never()).deleteById(1L);
    }

    @Test
    void eliminarClientePorId_CuandoClienteNoExiste_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: eliminarClientePorId_CuandoClienteNoExiste_DeberiaLanzarExcepcion");

        when(clienteRepository.existsById(999L)).thenReturn(false);

        ClienteNoEncontradoException exception = assertThrows(ClienteNoEncontradoException.class, () -> {
            clienteGestionService.eliminarClientePorId(999L);
        });

        String mensajeEsperado = "Cliente no encontrado con el ID: 999";
        log.error("Excepción lanzada: {}", exception.getMessage());

        assertEquals(mensajeEsperado, exception.getMessage(), "El mensaje de la excepción no coincide con el esperado");

        verify(clienteRepository, never()).deleteById(anyLong());
    }


    @Test
    void eliminarClientePorId_CuandoClienteNoTieneCuentasActivas_DeberiaEliminarExitosamente() {
        log.info("Iniciando prueba: eliminarClientePorId_CuandoClienteNoTieneCuentasActivas_DeberiaEliminarExitosamente");

        when(clienteRepository.existsById(1L)).thenReturn(true);

        ApiResponse<List<Cuenta>> apiResponse = ApiResponse.<List<Cuenta>>builder()
                .datos(Arrays.asList())
                .build();
        when(cuentaFeign.obtenerCuentasPorClienteId(1L)).thenReturn(ResponseEntity.ok(apiResponse));

        clienteGestionService.eliminarClientePorId(1L);

        log.info("Cliente con ID {} eliminado exitosamente.", 1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }
}
