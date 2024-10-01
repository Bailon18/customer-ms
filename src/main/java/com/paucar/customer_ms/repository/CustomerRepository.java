package com.paucar.customer_ms.repository;

import com.paucar.customer_ms.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // query metodos -> Métod de consulta basado en el nombre del métod

    /**
     Queremos saber si existe otro cliente con el mismo email, pero que no sea el
     cliente con el id proporcionado.
     */
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByDniAndIdNot(String dni, Long id);
}
