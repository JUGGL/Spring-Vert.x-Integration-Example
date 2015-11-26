package com.zanclus.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanclus.data.access.CustomerDAO;
import com.zanclus.data.entities.Customer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by dphillips on 11/25/15.
 */
@Component
@Slf4j
public class CustomerVerticle {

    @Autowired
    private CustomerDAO dao;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Vertx vertx;

    @PostConstruct
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/v1/customer/:id")
                .produces("application/json")
                .blockingHandler(this::getCustomerById);
        router.put("/v1/customer")
                .consumes("application/json")
                .produces("application/json")
                .blockingHandler(this::addCustomer);
        router.get("/v1/customer")
                .produces("application/json")
                .blockingHandler(this::getAllCustomers);
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void addCustomer(RoutingContext rc) {
        try {
            String body = rc.getBodyAsString();
            Customer customer = mapper.readValue(body, Customer.class);
            Customer saved = dao.save(customer);
            if (saved!=null) {
                rc.response().setStatusMessage("Accepted").setStatusCode(202).end(mapper.writeValueAsString(saved));
            } else {
                rc.response().setStatusMessage("Bad Request").setStatusCode(400).end("Bad Request");
            }
        } catch (IOException e) {
            rc.response().setStatusMessage("Server Error").setStatusCode(500).end("Server Error");
            log.error("Server error", e);
        }
    }

    private void getCustomerById(RoutingContext rc) {
        log.info("Request for single customer");
        Long id = Long.parseLong(rc.request().getParam("id"));
        try {
            Customer customer = dao.findOne(id);
            if (customer==null) {
                rc.response().setStatusMessage("Not Found").setStatusCode(404).end("Not Found");
            } else {
                rc.response().setStatusMessage("OK").setStatusCode(200).end(mapper.writeValueAsString(dao.findOne(id)));
            }
        } catch (JsonProcessingException jpe) {
            rc.response().setStatusMessage("Server Error").setStatusCode(500).end("Server Error");
            log.error("Server error", jpe);
        }
    }

    private void getAllCustomers(RoutingContext rc) {
        log.info("Request for all customers");
        List<Customer> customers = StreamSupport.stream(dao.findAll().spliterator(), false).collect(Collectors.toList());
        try {
            rc.response().setStatusMessage("OK").setStatusCode(200).end(mapper.writeValueAsString(customers));
        } catch (JsonProcessingException jpe) {
            rc.response().setStatusMessage("Server Error").setStatusCode(500).end("Server Error");
            log.error("Server error", jpe);
        }
    }
}
