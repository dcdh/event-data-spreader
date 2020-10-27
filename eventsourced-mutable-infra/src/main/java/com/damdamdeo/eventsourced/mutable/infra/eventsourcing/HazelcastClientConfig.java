package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Optional;

@ApplicationScoped
public class HazelcastClientConfig {

    @Produces
    public HazelcastInstance createInstance() {
        final ClientConfig clientConfig = new ClientConfig();
        final String[] members = Optional.ofNullable(System.getenv("HAZELCAST_IP"))
                .orElseGet(() -> System.getProperty("HAZELCAST_IP"))
                .split(",");

        clientConfig.getNetworkConfig().addAddress(members);
        return HazelcastClient.newHazelcastClient(clientConfig);
    }
}

