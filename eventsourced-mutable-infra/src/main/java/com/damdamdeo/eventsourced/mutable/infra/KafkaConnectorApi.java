package com.damdamdeo.eventsourced.mutable.infra;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

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

}
