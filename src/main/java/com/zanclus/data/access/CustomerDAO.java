package com.zanclus.data.access;

import com.zanclus.data.entities.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * A Spring Data JPA {@link Repository} which implements the default CRUD operations for the {@link Customer} entity
 */
@Repository
public interface CustomerDAO extends CrudRepository<Customer, Long> {
}
