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

    // Definición del Logger utilizando LoggerFactory
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

        // Crear un DTO de cliente utilizando el Builder
        ClienteDTO clienteDTO = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .email("juan@gmail.com")
                .build();

        // Crear entidad de Cliente utilizando el Builder
        Cliente clienteEntidad = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("12345678")
                .email("juan@gmail.com")
                .build();

        // Simular el mapeo de DTO a entidad y la validación
        when(clienteMapper.convertirAEntidad(clienteDTO)).thenReturn(clienteEntidad);
        when(clienteRepository.save(clienteEntidad)).thenReturn(clienteEntidad);
        when(clienteMapper.convertiADTO(clienteEntidad)).thenReturn(clienteDTO);

        // Ejecutar el método y verificar el resultado
        ClienteDTO resultado = clienteGestionService.guardarCliente(clienteDTO);

        // Log del resultado
        log.info("Resultado de guardar cliente: {}", resultado);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(clienteRepository, times(1)).save(clienteEntidad);
    }

    @Test
    void actualizarCliente_CuandoClienteExiste_DeberiaActualizarCorrectamente() {
        log.info("Iniciando prueba: actualizarCliente_CuandoClienteExiste_DeberiaActualizarCorrectamente");

        // Datos de prueba utilizando Builder
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

        // Simular la búsqueda del cliente existente
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteMapper.convertirAEntidad(clienteDTO)).thenReturn(clienteExistente);
        when(clienteRepository.save(clienteExistente)).thenReturn(clienteExistente);
        when(clienteMapper.convertiADTO(clienteExistente)).thenReturn(clienteDTO);

        // Ejecutar el método y validar el resultado
        ClienteDTO resultado = clienteGestionService.actualizarCliente(1L, clienteDTO);

        // Log del resultado
        log.info("Resultado de actualización de cliente: {}", resultado);

        assertNotNull(resultado);
        assertEquals("Juanito", resultado.getNombre());
        verify(clienteRepository, times(1)).save(clienteExistente);
    }

    @Test
    void eliminarClientePorId_CuandoClienteTieneCuentasActivas_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: eliminarClientePorId_CuandoClienteTieneCuentasActivas_DeberiaLanzarExcepcion");

        // Simular existencia de cliente por ID
        when(clienteRepository.existsById(1L)).thenReturn(true);

        // Simular respuesta de cuentas activas
        Cuenta cuentaActiva = Cuenta.builder().estado(EstadoCuenta.ACTIVO).build();
        ApiResponse<List<Cuenta>> apiResponse = ApiResponse.<List<Cuenta>>builder()
                .datos(Arrays.asList(cuentaActiva))
                .build();

        when(cuentaFeign.obtenerCuentasPorClienteId(1L)).thenReturn(ResponseEntity.ok(apiResponse));

        // Validar que lanza la excepción
        assertThrows(ClienteConCuentasActivasException.class, () -> clienteGestionService.eliminarClientePorId(1L));
        verify(clienteRepository, never()).deleteById(1L);
    }

    @Test
    void eliminarClientePorId_CuandoClienteNoExiste_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: eliminarClientePorId_CuandoClienteNoExiste_DeberiaLanzarExcepcion");

        // Simular que el cliente no existe en el repositorio
        when(clienteRepository.existsById(999L)).thenReturn(false);

        // Capturar la excepción lanzada y validar su mensaje
        ClienteNoEncontradoException exception = assertThrows(ClienteNoEncontradoException.class, () -> {
            clienteGestionService.eliminarClientePorId(999L);
        });

        // Cambiar el mensaje esperado para que coincida con el generado en la excepción
        String mensajeEsperado = "Cliente no encontrado con el ID: 999";
        log.error("Excepción lanzada: {}", exception.getMessage());

        // Validar que el mensaje de la excepción es el esperado
        assertEquals(mensajeEsperado, exception.getMessage(), "El mensaje de la excepción no coincide con el esperado");

        // Validar que el método `deleteById` nunca se llame
        verify(clienteRepository, never()).deleteById(anyLong());
    }


    @Test
    void eliminarClientePorId_CuandoClienteNoTieneCuentasActivas_DeberiaEliminarExitosamente() {
        log.info("Iniciando prueba: eliminarClientePorId_CuandoClienteNoTieneCuentasActivas_DeberiaEliminarExitosamente");

        // Simular existencia del cliente
        when(clienteRepository.existsById(1L)).thenReturn(true);

        // Simular respuesta sin cuentas activas
        ApiResponse<List<Cuenta>> apiResponse = ApiResponse.<List<Cuenta>>builder()
                .datos(Arrays.asList())
                .build();
        when(cuentaFeign.obtenerCuentasPorClienteId(1L)).thenReturn(ResponseEntity.ok(apiResponse));

        // Llamar al método eliminar y validar
        clienteGestionService.eliminarClientePorId(1L);

        // Log de eliminación exitosa
        log.info("Cliente con ID {} eliminado exitosamente.", 1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }
}
