package com.damdamdeo.eventsourced.mutable.publisher;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
@ApplicationScoped
@RegisterRestClient(configKey="kafka-connector-api")
public interface KafkaConnectorApi {

    @GET
    @Path("/connectors")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> getAllConnectors();

    @POST
    @Path("/connectors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Void registerConnector(String connectorConfiguration);

    @GET
    @Path("/connectors/{connectorName}/status")
    @Produces(MediaType.APPLICATION_JSON)
    KafkaConnectorStatus connectorStatus(@PathParam("connectorName") String connectorName);

}
