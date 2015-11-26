package com.zanclus.api;

import com.zanclus.data.access.CustomerDAO;
import com.zanclus.data.entities.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * {@link RestController} for {@link Customer} documents.
 */
@RestController
@RequestMapping(value = "/v1/customer", produces="application/json")
@Slf4j
public class CustomerEndpoints {
    @Autowired
    private CustomerDAO dao;

    /**
     * Return a list of all {@link Customer}s from the database
     * @return
     */
    @RequestMapping(method=GET)
    public @ResponseBody List<Customer> findAll() {
        return StreamSupport.stream(dao.findAll().spliterator(), false).collect(Collectors.toList());
    }

    /**
     * Return the {@link Customer} identified
     * @param id The ID of the {@link Customer} to be returned.
     * @return A {@link Customer} document or {@code null} if not found.
     */
    @RequestMapping(value="/{id}", method=GET)
    public @ResponseBody Customer findById(@PathVariable("id") Long id) {
        return dao.findOne(id);
    }

    @RequestMapping(method=PUT, consumes="application/json")
    public @ResponseBody Customer addCustomer(@RequestBody Customer customer) throws Exception {
        return dao.save(customer);
    }
}
