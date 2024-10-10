package com.paucar.customer_ms.exception;

import com.paucar.customer_ms.util.ApiResponse;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExcepcionesGlobales {

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionEmailYaRegistrado(EmailYaRegistradoException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DniYaRegistradoException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionDniYaRegistrado(DniYaRegistradoException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClienteConCuentasActivasException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionClienteConCuentasActivas(ClienteConCuentasActivasException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionArgumentoInvalido(IllegalArgumentException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionRecursoNoEncontrado(NoHandlerFoundException ex) {
        ApiResponse<Void> respuestaError = ApiResponse.<Void>builder()
                .estado(HttpStatus.NOT_FOUND.value())
                .mensaje("El recurso solicitado no fue encontrado. Verifique la URL.")
                .datos(null)
                .build();

        return new ResponseEntity<>(respuestaError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionTipoArgumento(MethodArgumentTypeMismatchException ex) {
        String mensajeError = String.format("El valor '%s' no es válido para el parámetro '%s'. Se esperaba un valor de tipo '%s'.",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        return construirRespuestaError(mensajeError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarExcepcionesValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> respuesta = ApiResponse.<Map<String, String>>builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .mensaje("Se encontraron errores de validación.")
                .datos(errores)
                .build();

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionFeign(FeignException ex) {
        System.out.println("Capturada la excepción FeignException con estado: " + ex.status());
        if (ex.status() == 404) {
            return construirRespuestaError("El recurso no fue encontrado en el servicio externo.", HttpStatus.NOT_FOUND);
        }
        return construirRespuestaError("Error al comunicarse con otro microservicio: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarExcepcionViolacionRestriccion(ConstraintViolationException ex) {
        Map<String, String> errores = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (mensaje1, mensaje2) -> mensaje1 + "; " + mensaje2
                ));

        ApiResponse<Map<String, String>> respuesta = ApiResponse.<Map<String, String>>builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .mensaje("Se encontraron errores de validación.")
                .datos(errores)
                .build();

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionGlobal(Exception ex) {
        return construirRespuestaError("Ocurrió un error inesperado. Por favor, intente más tarde.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> construirRespuestaError(String mensaje, HttpStatus estado) {
        ApiResponse<Void> respuestaError = ApiResponse.<Void>builder()
                .estado(estado.value())
                .mensaje(mensaje)
                .datos(null)
                .build();

        return new ResponseEntity<>(respuestaError, estado);
    }
}
