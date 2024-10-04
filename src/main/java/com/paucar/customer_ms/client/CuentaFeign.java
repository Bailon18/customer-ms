package com.paucar.customer_ms.client;

import com.paucar.customer_ms.model.Cuenta;
import com.paucar.customer_ms.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// Quitar el parámetro 'url' al manejarse localmente
@FeignClient(name = "ACCOUNT-MS", url = "https://account-ms-production.up.railway.app")
public interface CuentaFeign {

    @GetMapping("/cuentas/cliente/{clienteId}")
    ResponseEntity<ApiResponse<List<Cuenta>>> obtenerCuentasPorClienteId(@PathVariable("clienteId") Long clienteId);
}
