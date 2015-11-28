package com.zanclus.verticles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanclus.data.access.CustomerDAO;
import com.zanclus.data.entities.Customer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jacpfx.vertx.spring.SpringVerticle;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * A worker {@link io.vertx.core.Verticle} which gets injected with Spring DI when created.
 */
@SpringVerticle
@Slf4j
public class CustomerWorker extends AbstractVerticle {
    public static final String MESSAGE_NOT_JSON = "Invalid message format. Message is NOT a JsonObject";

    @Autowired
    private CustomerDAO dao;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer("com.zanclus.customer").handler(this::handleDatabaseRequest);
    }

    public void handleDatabaseRequest(Message<Object> msg) {
        String method = msg.headers().get("method");

        try {
            switch (method) {
                case "getAllCustomers":
                    msg.reply(mapper.writeValueAsString(dao.findAll()));
                    break;
                case "getCustomer":
                    Long id = Long.parseLong(msg.headers().get("id"));
                    msg.reply(mapper.writeValueAsString(dao.findOne(id)));
                    break;
                case "addCustomer":
                    msg.reply(mapper.writeValueAsString(dao.save(mapper.readValue(((JsonObject)msg.body()).encode(), Customer.class))));
                    break;
                default:
                    log.error("Invalid method '" + method + "'");
            }
        } catch (IOException | NullPointerException e) {
            log.error("Problem parsing JSON data.", e);
        }

        if (JsonObject.class.isInstance(msg.body())) {

        } else {
            log.error(MESSAGE_NOT_JSON);
            msg.reply(MESSAGE_NOT_JSON);
        }
    }
}
