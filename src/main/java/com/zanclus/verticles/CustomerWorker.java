package com.zanclus.verticles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanclus.Application;
import com.zanclus.data.access.CustomerDAO;
import com.zanclus.data.entities.Customer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jacpfx.vertx.spring.SpringVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * A worker {@link io.vertx.core.Verticle} which gets injected with Spring DI when created.
 */
@Component
@SpringVerticle(springConfig=Application.class)
@Slf4j
public class CustomerWorker extends AbstractVerticle {

    // Figure out why the Repository is not being injected.
    @Autowired
    private CustomerDAO dao;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Entry point for this {@link io.vertx.core.Verticle}
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        // Register a consumer on the event bus to listen for message sent to "com.zanclus.customer"
        vertx.eventBus().consumer("com.zanclus.customer").handler(this::handleDatabaseRequest);
    }

    /**
     * Handle database operations for a given message
     * @param msg The {@link Message} containing the information to tell which database operations to perform
     */
    public void handleDatabaseRequest(Message<Object> msg) {
        String method = msg.headers().get("method");

        DeliveryOptions opts = new DeliveryOptions();
        try {
            String retVal;
            switch (method) {
                case "getAllCustomers":
                    retVal = mapper.writeValueAsString(dao.findAll());
                    msg.reply(retVal, opts);
                    break;
                case "getCustomer":
                    Long id = Long.parseLong(msg.headers().get("id"));
                    retVal = mapper.writeValueAsString(dao.findOne(id));
                    msg.reply(retVal);
                    break;
                case "addCustomer":
                    retVal = mapper.writeValueAsString(
                                        dao.save(
                                                mapper.readValue(
                                                        ((JsonObject)msg.body()).encode(), Customer.class)));
                    msg.reply(retVal);
                    break;
                default:
                    log.error("Invalid method '" + method + "'");
                    opts.addHeader("error", "Invalid method '" + method + "'");
                    msg.fail(1, "Invalid method");
            }
        } catch (IOException | NullPointerException e) {
            log.error("Problem parsing JSON data.", e);
            msg.fail(2, e.getLocalizedMessage());
        }
    }
}
