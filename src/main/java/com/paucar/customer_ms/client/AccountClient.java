package com.paucar.customer_ms.client;
import com.paucar.customer_ms.model.Account;
import com.paucar.customer_ms.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ACCOUNT-MS", url = "https://customer-ms-production.up.railway.app")
public interface AccountClient {

    @GetMapping("/accounts/customer/{customerId}")
    ResponseEntity<ApiResponse<List<Account>>> getAccountsByCustomerId(@PathVariable("customerId") Long customerId);
}
