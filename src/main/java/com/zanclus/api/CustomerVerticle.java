package com.zanclus.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanclus.data.access.CustomerDAO;
import com.zanclus.data.entities.Customer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.jacpfx.vertx.spring.SpringVerticle;
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
@SpringVerticle
@Slf4j
public class CustomerVerticle {

    @Autowired
    private Vertx vertx;

    @PostConstruct
    public void start() throws Exception {
        log.info("Successfully create CustomerVerticle");
        DeploymentOptions deployOpts = new DeploymentOptions().setWorker(true).setMultiThreaded(true).setInstances(4);
        vertx.deployVerticle("java-spring:com.zanclus.verticles.CustomerWorker", deployOpts, res -> {
            if (res.succeeded()) {
                Router router = Router.router(vertx);
                router.route().handler(BodyHandler.create());
                final DeliveryOptions opts = new DeliveryOptions()
                        .setSendTimeout(2000);
                router.get("/v1/customer/:id")
                        .produces("application/json")
                        .handler(rc -> {
                            opts.addHeader("method", "getCustomer")
                                    .addHeader("id", rc.request().getParam("id"));
                            vertx.eventBus().send("com.zanclus.customer", null, opts, reply -> handleReply(reply, rc));
                        });
                router.put("/v1/customer")
                        .consumes("application/json")
                        .produces("application/json")
                        .handler(rc -> {
                            opts.addHeader("method", "addCustomer");
                            vertx.eventBus().send("com.zanclus.customer", rc.getBodyAsJson(), opts, reply -> handleReply(reply, rc));
                        });
                router.get("/v1/customer")
                        .produces("application/json")
                        .handler(rc -> {
                            opts.addHeader("method", "getAllCustomers");
                            vertx.eventBus().send("com.zanclus.customer", null, opts, reply -> handleReply(reply, rc));
                        });
                vertx.createHttpServer().requestHandler(router::accept).listen(8080);
            } else {
                log.error("Failed to deploy worker verticles.", res.cause());
            }
        });
    }

    private void handleReply(AsyncResult<Message<Object>> reply, RoutingContext rc) {
        if (reply.succeeded()) {
            Message<Object> replyMsg = reply.result();
            if (reply.succeeded()) {
                rc.response()
                        .setStatusMessage("OK")
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/json")
                        .end(replyMsg.body().toString());
            } else {
                rc.response()
                        .setStatusCode(500)
                        .setStatusMessage("Server Error")
                        .end(reply.cause().getLocalizedMessage());
            }
        }
    }
}
