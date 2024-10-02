package com.paucar.customer_ms.repository;

import com.paucar.customer_ms.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByDni(String dni);
}
