package com.zanclus.data.access;

import com.zanclus.data.entities.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by dphillips on 11/14/15.
 */
@Repository
public interface CustomerDAO extends CrudRepository<Customer, Long> {
}